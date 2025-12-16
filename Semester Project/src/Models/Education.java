package Models;

// Education.java
import java.io.Serializable;

public class Education implements Serializable {
    private static final long serialVersionUID = 1L;
    private String degree;
    private String fieldOfStudy;
    private String institution;
    private String startDate;
    private String endDate;
    private String gpa;
    private String location;
    private String description;

    public Education(String degree, String fieldOfStudy, String institution, String startDate,
                     String endDate, String gpa, String location, String description) {
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.gpa = gpa;
        this.location = location;
        this.description = description;
    }

    // Getters
    public String getDegree() { return degree; }
    public String getFieldOfStudy() { return fieldOfStudy; }
    public String getInstitution() { return institution; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getGpa() { return gpa; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
}

