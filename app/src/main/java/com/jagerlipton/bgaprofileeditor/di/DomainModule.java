package com.jagerlipton.bgaprofileeditor.di;

import com.jagerlipton.bgaprofileeditor.domain.interfaces.IRepository;
import com.jagerlipton.bgaprofileeditor.domain.interfaces.IServiceManager;
import com.jagerlipton.bgaprofileeditor.domain.usecase.LoadBaudrateIndex;
import com.jagerlipton.bgaprofileeditor.domain.usecase.SaveBaudrateIndex;
import com.jagerlipton.bgaprofileeditor.domain.usecase.SendCommandToPort;
import com.jagerlipton.bgaprofileeditor.domain.usecase.StartServ;
import com.jagerlipton.bgaprofileeditor.domain.usecase.StopServ;

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
