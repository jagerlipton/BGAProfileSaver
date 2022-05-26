package com.jagerlipton.bgaprofilesaver.data.service;

import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;

import java.util.ArrayList;

public class ServiceOutput {

    private IServiceOutputListener commandListener;

    public void setListener(IServiceOutputListener commandListener) {
        this.commandListener = commandListener;
    }

    public void setList(ArrayList<ArduinoProfileListData> input) {
        if (commandListener != null) commandListener.getServiceArrayList(input);
    }

    public void setConnection(Connection connection) {
        if (commandListener != null) commandListener.getServiceConnectionData(connection);
    }
}
