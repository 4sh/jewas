package fr.fsh.bbeeg.user.pojos;

import java.util.Date;

/**
 * @author driccio
 */
public class User {
    private Long id;
    private String login;
    private String lastName;
    private String firstName;
    private String email;
    private Date lastConnectionDate;

    public User id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }

    public String login() {
        return this.login;
    }

    public User login(String _login) {
        this.login = _login;
        return this;
    }

    public User lastName(String lastName){
        this.lastName = lastName;
        return this;
    }

    public String lastName(){
        return this.lastName;
    }

    public User firstName(String firstName){
        this.firstName = firstName;
        return this;
    }

    public String firstName(){
        return this.firstName;
    }

    public User email(String _email){
        this.email = _email;
        return this;
    }

    public String email(){
        return this.email;
    }

    public User lastConnectionDate(Date _lastConnectionDate){
        this.lastConnectionDate = _lastConnectionDate;
        return this;
    }

    public Date lastConnectionDate(){
        return this.lastConnectionDate;
    }
}
