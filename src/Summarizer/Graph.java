package Summarizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;

public class Graph {
    Corplus corplus;// = new Corplus();
    float[][] sentencevector;// = new float[corplus.no_of_sentences][corplus.no_of_words];
    Hashtable<String, Integer> wordindex = new Hashtable<String, Integer>();
    float[][] sentencematrix;// = new float[corplus.no_of_sentences][corplus.no_of_sentences];
    float threshold2 = 0.1f;
    ArrayList<Sentence> sentencescore = new ArrayList<Sentence>();
    
    public Graph(Corplus corplus) {
        this.corplus = corplus;
        sentencevector = new float[corplus.no_of_sentences][corplus.no_of_words];
        sentencematrix = new float[corplus.no_of_sentences][corplus.no_of_sentences];
    }
    public static void main(String[] args) {
        Graph g = new Graph(new Corplus("netneutrality/"));
        g.buildGraph();
    }
    public void buildGraph() {
        buildWordIndex();
        getSentenceVector();
        buildSentenceMatrix();
       
        
        getSentenceScore();
        //System.out.println(sentencescore);
        //showMatrix();
        /*for (int i = 0 ; i < sentencescore.size() ; i ++) {
            //System.out.println(sentencescore.get(i));
            System.out.println(i + 1);
        }*/
        
    }
    public void showMatrix() {
        int c = 2;
        for (int i = 0 ; i < sentencematrix.length ; i ++) {
            for (int j = 0 ; j < sentencematrix[0].length ; j ++) {
                //System.out.println(sentencematrix[i][j]);
                System.out.println(c);
                c ++;
            }

        }
    }
    public void buildWordIndex() {
        Set<String> keys = corplus.allwordfreq.keySet();
        int index = 0;
        for (String word : keys) {
            wordindex.put(word, index);
            index ++;
        }
        //System.out.println(wordindex.size() + " " + corplus.no_of_sentences);
    }
    public void getSentenceScore() {
        for (int i = 0 ; i < sentencematrix.length ; i ++) {
            int count = 0;
            for (int j = 0 ; j < sentencematrix[0].length ; j ++) {
                if (sentencematrix[i][j] >= threshold2) {
                    count ++;
                }
                sentencescore.get(i).score = count;
            }
        }
        Collections.sort(sentencescore);
    }
    public void buildSentenceMatrix() {
        int sentenceno1 = 0;
        for (int i = 0 ; i < corplus.fs.featureset.size() ; i ++) {
            for (int j = 0 ; j < corplus.fs.featureset.get(i).size() ; j ++) {
                int sentenceno2 = 0;
                for (int ii = 0 ; ii < corplus.fs.featureset.size() ; ii ++) {
                    for (int jj = 0 ; jj < corplus.fs.featureset.get(ii).size() ; jj ++) {
                        if (i == ii && j == jj) {
                            sentenceno2 ++;
                            continue;
                        }
                        
                     //   ArrayList<String> sentence1 = corplus.fs.featureset.get(i).get(j);
                     //   ArrayList<String> sentence2 = corplus.fs.featureset.get(ii).get(jj);
                        sentencematrix[sentenceno1][sentenceno2] = getCosineSimilarity(sentenceno1, sentenceno2);
                        
                        sentenceno2 ++;
                    }
                }
                sentenceno1 ++;
            }
        }
    }
    public float getCosineSimilarity(int sen1, int sen2) {
//        System.out.println("sen values " + sen1 + " " + sen2);
        float num = 0;
        for (int i = 0 ; i < wordindex.size() ; i ++) {
            num += sentencevector[sen1][i] * sentencevector[sen2][i];
        }
        
        float p1 = 0;
        float p2 = 0;
        for (int i = 0 ; i < wordindex.size() ; i ++) {
            p1 += sentencevector[sen1][i] * sentencevector[sen1][i];
            p2 += sentencevector[sen2][i] * sentencevector[sen2][i];
        }
        float den = ((float)Math.sqrt(p1)) * ((float)Math.sqrt(p2));
        if (den == 0) {
            //System.out.println("fuck " + num);
            return 0;
        }
        return num / den;
    }
    public void getSentenceVector() {
        int sentenceno = 0;
        for (int i = 0 ; i < corplus.fs.featureset.size() ; i ++) {
            for (int j = 0 ; j < corplus.fs.featureset.get(i).size() ; j ++) {
                sentencescore.add(new Sentence(i, j, 0, sentenceno));
                for (int k = 0 ; k < corplus.fs.featureset.get(i).get(j).size() ; k ++) {
                    String word = corplus.fs.featureset.get(i).get(j).get(k);
                    sentencevector[sentenceno][wordindex.get(word)] += corplus.allwordidf.get(word);
                    
                }
                sentenceno ++;
            }
        }
    }
}