package org.konata.udpsender;

import androidx.lifecycle.ViewModel;


public class MyViewModel extends ViewModel {
    private static Repository repository;

    public MyViewModel() {
        repository = new Repository();
    }

    public Repository getRepository() {
        return repository;
    }
}
