package com.stefan.countriesinfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountryInfo extends DBActivity implements OnMapReadyCallback{
    protected Country country = null;
    protected TextView countryName;
    protected ImageView flagImg;
    protected Button backBtn;
    protected Button saveBtn;
    protected Button GMAPbtn;
    protected TextView officialnameTV;
    protected TextView codeTV;
    protected TextView capitalTV;
    protected TextView continentsTV;
    protected TextView areaTV;
    protected TextView languageTV;
    protected TextView currencyTV;
    protected TextView currsymbolTV;
    protected TextView domainTV;
    protected TextView phoneTV;
    protected TextView timezoneTV;
    protected TextView bordersTV;
    protected TextView carsdriveTV;
    protected TextView independentTV;
    protected TextView unmemberTV;
    protected GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_info);
        country = (Country) getIntent().getSerializableExtra("Country");
        try {
            initDB();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error?", Toast.LENGTH_SHORT).show();
        }
        countryName = findViewById(R.id.CountryName);
        flagImg = findViewById(R.id.FlagImg);
        Picasso.with(CountryInfo.this).load(country.flagLink).resize(704,422).into(flagImg);
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            deleteCache(getApplicationContext());
            finish();
        });
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(view -> {
            saveCountry();
        });
        //scrollview elements
        countryName.setText(country.shortname);
        officialnameTV = findViewById(R.id.officialnameTV);
        codeTV = findViewById(R.id.codeTV);
        capitalTV = findViewById(R.id.capitalTV);
        continentsTV = findViewById(R.id.continentsTV);
        areaTV = findViewById(R.id.areaTV);
        languageTV = findViewById(R.id.languageTV);
        currencyTV = findViewById(R.id.currencyTV);
        currsymbolTV = findViewById(R.id.currsymbolTV);
        domainTV = findViewById(R.id.domainTV);
        phoneTV = findViewById(R.id.phoneTV);
        timezoneTV = findViewById(R.id.timezoneTV);
        bordersTV = findViewById(R.id.bordersTV);
        carsdriveTV = findViewById(R.id.carsdriveTV);
        independentTV = findViewById(R.id.independentTV);
        unmemberTV = findViewById(R.id.unmemberTV);
        //set texts
        officialnameTV.setText(country.officialname);
        codeTV.setText(country.countryCode);
        capitalTV.setText(country.capital);
        areaTV.setText(country.area+" km²");
        currencyTV.setText(country.currencyName);
        currsymbolTV.setText(country.currencySymbol);
        domainTV.setText(country.domain);
        phoneTV.setText(country.phonestart);
        carsdriveTV.setText(country.carsDriveOnThe+" side");
        //set bools
        if(country.independent){
            independentTV.setText("Yes");
        }else{
            independentTV.setText("No");
        }
        if(country.unMember){
            unmemberTV.setText("Yes");
        }else{
            unmemberTV.setText("No");
        }
        //set arrays
        String continents = "";
        String languages = "";
        String timezones = "";
        String borders = "";
        for (int i = 0; i<country.continents.length;i++){
            if(i==0){
                continents=country.continents[0];
            }else{
                continents+=" ,"+country.continents[i];
            }
        }
        for (int i = 0; i<country.languages.length;i++){
            if(i==0){
                languages=country.languages[0];
            }else{
                languages+=" ,"+country.languages[i];
            }
        }
        for (int i = 0; i<country.timeZones.length;i++){
            if(i==0){
                timezones=country.timeZones[0];
            }else{
                timezones+=" ,"+country.timeZones[i];
            }
        }
        for (int i = 0; i<country.borders.length;i++){
            if(i==0){
                borders=country.borders[0];
            }else{
                borders+=" ,"+country.borders[i];
            }
        }
        continentsTV.setText(continents);
        languageTV.setText(languages);
        timezoneTV.setText(timezones);
        bordersTV.setText(borders);
        //gmap button
        GMAPbtn = findViewById(R.id.GMAPbtn);
        GMAPbtn.setOnClickListener(view -> {
            //start new intent and pass the lats
            Intent intent = new Intent(CountryInfo.this,MapsActivity.class);
            intent.putExtra("Lats", country.gmapsLATs);
            startActivityForResult(intent,200);
        });
        //google map setup
        ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng point = new LatLng(Double.parseDouble(country.gmapsLATs[0]), Double.parseDouble(country.gmapsLATs[1]));
                mMap.addMarker(new MarkerOptions().position(point).title("Marker in "+country.shortname+"."));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,5));
            }
        });
    }

    protected String ArrayToSQLArray(String[]arr){
        String sqlArr = "{";
        for (int i = 0; i < arr.length; i++){
            if(i!=0){
                sqlArr+=",";
            }
            sqlArr+=arr[i];
        }
        sqlArr+="}";
        return sqlArr;
    }

    protected int BooleanToBit(boolean b){
        if(b){
            return 1;
        }else{

        }return 0;
    }

    protected void saveCountry(){
        if(country!=null){
            //delete old data of the country it exists
            try {
                ExecSQL(
                        "DELETE FROM COUNTRIES WHERE shortname = '"+country.shortname+"' AND officialname = '"+country.officialname+"'"
                );
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong when deleting!", Toast.LENGTH_SHORT).show();
            }
            //save the new data of the country
            String insertsql = "INSERT INTO COUNTRIES (shortname,officialname,countryCode,area,phonestart,domain,capital,currencyName,currencySymbol," +
                    "carsDriveOnThe,continents,timeZones,borders,languages,independent,unMember)" +
                    "VALUES('"+country.shortname+"','"+country.officialname+"','"+country.countryCode+"','"+country.area+
                    "','"+country.phonestart+"','"+country.domain+"','"+country.capital+"','"+country.currencyName+"','"+country.currencySymbol+
                    "','"+country.carsDriveOnThe+"','"+ArrayToSQLArray(country.continents)+"','"+ArrayToSQLArray(country.timeZones)+
                    "','"+ArrayToSQLArray(country.borders)+"','"+ArrayToSQLArray(country.languages)+"',"+BooleanToBit(country.independent)+","+BooleanToBit(country.unMember)+")";
            try {
                ExecSQL(insertsql);
                Toast.makeText(getApplicationContext(), "Country saved for offline viewing!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong when saving!", Toast.LENGTH_SHORT).show();
            }
            //save the image of the flag
            String img_name = country.shortname+"FLAG.png";
            flagImg.setDrawingCacheEnabled(true);
            Bitmap img = flagImg.getDrawingCache();
            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            File location = wrapper.getDir("images", Context.MODE_PRIVATE);
            File saveFile = new File(location,img_name);
            try {
                OutputStream stream = new FileOutputStream(saveFile);
                img.compress(Bitmap.CompressFormat.PNG,100,stream);
                stream.flush();
                stream.close();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "FlagImage not saved!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
