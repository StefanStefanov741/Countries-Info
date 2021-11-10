package com.stefan.countriesinfo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBActivity extends AppCompatActivity {
    protected interface OnSelectSuccess{
        public void SelectedCountry(
                String shortname,String officialname,String countryCode,String area,String phonestart,String domain,String capital,String currencyName,
                String currencySymbol,String carsDriveOnThe,String continents,String timeZones,String borders,String languages,
                int independent,int unMember
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void SelectSQL(String SelectQ,
                             String[] args,
                             OnSelectSuccess success
    )
            throws Exception
    {
        SQLiteDatabase db=SQLiteDatabase
                .openOrCreateDatabase(getFilesDir().getPath()+"/COUNTRIES.db", null);
        Cursor cursor=db.rawQuery(SelectQ, args);
        while (cursor.moveToNext()){
            @SuppressLint("Range") String shortname=cursor.getString(cursor.getColumnIndex("shortname"));
            @SuppressLint("Range") String officialname=cursor.getString(cursor.getColumnIndex("officialname"));
            @SuppressLint("Range") String countryCode=cursor.getString(cursor.getColumnIndex("countryCode"));
            @SuppressLint("Range") String area=cursor.getString(cursor.getColumnIndex("area"));
            @SuppressLint("Range") String phonestart=cursor.getString(cursor.getColumnIndex("phonestart"));
            @SuppressLint("Range") String domain=cursor.getString(cursor.getColumnIndex("domain"));
            @SuppressLint("Range") String capital=cursor.getString(cursor.getColumnIndex("capital"));
            @SuppressLint("Range") String currencyName=cursor.getString(cursor.getColumnIndex("currencyName"));
            @SuppressLint("Range") String currencySymbol=cursor.getString(cursor.getColumnIndex("currencySymbol"));
            @SuppressLint("Range") String carsDriveOnThe=cursor.getString(cursor.getColumnIndex("carsDriveOnThe"));
            @SuppressLint("Range") String continents=cursor.getString(cursor.getColumnIndex("continents"));
            @SuppressLint("Range") String timeZones=cursor.getString(cursor.getColumnIndex("timeZones"));
            @SuppressLint("Range") String borders=cursor.getString(cursor.getColumnIndex("borders"));
            @SuppressLint("Range") String languages=cursor.getString(cursor.getColumnIndex("languages"));
            @SuppressLint("Range") int independent=cursor.getInt(cursor.getColumnIndex("independent"));
            @SuppressLint("Range") int unMember=cursor.getInt(cursor.getColumnIndex("unMember"));
            success.SelectedCountry(shortname,officialname,countryCode,area,phonestart,domain,capital,currencyName,currencySymbol,carsDriveOnThe,continents,timeZones,borders,languages,independent,unMember);
        }
        db.close();
    }

    protected void ExecSQL(String SQL)
            throws Exception
    {
        SQLiteDatabase db=SQLiteDatabase
                .openOrCreateDatabase(getFilesDir().getPath()+"/COUNTRIES.db", null);

        db.execSQL(SQL);

        db.close();
    }

    protected void initDB() throws  Exception{
        ExecSQL(
                "CREATE TABLE if not exists COUNTRIES(" +
                        "  shortname text unique not null," +
                        "  officialname text unique not null," +
                        "  countryCode text unique not null," +
                        "  continents TEXT[] NOT null," +
                        "  timeZones TEXT[] NOT null," +
                        "  borders TEXT[] NOT null," +
                        "  languages TEXT[] NOT null," +
                        "  area TEXT not null," +
                        "  phonestart TEXT not null," +
                        "  domain TEXT not null," +
                        "  capital TEXT not null," +
                        "  currencyName TEXT not null," +
                        "  currencySymbol TEXT not null," +
                        "  carsDriveOnThe TEXT not null," +
                        "  unMember bit not null," +
                        "  independent bit not null" +
                        ")"
        );
    }
}
