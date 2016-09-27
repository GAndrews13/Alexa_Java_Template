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
            System.out.println("FAILURE! " + ex.getStackTrace());
        }
    }

    public String reportNaturally() {
        String reportTemplate = "There is an accident on %s. %s";
        String details = parseDetails();
        return String.format(reportTemplate, road, details);
    }

    private String stripTag(String text) {
        String[] textSplit = text.split(":");
        return textSplit[1].trim();
    }

    public void print() {
        System.out.println(reportNaturally());
    }

    public String parseDetails() {
        StringBuilder details = new StringBuilder();
        String[] stopSplit = description.split("\\.");
        for (String desc : stopSplit) {
            desc = desc.trim();
            String[] descDetails = desc.split(":");
            desc = desc.replace(".","");
            switch (descDetails[0].trim()) {
                case "Delay":
                case "Return to normal":
                    details.append(stripTag(desc));
                    details.append(".");
                    break;
                case "Reason":
                    details.append(" The reason for this issue is the ").append(stripTag(desc.toLowerCase()));
                    details.append(".");
                    break;
                case "Lane Closures":
                    details.append("Currently ").append(stripTag(desc.toLowerCase()));
                    details.append(".");
                    break;
                case "Location":
                    road = stripTag(desc.toLowerCase());
                    break;
            }
        }
        return details.toString();
    }
}
