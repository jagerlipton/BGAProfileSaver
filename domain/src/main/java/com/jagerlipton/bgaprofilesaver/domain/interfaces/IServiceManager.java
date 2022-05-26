package com.jagerlipton.bgaprofilesaver.domain.interfaces;

import com.jagerlipton.bgaprofilesaver.domain.model.CommandModel;
import com.jagerlipton.bgaprofilesaver.domain.model.ConnectionType;

public interface IServiceManager {

void startServ(ConnectionType connectionType);

void stopServ(ConnectionType connectionType);

void sendCommandToPort(CommandModel command);

}
