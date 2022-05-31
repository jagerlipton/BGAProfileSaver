package com.jagerlipton.bgaprofilesaver.data.service;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import androidx.annotation.NonNull;
import com.jagerlipton.bgaprofilesaver.data.repository.model.ArduinoProfileListData;
import com.jagerlipton.bgaprofilesaver.data.service.JSON.ParsingJSON;
import com.jagerlipton.bgaprofilesaver.data.service.model.Connection;
import com.jagerlipton.bgaprofilesaver.data.storage.IStorage;
import com.jagerlipton.bgaprofilesaver.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofilesaver.domain.model.Baudrate;
import com.jagerlipton.bgaprofilesaver.domain.model.CommandModel;
import com.jagerlipton.bgaprofilesaver.domain.model.ConnectionType;
import java.util.List;

public class ServiceManager implements IServiceManager {

    private final Context context;
    private final IStorage storage;
    private final ServiceOutput serviceOutput;

    public ServiceManager(Context context, IStorage storage, ServiceOutput serviceOutput) {
        this.context = context;
        this.storage = storage;
        this.serviceOutput = serviceOutput;
    }

    private UsbService usbService;
    private Boolean isBounded = false;

    Handler mainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.arg1) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    List<ArduinoProfileListData> data = (List<ArduinoProfileListData>) message.obj;
                    serviceOutput.setList(data);
                    break;
                case UsbService.MESSAGE_FROM_SERVICE:
                    Connection connection = (Connection) message.obj;
                    serviceOutput.setConnection(connection);
                    break;
            }
            return true;
        }
    });

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            isBounded = true;
            usbService.setMessageHandler(new Messenger(mainHandler));
            usbService.setBaudrate(Baudrate.getBaudrateByIndex(storage.loadBaudrateIndex()));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
            isBounded = false;
        }
    };

    private void startUSBService(Class<?> service, ServiceConnection serviceConnection) {
        if (!isBounded) {
            Intent intentService = new Intent(context, service);
            context.startService(intentService);
        }
        Intent bindingIntent = new Intent(context, service);
        context.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void startServ(ConnectionType connectionType) {
        if (connectionType == ConnectionType.USB) {
            startUSBService(UsbService.class, usbConnection);
        }
    }

    @Override
    public void stopServ(ConnectionType connectionType) {
        if (isBounded) {
            context.unbindService(usbConnection);
            isBounded = false;
        }
    }

    @Override
    public void sendCommandToPort(CommandModel command) {
        CommandModel.Commands cmd = command.getCommand();
        switch (cmd) {
            case COMMAND_GET_PROFILE: {
                usbService.writeCommandToPort(ParsingJSON.commandToJSONString("COMMAND_GET_PROFILE"));
                break;
            }
            case COMMAND_SAVE_PROFILE: {
                usbService.writeCommandToPort(ParsingJSON.commandToJSONString("COMMAND_SAVE_PROFILE"));
                break;
            }
            case JSON_PROFILE: {
                usbService.writeCommandToPort(ParsingJSON.listToJSONString(ArduinoProfileListData.mapDomainToData(command.getList())));
                break;
            }
            case SHORT_PROFILE: {
                usbService.writeCommandToPort(ParsingJSON.listToString(ArduinoProfileListData.mapDomainToData(command.getList())));
                break;
            }
        }
    }
}
