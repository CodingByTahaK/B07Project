package com.cscb07.museum;

public class Artifact {

    //Mandatory Fields
    private String lotNum;
    private String name;
    private String description;
    private String category;
    private String material;
    private String period;

    //Optional Fields work on later
/*    private String culturalOrigin;
    private String dimensions;
    private String report;
    private String currentLocation;
    private String aqMethod;
    private String provenance;
    private int accNum;
    private String notes;
    private String image;*/


    public Artifact(){
    }

    //!!note for the time being, im just making lotNum into a String,as then I can use .getkey() to generate a string
    public Artifact(String lotNum, String name, String description, String category, String material, String period) {
        this.lotNum = lotNum;
        this.name = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.period = period;
    }

    public String getLotNum() {
        return lotNum;
    }

    public void setLotNum(String lotNum) {
        this.lotNum = lotNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
