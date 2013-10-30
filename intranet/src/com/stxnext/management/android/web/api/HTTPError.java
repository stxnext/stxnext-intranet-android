
package com.stxnext.management.android.web.api;


public class HTTPError {
    private int code;
    private String message;

    public HTTPError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
