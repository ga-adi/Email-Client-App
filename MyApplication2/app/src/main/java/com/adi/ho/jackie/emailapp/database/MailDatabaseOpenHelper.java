package com.adi.ho.jackie.emailapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adi.ho.jackie.emailapp.Email;

/**
 * Created by JHADI on 2/26/16.
 */
public class MailDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MAILEMAILS";
    private static final int DATABASE_VERSION = 1;
    private static final String MAIL_EMAIL_TABLE = "EMAILS";
    public static final String MAIL_ID = "ID";
    public static final String MAIL_DATE = "DATE";
    public static final String MAIL_BODY = "BODY";
    public static final String MAIL_RECIPIENT = "RECIPIENT";
    public static final String MAIL_SENDER = "SENDER";
    public static final String MAIL_SNIPPET = "SNIPPET";
    public static final String MAIL_FAVORITE = "FAVORITE";
    public static final String MAIL_SUBJECT = "SUBJECT";
    private static final String[] MAIL_COLUMNS = {MAIL_ID, MAIL_SENDER ,MAIL_RECIPIENT,MAIL_DATE,MAIL_SNIPPET,MAIL_FAVORITE,MAIL_SUBJECT,MAIL_BODY};
    private static final String[] MAIL_COLUMNS_LISTACT = {MAIL_ID, MAIL_SENDER,MAIL_DATE,MAIL_SNIPPET,MAIL_FAVORITE,MAIL_SUBJECT};


    private Context context;
    private static MailDatabaseOpenHelper instance;
    private SQLiteDatabase mDatabase;

    private MailDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        mDatabase = getWritableDatabase();
    }

    public static MailDatabaseOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MailDatabaseOpenHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + MAIL_EMAIL_TABLE + " ("
                + MAIL_ID + " TEXT PRIMARY KEY, " +
                MAIL_SENDER + " TEXT, " +
                MAIL_RECIPIENT + " TEXT, " +
                MAIL_DATE + " TEXT, " +
                MAIL_SNIPPET + " TEXT, " +
                MAIL_FAVORITE + " TEXT, " +
                MAIL_SUBJECT + " TEXT, " +
                MAIL_BODY + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + MAIL_EMAIL_TABLE);
        onCreate(db);
    }

    public void addEmailsToDatabase(Email email){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MAIL_ID, email.getId());
        contentValues.put(MAIL_RECIPIENT, email.getRecipient());
        contentValues.put(MAIL_SENDER, email.getSender());
        contentValues.put(MAIL_DATE, email.getDate());
        contentValues.put(MAIL_SNIPPET, email.getSnippet());
        contentValues.put(MAIL_FAVORITE, email.getFavorite());
        contentValues.put(MAIL_BODY, email.getBody());
        contentValues.put(MAIL_SUBJECT, email.getSubject());
        if (!mDatabase.query(MAIL_EMAIL_TABLE,MAIL_COLUMNS, MAIL_ID + " = ? ", new String[]{email.getId()}, null, null, null, null).moveToFirst()) {
            Log.d("DATABASE", "Inserting in to db, id: " + email.getId());
            mDatabase.insert(MAIL_EMAIL_TABLE, null, contentValues);
        }

    }

    public Cursor getEmailById(String id){

        Cursor cursor = mDatabase.query(MAIL_EMAIL_TABLE, MAIL_COLUMNS, MAIL_ID + " = ? ",
                new String[]{id}, null, null, null, null);
        return cursor;
    }

    public Cursor getAllEmailsFromDb(){

        Cursor cursor = mDatabase.query(MAIL_EMAIL_TABLE, MAIL_COLUMNS_LISTACT, null, null, null, null, null);
        return cursor;

    }
}
