package com.example.restoreserve.data.restaurant.model;

import android.support.annotation.NonNull;

import com.example.restoreserve.data.StorageKeys;
import com.example.restoreserve.data.reservations.model.Table;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class Restaurant implements Serializable {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
    private String website;
    private String address;
    private String branch;
    private String openingHour;
    private String closingHour;
    private String tablesPicUrl;
    private int tablesCount;
    private int confirmationDelayMins;
    private ArrayList<Table> tables;

    public Restaurant(String name,
                      String phoneNumber,
                      String email,
                      String website,
                      String address,
                      String branch,
                      String openingHour,
                      String closingHour,
                      String tablesPicUrl,
                      int tablesCount,
                      int confirmationDelayMins,
                      ArrayList<Table> tables) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.address = address;
        this.branch = branch;
        this.openingHour = openingHour;
        this.closingHour = closingHour;
        this.tablesPicUrl = tablesPicUrl;
        this.confirmationDelayMins = confirmationDelayMins;
        this.tablesCount = tablesCount;
        this.tables = tables;
    }

    public Restaurant(DocumentSnapshot document) {
        this.id = document.getId();
        if (document.contains(StorageKeys.NAME)) {
            this.name = document.getString(StorageKeys.NAME);
        }
        if (document.contains(StorageKeys.PHONE_NUMBER)) {
            this.phoneNumber = document.getString(StorageKeys.PHONE_NUMBER);
        }
        if (document.contains(StorageKeys.EMAIL)) {
            this.email = document.getString(StorageKeys.EMAIL);
        }
        if (document.contains(StorageKeys.WEBSITE)) {
            this.website = document.getString(StorageKeys.WEBSITE);
        }
        if (document.contains(StorageKeys.ADDRESS)) {
            this.address = document.getString(StorageKeys.ADDRESS);
        }
        if (document.contains(StorageKeys.BRANCH)) {
            this.branch = document.getString(StorageKeys.BRANCH);
        }
        if (document.contains(StorageKeys.TABLES_PIC_URL)) {
            this.tablesPicUrl = document.getString(StorageKeys.TABLES_PIC_URL);
        }
        if (document.contains(StorageKeys.OPENING_HOUR)) {
            this.openingHour = document.getString(StorageKeys.OPENING_HOUR);
        }
        if (document.contains(StorageKeys.CLOSING_HOUR)) {
            this.closingHour = document.getString(StorageKeys.CLOSING_HOUR);
        }
        if (document.contains(StorageKeys.TABLES_COUNT)) {
            this.tablesCount = document.getDouble(StorageKeys.TABLES_COUNT).intValue();
        }
        if (document.contains(StorageKeys.CONFIRMATION_DELAY_MINS)) {
            this.confirmationDelayMins = document.getDouble(StorageKeys.CONFIRMATION_DELAY_MINS).intValue();
        }
        if (document.contains(StorageKeys.TABLES)) {
            final ArrayList<HashMap<String, Object>> tab = (ArrayList<HashMap<String, Object>>) document.getData().get(StorageKeys.TABLES);
            this.tables =  generateTables(tab);
        }
    }

    public Restaurant() {

    }

    private ArrayList<Table> generateTables(ArrayList<HashMap<String, Object>> tab) {
        ArrayList<Table> tables = new ArrayList<>();
        if (tab != null) {
            for (HashMap<String, Object> tableMap : tab) {
                tables.add(new Table(tableMap));
            }
        }
        return tables;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getTablesPicUrl() {
        return tablesPicUrl;
    }

    public int getTablesCount() {
        return tablesCount;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getAddress() {
        return address;
    }

    public String getBranch() {
        return branch;
    }

    public String getOpeningHour() {
        return openingHour;
    }

    public String getClosingHour() {
        return closingHour;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public void setOpeningHour(String openingHour) {
        this.openingHour = openingHour;
    }

    public void setClosingHour(String closingHour) {
        this.closingHour = closingHour;
    }

    public void setTablesPicUrl(String tablesPicUrl) {
        this.tablesPicUrl = tablesPicUrl;
    }

    public void setTablesCount(int tablesCount) {
        this.tablesCount = tablesCount;
    }

    public void setConfirmationDelayMins(int confirmationDelayMins) {
        this.confirmationDelayMins = confirmationDelayMins;
    }

    public int getConfirmationDelayMins() {
        return confirmationDelayMins;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public HashMap<String, Object> toMap(@NonNull String id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(StorageKeys.ID, id);
        map.put(StorageKeys.NAME, name);
        map.put(StorageKeys.EMAIL, email);
        map.put(StorageKeys.PHONE_NUMBER, phoneNumber);
        map.put(StorageKeys.WEBSITE, website);
        map.put(StorageKeys.ADDRESS, address);
        map.put(StorageKeys.BRANCH, branch);
        map.put(StorageKeys.OPENING_HOUR, openingHour);
        map.put(StorageKeys.CLOSING_HOUR, closingHour);
        map.put(StorageKeys.TABLES_PIC_URL, tablesPicUrl);
        map.put(StorageKeys.TABLES_COUNT, tablesCount);
        map.put(StorageKeys.TABLES, getFormatedTables());
        map.put(StorageKeys.CONFIRMATION_DELAY_MINS, confirmationDelayMins);
        return map;
    }

    private ArrayList<HashMap<Object, Object>> getFormatedTables() {
        ArrayList<HashMap<Object, Object>> formattedTables = new ArrayList<>();
        for (int i = 0; i < tables.size(); i++) {
            HashMap<Object, Object> map1 = new HashMap<>();
            final Table table = tables.get(i);
            map1.put("count", table.getSeatsCount());
            map1.put("id", table.getId());
            formattedTables.add(map1);
        }
        return formattedTables;
    }
}
