
package com.stxnext.management.android.web.api;


public interface ApiExecutable<T> {
    public T call() throws Exception;
}
