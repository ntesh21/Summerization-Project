package Summarizer;

public class Sentence implements Comparable<Sentence>{
    int docno;
    int sentenceno;
    float score;
    int sentence_id;
    //int hybridscore;
    public int compareTo(Sentence a) {
        Float x = new Float(score);
        Float y = new Float(a.score);
        return -x.compareTo(y);
    }
    public String toString() {
        return String.valueOf(score);
    }
    public Sentence(int docno, int sentenceno, int intersec) {
        this.docno = docno;
        this.sentenceno = sentenceno;
        this.score = intersec;
    }
    public Sentence(int docno, int sentenceno, int intersec, int id) {
        this.docno = docno;
        this.sentenceno = sentenceno;
        this.score = intersec;
        this.sentence_id = id;
    }
}