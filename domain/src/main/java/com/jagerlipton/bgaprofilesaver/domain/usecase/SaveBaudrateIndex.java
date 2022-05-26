package com.jagerlipton.bgaprofilesaver.domain.usecase;

import com.jagerlipton.bgaprofilesaver.domain.interfaces.IRepository;

public class SaveBaudrateIndex {

    private final IRepository repository;

    public SaveBaudrateIndex(IRepository repository) {
        this.repository = repository;
    }

    public void execute(Integer index) {
        repository.saveBaudrateIndex(index);
    }

}

