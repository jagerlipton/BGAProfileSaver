package com.jagerlipton.bgaprofilesaver.domain.usecase;

import com.jagerlipton.bgaprofilesaver.domain.interfaces.IRepository;


public class LoadBaudrateIndex {

    private final IRepository repository;

    public LoadBaudrateIndex(IRepository repository) {
        this.repository = repository;
    }

    public Integer execute() {
        return repository.loadBaudrateIndex();
    }

}
