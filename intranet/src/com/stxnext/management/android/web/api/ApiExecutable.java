
package com.stxnext.management.android.web.api;


public interface ApiExecutable<T> {
    public HTTPResponse<T> call() throws Exception;
}
