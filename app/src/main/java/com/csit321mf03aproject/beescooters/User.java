package com.csit321mf03aproject.beescooters;

public class User {
    public String fullName, email, userName, address, memberSince;

    public User (String email, String firstName, String lastName, String address, String memberSince)
    {
        this.fullName = firstName + " " + lastName;
        this.email = email;
        this.userName = userName;
        this.address = address;
        this.memberSince = memberSince;
    }

    public String getEmail ()
    {
        return email;
    }
}
