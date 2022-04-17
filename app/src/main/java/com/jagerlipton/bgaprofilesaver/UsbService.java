package com.jagerlipton.bgaprofilesaver;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UsbService extends Service {

    public static final String TAG = "UsbService";

    public static final String ACTION_USB_READY = "com.jagerlipton.bgaprofilesaver.USB_READY";
    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    public static final String ACTION_USB_NOT_SUPPORTED = "com.jagerlipton.bgaprofilesaver.USB_NOT_SUPPORTED";
    public static final String ACTION_NO_USB = "com.jagerlipton.bgaprofilesaver.NO_USB";
    public static final String ACTION_USB_PERMISSION_GRANTED = "com.jagerlipton.bgaprofilesaver.USB_PERMISSION_GRANTED";
    public static final String ACTION_USB_PERMISSION_NOT_GRANTED = "com.jagerlipton.bgaprofilesaver.USB_PERMISSION_NOT_GRANTED";
    public static final String ACTION_USB_DISCONNECTED = "com.jagerlipton.bgaprofilesaver.USB_DISCONNECTED";
    public static final String ACTION_CDC_DRIVER_NOT_WORKING = "com.jagerlipton.bgaprofilesaver.ACTION_CDC_DRIVER_NOT_WORKING";
    public static final String ACTION_USB_DEVICE_NOT_WORKING = "com.jagerlipton.bgaprofilesaver.ACTION_USB_DEVICE_NOT_WORKING";
    private static final String ACTION_USB_PERMISSION = "com.jagerlipton.bgaprofilesaver.USB_PERMISSION";

    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    private static int baudrate = 9600; // default
    public static boolean SERVICE_CONNECTED = false;

    private IBinder binder = new UsbBinder();

    private Context context;
    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbSerialDevice serialPort;
    private boolean serialPortConnected;

    @Override
    public void onCreate() {
        this.context = this;

        Log.d("ololo", "сервис create");

        serialPortConnected = false;
        App.setPortState(false);
        UsbService.SERVICE_CONNECTED = true;
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ololo", "сервис destroy");
        serialPort.close();
        App.setPortState(false);
        unregisterReceiver(usbReceiver);
        UsbService.SERVICE_CONNECTED = false;
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
                  return UsbService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("ololo", "сервис bind");
        return binder;
          }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ololo", "сервис unbind");
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    private void setFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_DETACHED);
        filter.addAction(ACTION_USB_ATTACHED);
        registerReceiver(usbReceiver, filter);
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = arg1.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) // User accepted our USB connection. Try to open the device as a serial port
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION_GRANTED);
                    arg0.sendBroadcast(intent);
                    connection = usbManager.openDevice(device);
                    new ConnectionThread().start();
                } else // User not accepted our USB connection. Send an Intent to the Main Activity
                {
                    Intent intent = new Intent(ACTION_USB_PERMISSION_NOT_GRANTED);
                    arg0.sendBroadcast(intent);
                }
            } else if (arg1.getAction().equals(ACTION_USB_ATTACHED)) {
                if (!serialPortConnected)
                    findSerialPortDevice(); // A USB device has been attached. Try to open it as a Serial port
            } else if (arg1.getAction().equals(ACTION_USB_DETACHED)) {
                // Usb device was disconnected. send an intent to the Main Activity
                Intent intent = new Intent(ACTION_USB_DISCONNECTED);
                arg0.sendBroadcast(intent);
                if (serialPortConnected) {
                    serialPort.close();
                }
                serialPortConnected = false;
                App.setPortState(false);
            }
        }
    };

    private void findSerialPortDevice() {
        // This snippet will try to open the first encountered usb device connected, excluding usb root hubs
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {

            // first, dump the hashmap for diagnostic purposes
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                Log.d(TAG, String.format("USBDevice.HashMap (vid:pid) (%X:%X)-%b class:%X:%X name:%s",
                        device.getVendorId(), device.getProductId(),
                        UsbSerialDevice.isSupported(device),
                        device.getDeviceClass(), device.getDeviceSubclass(),
                        device.getDeviceName()));
            }

            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();

//                if (deviceVID != 0x1d6b && (devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003) && deviceVID != 0x5c6 && devicePID != 0x904c) {
                if (UsbSerialDevice.isSupported(device)) {
                    // There is a supported device connected - request permission to access it.
                    requestUserPermission();
                    break;
                } else {
                    connection = null;
                    device = null;
                }
            }
            if (device == null) {
                // There are no USB devices connected (but usb host were listed). Send an intent to MainActivity.
                Intent intent = new Intent(ACTION_NO_USB);
                sendBroadcast(intent);
            }
        } else {
            Log.d(TAG, "findSerialPortDevice() usbManager returned empty device list.");
            // There is no USB devices connected
            Intent intent = new Intent(ACTION_NO_USB);
            sendBroadcast(intent);
        }
    }

    private void requestUserPermission() {
        Log.d(TAG, String.format("requestUserPermission(%X:%X)", device.getVendorId(), device.getProductId()));

        Intent intent = new Intent(ACTION_USB_PERMISSION);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        usbManager.requestPermission(device, mPendingIntent);
    }

    private class ConnectionThread extends Thread {
        @Override
        public void run() {
            serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
            if (serialPort != null) {
                if (serialPort.open()) {

                    App.setPortState(true);
                    baudrate = App.getBaudrate();

                    serialPort.setBaudRate(baudrate);

                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);

                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback);

                } else {

                    //
                }
            } else {
                Intent intent = new Intent(ACTION_USB_NOT_SUPPORTED);
                context.sendBroadcast(intent);
            }
        }
    }

    public void write(byte[] data) {
        if (serialPort != null)
            serialPort.write(data);
    }

    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                String data = new String(arg0, "UTF-8");

                if (isJSONValid(data)) parsingJSON(data);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    private static boolean isJSONValid(String jsonInput) {
        Gson gson = new Gson();

        try {
            gson.fromJson(jsonInput, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }


   // String ss = "{\"APPEND\":{\"firefox\":4,\"opera\":5,\"ie\":6}}";
    String s = "{\"PROFILE\":{\"profileSteps\":4,\"Setpoint2\":20,\"min_pwr_TOPStep[1]888787878787878\":10,\"min_pwr_TOPStep[2]\":20,\"min_pwr_TOPStep[3]\":30,\"min_pwr_TOPStep[4]\":40,\"max_pwr_TOPStep[1]\":40,\"max_pwr_TOPStep[2]\":50,\"max_pwr_TOPStep[3]\":60,\"max_pwr_TOPStep[4]\":70,\"rampRateStep[1]\":5,\"rampRateStep[2]\":6,\"rampRateStep[3]\":7,\"rampRateStep[4]\":8,\"temperatureStep[1]\":6,\"temperatureStep[2]\":7,\"temperatureStep[3]\":8,\"temperatureStep[4]\":9,\"dwellTimerStep[1]\":5,\"dwellTimerStep[2]\":5,\"dwellTimerStep[3]\":5,\"dwellTimerStep[4]\":5,\"kp1\":100,\"ki1\":100,\"kd1\":100,\"kp2\":100,\"ki2\":100,\"kd2\":100}}";
     String ss = "{\"APPEND\":{\"firefox\":4}}";

    private void parsingJSON(String jsonInput) {
        try {
            JSONObject jObject = new JSONObject(jsonInput);

            String nameObject = "";
            Integer sizeList = 0;
            ArrayList<InputClass> inputList = new ArrayList<>();

            if (jObject.has("PROFILE")) {
                nameObject = "PROFILE";
            }
            if (jObject.has("APPEND")) {
                if (App.inputList.getValue()!= null) {
                    inputList.addAll(App.inputList.getValue());
                    sizeList = inputList.size();
                }
                nameObject = "APPEND";
            }

            if (nameObject.equals("PROFILE") || nameObject.equals("APPEND")) {
                JSONObject jsonData = jObject.getJSONObject(nameObject);

                for (int i = 0; i < jsonData.names().length(); i++) {

                    InputClass input = new InputClass();

                    Integer counter = sizeList + i + 1;
                    input.setId(counter);

                    String key = jsonData.names().getString(i);
                    input.setKeyName(key);

                    String value = jsonData.get(key).toString();
                    input.setValueName(value);
                    inputList.add(input);
                }
            }

            if (inputList.size() > 0) App.inputList.postValue(inputList);

        } catch (JSONException e) {
            //TODO();
        }
    }


}
