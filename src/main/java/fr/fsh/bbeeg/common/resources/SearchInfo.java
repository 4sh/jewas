package fr.fsh.bbeeg.common.resources;

import java.util.List;

/**
 * @author fcamblor
 */
public class SearchInfo<T> {

    //private List<T> results;
    private List results;
    private long serverTimestamp;
    private long endingOffset;

    public SearchInfo() {
        // Good idea to initialize this here ? Wondering ...
        this.serverTimestamp = System.currentTimeMillis();
    }

    public SearchInfo results(List<T> _results) {
        this.results = _results;
        return this;
    }

    public List/*<T>*/ results() {
        return this.results;
    }

    public SearchInfo endingOffset(long _endingOffset) {
        this.endingOffset = _endingOffset;
        return this;
    }

    public long endingOffset() {
        return this.endingOffset;
    }
}
