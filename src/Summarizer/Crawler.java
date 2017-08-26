package Summarizer;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OQueryRequest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Safer
 */
public class Crawler {
    ArrayList<String> links = new ArrayList<String>();
    String qry;
    int count = 10;
    public Crawler(String qry) {
        this.qry = qry;
        getURL();
        printToFile();
    }
    public Crawler(String qry, int count) {
        this.qry = qry;
        this.count = count;
        getURL();
        printToFile();
    }
    public static void main(String[] args) {
        Crawler crawller = new Crawler("paris attack");
        crawller.getURL();
        crawller.printToFile();
        
    }
    public void getURL() {
        qry = "\'" + qry + "\'";
        System.setProperty("https.proxyHost", "172.31.1.3");
        System.setProperty("https.proxyPort", "8080");
        ODataConsumer c = ODataConsumers.newBuilder("https://api.datamarket.azure.com/Bing/Search").setClientBehaviors(OClientBehaviors.basicAuth("accountKey", "I/JMZMShDneSQBg6o+21hcz5eEjR4ROyxnFwG1K4iRM")).build();
        OQueryRequest<OEntity> oRequest = c.getEntities("Web").custom("Query", qry);

        Enumerable<OEntity> entities = oRequest.execute();
        Iterator i = entities.iterator();
        
        while (i.hasNext() && count > 0) {
            String[] fields = i.next().toString().split("OProperty");
            String link = fields[fields.length - 1].split(",")[2];
            links.add(link.substring(0, link.length() - 2));
            System.out.println(link.substring(0, link.length() - 2));
            count --;
        }

    }
    public void printToFile() {
        System.out.println(links.size());
        try {
            PrintWriter out = new PrintWriter("URL\\urlfile.txt");
            for (int i = 0 ; i < links.size() ; i ++) {
                out.println(links.get(i));
            }
            out.close();
        }catch(Exception ex){}
    }
}