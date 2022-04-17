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
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    private static ScrollView mScrollView;
    private static ProgressBar mProgressBar;

    SharedPreferencesHelper mSharedPreferencesHelper;
    @NonNull
    private final ListAdapter mListAdapter = new ListAdapter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Log.d("ololo", "активити create");

        //TODO сделать viewbinding
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);

        mUARTTextview = (TextView) findViewById(R.id.UARTTextview);
        mImportButton = (Button) findViewById(R.id.importButton);
        mExportJSONButton = (Button) findViewById(R.id.exportJsonButton);
        mExportShortButton = (Button) findViewById(R.id.exportShortButton);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mSpeedSpinner = (Spinner) findViewById(R.id.speedSpinner);
        mImageView = (ImageView) findViewById(R.id.bgImageView);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mListAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(mRecycler.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.separator));
        mRecycler.addItemDecoration(divider);

// TODO сделать отдельный обсервер на старт стоп
        App.isPortState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean value) {
                if (value) onConnectControl();
                if (!value) onDisconnectControl();
            }
        });

        App.getScreenState().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
               Log.d("ololo", String.valueOf(integer));

                if (integer==1) onClearState();
                if (integer==2) onImport();
                if (integer==3) onFullState();
            }
    });


        App.inputList.observe(this, new Observer<ArrayList<InputClass>>() {
            @Override
            public void onChanged(ArrayList<InputClass> inputClasses) {

                    mListAdapter.addData(inputClasses);
                   if (App.getScreenState().getValue()==2) App.setScreenState(3);
            }
        });

        App.isValidValuesState().observe(this, new Observer<Boolean>() {
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
                    App.setScreenState(2);
                    usbService.write(data.getBytes());
                }
            }
        });


        mExportShortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = List2Short();
                if (usbService != null) {
                    usbService.write(data.getBytes());
                    Log.d("ololo",data);
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
                App.setScreenState(1);
                App.clearList();

            }
        });
    }

    private String List2Short() {
        String data = "SHORT_PROFILE:";

        for (int i = 0; i < ListAdapter.mInputArrayList.size(); i++) {
            data = data.concat(ListAdapter.mInputArrayList.get(i).getValueName());
         //   if (i < ListAdapter.mInputArrayList.size() - 1)
              data = data.concat(",");
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
        Log.d("ololo", "активити resume");
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ololo", "активити pause");
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("ololo", "активити start");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ololo", "активити stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ololo", "активити destroy");
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

    private static void onClearState() {
        mImportButton.setVisibility(View.VISIBLE);
        mRecycler.setVisibility(View.GONE);
        mExportJSONButton.setVisibility(View.GONE);
        mExportShortButton.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
    }
    private static void onImport() {
        mImportButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);

    }

    private static void onFullState() {
        mImportButton.setEnabled(true);
        mImportButton.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
        mExportJSONButton.setVisibility(View.VISIBLE);
        mExportShortButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
        mSaveButton.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }

    private void onConnectControl() {
        mUARTTextview.setText(R.string.label_connected);
        mSpeedSpinner.setEnabled(false);
        mImageView.setImageResource(R.drawable.usb_online);
        mScrollView.setVisibility(View.VISIBLE);
    }

    private void onDisconnectControl() {
        mUARTTextview.setText(R.string.label_disconnected);
        mSpeedSpinner.setEnabled(true);
        mImageView.setImageResource(R.drawable.usb_offline);
        mScrollView.setVisibility(View.GONE);
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