package com.jagerlipton.bgaprofilesaver.presentation.ui;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jagerlipton.bgaprofilesaver.R;
import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.IServiceOutputListener;
import com.jagerlipton.bgaprofilesaver.data.service.ServiceOutput;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;
import com.jagerlipton.bgaprofilesaver.domain.model.ConnectionType;
import com.jagerlipton.bgaprofilesaver.presentation.model.ArduinoProfileListUI;
import com.jagerlipton.bgaprofilesaver.presentation.model.ScreenState;
import com.jagerlipton.bgaprofilesaver.presentation.util.IValidEditTextListener;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private TextView uartTextView;
    private Button importButton, exportShortButton, exportJSONButton, saveButton, cancelButton;
    private Spinner speedSpinner;
    private ImageView bgImageView;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private ProgressBar progressBar;


    private final ListAdapter listAdapter = new ListAdapter();

    private MainViewModel mainViewModel;

    private Observer<Boolean> portStateObserver;
    private Observer<Boolean> progressbarStateObserver;
    private Observer<ScreenState> screenStateObserver;
    private Observer<ArrayList<ArduinoProfileListUI>> arrayListObserver;
    private Observer<Boolean> validValuesEditTextObserver;
    private Observer<String> liveCastObserver;

    @Inject
    ServiceOutput serviceOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // TODO пофиксить
        setContentView(R.layout.activity_main);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        uartTextView = (TextView) findViewById(R.id.UARTTextview);
        importButton = (Button) findViewById(R.id.importButton);
        exportJSONButton = (Button) findViewById(R.id.exportJsonButton);
        exportShortButton = (Button) findViewById(R.id.exportShortButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        speedSpinner = (Spinner) findViewById(R.id.speedSpinner);
        bgImageView = (ImageView) findViewById(R.id.bgImageView);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.separator));  //TODO пофиксить
        recyclerView.addItemDecoration(divider);

        portStateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean portState) {
                if (portState) onConnect();
                else onDisconnect();
            }
        };

        progressbarStateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean progressState) {
                if (progressState) progressBar.setVisibility(View.VISIBLE);
                else progressBar.setVisibility(View.GONE);
            }
        };

        screenStateObserver = new Observer<ScreenState>() {
            @Override
            public void onChanged(ScreenState screenState) {

                switch (screenState) {
                    case CLEAR:
                    case CANCELLED: {
                        clearState();
                        break;
                    }
                    case WAIT: {
                        waitState();
                        break;
                    }
                    case RECIEVED: {
                        recievedState();
                        break;
                    }
                }
            }
        };

        arrayListObserver = new Observer<ArrayList<ArduinoProfileListUI>>() {
            @Override
            public void onChanged(ArrayList<ArduinoProfileListUI> input) {
                listAdapter.addData(input);
                if (mainViewModel.getScreenState().getValue() != ScreenState.CANCELLED)
                    mainViewModel.changeScreenState(ScreenState.RECIEVED);
            }
        };

        validValuesEditTextObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean valid) {
                if (valid) enableControlButton();
                if (!valid) disableControlButton();
            }
        };

        liveCastObserver = new Observer<String>() {
            @Override
            public void onChanged(String castMessage) {
                showToast(castMessage);
            }
        };


        speedSpinner.setSelection(mainViewModel.loadSpinnerValue());

        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                mainViewModel.saveSpinnerValue(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //
            }

        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.importButtonClick();

            }
        });

        exportShortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.exportSHORTButtonClick(listAdapter.getInputList());
            }
        });

        exportJSONButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.exportJSONButtonClick(listAdapter.getInputList());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.saveButtonClick();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.cancelButtonClick();
            }
        });


        serviceOutput.setListener(new IServiceOutputListener() {
            @Override
            public void getServiceArrayList(ArrayList<ArduinoProfileListData> list) {
                mainViewModel.getConnDataList(list);
            }

            @Override
            public void getServiceConnectionData(Connection connection) {
                mainViewModel.getConnState(connection);
            }
        });

        listAdapter.setValidValuesListener(new IValidEditTextListener() {
            @Override
            public void isValid(boolean flag) {
                mainViewModel.setValidValuesState(flag);
            }
        });

    }

    private void showToast(final String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.isPortState().observe(this, portStateObserver);
        mainViewModel.isProgressbarState().observe(this, progressbarStateObserver);
        mainViewModel.getScreenState().observe(this, screenStateObserver);
        mainViewModel.getDataArrayList().observe(this, arrayListObserver);
        mainViewModel.isValidValuesState().observe(this, validValuesEditTextObserver);
        mainViewModel.getLiveCast().observe(this, liveCastObserver);

        mainViewModel.startService(ConnectionType.USB);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainViewModel.isPortState().removeObserver(portStateObserver);
        mainViewModel.isProgressbarState().removeObserver(progressbarStateObserver);
        mainViewModel.getScreenState().removeObserver(screenStateObserver);
        mainViewModel.getDataArrayList().removeObserver(arrayListObserver);
        mainViewModel.isValidValuesState().removeObserver(validValuesEditTextObserver);
        mainViewModel.getLiveCast().removeObserver(liveCastObserver);
    }

    private void disableControlButton() {
        exportJSONButton.setEnabled(false);
        exportShortButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    private void enableControlButton() {
        exportJSONButton.setEnabled(true);
        exportShortButton.setEnabled(true);
        saveButton.setEnabled(true);
    }

    //-------------------------------------------------------------
    private void clearState() {
        importButton.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        exportJSONButton.setVisibility(View.GONE);
        exportShortButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        bgImageView.setVisibility(View.VISIBLE);
    }

    private void waitState() {
        importButton.setEnabled(false);
    }

    private void recievedState() {
        importButton.setEnabled(true);
        importButton.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        exportJSONButton.setVisibility(View.VISIBLE);
        exportShortButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        bgImageView.setVisibility(View.GONE);
    }

    //-----------------------------------------------------------------
    private void onConnect() {
        uartTextView.setText(R.string.label_connected);
        speedSpinner.setEnabled(false);
        bgImageView.setImageResource(R.drawable.usb_online);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void onDisconnect() {
        uartTextView.setText(R.string.label_disconnected);
        speedSpinner.setEnabled(true);
        bgImageView.setImageResource(R.drawable.usb_offline);
        scrollView.setVisibility(View.GONE);
    }

}