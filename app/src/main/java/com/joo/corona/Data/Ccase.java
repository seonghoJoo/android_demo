package com.joo.corona.Data;

import com.google.gson.annotations.SerializedName;

public class Ccase {
    @SerializedName("case_no")
    int case_no;
    @SerializedName("case_sex")
    int case_sex;
    @SerializedName("case_age")
    int case_age;
    @SerializedName("case_address")
    String case_address;
    @SerializedName("c_no")
    int c_no;
    @SerializedName("c_address")
    String c_address;
    @SerializedName("place_order")
    int place_order;
    @SerializedName("place_name")
    String place_name;
    @SerializedName("visit_start_time")
    String visit_start_time;
    @SerializedName("visit_end_time")
    String visit_end_time;
    @SerializedName("latitude")
    String latitude;
    @SerializedName("longtitude")
    String longtitude;
    @SerializedName("mask")
    int mask;

    public int getCase_no() {
        return case_no;
    }

    public void setCase_no(int case_no) {
        this.case_no = case_no;
    }

    public int getCase_sex() {
        return case_sex;
    }

    public void setCase_sex(int case_sex) {
        this.case_sex = case_sex;
    }

    public int getCase_age() {
        return case_age;
    }

    public void setCase_age(int case_age) {
        this.case_age = case_age;
    }

    public String getCase_address() {
        return case_address;
    }

    public void setCase_address(String case_address) {
        this.case_address = case_address;
    }

    public int getC_no() {
        return c_no;
    }

    public void setC_no(int c_no) {
        this.c_no = c_no;
    }

    public String getC_address() {
        return c_address;
    }

    public void setC_address(String c_address) {
        this.c_address = c_address;
    }

    public int getPlace_order() {
        return place_order;
    }

    public void setPlace_order(int place_order) {
        this.place_order = place_order;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getVisit_start_time() {
        return visit_start_time;
    }

    public void setVisit_start_time(String visit_start_time) {
        this.visit_start_time = visit_start_time;
    }

    public String getVisit_end_time() {
        return visit_end_time;
    }

    public void setVisit_end_time(String visit_end_time) {
        this.visit_end_time = visit_end_time;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }
   /*Rows :
    [
        {
            "case_no":1,
            "case_sex":0,
            "case_age":21,
            "case_address":"용산구",
            "c_no":1,
            "c_address":"용산구",
            "place_order":1,
            "place_name":"백원노래방",
            "visit_start_time":"2020/05/23/20/56",
            "visit_end_time":"2020/05/23/22/00",
            "latitude":"37.551224162576",
            "longtitude":"126.925539577657",
            "mask":0
        },
        {
            "case_no":1,
            "case_sex":0,
            "case_age":21,
            "case_address":"용산구",
            "c_no":1,
            "c_address":"용산구",
            "place_order":2,
            "place_name":"이태원클럽",
            "visit_start_time":"2020/05/23/23/00",
            "visit_end_time":"2020/05/23/23/56",
            "latitude":"37.551224162576",
            "longtitude":"126.925539577657",
            "mask":0
        }
    ]


* */
}
