package com.jagerlipton.bgaprofilesaver.presentation.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;
import com.jagerlipton.bgaprofilesaver.domain.model.ArduinoProfileListDomain;
import com.jagerlipton.bgaprofilesaver.domain.model.CommandModel;
import com.jagerlipton.bgaprofilesaver.domain.model.ConnectionType;
import com.jagerlipton.bgaprofilesaver.domain.usecase.LoadBaudrateIndex;
import com.jagerlipton.bgaprofilesaver.domain.usecase.SaveBaudrateIndex;
import com.jagerlipton.bgaprofilesaver.domain.usecase.SendCommandToPort;
import com.jagerlipton.bgaprofilesaver.domain.usecase.StartServ;
import com.jagerlipton.bgaprofilesaver.domain.usecase.StopServ;
import com.jagerlipton.bgaprofilesaver.presentation.model.ArduinoProfileListUI;
import com.jagerlipton.bgaprofilesaver.presentation.model.ScreenState;
import com.jagerlipton.bgaprofilesaver.presentation.util.SingleLiveEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    //==============================================================================================

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


    //------------------------------------------------------------------------------------
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

    private final MutableLiveData<ArrayList<ArduinoProfileListUI>> dataArrayList = new MutableLiveData<>();

    public LiveData<ArrayList<ArduinoProfileListUI>> getDataArrayList() {
        return dataArrayList;
    }

    //------------------------------------------------------------------------------------
    public void importButtonClick() {
        changeScreenState(ScreenState.WAIT);

        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.COMMAND_GET_PROFILE);
        command.setList(null);
        sendCommandToPort.execute(command);
    }

    public void exportSHORTButtonClick(ArrayList<ArduinoProfileListUI> inputList) {

        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.SHORT_PROFILE);
        command.setList(ArduinoProfileListUI.mapUIToDomain(inputList));
        sendCommandToPort.execute(command);
    }

    public void exportJSONButtonClick(ArrayList<ArduinoProfileListUI> inputList) {

        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.JSON_PROFILE);
        command.setList(ArduinoProfileListUI.mapUIToDomain(inputList));
        sendCommandToPort.execute(command);
    }

    public void saveButtonClick() {

        CommandModel command = new CommandModel();
        command.setCommand(CommandModel.Commands.COMMAND_SAVE_PROFILE);
        command.setList(null);
        sendCommandToPort.execute(command);
    }

    public void cancelButtonClick() {
        changeScreenState(ScreenState.CANCELLED);
    }

    //===================================================================================
    public void changeScreenState(ScreenState state) {
        screenState.setValue(state);
    }

    //------------------------------------------------------------------------------------
    public Integer loadSpinnerValue() {
        Integer index = loadBaudrateIndex.execute();
        if (index != null) return index;
        else return 0;
    }

    public void saveSpinnerValue(Integer position) {
        saveBaudrateIndex.execute(position);
    }


    //--------------------------------------------------------------
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

    void getConnDataList(ArrayList<ArduinoProfileListData> dataList) {
        if (!dataList.isEmpty()) {
            dataArrayList.setValue(ArduinoProfileListUI.mapDataToUI(dataList));
        }
    }


    //-----------------------------------------------------------

}


