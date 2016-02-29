package com.adi.ho.jackie.emailapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.adi.ho.jackie.emailapp.Email;

import java.util.HashMap;

/**
 * Created by JHADI on 2/26/16.
 */
public class MailDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MAILEMAILS";
    private static final int DATABASE_VERSION = 1;
    private static final String MAIL_EMAIL_TABLE = "EMAILS";
    private static final String DRAFT_EMAIL_TABLE = "DRAFTS";
    public static final String MAIL_ID = "ID";
    public static final String MAIL_DATE = "DATE";
    public static final String MAIL_BODY = "BODY";
    public static final String MAIL_RECIPIENT = "RECIPIENT";
    public static final String MAIL_SENDER = "SENDER";
    public static final String MAIL_SNIPPET = "SNIPPET";
    public static final String MAIL_DRAFT = "DRAFT";
    public static final String MAIL_SUBJECT = "SUBJECT";
    private static final String[] MAIL_COLUMNS = {MAIL_ID, MAIL_SENDER, MAIL_RECIPIENT, MAIL_DATE, MAIL_SNIPPET, MAIL_DRAFT, MAIL_SUBJECT, MAIL_BODY};
    private static final String[] MAIL_COLUMNS_LISTACT = {MAIL_ID, MAIL_SENDER, MAIL_DATE, MAIL_SNIPPET, MAIL_DRAFT, MAIL_SUBJECT};

    public static final String DRAFT_ID = "ID";
    public static final String DRAFT_RECIPIENT = "RECIPIENT";
    public static final String DRAFT_SUBJECT = "SUBJECT";
    public static final String DRAFT_BODY = "BODY";
    private static final String[] DRAFT_COLUMNS = {DRAFT_ID, DRAFT_RECIPIENT, DRAFT_SUBJECT, DRAFT_BODY};


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
                MAIL_DRAFT + " TEXT, " +
                MAIL_SUBJECT + " TEXT, " +
                MAIL_BODY + " TEXT)");
        db.execSQL("CREATE TABLE " + DRAFT_EMAIL_TABLE + " ("
                + DRAFT_ID + " TEXT PRIMARY KEY, " +
                DRAFT_RECIPIENT + " TEXT, " +
                DRAFT_SUBJECT + " TEXT, " +
                DRAFT_BODY + " TEXT) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + MAIL_EMAIL_TABLE);
        db.execSQL("DROP TABLE IF EXIST " + DRAFT_EMAIL_TABLE);
        onCreate(db);
    }

    public void addEmailsToDatabase(Email email) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MAIL_ID, email.getId());
        contentValues.put(MAIL_RECIPIENT, email.getRecipient());
        contentValues.put(MAIL_SENDER, email.getSender());
        contentValues.put(MAIL_DATE, email.getDate());
        contentValues.put(MAIL_SNIPPET, email.getSnippet());
        contentValues.put(MAIL_DRAFT, email.getFavorite());
        contentValues.put(MAIL_BODY, email.getBody());
        contentValues.put(MAIL_SUBJECT, email.getSubject());
        if (!mDatabase.query(MAIL_EMAIL_TABLE, MAIL_COLUMNS, MAIL_ID + " = ? ", new String[]{email.getId()}, null, null, null, null).moveToFirst()) {
            Log.d("DATABASE", "Inserting in to db, id: " + email.getId());
            mDatabase.insert(MAIL_EMAIL_TABLE, null, contentValues);
        }

    }

    public Cursor getEmailById(String id) {

        Cursor cursor = mDatabase.query(MAIL_EMAIL_TABLE, MAIL_COLUMNS, MAIL_ID + " = ? ",
                new String[]{id}, null, null, null, null);
        return cursor;
    }

    public Cursor getAllEmailsFromDb() {

        Cursor cursor = mDatabase.query(MAIL_EMAIL_TABLE, MAIL_COLUMNS_LISTACT, null, null, null, null, null);
        return cursor;

    }

    public void saveDraftToDb(HashMap<String, String> draftMap) {
        ContentValues values = new ContentValues();
        String emailDraftId = draftMap.get("ID");
        String draftSubject = draftMap.get("SUBJECT");
        String draftRecipient = draftMap.get("RECIPIENT");
        String draftBody = draftMap.get("BODY");
        values.put(DRAFT_ID, emailDraftId);
        values.put(DRAFT_BODY, draftBody);
        values.put(DRAFT_RECIPIENT, draftRecipient);
        values.put(DRAFT_SUBJECT, draftSubject);

        mDatabase.insert(DRAFT_EMAIL_TABLE, null, values);
    }

    public Cursor retrieveDraftsFromDb() {
        return mDatabase.query(DRAFT_EMAIL_TABLE, DRAFT_COLUMNS, null, null, null, null, null);
    }

    public Cursor searchEmailDb(String query) {
        Cursor cursor = mDatabase.query(MAIL_EMAIL_TABLE, MAIL_COLUMNS, MAIL_RECIPIENT + " LIKE ? OR " +
                        MAIL_SENDER + " LIKE ? OR " + MAIL_SUBJECT + " LIKE ? OR " + MAIL_BODY + " LIKE ? ",
                new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%", "%" + query + "%"}, null, null, null);
        return cursor;
    }
    public void clearDb(){
//        mDatabase.execSQL("DROP TABLE IF EXIST " +MAIL_EMAIL_TABLE);
        mDatabase.delete(MAIL_EMAIL_TABLE,null,null);
    }

}
