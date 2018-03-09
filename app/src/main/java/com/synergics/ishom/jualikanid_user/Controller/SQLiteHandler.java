package com.synergics.ishom.jualikanid_user.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.synergics.ishom.jualikanid_user.Model.Retrofit.Object.ResponseLogin;

/**
 * Created by asmarasusanto on 9/27/17.
 */

public class SQLiteHandler extends SQLiteOpenHelper{

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    private static final int DB_VERSION = 1;

    //nama database
    public static final String DATABASE_NAME = "Synergics.WiraWiri";

    //nama tabel
    public static final String TABEL_USER = "tbUser";

    public static final String KEY_USER_ID= "userId";               //0
    public static final String KEY_USER_ID_SERVER= "userIdServer";  //1
    public static final String KEY_USER_FULL_NAME= "userName";      //2
    public static final String KEY_USER_IMAGE= "userImage";         //3
    public static final String KEY_USER_PHONE= "userPhone";         //4
    public static final String KEY_USER_EMAIL= "userEmail";         //5
    public static final String KEY_USER_PASSWORD= "userPassword";   //6
    public static final String KEY_USER_DEVICE_ID= "userDeviceId";  //7
    public static final String KEY_USER_KOTA_ID= "userKotaId";      //8
    public static final String KEY_USER_ADDRESS= "userAddress";     //9
    public static final String KEY_USER_SALDO= "userSaldo";         //10

    private  static  final String CREATE_TB_USER = "CREATE TABLE " + TABEL_USER + "("
            + KEY_USER_ID+ " INTEGER PRIMARY KEY,"
            + KEY_USER_ID_SERVER+ " TEXT,"
            + KEY_USER_FULL_NAME+ " TEXT,"
            + KEY_USER_IMAGE+ " TEXT,"
            + KEY_USER_PHONE+ " TEXT,"
            + KEY_USER_EMAIL+ " TEXT,"
            + KEY_USER_PASSWORD+ " TEXT,"
            + KEY_USER_DEVICE_ID+ " TEXT,"
            + KEY_USER_KOTA_ID+ " TEXT,"
            + KEY_USER_ADDRESS+ " TEXT,"
            + KEY_USER_SALDO+ " TEXT" + ")";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TB_USER);
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABEL_USER);

        // Create tables again
        onCreate(db);
    }


    //user
    public void addUser(ResponseLogin.Data user) {
        SQLiteDatabase db = this.getWritableDatabase();

        //menginisialisasi value string dengan kolom tabel
        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID_SERVER, user.user_id);
        values.put(KEY_USER_FULL_NAME, user.user_full_name);
        values.put(KEY_USER_IMAGE, user.user_image);
        values.put(KEY_USER_PHONE, user.user_phone);
        values.put(KEY_USER_EMAIL, user.user_email);
        values.put(KEY_USER_PASSWORD, user.user_password);
        values.put(KEY_USER_DEVICE_ID, user.user_device_id);
        values.put(KEY_USER_KOTA_ID, user.user_kota_id);
        values.put(KEY_USER_ADDRESS, user.user_address);
        values.put(KEY_USER_SALDO, user.user_saldo);

        // masukan ke tabel
        long id = db.insert(TABEL_USER, null, values);
    }

    public ResponseLogin.Data getUser(){
        String selectQuery = "SELECT * FROM " + TABEL_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ResponseLogin.Data user = new ResponseLogin.Data();
        user.user_id = cursor.getString(1);
        user.user_full_name = cursor.getString(2);
        user.user_image = cursor.getString(3);
        user.user_phone = cursor.getString(4);
        user.user_email = cursor.getString(5);
        user.user_password = cursor.getString(6);
        user.user_device_id = cursor.getString(7);
        user.user_kota_id = cursor.getString(8);
        user.user_address = cursor.getString(9);
        user.user_saldo = cursor.getString(10);
        return user;
    }

    public void hapusUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABEL_USER, null, null);
        db.close(); // Closing database connection
    }

}
