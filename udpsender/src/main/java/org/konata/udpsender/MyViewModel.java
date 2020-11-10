package org.konata.udpsender;

import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private static Repository repository;

    public MyViewModel() {
        repository = new Repository();
    }

    public void makeRequest(String s) {
        repository.sendUDPPacket("172.31.160.138",4001, "FFFFFF", new RepositoryCallback() {
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
