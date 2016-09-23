package com.alexis.alexis_java_template.Traffic;

import com.alexis.alexis_java_template.Utils.XmlParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TrafficReportHandler {

    private ArrayList<TrafficReport> trafficReports = new ArrayList<>();

    public void parseTrafficReportDocument(String document) {
        try {
            trafficReports.clear();
            Document doc = XmlParser.parseXmlFrom(document);
            Element focus = doc.getElementById("channel");
            NodeList focusList = doc.getElementsByTagName("item");
            System.out.println(focusList.getLength());
            for (int i = 0; i < focusList.getLength(); i++) {
                String node = XmlParser.nodeToString(focusList.item(i));
                TrafficReport report = new TrafficReport(node);
                trafficReports.add(report);
            }
        } catch (Exception ex) {
            Logger.getLogger(TrafficReportHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    public int getNumberOfIncidents() {
        return trafficReports.size();
    }

    public ArrayList<TrafficReport> getCountyReportsFor(String county) {
        ArrayList<TrafficReport> tempList = new ArrayList<>();
        for (TrafficReport report : trafficReports) {
            if (report.getCounty().equals(county)) {
                tempList.add(report);
            }
        }
        return tempList;
    }

    public String getLatestTrafficReports() throws MalformedURLException, IOException {
        URL url = new URL("http://m.highways.gov.uk/feeds/rss/UnplannedEvents.xml");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        con.disconnect();
        System.out.println(response.toString());
        return response.toString();
    }
    
    public void updateTrafficReports() throws IOException {
        String xml = getLatestTrafficReports();
        parseTrafficReportDocument(xml);
        System.out.println("Traffic has been updated with : " + trafficReports.size() + " reports");
    }
    
    public void printAllReports() {
        for (TrafficReport tr : trafficReports) {
            tr.print();
        }
    }
}
