package com.davidp.chessjourney.domain;


import java.util.Optional;

public class User {

    private final long id;          // autogenerado en la BBDD
    private final String email;     // unique
    private final String firstname; // NOT NULL
    private final String lastname;  // NOT NULL

    public User(long id, String email, String firstname, String lastname) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getInitials(){

        return Optional.ofNullable(firstname).map( e-> e.substring(0,1).toUpperCase())
                .orElseGet(()->"")
                + Optional.ofNullable(lastname).map( e-> e.substring(0,1).toUpperCase())
                .orElseGet(()->"");
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
