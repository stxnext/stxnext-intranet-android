
package com.stxnext.management.android.storage.sqlite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DatabaseSchemaPopulator  {

    private final static String assetFileName = "database_schema";
    private final static String updateAssetFileName = "database_updates";

    private final Application app;

    public DatabaseSchemaPopulator(Application app) {
        this.app = app;
    }

    private List<String> getSchema(String scriptFileName) throws IOException {
        InputStream input = app.getAssets().open(scriptFileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));

        List<String> commands = new ArrayList<String>();
        StringBuilder command = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            command.append(line);
            if (line.contains(";")) {
                commands.add(command.toString());
                command = new StringBuilder();
            }
        }

        Iterable<String> iterable = Iterables.transform(commands,
                new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return input.trim();
                    }
                });
        return Lists.newArrayList(Iterables.filter(iterable,
                new Predicate<String>() {
                    @Override
                    public boolean apply(String input) {
                        if (Strings.isNullOrEmpty(input))
                            return false;
                        if (input.startsWith("#"))
                            return false;
                        return true;
                    }
                }));
    }

    public void populate(SQLiteDatabase database) throws SQLException, IOException {
        for (String command : getSchema(assetFileName)) {
            database.execSQL(command);
        }
    }

    public void update(SQLiteDatabase database, int version) throws SQLException, IOException {
        for (String command : getSchema(updateAssetFileName + version)) {
            database.execSQL(command);
        }
    }

}
