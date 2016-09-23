package com.alexis.alexis_java_template.Traffic;

import com.alexis.alexis_java_template.Utils.XmlParser;
import org.w3c.dom.Document;

public class TrafficReport {

    private String road;
    private String region;
    private String county;
    private String description;
    private String category;

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TrafficReport(String text) {
        try {
            Document item = XmlParser.parseXmlFrom(text);

            road = item.getElementsByTagName("road").item(0).getTextContent();
            region = item.getElementsByTagName("region").item(0).getTextContent();
            county = item.getElementsByTagName("county").item(0).getTextContent();
            description = item.getElementsByTagName("description").item(0).getTextContent();
            category = item.getElementsByTagName("category").item(0).getTextContent();
        } catch (Exception ex) {
            System.out.println("FAILURE! " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public String reportNaturally() {
        String reportTemplate = "There is an accident on %s. %s";
        String location = "";
        StringBuilder details = new StringBuilder();
        String[] stopSplit = description.split("\\.");
        for (String desc : stopSplit) {
            desc = desc.trim();
            if (desc.startsWith("Delay :")) {
                details.append(stripTag(desc)).append(" . ");
            } else if (desc.startsWith("Return to normal :")) {
                details.append(stripTag(desc)).append(".");
            } else if (desc.startsWith("Reason : ")) {
                details.append("The reason for this issue is the ").append(stripTag(desc.toLowerCase()));
            } else if (desc.startsWith("Location :" )) {
                 location = stripTag(desc);
            }
        }
        if("".equals(location)) {
            location = road;
        } 
        return String.format(reportTemplate, location, details.toString());
    }
    
    private String stripTag( String text) {
        String[] textSplit = text.split(":");
        return textSplit[1].trim();
    }
    
    public void print() {
        System.out.println(reportNaturally());
    }
}
