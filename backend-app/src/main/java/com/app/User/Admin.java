package com.app.User;

import org.springframework.stereotype.Service;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {
    public Admin(String email, String password, String firstName, String lastName, String role) {
        super(email, password, firstName, lastName, role);
    };

    public User createUser(String email, String password, String firstName, String lastName, String role) {
        return new User(email, password, firstName, lastName, role);
    }

    public User editUser(String email, String password, String firstName, String lastName, String role) {
        return new User(email, password, firstName, lastName, role);
    }

    public boolean deleteUser() {
        return true;
    }

    public User viewUser(String email, String password, String firstName, String lastName, String role) {
        return new User(email, password, firstName, lastName, role);
    }
}
