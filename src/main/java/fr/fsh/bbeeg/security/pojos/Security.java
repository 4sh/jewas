package fr.fsh.bbeeg.security.pojos;

import java.io.Serializable;

public class Security implements Serializable {

    /**
     * The connected user login.
     */
    private String login;

    private String name;

    private String surname;

    /**
     * The connected user role.
     */
    private String role;

    public Security() {
    }

    public Security(String _login, String _name, String _surname, String _role) {
        this.login = _login;
        this.name = _name;
        this.surname = _surname;
        this.role = _role;
    }


    public String login() {
        return login;
    }

    public Security login(String _login) {
        this.login = _login;
        return this;
    }

    public String name() {
        return name;
    }

    public Security name(String _name) {
        this.name = _name;
        return this;
    }

    public String surname() {
        return surname;
    }

    public Security surname(String _surname) {
        this.surname = _surname;
        return this;
    }

    public String role() {
        return role;
    }

    public Security role(String _role) {
        this.role = _role;
        return this;
    }
}
