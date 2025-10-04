package com.allygo.allygoauthenticatorms.DTO;

import jakarta.persistence.*;

@Entity
@Table(name= "auth_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String roles;

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    // --- Getters (para completar la clase) ---
    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }
}
