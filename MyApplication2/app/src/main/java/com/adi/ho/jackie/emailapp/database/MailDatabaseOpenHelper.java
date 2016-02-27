package com.adi.ho.jackie.emailapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JHADI on 2/26/16.
 */
public class MailDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MAILDRAFTS";
    private static final int DATABASE_VERSION = 1;
    private static final String MAIL_DRAFT_TABLE = "DRAFTS";
    public static final String MAIL_ID = "ID";
    public static final String MAIL_DATE = "DATE";
    public static final String MAIL_BODY = "BODY";
    public static final String MAIL_RECIPIENT = "RECIPIENT";


    private Context context;
    private static MailDatabaseOpenHelper instance;

    private MailDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    public MailDatabaseOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MailDatabaseOpenHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+MAIL_DRAFT_TABLE+" ("
        + MAIL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+
        MAIL_RECIPIENT + " TEXT, " +
        MAIL_DATE + " TEXT, "+
        MAIL_BODY + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+MAIL_DRAFT_TABLE);
        onCreate(db);

    }
}
