package com.jagerlipton.bgaprofilesaver.domain.usecase;

import com.jagerlipton.bgaprofilesaver.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofilesaver.domain.model.ConnectionType;

public class StopServ {

    private final IServiceManager serviceInput;

    public StopServ(IServiceManager serviceInput) {
        this.serviceInput = serviceInput;
    }

    public void execute(ConnectionType type) {
        serviceInput.stopServ(type);
    }

}
