package db.vissat.filevalidationbymail.Frontend.Models;

import java.util.ArrayList;
import java.util.List;

public class InputData {
    private String fileName;
    private String fileType;
    private String fileSubtype;
    private String frequency;
    private String frequencyDetails; // Change to String
    private List<String> emails;
    private List<Field> fields = new ArrayList<>();
    private String organization;

    // Getters and setters

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSubtype() {
        return fileSubtype;
    }

    public void setFileSubtype(String fileSubtype) {
        this.fileSubtype = fileSubtype;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getFrequencyDetails() {
        return frequencyDetails;
    }

    public void setFrequencyDetails(String frequencyDetails) {
        this.frequencyDetails = frequencyDetails;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public List<String> getFileFormatElementsList() {
        List<String> subtypeElementsList = new ArrayList<>();
        for (Field element : fields) {
            subtypeElementsList.add(element.getFieldType() + ":%" + element.getFieldName());
        }
        return subtypeElementsList;
    }
}

class Field {
    private String fieldName;
    private String fieldType;

    // Getters and setters

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
}
