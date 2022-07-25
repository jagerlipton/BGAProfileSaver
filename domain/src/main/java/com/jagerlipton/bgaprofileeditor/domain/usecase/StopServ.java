package com.jagerlipton.bgaprofileeditor.domain.usecase;

import com.jagerlipton.bgaprofileeditor.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofileeditor.domain.model.ConnectionType;

public class StopServ {

    private final IServiceManager serviceInput;

    public StopServ(IServiceManager serviceInput) {
        this.serviceInput = serviceInput;
    }

    public void execute(ConnectionType type) {
        serviceInput.stopServ(type);
    }

}
