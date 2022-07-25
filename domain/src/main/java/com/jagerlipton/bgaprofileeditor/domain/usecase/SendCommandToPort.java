package com.jagerlipton.bgaprofileeditor.domain.usecase;

import com.jagerlipton.bgaprofileeditor.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofileeditor.domain.model.CommandModel;

public class SendCommandToPort {

    private final IServiceManager serviceInput;

    public SendCommandToPort(IServiceManager serviceInput) {
        this.serviceInput = serviceInput;
    }

    public void execute(CommandModel model) {
        serviceInput.sendCommandToPort(model);
    }

}
