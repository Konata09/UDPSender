package org.konata.udpsender;

interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}
