package fr.fsh.bbeeg.content.resources;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 26/07/11
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class ContentQueryObject {
    private String ordering;
    private Integer number;

    public Integer number() {
        return (number ==  null)? 5 : number;
    }

    public ContentQueryObject number(Integer number) {
        this.number = number;
        return this;
    }

    public String ordering() {
        return ordering;
    }

    public ContentQueryObject ordering(String ordering) {
        this.ordering = ordering;
        return this;
    }
}
