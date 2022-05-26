package com.jagerlipton.bgaprofilesaver.data.service;

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
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.JSON.JSONCommand;
import com.jagerlipton.bgaprofilesaver.data.service.JSON.ParsingJSON;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;
import com.jagerlipton.bgaprofilesaver.domain.model.CommandModel;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UsbService extends Service {

    public static final String ACTION_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String ACTION_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    private static final String ACTION_USB_PERMISSION = "com.jagerlipton.bgaprofilesaver.USB_PERMISSION";

    public static final int MESSAGE_FROM_SERIAL_PORT = 0;
    public static final int MESSAGE_FROM_SERVICE = 1;

    private int baudrate = 9600; // default

    private final IBinder binder = new UsbBinder();

    private UsbManager usbManager;
    private UsbDevice device;
    private UsbDeviceConnection connection;
    private UsbSerialDevice serialPort;
    private boolean serialPortConnected = false;
    private final Connection connState = new Connection(false, false, "", 0, 0, false);

    //==============================================================================================
    // send messages to ServiceManager

    private Messenger messageHandler;

    public boolean sendToServiceManager(int state, Object obj) {

        if (messageHandler != null) {
            Message message = Message.obtain();
            message.arg1 = state;
            message.obj = obj;
            try {
                messageHandler.send(message);
                return true;

            } catch (
                    RemoteException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public void setMessageHandler(Messenger messageHandler) {
        this.messageHandler = messageHandler;
    }

    //==============================================================================================
    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public class UsbBinder extends Binder {
        public UsbService getService() {
            return UsbService.this;
        }
    }

    //==============================================================================================
    // Override methods
    @Override
    public void onCreate() {
        serialPortConnected = false;
        setFilter();
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        findSerialPortDevice();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        serialPort.close();
        unregisterReceiver(usbReceiver);
        connState.setPortState(false);
        sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    //==============================================================================================

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
                if (granted) {
                    connState.setBroadcast("USB Ready");
                    sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
                    connection = usbManager.openDevice(device);
                    new ConnectionThread().start();
                } else {
                    connState.setBroadcast("USB Permission not granted");
                    sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
                }
            } else if (arg1.getAction().equals(ACTION_USB_ATTACHED)) {
                if (!serialPortConnected)
                    findSerialPortDevice();
            } else if (arg1.getAction().equals(ACTION_USB_DETACHED)) {
                connState.setBroadcast("USB Disconnected");
                sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
                if (serialPortConnected) {
                    serialPort.close();
                }
                serialPortConnected = false;
                connState.setPortState(false);
                sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
            }
        }
    };

    private void findSerialPortDevice() {
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
            }
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                int devicePID = device.getProductId();
                connState.setPID(devicePID);
                connState.setVID(deviceVID);

                if (UsbSerialDevice.isSupported(device)) {
                    connState.setSupportedDevice(true);
                    requestUserPermission();
                    break;
                } else {
                    connection = null;
                    device = null;
                }
            }
            if (device == null) {
                connState.setBroadcast("No USB");
                sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
            }
        } else {
            connState.setBroadcast("No USB");
            sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
        }
    }

    private void requestUserPermission() {
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
                    connState.setPortState(true);
                    sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
                    serialPort.setBaudRate(baudrate);
                    serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                    serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    serialPort.read(mCallback);
                }
            } else {
                connState.setBroadcast("USB not supported");
                sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
            }
        }
    }

    private void write(byte[] data) {
        if (serialPort != null)
            serialPort.write(data);
    }

    public void writeCommandToPort(String outputString, boolean progressbar) {
        write(outputString.getBytes());
        if (progressbar) {
            connState.setProgressbarState(true);
            sendToServiceManager(MESSAGE_FROM_SERVICE, connState);
        }
    }



    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @Override
        public void onReceivedData(byte[] arg0) {
            connState.setProgressbarState(false);
            sendToServiceManager(MESSAGE_FROM_SERVICE, connState);

            String data = new String(arg0, StandardCharsets.UTF_8);
            if (ParsingJSON.isJSONValid(data)) parsing(data);
        }
    };


    private void parsing(String jsonInput) {

        if (ParsingJSON.getJSONCommand(jsonInput) == JSONCommand.REPLACE) {
            ArrayList<ArduinoProfileListData> inputList = new ArrayList<>();
            inputList = ParsingJSON.JSONToReplaceArrayList(jsonInput);
            assert inputList != null;
            if (inputList.size() > 0) {
                sendToServiceManager(MESSAGE_FROM_SERIAL_PORT, inputList);

            }
        }
    }




}
