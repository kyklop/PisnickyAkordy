package cz.pisnickyakordy.android.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

import cz.pisnickyakordy.android.model.Movie;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PisnickyAkordy";

    // Contacts table name
    private static final String TABLE_FAVSONGS = "favouriteSongs";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_YEAR = "releaseYear";
    private static final String KEY_RATING = "rating";
    private static final String KEY_GENRE = "genre";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVSONGS_TABLE = "CREATE TABLE " + TABLE_FAVSONGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_IMAGE + " TEXT," + KEY_YEAR + " TEXT," + KEY_GENRE + " TEXT," + KEY_RATING + " TEXT" + ")";
        db.execSQL(CREATE_FAVSONGS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        /// db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVSONGS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new song
    public void addSong(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, movie.getId());
        values.put(KEY_NAME, movie.getTitle());
        values.put(KEY_IMAGE, movie.getThumbnailUrl());
        values.put(KEY_YEAR, movie.getYear() );
        values.put(KEY_RATING, movie.getRating() );
        values.put(KEY_GENRE, movie.getGenre().toString());
        // Inserting Row
        db.insert(TABLE_FAVSONGS, null, values);
        db.close();
    }

    // Getting single contact
    Movie getSong(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVSONGS, new String[] { KEY_ID,
                        KEY_NAME, KEY_IMAGE, KEY_YEAR, KEY_GENRE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String dbgenre = cursor.getString(5);
        /*
        JSONArray temp = new JSONArray(dbgenre);
        String[] gArray = temp.join(",").split(",");
        */
        ArrayList<String> genre = new ArrayList<String>(Arrays.asList(dbgenre.split(",")));

        Movie movie = new Movie(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getDouble(4), genre);
        return movie;
    }

    /*
    // Getting All Songs
    public List<Movie> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_PH_NO, contact.getPhoneNumber());

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    // Deleting single contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }
*/

    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_FAVSONGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}
