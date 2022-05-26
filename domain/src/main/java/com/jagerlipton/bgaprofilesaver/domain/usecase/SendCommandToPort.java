package com.jagerlipton.bgaprofilesaver.domain.usecase;

import com.jagerlipton.bgaprofilesaver.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofilesaver.domain.model.CommandModel;

public class SendCommandToPort {

    private final IServiceManager serviceInput;

    public SendCommandToPort(IServiceManager serviceInput) {
        this.serviceInput = serviceInput;
    }

    public void execute(CommandModel model) {
        serviceInput.sendCommandToPort(model);
    }

}
