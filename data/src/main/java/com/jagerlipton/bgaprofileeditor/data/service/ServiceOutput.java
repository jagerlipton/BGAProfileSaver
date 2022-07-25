package com.jagerlipton.bgaprofileeditor.data.service;

import com.jagerlipton.bgaprofileeditor.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofileeditor.data.service.model.Connection;
import java.util.List;

public class ServiceOutput {

    private IServiceOutputListener commandListener;

    public void setListener(IServiceOutputListener commandListener) {
        this.commandListener = commandListener;
    }

    public void setList(List<ArduinoProfileListData> input) {
        if (commandListener != null) commandListener.getServiceArrayList(input);
    }

    public void setConnection(Connection connection) {
        if (commandListener != null) commandListener.getServiceConnectionData(connection);
    }

}
