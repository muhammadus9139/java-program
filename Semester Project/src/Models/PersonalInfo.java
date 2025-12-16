package Models;

// PersonalInfo.java
import java.io.Serializable;

public class PersonalInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;
    private String postalCode;
    private String linkedIn;
    private String github;
    private String website;
    private String portfolio;

    public PersonalInfo() {
        this.fullName = "";
        this.email = "";
        this.phone = "";
        this.address = "";
        this.city = "";
        this.country = "";
        this.postalCode = "";
        this.linkedIn = "";
        this.github = "";
        this.website = "";
        this.portfolio = "";
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getLinkedIn() { return linkedIn; }
    public void setLinkedIn(String linkedIn) { this.linkedIn = linkedIn; }
    public String getGithub() { return github; }
    public void setGithub(String github) { this.github = github; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }
}