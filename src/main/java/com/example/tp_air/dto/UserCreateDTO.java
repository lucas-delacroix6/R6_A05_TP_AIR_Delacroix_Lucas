package com.example.tp_air.dto;

import com.example.tp_air.models.User.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserCreateDTO {

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Email is mandatory")
    @Email
    private String email;

    private Role role = Role.ROLE_USER;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
