package Summarizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;



//must use getAllDoc function to receive the corplus in the form of 3d arraylist (corplus)
public class GetAllDoc {
    String path; //= "Documents/";
    File folder;// = new File(path);
    File[] listOfFiles;// = folder.listFiles();
    public ArrayList<Integer> docsen = new ArrayList<Integer>();
    ArrayList<ArrayList<ArrayList<String>>> corpus = new ArrayList<ArrayList<ArrayList<String>>>();
    ArrayList<ArrayList<String>> doc;
    ArrayList<ArrayList<String>> original;
    public TokenizerME tokenizer;
    public SentenceDetectorME sentenceDetect;
    ArrayList<String> orgdoc;
    ArrayList<ArrayList<String>> allorgdoc = new ArrayList<ArrayList<String>>();
    int no_of_sentences;
    
    public GetAllDoc(String path) {
        initialize();
        this.path = path;
        folder = new File(path);
        listOfFiles = folder.listFiles();
    }
    public void initialize() {
        try {
            InputStream sentenceModelIS = new FileInputStream("en-sent.zip");
            SentenceModel model;
            try  {	
                model = new SentenceModel(sentenceModelIS);
                sentenceDetect = new SentenceDetectorME(model);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            InputStream tokenizerModelIS = new FileInputStream("en-token.zip");
            TokenizerModel tokenModel;
            try {	
                tokenModel = new TokenizerModel(tokenizerModelIS);
                tokenizer = new TokenizerME(tokenModel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }catch(Exception ex) {
            
        }
    }
    public static void main(String[] args) {
        GetAllDoc obj = new GetAllDoc("farmer suicide/");
        obj.getAllDoc();
        System.out.println(obj.corpus);
    }
    public ArrayList<ArrayList<ArrayList<String>>> getAllDoc() {
        for (File file : listOfFiles) {
            if (file.isFile()) {
                //System.out.println(file.getName());
                getSingleDoc(file.getName());
            }
        }
        return corpus;
    }
    public void getSingleDoc(String filename) {
        filename = path + filename;
        BufferedReader br = null;
        doc = new ArrayList<ArrayList<String>>();
        orgdoc = new ArrayList<String>();
        ArrayList<String> singleline;
	try {
            String line;
            br = new BufferedReader(new FileReader(filename));
            while ((line = br.readLine()) != null) {
                String[] sentences = splitToSentences(line);
                for (int i = 0 ; i < sentences.length ; i ++) {
                    StringTokenizer st = new StringTokenizer(sentences[i], "[] ?()<>!-\\s+/\t\n\r\f.,:\'\";");
                    singleline = new ArrayList<String>();
                    while (st.hasMoreTokens()) {
                        singleline.add(st.nextToken());
                    }
                    if (singleline.size() > 7) {
                        doc.add(singleline);
                        orgdoc.add(sentences[i]);
                        no_of_sentences ++;
                    }
                }
                
                //System.out.println(singleline);
                
            }
            docsen.add(doc.size());
            allorgdoc.add(orgdoc);
            corpus.add(doc);
	} catch (Exception e) {
            e.printStackTrace();
	} finally {
            try {
		if (br != null)br.close();
            } catch (Exception ex) {
		ex.printStackTrace();
            }
	}
    }
    public String[] splitToSentences(String para) {
        String[] sentences = sentenceDetect.sentDetect(para);
        return sentences;
    }
}
