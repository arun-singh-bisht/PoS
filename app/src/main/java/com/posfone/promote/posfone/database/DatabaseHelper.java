package com.posfone.promote.posfone.database;
import android.content.ContentValues;

import android.content.Context;

import android.database.Cursor;

import android.database.DatabaseErrorHandler;

import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;



/**

 * Created by User on 2/28/2017.

 */



public class DatabaseHelper extends SQLiteOpenHelper {



    private static final String TAG = "DatabaseHelper";

     String TABLE_NAME;


    private static final String KEY_ID = "ID";

    private static final String KEY_FNAME = "Name";

    private static final String KEY_PH_NO = "Number";

    private static final String KEY_TIME = "Time";



    public DatabaseHelper(Context context,String table_name) {

        super(context, table_name, null, 1);
        this.TABLE_NAME=table_name;
    }



    @Override

    public void onCreate(SQLiteDatabase db) {
        if ("people_table".equals(TABLE_NAME)) {
            String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_NAME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_FNAME + " TEXT,"
                    + KEY_TIME + " TEXT,"
                    + KEY_PH_NO + " TEXT" + ")";
            db.execSQL(CREATE_TABLE_CONTACTS);
        }
        else{
            String CREATE_TABLE_CONTACTS = "CREATE TABLE " + TABLE_NAME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_FNAME + " TEXT,"
                    + KEY_PH_NO + " TEXT" + ")";
        db.execSQL(CREATE_TABLE_CONTACTS);
        }

    }



    @Override

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);

        onCreate(db);

    }



    public boolean addData(String item1,String item2,String item3) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_FNAME, item1);
        contentValues.put(KEY_TIME, item2);
        contentValues.put(KEY_PH_NO, item3);

        Log.d(TAG, "addData: Adding " + item1 + " to " + TABLE_NAME);

        long result = db.insert(TABLE_NAME, null, contentValues);
        //if date as inserted incorrectly it will return -1

        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }


    public boolean addContactData(String item1,String item2) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_FNAME, item1);
        contentValues.put(KEY_PH_NO, item2);

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1

        if (result == -1) {

            return false;

        } else {

            return true;

        }

    }



    /**

     * Returns all the data from database

     * @return

     */

    public Cursor getData(){

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor data = db.rawQuery(query, null);

        return data;
    }



    /**

     * Returns only the ID that matches the name passed in

     * @param name

     * @return

     */

   /* public Cursor getItemID(String name){

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT " + COL1 + " FROM " + TABLE_NAME +

                " WHERE " + COL2 + " = '" + name + "'";

        Cursor data = db.rawQuery(query, null);

        return data;

    }
*/


    /**

     * Updates the name field

     * @param newName

     * @param id

     * @param oldName

     */

    /*public void updateName(String newName, int id, String oldName){

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_NAME + " SET " + COL2 +

                " = '" + newName + "' WHERE " + COL1 + " = '" + id + "'" +

                " AND " + COL2 + " = '" + oldName + "'";

        Log.d(TAG, "updateName: query: " + query);

        Log.d(TAG, "updateName: Setting name to " + newName);

        db.execSQL(query);

    }
*/


    /**

     * Delete from database

     */

    public void deletesingleName(){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM people_table WHERE ID = (SELECT ID FROM people_table LIMIT 1 )");
    }


    public void deleteName(String time){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM people_table WHERE ID = "+time);
        System.out.println("Executed  ------n    ");
    }


    public void deleteContacts(){

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM contact_table");
    }


}