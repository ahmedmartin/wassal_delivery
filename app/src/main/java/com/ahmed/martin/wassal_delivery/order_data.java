package com.ahmed.martin.wassal_delivery;

import java.io.Serializable;

public class order_data implements Serializable {

    private String s_uid,r_name,r_phone,r_address,description,weight,provide_pay,delivery_estimate,date,order_id ;
    private Double r_long,r_lat,s_long,s_lat,KM;
    private String city,delivery_type;



    public String getR_name() {
        return r_name;
    }

    public String getR_phone() {
        return r_phone;
    }

    public String getR_address() {
        return r_address;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getProvide_pay() {
        return provide_pay;
    }

    public String getDelivery_estimate() {
        return delivery_estimate;
    }

    public String getDate() {
        return date;
    }





    public void setR_name(String r_name) {
        this.r_name = r_name;
    }

    public void setR_phone(String r_phone) {
        this.r_phone = r_phone;
    }

    public void setR_address(String r_address) {
        this.r_address = r_address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setProvide_pay(String provide_pay) {
        this.provide_pay = provide_pay;
    }

    public void setDelivery_estimate(String delivery_estimate) {
        this.delivery_estimate = delivery_estimate;
    }


    public void setR_long(Double r_long) {
        this.r_long = r_long;
    }

    public void setR_lat(Double r_lat) {
        this.r_lat = r_lat;
    }


    public void setS_uid(String s_uid) {
        this.s_uid = s_uid;
    }

    public Double getR_long() {
        return r_long;
    }

    public Double getR_lat() {
        return r_lat;
    }

    public String getS_uid() {
        return s_uid;
    }

    public Double getS_long() {
        return s_long;
    }

    public void setS_long(Double s_long) {
        this.s_long = s_long;
    }

    public Double getS_lat() {
        return s_lat;
    }

    public void setS_lat(Double s_lat) {
        this.s_lat = s_lat;
    }

    public Double getKM() {
        return KM;
    }

    public void setKM(Double KM) {
        this.KM = KM;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDelivery_type() {
        return delivery_type;
    }

    public void setDelivery_type(String delivery_type) {
        this.delivery_type = delivery_type;
    }
}
