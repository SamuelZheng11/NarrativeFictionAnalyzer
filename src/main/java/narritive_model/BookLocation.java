package narritive_model;

import java.io.Serializable;

public class BookLocation implements Serializable {

    private int _segmentIndex;
    private int _sentenceIndex;

    public BookLocation(int segmentIndex, int sentenceIndex) {
        this._segmentIndex = segmentIndex;
        this._sentenceIndex = sentenceIndex;
    }

    public int getSegmentIndex() {
        return _segmentIndex;
    }

    public int getSentenceIndex() {
        return _sentenceIndex;
    }
}
