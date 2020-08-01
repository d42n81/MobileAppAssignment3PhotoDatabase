package unc.live.d42n81.assignment3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.strictmode.SqliteObjectLeakedViolation;

import java.io.Serializable;

public class CustomSQLDB implements Serializable {
    SQLiteDatabase db;

    public CustomSQLDB(SQLiteDatabase d) {
        this.db = d;
    }

    public SQLiteDatabase getDB() {
        return this.db;
    }


}
