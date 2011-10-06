package fr.fsh.bbeeg.tag.pojos;

public class Tag {

    private String tag;

    private Long weight;

    public Tag tag(String _tag) {
        this.tag = _tag;
        return this;
    }

    public String tag() {
        return this.tag;
    }

    public Tag weight(Long _weight) {
        this.weight = _weight;
        return this;
    }

    public Long weight() {
        return this.weight;
    }
}
