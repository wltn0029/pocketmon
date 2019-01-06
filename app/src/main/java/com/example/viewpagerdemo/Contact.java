package com.example.viewpagerdemo;

public class Contact {
    String id;
    String name;
    String phone_number;
    String account;
    //getter/setter
    public String getId() {return id;}
    public void setId(String mid){
        id =mid;
    }

    public String getName(){return name;}
    public void setName(String mname){
        name = mname;
    }

    public String getPhone_number(){return phone_number;}
    public void setPhone_number(String mphone_number){
        phone_number = mphone_number;
    }
}
