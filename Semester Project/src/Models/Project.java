package Models;

// Project.java
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private static final long serialVersionUID = 1L;
    private String projectName;
    private String projectLink;
    private String duration;
    private List<String> technologies;
    private String description;

    public Project(String projectName, String projectLink, String duration, String description) {
        this.projectName = projectName;
        this.projectLink = projectLink;
        this.duration = duration;
        this.description = description;
        this.technologies = new ArrayList<>();
    }

    public void addTechnology(String tech) {
        technologies.add(tech);
    }

    // Getters
    public String getProjectName() { return projectName; }
    public String getProjectLink() { return projectLink; }
    public String getDuration() { return duration; }
    public List<String> getTechnologies() { return technologies; }
    public String getDescription() { return description; }
}