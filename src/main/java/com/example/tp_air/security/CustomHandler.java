package com.example.tp_air.security;

import com.example.tp_air.dto.LoginDTO;

import javax.security.auth.callback.*;

public class CustomHandler implements CallbackHandler {
    private final LoginDTO request;

    public CustomHandler(LoginDTO request) { this.request = request; }

    @Override
    public void handle(Callback[] callbacks) {
        for (Callback cb : callbacks) {
            if (cb instanceof NameCallback nc) nc.setName(request.getUsername());
            else if (cb instanceof PasswordCallback pc) pc.setPassword(request.getPassword().toCharArray());
        }
    }
}