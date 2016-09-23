package com.alexis.alexis_java_template;


import com.alexis.alexis_java_template.Traffic.TrafficReport;
import com.alexis.alexis_java_template.Traffic.TrafficReportHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.junit.Test;


/**
 *
 * @author G Andrews - gareth.andrews@capgemini.com
 * @since
 */
public class TrafficReportTest {
    
    private String getXmlTextFromFile( String filename) throws IOException {
        List<String> texts = Files.readAllLines(Paths.get("src/test/resources/" + filename + ".xml")); 
        String text = "";
        for (String line : texts) {
            text +=line;
        }
        return text;
    }
    
    @Test
    public void parsingTest_01() throws IOException {
        String text = getXmlTextFromFile("TrafficReportTC01");
        TrafficReport report = new TrafficReport( text);
        assertEquals("Staffordshire", report.getCounty());
    }
    
    
    @Test
    public void TrafficReportHandlerTest_01() throws IOException {
        String text = getXmlTextFromFile("TrafficReportHandlerTC01");
        TrafficReportHandler trh = new TrafficReportHandler();
        trh.parseTrafficReportDocument(text);
        assertEquals(28, trh.getNumberOfIncidents());
    }
    
    @Test
    public void TrafficReportNaturalReport_01() throws IOException {
        String text = getXmlTextFromFile("TrafficReportTC01");
        TrafficReport report = new TrafficReport( text);
        assertEquals("There is an accident on The M54 eastbound between junction J1 and the M6. There are currently delays of 15 minutes against expected traffic . The reason for this issue is the congestion", report.reportNaturally());
    }
    
    @Test
    public void TrafficReportHandler_GetLatest() {
        try {
            TrafficReportHandler trh = new TrafficReportHandler();
            trh.getLatestTrafficReports();
        } catch (Exception ex) {
            fail("Failed with exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void TrafficReportHandler_GetLatestReports() {
        try {
            TrafficReportHandler trh = new TrafficReportHandler();
            trh.updateTrafficReports();
        } catch (Exception ex) {
            fail("Failed with exception: " + ex.getMessage());
        }
    }
    
    @Test
    public void TrafficReportHandler_GetKentReports() throws IOException {
        String text = getXmlTextFromFile("TrafficReportHandlerTC01");
        TrafficReportHandler trh = new TrafficReportHandler();
        trh.parseTrafficReportDocument(text);
        ArrayList<TrafficReport> kentReports = trh.getCountyReportsFor("Kent");
        assertEquals(2, kentReports.size());
        
    }
}
