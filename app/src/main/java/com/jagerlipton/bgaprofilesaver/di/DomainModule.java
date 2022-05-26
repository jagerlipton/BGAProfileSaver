package com.jagerlipton.bgaprofilesaver.di;

import com.jagerlipton.bgaprofilesaver.domain.interfaces.IRepository;
import com.jagerlipton.bgaprofilesaver.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofilesaver.domain.usecase.LoadBaudrateIndex;
import com.jagerlipton.bgaprofilesaver.domain.usecase.SaveBaudrateIndex;
import com.jagerlipton.bgaprofilesaver.domain.usecase.SendCommandToPort;
import com.jagerlipton.bgaprofilesaver.domain.usecase.StartServ;
import com.jagerlipton.bgaprofilesaver.domain.usecase.StopServ;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;

@Module
@InstallIn(ViewModelComponent.class)
public class DomainModule {

    @Provides
    LoadBaudrateIndex provideLoadBaudrateIndex(IRepository repository) {
        return new LoadBaudrateIndex(repository);
    }
    @Provides
    SaveBaudrateIndex provideSaveBaudrateIndex(IRepository repository) {
        return new SaveBaudrateIndex(repository);
    }
    @Provides
    SendCommandToPort provideSendMessageToPort (IServiceManager serviceManager){
        return new SendCommandToPort(serviceManager);
    }
    @Provides
    StartServ provideStartServ (IServiceManager serviceManager){
        return new StartServ(serviceManager);
    }
    @Provides
    StopServ provideStopServ (IServiceManager serviceManager){
        return new StopServ(serviceManager);
    }

}
