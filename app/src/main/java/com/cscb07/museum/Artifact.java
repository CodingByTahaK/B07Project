package com.cscb07.museum;

public class Artifact {

    //Mandatory Fields
    private String lotNum;
    private String name;
    private String description;
    private String category;
    private String material;
    private String period;
    private String culturalOrigin;
    private String dimensions;
    private String conditionReport;
    private String location;
    private String acqMethod;
    private String provenance;
    private String accNum; //later change to int
    private String notes;
    private String image; //this is a uri

    public Artifact(){
    }

    //!!note for the time being, im just making lotNum into a String,as then I can use .getkey() to generate a string
    public Artifact(String lotNum, String name, String description, String category, String material, String period, String culturalOrigin, String dimensions, String conditionReport, String location, String acqMethod, String provenance, String accNum, String notes, String image) {
        this.lotNum = lotNum;
        this.name = name;
        this.description = description;
        this.category = category;
        this.material = material;
        this.period = period;
        this.culturalOrigin = culturalOrigin;
        this.dimensions = dimensions;
        this.conditionReport = conditionReport;
        this.location = location;
        this.acqMethod = acqMethod;
        this.provenance = provenance;
        this.accNum = accNum;
        this.notes = notes;
        this.image=image;
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

    public String getCulturalOrigin() {
        return culturalOrigin;
    }

    public void setCulturalOrigin(String culturalOrigin) {
        this.culturalOrigin = culturalOrigin;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public String getConditionReport() {
        return conditionReport;
    }

    public void setConditionReport(String conditionReport) {
        this.conditionReport = conditionReport;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAcqMethod() {
        return acqMethod;
    }

    public void setAcqMethod(String acqMethod) {
        this.acqMethod = acqMethod;
    }

    public String getProvenance() {
        return provenance;
    }

    public void setProvenance(String provenance) {
        this.provenance = provenance;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}