package com.stefan.countriesinfo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CountryInfoSaved extends DBActivity {
    protected Country country = null;
    protected TextView countryName;
    protected ImageView flagImg;
    protected Button backBtn;
    protected Button saveBtn;
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
    protected String images_folder_path;
    protected Button GMAPbtn;
    protected LinearLayout GMAPlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_info);
        //remove google maps option form offline view
        GMAPbtn = findViewById(R.id.GMAPbtn);
        GMAPlayout = findViewById(R.id.MapContainer);
        GMAPbtn.setVisibility(View.GONE);
        GMAPlayout.setVisibility(View.GONE);
        //get country
        country = (Country) getIntent().getSerializableExtra("Country");
        try {
            initDB();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error?", Toast.LENGTH_SHORT).show();
        }
        countryName = findViewById(R.id.CountryName);
        flagImg = findViewById(R.id.FlagImg);
        //load image of flag
        images_folder_path = new ContextWrapper(getApplicationContext()).getDir("images", Context.MODE_PRIVATE).toString();
        try {
            File f = new File(images_folder_path, country.shortname+"FLAG.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Bitmap resB = Bitmap.createScaledBitmap(b,704,422,false);
            flagImg.setImageBitmap(resB);
        } catch (Exception e) {
            e.printStackTrace();
        }
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> {
            finish();
        });
        saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setText("❌");
        saveBtn.setTextSize(20);
        saveBtn.setOnClickListener(view -> {
            //create popup to confirm
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm Erase");
            builder.setMessage("Are you sure you want to delete "+country.shortname+"?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteCountry();
                            finish();
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
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
    }

    protected void deleteCountry(){
        if(country!=null) {
            try {
                ExecSQL(
                        "DELETE FROM COUNTRIES WHERE shortname = '"+country.shortname+"'"
                );
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(images_folder_path+"/"+country.shortname+"FLAG.png");
            file.delete();
        }
    }

}
