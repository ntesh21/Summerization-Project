package Summarizer;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import org.tartarus.snowball.ext.PorterStemmer;


//must use makeSummary method which makes use of centroid object.
public class Summary {
    String path;
    Corplus corplus;
    public StringBuilder summary;
    float alpha = 0.5f;
    float beta = 0.5f;
    int summarylength = 5;
    public static void main(String[] args) {
        Summary summary = new Summary("Documents/");
        
          //summary.makeGraphSummary();
          //summary.makeCentroidSummary();
        summary.makeHybridSummary();
        
    }
    public Summary(String path, String qry, int no_of_links, int summary_length) {
        this.path = path;
        Crawler crawler = new Crawler(qry, no_of_links);
        URLReader reader = new URLReader();
        this.summarylength = summary_length;
        corplus = new Corplus(path);
    }
    public Summary(String path) {
        this.path = path;
        corplus = new Corplus(path);
    }
    public Summary(String path, String qry) {
        this.path = path;
        newQuery(qry);
        corplus = new Corplus(path);
    }
    public void newQuery(String qry) {
        Crawler crawler = new Crawler(qry);
        URLReader reader = new URLReader();
    }
    public void makeHybridSummary() {
        Graph graph = new Graph(corplus);
        graph.buildGraph();
        
        Centroid centroid = new Centroid(corplus);
        centroid.buildCentroid();

        
        
 //       System.out.println("started");
        for (int i = 0 ; i < centroid.sentencescore.size() ; i ++) {
            centroid.sentencescore.get(i).score = alpha * i;
            graph.sentencescore.get(i).score = beta * i;
        }
        Collections.sort(centroid.sentencescore, new IdBasedSort());
        Collections.sort(graph.sentencescore, new IdBasedSort());
        for (int i = 0 ; i < graph.sentencescore.size() ; i ++) {
            //int id = graph.sentencescore.get(i).sentence_id;
            centroid.sentencescore.get(i).score += graph.sentencescore.get(i).score;
        }
        Collections.sort(centroid.sentencescore, new ScoreBasedAsceSort());

        
        summary = new StringBuilder();
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<Sentence> chrono = new ArrayList<Sentence>();
        
        int count = 0;
        for (int i = 0 ; i < centroid.sentencescore.size() ; i ++) {
            if (count == summarylength) {break;}
            String line = centroid.corplus.fs.obj.allorgdoc.get(centroid.sentencescore.get(i).docno).get(centroid.sentencescore.get(i).sentenceno);
            if (!isSentenceRed(line, output)) {
                output.add(line);
                chrono.add(centroid.sentencescore.get(i));
                //summary.append(line + "\n\n");
                count ++;
            }
            
        }
        
        for (int i = 0 ; i < chrono.size() ; i ++) {
            chrono.get(i).score = 1 - chrono.get(i).sentenceno / corplus.fs.obj.docsen.get(chrono.get(i).docno);
        }
        Collections.sort(chrono);
        for (int i = 0 ; i < chrono.size() ; i ++) {
            Sentence x = chrono.get(i);
            String line = corplus.fs.obj.allorgdoc.get(x.docno).get(x.sentenceno);
            summary.append(line + "\n\n");
        }
        printSummary("SummaryHybrid.txt");
        
    }
    public boolean isSentenceRed(String line, ArrayList<String> output) {
        ArrayList<String> linewords = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(line, "[] ?()<>!-\\s+/\t\n\r\f.,:\'\";");
        while (st.hasMoreTokens()) {
            String word = st.nextToken().toLowerCase();
            if (!isStopWord(word) && !linewords.contains(stem(word))) {
                linewords.add(stem(word));
            }
        }
        for (int i = 0 ; i < output.size() ; i ++) {
            ArrayList<String> eachlinewords = new ArrayList<String>();
            String eachline = output.get(i);
            st = new StringTokenizer(eachline, "[] ?()<>!-\\s+/\t\n\r\f.,:\'\";");
            while (st.hasMoreTokens()) {
                String word = st.nextToken().toLowerCase();
                if (!isStopWord(word) && !eachlinewords.contains(stem(word))) {
                    eachlinewords.add(stem(word));
                }
            }
            if (findIntersection(linewords, eachlinewords) >= (int)(0.5 * linewords.size())) {
                return true;
            }
        }
        return false;
    }
    public String stem(String word) {
        PorterStemmer stem = new PorterStemmer();
        stem.setCurrent(word.toLowerCase());
        stem.stem();
        return stem.getCurrent();
    }
    public boolean isStopWord(String word) {
        String[] stopwords = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
        for (int i = 0 ; i < stopwords.length ; i ++) {
            if (stopwords[i].equals(word)) {
                return true;
            }
        }
        return false;
    }
    public int findIntersection(ArrayList<String> a, ArrayList<String> b) {
        int count = 0;
        for (String s : a) {
            if (b.contains(s)) {
                count ++;
            }
        }
        return count;
    }
    public void printSummary(String filename) {
        try {
            PrintWriter out = new PrintWriter(filename);
            System.out.println(summary);
            out.println(summary.toString());
            out.close();
        }
        catch(Exception ex) {}        
    }
    public void makeCentroidSummary() {
        Centroid centroid = new Centroid(corplus);
        centroid.buildCentroid();
        summary = new StringBuilder();
        
        int summarylength = 5;
        for (int i = 0 ; i < summarylength ; i ++) {
            String line = centroid.corplus.fs.obj.allorgdoc.get(centroid.sentencescore.get(i).docno).get(centroid.sentencescore.get(i).sentenceno);
            summary.append(line + "\n");
        }
        printSummary("SummaryCentroid.txt");
    }
    public void makeGraphSummary() {
        Graph graph = new Graph(corplus);
        graph.buildGraph();
        summary = new StringBuilder();
        
        int summarylength = 5;
        for (int i = 0 ; i < summarylength ; i ++) {
            String line = graph.corplus.fs.obj.allorgdoc.get(graph.sentencescore.get(i).docno).get(graph.sentencescore.get(i).sentenceno);
            summary.append(line + "\n");
        }
        printSummary("SummaryGraph.txt");
    }
}

class IdBasedSort implements Comparator <Sentence> {
    public int compare(Sentence a, Sentence b) {
        return a.sentence_id < b.sentence_id ? -1 : 1;
    }
}
class ScoreBasedAsceSort implements Comparator <Sentence> {
    public int compare(Sentence a, Sentence b) {
        return a.score < b.score ? -1 : 1;
    }
}