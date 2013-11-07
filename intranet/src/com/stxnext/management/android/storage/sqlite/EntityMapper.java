
package com.stxnext.management.android.storage.sqlite;

import java.util.List;

import android.database.Cursor;

public interface EntityMapper<T> {
    public List<T> mapEntity(Cursor c);
    public T mapEntity(Cursor c, int position);
}
