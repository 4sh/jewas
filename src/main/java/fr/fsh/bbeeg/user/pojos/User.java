package fr.fsh.bbeeg.user.pojos;

import java.util.Date;

/**
 * @author driccio
 */
public class User {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private Date lastConnectionDate;

    public User id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }

    public User name(String _name){
        this.name = _name;
        return this;
    }

    public String name(){
        return this.name;
    }

    public User surname(String _surname){
        this.surname = _surname;
        return this;
    }

    public String surname(){
        return this.surname;
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
