package org.konata.udpsender;

public interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}
