package fr.fsh.bbeeg.security.resources;

/**
 * @author driccio
 */
public class ConnectionInformation {
    private String login;
    private String password;

    public ConnectionInformation login(String _login){
        this.login = _login;
        return this;
    }

    public String login(){
        return this.login;
    }

    public ConnectionInformation password(String _password){
        this.password = _password;
        return this;
    }

    public String password(){
        return this.password;
    }
}
