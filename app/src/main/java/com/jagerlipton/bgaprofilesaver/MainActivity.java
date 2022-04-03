package com.jagerlipton.bgaprofilesaver;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static TextView mUARTTextview;
    private static Button mImportButton, mExportShortButton, mExportJSONButton, mSaveButton, mCancelButton;
    private static Spinner mSpeedSpinner;

    private UsbService usbService;
    private MyHandler mHandler;
    private static LinearLayout mContainer;
    private static List<View> listViews;

    public static final String APP_PREFERENCES = "preferences";
    public static final String APP_PREFERENCES_BAUDRATE = "baudrate";
    SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        mHandler = new MyHandler(this);
        mUARTTextview = (TextView) findViewById(R.id.UARTTextview);
        mImportButton = (Button) findViewById(R.id.importButton);
        mExportJSONButton = (Button) findViewById(R.id.exportJsonButton);
        mExportShortButton = (Button) findViewById(R.id.exportShortButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mSpeedSpinner = (Spinner) findViewById(R.id.speedSpinner);

        if (mSharedPreferences.contains(APP_PREFERENCES_BAUDRATE)) {
            mSpeedSpinner.setSelection(mSharedPreferences.getInt(APP_PREFERENCES_BAUDRATE, 0));

        }

        mSpeedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Editor editor = mSharedPreferences.edit();
                editor.putInt(APP_PREFERENCES_BAUDRATE, position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        mContainer = (LinearLayout) findViewById(R.id.container);
        listViews = new ArrayList<View>();

        mImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "COMMAND_GET_PROFILE";
                listViews.clear();
                mContainer.removeAllViews();

                if (usbService != null) { // if UsbService was correctly binded, Send data
                    usbService.write(data.getBytes());
                }
            }
        });

        mExportShortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "SHORT_PROFILE:";

                String[] items = new String[listViews.size()];

                for (int i = 0; i < listViews.size(); i++) {
                    data = data.concat(((EditText) listViews.get(i).findViewById(R.id.edittext)).getText().toString());
                    if (i < listViews.size() - 1) data = data.concat(",");
                }
                Log.d("ololo", data);

                if (usbService != null) {
                    usbService.write(data.getBytes());
                }
            }
        });

        mExportJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "JSON_PROFILE";
                try {
                    JSONObject profile = new JSONObject();

                    for (int i = 0; i < listViews.size(); i++) {
                        String value = ((EditText) listViews.get(i).findViewById(R.id.edittext)).getText().toString();
                        String key = ((TextView) listViews.get(i).findViewById(R.id.textview)).getText().toString();
                        profile.put(key, Integer.valueOf(value));
                    }

                    JSONObject JSONprofile = new JSONObject();
                    JSONprofile.put(data, profile);
                    data = JSONprofile.toString();
                    Log.d("ololo", data);
                    if (usbService != null) {
                        usbService.write(data.getBytes());
                    }
                } catch (JSONException e) {
                    //some exception handler code.
                }

            }
        });


        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "COMMAND_SAVE_PROFILE";
                if (usbService != null) {
                    usbService.write(data.getBytes());
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listViews.clear();
                mContainer.removeAllViews();

                mImportButton.setVisibility(View.VISIBLE);
                mExportJSONButton.setVisibility(View.GONE);
                mExportShortButton.setVisibility(View.GONE);
                mSaveButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.GONE);


            }
        });

        JSONParsing(s, this);

    }


    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }



    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */

    // String s = "{\"browsers\":{\"firefox\":4,\"opera\":5,\"ie\":6}}";
    String s = "{\"PROFILE\":{\"profileSteps\":4,\"Setpoint2\":20,\"min_pwr_TOPStep[1]\":10,\"min_pwr_TOPStep[2]\":20,\"min_pwr_TOPStep[3]\":30,\"min_pwr_TOPStep[4]\":40,\"max_pwr_TOPStep[1]\":40,\"max_pwr_TOPStep[2]\":50,\"max_pwr_TOPStep[3]\":60,\"max_pwr_TOPStep[4]\":70,\"rampRateStep[1]\":5,\"rampRateStep[2]\":6,\"rampRateStep[3]\":7,\"rampRateStep[4]\":8,\"temperatureStep[1]\":6,\"temperatureStep[2]\":7,\"temperatureStep[3]\":8,\"temperatureStep[4]\":9,\"dwellTimerStep[1]\":5,\"dwellTimerStep[2]\":5,\"dwellTimerStep[3]\":5,\"dwellTimerStep[4]\":5,\"kp1\":100,\"ki1\":100,\"kd1\":100,\"kp2\":100,\"ki2\":100,\"kd2\":100}}";

    private static void JSONParsing(String jsonInput, Context context) {
        try {
            JSONObject jObject = new JSONObject(jsonInput);

            if (jObject.has("PROFILE")) {

                mImportButton.setVisibility(View.GONE);
                mExportJSONButton.setVisibility(View.VISIBLE);
                mExportShortButton.setVisibility(View.VISIBLE);
                mSaveButton.setVisibility(View.VISIBLE);
                mCancelButton.setVisibility(View.VISIBLE);

                JSONObject jsonData = jObject.getJSONObject("PROFILE");

                Iterator<String> keys = jsonData.keys();

                listViews.clear();
                mContainer.removeAllViews();

                for (int i = 0; i < jsonData.names().length(); i++) {

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.custom_edittext_layout, null);

                    TextView tw = (TextView) view.findViewById(R.id.textview);
                    String key = jsonData.names().getString(i);
                    tw.setText(key);

                    EditText et = (EditText) view.findViewById(R.id.edittext);
                    String value = jsonData.get(key).toString();
                    et.setText(value);


                    listViews.add(view);
                    mContainer.addView(view);
                }


            }


        } catch (JSONException e) {
            //some exception handler code.
        }


    }

    private boolean isTextValid(EditText edittext, Context context) {

        Resources resources = context.getResources();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && TextUtils.isEmpty(edittext.getText())) {
            edittext.setBackgroundTintList(resources.getColorStateList(R.color.error, context.getTheme()));

        } else {
            edittext.setBackgroundTintList(resources.getColorStateList(R.color.error));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !TextUtils.isEmpty(edittext.getText())) {
            edittext.setBackgroundTintList(resources.getColorStateList(R.color.ok, context.getTheme()));

        } else {
            edittext.setBackgroundTintList(resources.getColorStateList(R.color.ok));
        }

        return !TextUtils.isEmpty(edittext.getText());
    }


    private static boolean isJSONValid(String jsonInput) {
        Gson gson = new Gson();

        try {
            gson.fromJson(jsonInput, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;

                    if (isJSONValid(data)) {
                        JSONParsing(data, mActivity.get());

                        //получение сообщений из порта

                    }

                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED

                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    mUARTTextview.setText("Connected");
                    mImportButton.setVisibility(View.VISIBLE);
                    mSpeedSpinner.setEnabled(false);
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    mImportButton.setVisibility(View.GONE);
                    mSpeedSpinner.setEnabled(true);
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    mUARTTextview.setText("Disconnected");
                    mImportButton.setVisibility(View.GONE);
                    mSpeedSpinner.setEnabled(true);
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    mUARTTextview.setText("Disconnected");
                    mImportButton.setVisibility(View.GONE);
                    mSpeedSpinner.setEnabled(true);
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    mUARTTextview.setText("Disconnected");
                    mImportButton.setVisibility(View.GONE);
                    mSpeedSpinner.setEnabled(true);
                    break;
            }
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };


}