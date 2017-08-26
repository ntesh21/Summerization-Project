package Summarizer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;

//output is allwordfreq and allwordidf


public class Corplus {
    FeatureSet fs;// = new FeatureSet();
    ArrayList<ArrayList<ArrayList<String>>> featureset;// = fs.extractFeatureSet();//featureset extracted
    Hashtable<String, Integer> allwordfreq = new Hashtable<String, Integer>();
    Hashtable<String, Float> allwordidf = new Hashtable<String, Float>();
    ArrayList<Hashtable<String, Boolean>> docfreqies = new ArrayList<Hashtable<String, Boolean>>();
    int no_of_words;
    int no_of_sentences;// = fs.obj.no_of_sentences;
    public Corplus(String path) {
        fs = new FeatureSet(path);
        featureset = fs.extractFeatureSet();
        no_of_sentences = fs.obj.no_of_sentences;
        prepCorplus();    
    }
    
    public static void main(String[] args) {
        Corplus corplus = new Corplus("Documents/");
        corplus.findFreq();
        corplus.findIdf();
        corplus.showFreq();
    }
    public void prepCorplus() {
        findFreq();
        findIdf();
        showFreq();
    }
    public void showFreq() {
        Set<String> keys = allwordfreq.keySet();
        
        for (String s : keys) {
            
            //System.out.println(s + " " + allwordfreq.get(s) + " " + allwordidf.get(s));
            no_of_words ++;
        }
    }
    public void findIdf() {
        Set<String> keys = allwordfreq.keySet();
        for (String s : keys) {
            int count = 0;
            for (int i = 0 ; i < docfreqies.size() ; i ++) {
                if (docfreqies.get(i).get(s) != null) {
                    count ++;
                }
            }
            allwordidf.put(s, (float)(Math.log((float)docfreqies.size() / (float)count)));
        }
    }
    public void findFreq() {
        for (int i = 0 ; i < featureset.size() ; i ++) {
                Hashtable<String, Boolean> docfreq = new Hashtable<String, Boolean>();
                for (int j = 0 ; j < featureset.get(i).size() ; j ++) {
                    for (int k = 0 ; k < featureset.get(i).get(j).size() ; k ++) {
                        String word = featureset.get(i).get(j).get(k);
                        if (allwordfreq.get(word) == null) {
                            allwordfreq.put(word, 1);
                        }else {
                            int f = allwordfreq.get(word);
                            allwordfreq.put(word, f + 1);
                        }
                        if (docfreq.get(word) == null) {
                            docfreq.put(word, true);
                        }
                    }
                }
                docfreqies.add(docfreq);
        }
    }
    
}
