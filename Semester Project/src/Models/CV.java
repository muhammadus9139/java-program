package Models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CV implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cvName;
    private PersonalInfo personalInfo;
    private String summary;
    private List<Experience> experienceList;
    private List<Education> educationList;
    private List<Project> projectList;
    private List<String> skills;
    private List<Certification> certifications;
    private List<String> languages;
    private String lastModified;

    // Template and Color fields
    private CVTemplateStyle templateStyle;
    private CVColor selectedColor;
    private CVTemplate template;

    // NEW: Profile Photo
    private String profilePhotoPath;

    public CV() {
        this.cvName = "New CV - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.personalInfo = new PersonalInfo();
        this.summary = "";
        this.experienceList = new ArrayList<>();
        this.educationList = new ArrayList<>();
        this.projectList = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.certifications = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.lastModified = getCurrentDateTime();

        // Set default template
//        this.templateStyle = CVTemplateStyle.SIMPLE;
//        this.selectedColor = CVColor.BLUE;
//        this.template = new CVTemplate("simple_classic", "Classic Blue", "SIMPLE",
//                "Traditional professional design", "#2c3e50");
//
//        this.profilePhotoPath = null;
    }

    // Profile Photo getter/setter
    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    // Template and Color methods
    public CVTemplateStyle getTemplateStyle() {
        return templateStyle;
    }

    public void setTemplateStyle(CVTemplateStyle templateStyle) {
        this.templateStyle = templateStyle;
        updateTemplateFromStyle();
    }

    public CVColor getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(CVColor selectedColor) {
        this.selectedColor = selectedColor;
        updateTemplateFromStyle();
    }

    public CVTemplate getTemplate() {
        if (template == null) {
            template = new CVTemplate("simple_classic", "Classic Blue", "SIMPLE",
                    "Traditional professional design", "#2c3e50");
        }
        return template;
    }

    public void setTemplate(CVTemplate template) {
        this.template = template;
    }

    private void updateTemplateFromStyle() {
        if (templateStyle != null && selectedColor != null) {
            String templateId = templateStyle.name().toLowerCase() + "_" + selectedColor.name().toLowerCase();
            String templateName = templateStyle.getName() + " " + selectedColor.getDisplayName();
            this.template = new CVTemplate(
                    templateId,
                    templateName,
                    templateStyle.name(),
                    templateStyle.getDescription(),
                    selectedColor.getHexCode()
            );
        }
    }

    // Existing getters and setters
    public String getCvName() { return cvName; }
    public void setCvName(String cvName) { this.cvName = cvName; }

    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public void setPersonalInfo(PersonalInfo personalInfo) { this.personalInfo = personalInfo; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<Experience> getExperienceList() { return experienceList; }
    public void setExperienceList(List<Experience> experienceList) { this.experienceList = experienceList; }

    public List<Education> getEducationList() { return educationList; }
    public void setEducationList(List<Education> educationList) { this.educationList = educationList; }

    public List<Project> getProjectList() { return projectList; }
    public void setProjectList(List<Project> projectList) { this.projectList = projectList; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<Certification> getCertifications() { return certifications; }
    public void setCertifications(List<Certification> certifications) { this.certifications = certifications; }

    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }

    public String getLastModified() { return lastModified; }

    public void updateLastModified() {
        this.lastModified = getCurrentDateTime();
    }

    private String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
    }
}