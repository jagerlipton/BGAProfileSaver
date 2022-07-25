package com.jagerlipton.bgaprofileeditor.di;


import android.content.Context;

import com.jagerlipton.bgaprofileeditor.data.repository.Repository;
import com.jagerlipton.bgaprofileeditor.data.service.ServiceManager;
import com.jagerlipton.bgaprofileeditor.data.service.ServiceOutput;
import com.jagerlipton.bgaprofileeditor.data.storage.IStorage;
import com.jagerlipton.bgaprofileeditor.data.storage.Storage;
import com.jagerlipton.bgaprofileeditor.domain.interfaces.IRepository;
import com.jagerlipton.bgaprofileeditor.domain.interfaces.IServiceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataModule {

    @Provides
    @Singleton
    IRepository provideRepository(IStorage storage) {
        return new Repository(storage);
    }

    @Provides
    @Singleton
    IStorage provideStorage(@ApplicationContext Context ctx) {
        return new Storage(ctx);
    }

    @Provides
    @Singleton
    IServiceManager provideServiceManager(@ApplicationContext Context ctx, IStorage storage, ServiceOutput serviceOutput) {
        return new ServiceManager(ctx, storage, serviceOutput);
    }

    @Provides
    @Singleton
    ServiceOutput provideServiceOutput() {
        return new ServiceOutput();
    }

}

