package com.jagerlipton.bgaprofileeditor.presentation.ui;

import android.os.Handler;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jagerlipton.bgaprofileeditor.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofileeditor.data.service.model.Connection;
import com.jagerlipton.bgaprofileeditor.domain.model.CommandModel;
import com.jagerlipton.bgaprofileeditor.domain.model.ConnectionType;
import com.jagerlipton.bgaprofileeditor.domain.usecase.LoadBaudrateIndex;
import com.jagerlipton.bgaprofileeditor.domain.usecase.SaveBaudrateIndex;
import com.jagerlipton.bgaprofileeditor.domain.usecase.SendCommandToPort;
import com.jagerlipton.bgaprofileeditor.domain.usecase.StartServ;
import com.jagerlipton.bgaprofileeditor.domain.usecase.StopServ;
import com.jagerlipton.bgaprofileeditor.presentation.model.ArduinoProfileListUI;
import com.jagerlipton.bgaprofileeditor.presentation.model.ScreenState;
import com.jagerlipton.bgaprofileeditor.presentation.util.SingleLiveEvent;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    private final LoadBaudrateIndex loadBaudrateIndex;
    private final SaveBaudrateIndex saveBaudrateIndex;
    private final SendCommandToPort sendCommandToPort;
    private final StartServ startServ;
    private final StopServ stopServ;

    @Inject
    public MainViewModel(
            LoadBaudrateIndex loadBaudrateIndex,
            SaveBaudrateIndex saveBaudrateIndex,
            SendCommandToPort sendCommandToPort,
            StartServ startServ,
            StopServ stopServ
    ) {
        this.loadBaudrateIndex = loadBaudrateIndex;
        this.saveBaudrateIndex = saveBaudrateIndex;
        this.sendCommandToPort = sendCommandToPort;
        this.startServ = startServ;
        this.stopServ = stopServ;
    }

    @Override
    protected void onCleared() {
        stopService(ConnectionType.USB);
    }

    public void startService(ConnectionType connectionType) {
        startServ.execute(connectionType);
    }

    public void stopService(ConnectionType connectionType) {
        stopServ.execute(connectionType);
    }

    private final MutableLiveData<Boolean> portState = new MutableLiveData<>(false);
    public LiveData<Boolean> isPortState() {
        return portState;
    }

    private final MutableLiveData<Boolean> progressbarState = new MutableLiveData<>(false);
    public LiveData<Boolean> isProgressbarState() {
        return progressbarState;
    }

    private final MutableLiveData<ScreenState> screenState = new MutableLiveData<ScreenState>(ScreenState.CLEAR);
    public LiveData<ScreenState> getScreenState() {
        return screenState;
    }

    private final MutableLiveData<Boolean> validValuesState = new MutableLiveData<>(false);
    public LiveData<Boolean> isValidValuesState() {
        return validValuesState;
    }
    public void setValidValuesState(boolean flag) {
        validValuesState.setValue(flag);
    }

    private final SingleLiveEvent<String> liveCast = new SingleLiveEvent<>();
    public SingleLiveEvent<String> getLiveCast() {
        return liveCast;
    }

    private final MutableLiveData<List<ArduinoProfileListUI>> dataArrayList = new MutableLiveData<>();
    public LiveData<List<ArduinoProfileListUI>> getDataArrayList() {
        return dataArrayList;
    }

    public void importButtonClick() {
        changeScreenState(ScreenState.WAIT);
        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.COMMAND_GET_PROFILE);
        command.setList(null);
        sendCommandToPort.execute(command);
        setTimeOut(10000);
    }

    public void exportSHORTButtonClick(List<ArduinoProfileListUI> inputList) {
        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.SHORT_PROFILE);
        command.setList(ArduinoProfileListUI.mapUIToDomain(inputList));
        sendCommandToPort.execute(command);
        setTimeOut(10000);
    }

    public void exportJSONButtonClick(List<ArduinoProfileListUI> inputList) {
        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.JSON_PROFILE);
        command.setList(ArduinoProfileListUI.mapUIToDomain(inputList));
        sendCommandToPort.execute(command);
        setTimeOut(10000);
    }

    public void saveButtonClick() {
        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.COMMAND_SAVE_PROFILE);
        command.setList(null);
        sendCommandToPort.execute(command);
        setTimeOut(10000);
    }

    public void cancelButtonClick() {
        changeScreenState(ScreenState.CANCELLED);
    }

    public void changeScreenState(ScreenState state) {
        screenState.setValue(state);
    }

    public Integer loadSpinnerValue() {
        Integer index = loadBaudrateIndex.execute();
        if (index != null) return index;
        else return 0; //default
    }

    public void saveSpinnerValue(Integer position) {
        saveBaudrateIndex.execute(position);
    }

    void getConnState(Connection connection) {
        if (portState.getValue() != null) if (portState.getValue() != connection.isPortState())
            portState.setValue(connection.isPortState());
        if (progressbarState.getValue() != null)
            if (progressbarState.getValue() != connection.isProgressbarState())
                progressbarState.setValue(connection.isProgressbarState());
        if (!connection.getBroadcast().isEmpty())
            if (liveCast.getValue() != connection.getBroadcast())
                liveCast.setValue(connection.getBroadcast());
    }

    void getConnDataList(List<ArduinoProfileListData> dataList) {
        if (!dataList.isEmpty()) {
            dataArrayList.setValue(ArduinoProfileListUI.mapDataToUI(dataList));
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable;

    public void setTimeOut (int msec){
         runnable = () -> {
             liveCast.setValue("TimeOut");
             screenState.setValue(ScreenState.CLEAR);
             progressbarState.setValue(false);
         };
        if (handler == null) return;
        handler.postDelayed(runnable, msec);
    }

    public void cancelTimeOut() {
        try {
            handler.removeCallbacks(runnable);
            handler.removeCallbacksAndMessages(null);
            handler = null;
            runnable = null;
        }catch (Exception e){
          e.printStackTrace();
        }
    }
}


