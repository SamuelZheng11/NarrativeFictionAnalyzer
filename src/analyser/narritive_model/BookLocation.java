package analyser.narritive_model;

public class BookLocation {

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
