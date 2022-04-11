package com.jagerlipton.bgaprofilesaver;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static TextView mUARTTextview;
    private static Button mImportButton, mExportShortButton, mExportJSONButton, mSaveButton, mCancelButton;
    private static Spinner mSpeedSpinner;
    private UsbService usbService;
    private static ImageView mImageView;
    private static RecyclerView mRecycler;

    SharedPreferencesHelper mSharedPreferencesHelper;
    @NonNull
    private final ListAdapter mListAdapter = new ListAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        mSharedPreferencesHelper = new SharedPreferencesHelper(this);

        mUARTTextview = (TextView) findViewById(R.id.UARTTextview);
        mImportButton = (Button) findViewById(R.id.importButton);
        mExportJSONButton = (Button) findViewById(R.id.exportJsonButton);
        mExportShortButton = (Button) findViewById(R.id.exportShortButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mSpeedSpinner = (Spinner) findViewById(R.id.speedSpinner);
        mImageView = (ImageView) findViewById(R.id.bgImageView);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mListAdapter);
        DividerItemDecoration divider = new DividerItemDecoration(mRecycler.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.separator));
        mRecycler.addItemDecoration(divider);


        App.getPortStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) onConnectControl();
                if (!value) onDisconnectControl();
            }
        });

        App.inputList.observe(this, new Observer<ArrayList<InputClass>>() {
            @Override
            public void onChanged(ArrayList<InputClass> inputClasses) {
                if (!App.isCanceled) {
                    mListAdapter.addData(inputClasses);
                    visibleControlButton();
                }
                if (App.isCanceled) {
                    mListAdapter.addData(inputClasses);
                    invisibleControlButton();
                }
            }
        });

        App.getValidValues().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) enableControlButton();
                if (!value) disableControlButton();
            }
        });

        mSpeedSpinner.setSelection(mSharedPreferencesHelper.loadBaudrateIndex());

        mSpeedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                App.setBaudrateIndex(position);
                mSharedPreferencesHelper.saveBaudrateIndex(position);
                String[] choose = getResources().getStringArray(R.array.speed);
                App.setBaudrate(Integer.parseInt(choose[position]));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }

        });


        mImportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "COMMAND_GET_PROFILE";
                if (usbService != null) {
                    usbService.write(data.getBytes());
                    App.isCanceled=false;
                }
            }
        });


        mExportShortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = List2Short();
                if (usbService != null) {
                    usbService.write(data.getBytes());
                }
            }
        });

        mExportJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = List2JSON();
                if (usbService != null) {
                    usbService.write(data.getBytes());
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
              App.isCanceled=true;
              App.clearList();
            }
        });
    }

    private String List2Short() {
        String data = "SHORT_PROFILE:";

        for (int i = 0; i < ListAdapter.mInputArrayList.size(); i++) {
            data = data.concat(ListAdapter.mInputArrayList.get(i).getValueName());
            if (i < ListAdapter.mInputArrayList.size() - 1) data = data.concat(",");
        }
        return data;
    }

    private String List2JSON (){
        String data = "JSON_PROFILE";

        try {
            JSONObject profile = new JSONObject();

            for (int i = 0; i < ListAdapter.mInputArrayList.size(); i++) {
                String key = ListAdapter.mInputArrayList.get(i).getKeyName();
                String value = ListAdapter.mInputArrayList.get(i).getValueName();
                profile.put(key, Integer.valueOf(value));
            }

            JSONObject JSONprofile = new JSONObject();
            JSONprofile.put(data, profile);
            data = JSONprofile.toString();

        } catch (JSONException e) {
            //
        }
        return data;
    }


    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null);
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

    private static void disableControlButton() {
        mExportJSONButton.setEnabled(false);
        mExportShortButton.setEnabled(false);
        mSaveButton.setEnabled(false);
    }

    private static void enableControlButton() {
        mExportJSONButton.setEnabled(true);
        mExportShortButton.setEnabled(true);
        mSaveButton.setEnabled(true);
    }

    private static void invisibleControlButton() {
        mExportJSONButton.setVisibility(View.GONE);
        mExportShortButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
        mRecycler.setVisibility(View.GONE);
        mImportButton.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
    }

    private static void visibleControlButton() {
        mExportJSONButton.setVisibility(View.VISIBLE);
        mExportShortButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.VISIBLE);
        mImportButton.setVisibility(View.GONE);
        mImageView.setVisibility(View.GONE);

    }


    private void onConnectControl() {
        mUARTTextview.setText(R.string.label_connected);
        mImportButton.setVisibility(View.VISIBLE);
        mSpeedSpinner.setEnabled(false);
        mImageView.setImageResource(R.drawable.usb_online);
    }

    private void onDisconnectControl() {
        mUARTTextview.setText(R.string.label_disconnected);
        mImportButton.setVisibility(View.GONE);
        mSpeedSpinner.setEnabled(true);
        mImageView.setImageResource(R.drawable.usb_offline);
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, R.string.USBready, Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, R.string.USBnotgranted, Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, R.string.noUSB, Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, R.string.USBdisconnected, Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, R.string.USBnotsupported, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };


}