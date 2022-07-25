package com.jagerlipton.bgaprofileeditor.domain.usecase;

import com.jagerlipton.bgaprofileeditor.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofileeditor.domain.model.ConnectionType;

public class StartServ {

    private final IServiceManager serviceInput;

    public StartServ(IServiceManager serviceInput) {
        this.serviceInput = serviceInput;
    }

    public void execute(ConnectionType type) {
        serviceInput.startServ(type);
    }

}
