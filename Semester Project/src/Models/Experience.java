package Models;

// Experience.java

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Experience implements Serializable {
    private static final long serialVersionUID = 1L;
    private String jobTitle;
    private String company;
    private String location;
    private String startDate;
    private String endDate;
    private boolean currentlyWorking;
    private List<String> responsibilities;

    public Experience(String jobTitle, String company, String location, String startDate,
                      String endDate, boolean currentlyWorking) {
        this.jobTitle = jobTitle;
        this.company = company;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentlyWorking = currentlyWorking;
        this.responsibilities = new ArrayList<>();
    }

    public void addResponsibility(String responsibility) {
        responsibilities.add(responsibility);
    }

    // Getters
    public String getJobTitle() { return jobTitle; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return currentlyWorking ? "Present" : endDate; }
    public boolean isCurrentlyWorking() { return currentlyWorking; }
    public List<String> getResponsibilities() { return responsibilities; }
}
