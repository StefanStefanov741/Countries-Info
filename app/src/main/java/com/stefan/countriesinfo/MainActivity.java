package com.stefan.countriesinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String url = "https://restcountries.com/v3.1/name/";
    String url2 = "https://restcountries.com/v3.1/alpha/";
    String url3 = "https://restcountries.com/v3.1/capital/";
    Country country = null;
    protected Button searchBtn;
    protected Button SavedListBtn;
    protected TextView searchBox;
    protected RadioButton nameChoice;
    protected RadioButton capitalChoice;
    protected RadioButton codeChoice;
    String mode = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBtn = findViewById(R.id.searchBtn);
        SavedListBtn = findViewById(R.id.SavedListBtn);
        searchBox = findViewById(R.id.searchBox);
        nameChoice = findViewById(R.id.nameChoice);
        capitalChoice = findViewById(R.id.capitalChoice);
        codeChoice = findViewById(R.id.codeChoice);
        searchBtn.setOnClickListener(view -> {
            if (searchBox.getText().toString().equals("")||searchBox.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(),"Search is empty!",Toast.LENGTH_SHORT).show();
            }else{
                if(mode == "name"){
                    GetCountryFromName(searchBox.getText().toString());
                }else if(mode == "code"){
                    GetCountryFromCode(searchBox.getText().toString());
                }else if(mode == "capital"){
                    GetCountryFromCapital(searchBox.getText().toString());
                }else{
                    Toast.makeText(getApplicationContext(),"Please select \"Search by\"",Toast.LENGTH_LONG).show();
                }
            }
        });

        SavedListBtn.setOnClickListener(view -> {
            Intent saved_intent = new Intent(MainActivity.this,SavedActivity.class);
            startActivityForResult(saved_intent,200);
        });

        nameChoice.setOnClickListener(view -> {
            capitalChoice.setChecked(false);
            codeChoice.setChecked(false);
            mode = "name";
        });

        codeChoice.setOnClickListener(view -> {
            capitalChoice.setChecked(false);
            nameChoice.setChecked(false);
            mode = "code";
        });

        capitalChoice.setOnClickListener(view -> {
            nameChoice.setChecked(false);
            codeChoice.setChecked(false);
            mode = "capital";
        });

        deleteCache(getApplicationContext());
    }


    private void GetCountryFromName(String name){
        country = new Country();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+name,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String responseString = response.substring(1, response.length() - 1);
                        try {
                            String newStr = URLDecoder.decode(URLEncoder.encode(responseString, "iso8859-1"),"UTF-8");
                            responseString = newStr;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject json = new JSONObject(responseString);
                            country.shortname = json.getJSONObject("name").getString("common");
                            country.officialname = json.getJSONObject("name").getString("official");
                            country.area = json.getString("area");
                            country.countryCode = json.getString("cca2");
                            if(json.has("tld")){
                                country.domain = json.getJSONArray("tld").getString(0);
                            }
                            country.phonestart = json.getJSONObject("idd").getString("root") + json.getJSONObject("idd").getJSONArray("suffixes").getString(0);
                            if(json.has("capital")){
                                country.capital = json.getJSONArray("capital").getString(0);
                            }
                            Iterator x = json.getJSONObject("currencies").keys();
                            String keyCurr = (String) x.next();
                            country.currencyName = json.getJSONObject("currencies").getJSONObject(keyCurr).getString("name");
                            country.currencySymbol = json.getJSONObject("currencies").getJSONObject(keyCurr).getString("symbol");
                            x = null;
                            if(json.has("car")){
                                country.carsDriveOnThe = json.getJSONObject("car").getString("side");
                            }
                            if(json.has("flags")){
                                country.flagLink = json.getJSONObject("flags").getString("png");
                            }
                            country.gmapsLATs = new String[json.getJSONArray("latlng").length()];
                            for (int i = 0; i<country.gmapsLATs.length;i++){
                                country.gmapsLATs[i]=json.getJSONArray("latlng").getString(i);
                            }
                            country.continents = new String[json.getJSONArray("continents").length()];
                            for (int i = 0; i<country.continents.length;i++){
                                country.continents[i]=json.getJSONArray("continents").getString(i);
                            }
                            for (int i = 0; i<country.gmapsLATs.length;i++){
                                country.gmapsLATs[i]=json.getJSONArray("latlng").getString(i);
                            }
                            if(json.has("timezones")){
                                country.timeZones = new String[json.getJSONArray("timezones").length()];
                                for (int i = 0; i<country.timeZones.length;i++){
                                    country.timeZones[i]=json.getJSONArray("timezones").getString(i);
                                }
                            }
                            if(json.has("borders")){
                                country.borders = new String[json.getJSONArray("borders").length()];
                                if(country.borders.length>0){
                                    for (int i = 0; i<country.borders.length;i++){
                                        country.borders[i]=json.getJSONArray("borders").getString(i);
                                    }
                                }else{
                                    country.borders = new String[1];
                                    country.borders[0]="None";
                                }
                            }else{
                                country.borders = new String[1];
                                country.borders[0]="None";
                            }
                            country.languages = new String[json.getJSONObject("languages").length()];
                            x = json.getJSONObject("languages").keys();
                            int index = 0;
                            while (x.hasNext()){
                                String keyLang = (String) x.next();
                                country.languages[index]=json.getJSONObject("languages").getString(keyLang);
                                index++;
                            }
                            if(json.has("independent")){
                                country.independent = json.getBoolean("independent");
                            }
                            if(json.has("unMember")){
                                country.unMember = json.getBoolean("unMember");
                            }
                            Toast.makeText(getApplicationContext(),"Country found!",Toast.LENGTH_SHORT).show();
                            //start new intent and pass the country
                            Intent intent = new Intent(MainActivity.this,CountryInfo.class);
                            intent.putExtra("Country", country);
                            startActivityForResult(intent,200);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Country not found!",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Country not found!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(stringRequest);
    }

    private void GetCountryFromCode(String code){
        country = new Country();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url2+code,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String responseString = response.substring(1, response.length() - 1);
                        try {
                            String newStr = URLDecoder.decode(URLEncoder.encode(responseString, "iso8859-1"),"UTF-8");
                            responseString = newStr;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject json = new JSONObject(responseString);
                            country.shortname = json.getJSONObject("name").getString("common");
                            country.officialname = json.getJSONObject("name").getString("official");
                            country.area = json.getString("area");
                            country.countryCode = json.getString("cca2");
                            country.phonestart = json.getJSONObject("idd").getString("root") + json.getJSONObject("idd").getJSONArray("suffixes").getString(0);
                            country.domain = json.getJSONArray("tld").getString(0);
                            country.capital = json.getJSONArray("capital").getString(0);
                            Iterator x = json.getJSONObject("currencies").keys();
                            String keyCurr = (String) x.next();
                            country.currencyName = json.getJSONObject("currencies").getJSONObject(keyCurr).getString("name");
                            country.currencySymbol = json.getJSONObject("currencies").getJSONObject(keyCurr).getString("symbol");
                            x = null;
                            country.carsDriveOnThe = json.getJSONObject("car").getString("side");
                            country.flagLink = json.getJSONObject("flags").getString("png");
                            country.gmapsLATs = new String[json.getJSONArray("latlng").length()];
                            for (int i = 0; i<country.gmapsLATs.length;i++){
                                country.gmapsLATs[i]=json.getJSONArray("latlng").getString(i);
                            }
                            country.continents = new String[json.getJSONArray("continents").length()];
                            for (int i = 0; i<country.continents.length;i++){
                                country.continents[i]=json.getJSONArray("continents").getString(i);
                            }
                            country.timeZones = new String[json.getJSONArray("timezones").length()];
                            for (int i = 0; i<country.timeZones.length;i++){
                                country.timeZones[i]=json.getJSONArray("timezones").getString(i);
                            }
                            country.borders = new String[json.getJSONArray("borders").length()];
                            for (int i = 0; i<country.borders.length;i++){
                                country.borders[i]=json.getJSONArray("borders").getString(i);
                            }
                            country.languages = new String[json.getJSONObject("languages").length()];
                            x = json.getJSONObject("languages").keys();
                            int index = 0;
                            while (x.hasNext()){
                                String keyLang = (String) x.next();
                                country.languages[index]=json.getJSONObject("languages").getString(keyLang);
                                index++;
                            }
                            country.independent = json.getBoolean("independent");
                            country.unMember = json.getBoolean("unMember");
                            Toast.makeText(getApplicationContext(),"Country found!",Toast.LENGTH_SHORT).show();
                            //start new intent and pass the country
                            Intent intent = new Intent(MainActivity.this,CountryInfo.class);
                            intent.putExtra("Country", country);
                            startActivityForResult(intent,200);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Country not found!",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Country not found!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(stringRequest);
    }

    private void GetCountryFromCapital(String capital){
        country = new Country();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url3+capital,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String responseString = response.substring(1, response.length() - 1);
                        try {
                            String newStr = URLDecoder.decode(URLEncoder.encode(responseString, "iso8859-1"),"UTF-8");
                            responseString = newStr;
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        try {
                            JSONObject json = new JSONObject(responseString);
                            country.shortname = json.getJSONObject("name").getString("common");
                            country.officialname = json.getJSONObject("name").getString("official");
                            country.area = json.getString("area");
                            country.countryCode = json.getString("cca2");
                            country.phonestart = json.getJSONObject("idd").getString("root") + json.getJSONObject("idd").getJSONArray("suffixes").getString(0);
                            country.domain = json.getJSONArray("tld").getString(0);
                            country.capital = json.getJSONArray("capital").getString(0);
                            Iterator x = json.getJSONObject("currencies").keys();
                            String keyCurr = (String) x.next();
                            country.currencyName = json.getJSONObject("currencies").getJSONObject(keyCurr).getString("name");
                            country.currencySymbol = json.getJSONObject("currencies").getJSONObject(keyCurr).getString("symbol");
                            x = null;
                            country.carsDriveOnThe = json.getJSONObject("car").getString("side");
                            country.flagLink = json.getJSONObject("flags").getString("png");
                            country.gmapsLATs = new String[json.getJSONArray("latlng").length()];
                            for (int i = 0; i<country.gmapsLATs.length;i++){
                                country.gmapsLATs[i]=json.getJSONArray("latlng").getString(i);
                            }
                            country.continents = new String[json.getJSONArray("continents").length()];
                            for (int i = 0; i<country.continents.length;i++){
                                country.continents[i]=json.getJSONArray("continents").getString(i);
                            }
                            country.timeZones = new String[json.getJSONArray("timezones").length()];
                            for (int i = 0; i<country.timeZones.length;i++){
                                country.timeZones[i]=json.getJSONArray("timezones").getString(i);
                            }
                            country.borders = new String[json.getJSONArray("borders").length()];
                            for (int i = 0; i<country.borders.length;i++){
                                country.borders[i]=json.getJSONArray("borders").getString(i);
                            }
                            country.languages = new String[json.getJSONObject("languages").length()];
                            x = json.getJSONObject("languages").keys();
                            int index = 0;
                            while (x.hasNext()){
                                String keyLang = (String) x.next();
                                country.languages[index]=json.getJSONObject("languages").getString(keyLang);
                                index++;
                            }
                            country.independent = json.getBoolean("independent");
                            country.unMember = json.getBoolean("unMember");
                            Toast.makeText(getApplicationContext(),"Country found!",Toast.LENGTH_SHORT).show();
                            //start new intent and pass the country
                            Intent intent = new Intent(MainActivity.this,CountryInfo.class);
                            intent.putExtra("Country", country);
                            startActivityForResult(intent,200);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Country not found!",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Country not found!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        queue.add(stringRequest);
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

}