package com.jagerlipton.bgaprofileeditor.domain.interfaces;

import com.jagerlipton.bgaprofileeditor.domain.model.CommandModel;
import com.jagerlipton.bgaprofileeditor.domain.model.ConnectionType;

public interface IServiceManager {

void startServ(ConnectionType connectionType);

void stopServ(ConnectionType connectionType);

void sendCommandToPort(CommandModel command);

}
