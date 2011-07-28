package fr.fsh.bbeeg.user.resources;

import org.joda.time.DateMidnight;

/**
 * @author driccio
 */
public class User {
    private String name;
    private String surname;
    private String email;
    private DateMidnight lastConnectionDate;
    
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
    
    public User lastConnectionDate(DateMidnight _lastConnectionDate){
        this.lastConnectionDate = _lastConnectionDate;
        return this;
    }
     
    public DateMidnight lastConnectionDate(){
        return this.lastConnectionDate;
    }
}
