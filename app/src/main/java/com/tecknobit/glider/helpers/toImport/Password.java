package com.tecknobit.glider.helpers.toImport;

import java.util.ArrayList;
import java.util.Collection;

// TODO: 20/12/2022 IMPORT FROM THE GLIDER LIBRARY
public class Password {

    private final String tail;
    private final ArrayList<String> scopes;
    private final String password;

    public Password(String tail, String password) {
        this(tail, new ArrayList<>(), password);
    }

    public Password(String tail, ArrayList<String> scopes, String password) {
        this.tail = tail;
        this.scopes = scopes;
        this.password = password;
    }

    public String getTail() {
        return tail;
    }

    public Collection<String> getScopes() {
        return scopes;
    }

    public String getPassword() {
        return password;
    }

}
