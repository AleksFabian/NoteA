package com.example.notea;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AccountDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "NoteAUserDB";
    private static final int DB_VERSION = 4;
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TABLE = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_NTITLE = "title";
    public static final String COLUMN_NTIME = "time";
    public static final String COLUMN_NDATE = "date";
    public static final String COLUMN_NINFO = "info";

    public static String email;
    public static String name;
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email.replace("@", "_").replace(".","_");
    }

    public void getInfo(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {COLUMN_NAME, COLUMN_EMAIL};
        String selection = COLUMN_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query("users", projection, selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_NAME);
            this.name = cursor.getString(nameIndex);
        }

        cursor.close();
        db.close();
    }


    public AccountDatabase(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + COLUMN_TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_EMAIL + " TEXT NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_NAME + " TEXT NOT NULL);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // query to obtain the names of all tables in your database
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

        // Iterate over the result set, adding every table name to a list
        while (c.moveToNext()) {
            String tableName = c.getString(0);
            if (!tableName.equals("sqlite_sequence")) { // Exclude sqlite_sequence table
                tables.add(tableName);
            }
        }

        // Call DROP TABLE on every table name except sqlite_sequence
        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            db.execSQL(dropQuery);
        }
        onCreate(db);
    }

    public void createUserNotesTable(String newUser){
        SQLiteDatabase db = getWritableDatabase();

        newUser = newUser.replace("@", "_").replace(".","_");
        newUser = "notes_" + newUser;

        db.execSQL("CREATE TABLE " + newUser + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NTITLE + " TEXT NOT NULL,"
                + COLUMN_NTIME + " BLOB ,"
                + COLUMN_NDATE + " BLOB ,"
                + COLUMN_NINFO + " TEXT NOT NULL);"
        );
    }

    public ArrayList<List<Object>> getNotes() {
        ArrayList<List<Object>> noteList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {"*"};
        String userNotesTable = "notes_" + email.replace("@", "_").replace(".","_");

        Cursor cursor = db.query(userNotesTable, projection, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    List<Object> notesInfo = new ArrayList<>();
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                    String info = cursor.getString(cursor.getColumnIndexOrThrow("info"));

                    notesInfo.add(id);
                    notesInfo.add(title);
                    notesInfo.add(date);
                    notesInfo.add(time);
                    notesInfo.add(info);

                    noteList.add(notesInfo);
                } while (cursor.moveToNext());
            }
            cursor.close(); // Close the cursor when done
        }
        db.close();

        return noteList;
    }

    public void removeNote(int noteId) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(noteId)};
        String noteTable = "notes_" + email.replace("@", "_").replace(".", "_");

        db.delete(noteTable, selection, selectionArgs);

        db.close();
    }
}
