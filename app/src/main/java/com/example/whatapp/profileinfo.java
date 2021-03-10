package com.example.whatapp;

public class profileinfo {
    public String uid;
    public  String name,status,image,phone;
    public  profileinfo (){

    }

    public String getImage() {
        return image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public  profileinfo (String name, String phone, String uid, String image, String status){
        this.name=name;
        this.phone=phone;
        this.uid=uid;
        this.image=image;
        this.status=status;
    }

}
