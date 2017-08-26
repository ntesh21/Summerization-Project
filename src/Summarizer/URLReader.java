package Summarizer;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Safer
 */
public class URLReader {
    ArrayList<ArrayList<String>> doc;
    public static void main(String[] args) {
        URLReader reader = new URLReader();
        
    }
    public URLReader() {
        try {
            getDocs();
        }catch(Exception ex) {}
    }
    public void getDocs() throws MalformedURLException, BoilerpipeProcessingException, FileNotFoundException, UnsupportedEncodingException, InterruptedException {
           System.getProperties().put("http.proxyHost", "172.31.1.4");
           System.getProperties().put("http.proxyPort", "8080");
           int fileCount = 0;
           
           InputStream in = new FileInputStream("URL\\urlfile.txt");
           Scanner sc = new Scanner(in);
           doc = new ArrayList<ArrayList<String>>();
           while (sc.hasNextLine() && fileCount < 5) {
               String text = "";
               String data = sc.nextLine();
               fetchContent ob = new fetchContent(data);
               ob.returnContent();
               if (ob.content.size() > 0 ) doc.add(ob.content);
               fileCount++;
           }
           printDocs();
           //System.out.println(doc);
           //return toReturn;
    }
    public void printDocs() {
        for (int i = 0 ; i < doc.size() ; i ++) {
            try {
                
                PrintWriter out = new PrintWriter("Documents\\inp" + String.valueOf(i) + ".txt");
                out.println(doc.get(i));
                out.close();
            }
            catch(Exception ex) {}
        }
    }
}
class fetchContent implements Runnable {
    Thread fetcher;
    String urlString;
    String text = "nothing fetched";
    URL url;
    ArrayList<String> content = new ArrayList<String>();
    fetchContent(String urlParam) throws MalformedURLException {
        urlString = urlParam;
        url = new URL(urlString);
        //System.out.println("finished constructor");
    }
    
    public void returnContent() throws InterruptedException {
        fetcher = new Thread(this, "IamAthread");
        fetcher.start();
        fetcher.join();
    }
    
    @Override
    public void run() {
        //System.out.println("thread started working");
        try {
            text = ArticleExtractor.INSTANCE.getText(url);
            System.out.println("good " + url);
            content.add(text);
        } catch(Exception e) {
            System.out.println("error " + url);
        }
        
        
        //System.out.println("Thread finished working");
        //MultiDocumentSummarization.content.add(text);
        //MultiDocumentSummarization.noOfFilesFetched++;
    }
}