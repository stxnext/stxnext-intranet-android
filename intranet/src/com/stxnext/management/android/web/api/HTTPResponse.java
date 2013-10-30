
package com.stxnext.management.android.web.api;

public class HTTPResponse<T> {

    private T expectedResponse;
    private HTTPError error;

    public boolean ok() {
        return error == null;
    }

    public T getExpectedResponse() {
        return expectedResponse;
    }

    public HTTPError getError() {
        return error;
    }

    public void setError(HTTPError error) {
        this.error = error;
    }

    public void setExpectedResponse(T expectedResponse) {
        this.expectedResponse = expectedResponse;
    }
}
