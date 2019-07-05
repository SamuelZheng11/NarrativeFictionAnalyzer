package narritive_model;

public class BookLocation {

    private int _segmentIndex;
    private int _sentenceIndex;
    private int _tokenIndex;

    public BookLocation(int segmentIndex, int sentenceIndex, int tokenIndex) {
        this._segmentIndex = segmentIndex;
        this._sentenceIndex = sentenceIndex;
        this._tokenIndex = tokenIndex;
    }

    public int getSegmentIndex() {
        return _segmentIndex;
    }

    public int getSentenceIndex() {
        return _sentenceIndex;
    }

    public int getTokenIndex() {
        return _tokenIndex;
    }
}
