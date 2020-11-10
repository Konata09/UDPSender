package org.konata.udpsender;

import java.util.List;

public abstract class Result<T> {
    private Result() {
    }

    public static final class Success<T> extends Result<T> {
        public T data;

        public Success(T data) {
            this.data = data;
        }
    }

    public static final class Error<T> extends Result<T> {
        public Exception exception;
        public String message;

        public Error(Exception exception, String message) {
            this.exception = exception;
            this.message = message;
        }
    }

    public static final class Errors<T> extends Result<T> {
        public List errorList;

        public Errors(List list) {
            this.errorList = list;
        }
    }
}

