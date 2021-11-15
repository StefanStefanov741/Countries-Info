package com.stefan.countriesinfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class Country implements Serializable {
    public String shortname;
    public String officialname;
    public String countryCode;
    public String[] continents;
    public String[] timeZones;
    public String[] borders;
    public String[] languages;
    public String area;
    public String population;
    public String phonestart;
    public String domain;
    public String capital;
    public String currencyName;
    public String currencySymbol;
    public String carsDriveOnThe;
    public boolean independent;
    public boolean unMember;
    public String[] gmapsLATs;
    public String flagLink;

    public Country(){
        shortname="";
        officialname="";
        countryCode="";
        area="";
        population="";
        phonestart="";
        domain="";
        capital="";
        carsDriveOnThe="";
        currencyName="";
        currencySymbol="";
        flagLink="";
        independent=true;
        unMember=false;
        gmapsLATs=new String[0];
        continents=new String[0];
        timeZones=new String[0];
        borders=new String[0];
        languages=new String[0];
    }

}
