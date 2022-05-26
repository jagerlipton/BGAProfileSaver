package com.jagerlipton.bgaprofilesaver.domain.usecase;

import com.jagerlipton.bgaprofilesaver.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofilesaver.domain.model.ConnectionType;

public class StartServ {

    private final IServiceManager serviceInput;

    public StartServ(IServiceManager serviceInput) {
        this.serviceInput = serviceInput;
    }

    public void execute(ConnectionType type) {
        serviceInput.startServ(type);
    }

}
