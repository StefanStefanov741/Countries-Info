package com.stefan.countriesinfo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class SavedActivity extends DBActivity {

    List<Country>SavedCountries = new LinkedList<>();
    private Button backBtn2;
    private Button delAllBtn;
    private TextView emptyTxt;
    private LinearLayout CountriesList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        try {
            SelectSQL(
                    "SELECT * FROM COUNTRIES",
                    null,
                    (shortname,officialname,countryCode,area,phonestart,domain,capital,currencyName,currencySymbol,carsDriveOnThe,continents,timeZones,borders,languages,independent,unMember)->{
                        Country c = new Country();
                        c.shortname=shortname;
                        c.officialname=officialname;
                        c.countryCode=countryCode;
                        c.phonestart=phonestart;
                        c.domain=domain;
                        c.capital=capital;
                        c.area=area;
                        c.currencyName=currencyName;
                        c.currencySymbol=currencySymbol;
                        c.carsDriveOnThe=carsDriveOnThe;
                        c.continents = SQLArrayToArray(continents);
                        c.timeZones = SQLArrayToArray(timeZones);
                        c.languages = SQLArrayToArray(languages);
                        c.borders = SQLArrayToArray(borders);
                        if(independent>0){
                            c.independent = true;
                        }else{
                            c.independent = false;
                        }
                        if(unMember>0){
                            c.unMember = true;
                        }else{
                            c.unMember = false;
                        }
                        SavedCountries.add(c);
                    }
            );
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }

        CountriesList = findViewById(R.id.SavedCountriesList);
        backBtn2 = findViewById(R.id.backBtn2);
        backBtn2.setOnClickListener(view -> {
            finish();
        });
        delAllBtn = findViewById(R.id.delAllBtn);
        delAllBtn.setOnClickListener(view -> {
            //check if there is anything for deleting
            if(SavedCountries.size()<1){
                return;
            }
            //create popup to confirm
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Confirm Erase");
            builder.setMessage("Are you sure you want to delete all saved countries?");
            builder.setPositiveButton("Confirm",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DeleteAllSaves();
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
        emptyTxt = findViewById(R.id.emptyTxt);

        if(SavedCountries.size()>0){
            //fill list with items
            RefreshList();
        }

    }

    protected  String[] SQLArrayToArray(String sqlArr){
        String[] arr = new String[0];
        String almostRdy = sqlArr.replace("{","");
        String almostalmostRdy = almostRdy.replace("}","");
        arr = almostalmostRdy.split(",");
        return  arr;
    }

    protected void RefreshList(){
        CountriesList.removeAllViews();
        int countriesCount = SavedCountries.size();
        if(countriesCount<1){
            emptyTxt.setVisibility(View.VISIBLE);
            return;
        }else{
            emptyTxt.setVisibility(View.GONE);
        }
        String images_folder_path = new ContextWrapper(getApplicationContext()).getDir("images", Context.MODE_PRIVATE).toString();
        int i = 0;
        while(i<countriesCount){
            //create a child layer to store the name and flag buttons
            LinearLayout ChildLayer = new LinearLayout(this);
            CountriesList.addView(ChildLayer);
            ChildLayer.setOrientation(LinearLayout.HORIZONTAL);
            //set layer width and height
            LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) ChildLayer.getLayoutParams();
            p.width= ActionBar.LayoutParams.MATCH_PARENT;
            p.height=ActionBar.LayoutParams.WRAP_CONTENT;
            //set margin to child layer
            p.setMargins(1,2,1,2);
            ChildLayer.setLayoutParams(p);
            //set layer bg color
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor("#f0f0f0"));
            ChildLayer.setBackground(bg);
            //create buttons
            Button CountryName = new Button(this);
            CountryName.setText(SavedCountries.get(i).shortname);
            CountryName.setBackgroundColor(Color.TRANSPARENT);
            CountryName.setPadding(20,0,10,0);
            CountryName.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            LinearLayout.LayoutParams Nameparam = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            CountryName.setLayoutParams(Nameparam);
            Button CountryDelete = new Button(this);
            CountryDelete.setText("Delete");
            CountryDelete.setBackgroundColor(Color.parseColor("#eb4034"));
            CountryDelete.setTextColor(Color.WHITE);
            CountryDelete.setBackgroundResource(R.drawable.delbtn);
            ImageButton CountryFlag = new ImageButton(this);
            CountryFlag.setPadding(30,12,10,10);
            CountryFlag.setBackgroundColor(Color.TRANSPARENT);
            //load image of flag
            try {
                File f = new File(images_folder_path, SavedCountries.get(i).shortname+"FLAG.png");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                Bitmap resB = Bitmap.createScaledBitmap(b,(int)(b.getWidth()/4),(int)(b.getHeight()/4),false);
                CountryFlag.setImageBitmap(resB);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //add buttons to child layer
            ChildLayer.addView(CountryFlag);
            ChildLayer.addView(CountryName);
            ChildLayer.addView(CountryDelete);
            CountryFlag.setOnClickListener(view -> {
                //start new intent and pass the correct country
                Intent intent = new Intent(SavedActivity.this,CountryInfoSaved.class);
                for (int j = 0; j<SavedCountries.size();j++){
                    if(SavedCountries.get(j).shortname.equals(CountryName.getText().toString())){
                        intent.putExtra("Country", SavedCountries.get(j));
                        startActivityForResult(intent,200);
                        return;
                    }
                }
            });
            CountryName.setOnClickListener(view -> {
                //start new intent and pass the correct country
                Intent intent = new Intent(SavedActivity.this,CountryInfoSaved.class);
                for (int j = 0; j<SavedCountries.size();j++){
                    if(SavedCountries.get(j).shortname.equals(CountryName.getText().toString())){
                        intent.putExtra("Country", SavedCountries.get(j));
                        startActivityForResult(intent,200);
                        return;
                    }
                }
            });
            CountryDelete.setOnClickListener(view -> {
                //create popup to confirm
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Confirm Erase");
                builder.setMessage("Are you sure you want to delete "+CountryName.getText()+"?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteSpecificCountry(CountryName.getText().toString());
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
            ChildLayer.setOnClickListener(view -> {
                //start new intent and pass the correct country
                Intent intent = new Intent(SavedActivity.this,CountryInfoSaved.class);
                for (int j = 0; j<SavedCountries.size();j++){
                    if(SavedCountries.get(j).shortname.equals(CountryName.getText().toString())){
                        intent.putExtra("Country", SavedCountries.get(j));
                        startActivityForResult(intent,200);
                        return;
                    }
                }
            });
            i++;
        }
    }

    private void DeleteAllSaves(){
        String images_folder_path = new ContextWrapper(getApplicationContext()).getDir("images", Context.MODE_PRIVATE).toString();
        for (int i =0;i<SavedCountries.size();i++){
            try {
                ExecSQL(
                        "DELETE FROM COUNTRIES WHERE shortname = '"+SavedCountries.get(i).shortname+"'"
                );
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                return;
            }
            File file = new File(images_folder_path+"/"+SavedCountries.get(i).shortname+"FLAG.png");
            file.delete();
        }
        SavedCountries.clear();
        RefreshList();
    }

    private void DeleteSpecificCountry(String name){
        String images_folder_path = new ContextWrapper(getApplicationContext()).getDir("images", Context.MODE_PRIVATE).toString();
        try {
            ExecSQL(
                    "DELETE FROM COUNTRIES WHERE shortname = '"+name+"'"
            );
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(images_folder_path+"/"+name+"FLAG.png");
        file.delete();
        for (int i =0;i<SavedCountries.size();i++){
            if(SavedCountries.get(i).shortname.equals(name)){
                SavedCountries.remove(i);
                break;
            }
        }
        RefreshList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SavedCountries.clear();
        try {
            SelectSQL(
                    "SELECT * FROM COUNTRIES",
                    null,
                    (shortname,officialname,countryCode,area,phonestart,domain,capital,currencyName,currencySymbol,carsDriveOnThe,continents,timeZones,borders,languages,independent,unMember)->{
                        Country c = new Country();
                        c.shortname=shortname;
                        c.officialname=officialname;
                        c.countryCode=countryCode;
                        c.phonestart=phonestart;
                        c.domain=domain;
                        c.capital=capital;
                        c.area=area;
                        c.currencyName=currencyName;
                        c.currencySymbol=currencySymbol;
                        c.carsDriveOnThe=carsDriveOnThe;
                        c.continents = SQLArrayToArray(continents);
                        c.timeZones = SQLArrayToArray(timeZones);
                        c.languages = SQLArrayToArray(languages);
                        c.borders = SQLArrayToArray(borders);
                        if(independent>0){
                            c.independent = true;
                        }else{
                            c.independent = false;
                        }
                        if(unMember>0){
                            c.unMember = true;
                        }else{
                            c.unMember = false;
                        }
                        SavedCountries.add(c);
                    }
            );
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        RefreshList();
    }
}