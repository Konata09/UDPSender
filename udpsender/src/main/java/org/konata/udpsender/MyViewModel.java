package org.konata.udpsender;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

public class MyViewModel extends ViewModel {
    private static Repository repository;

    public MyViewModel() {
        repository = new Repository();
    }

    public void makeRequest(String s) {
        repository.makeLoginRequest("172.31.160.138", "FFFFFF", new RepositoryCallback() {
            @Override
            public void onComplete(Result result) {
                if (result instanceof Result.Success) {
                    System.out.println("success");
                    System.out.println(((Result.Success) result).data);
                } else {
                    System.out.println("MyError");
                    System.out.println(((Result.Error) result).exception);
                }
            }
        });
    }
}
