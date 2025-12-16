package Models;


import java.io.Serializable;

public class Certification implements Serializable {
    private static final long serialVersionUID = 1L;
    private String certificationName;
    private String issuingOrganization;
    private String issueDate;
    private String expiryDate;
    private String credentialId;

    public Certification(String certificationName, String issuingOrganization,
                         String issueDate, String expiryDate, String credentialId) {
        this.certificationName = certificationName;
        this.issuingOrganization = issuingOrganization;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.credentialId = credentialId;
    }

    // Getter,setter:
    public String getCertificationName() { return certificationName; }
    public String getIssuingOrganization() { return issuingOrganization; }
    public String getIssueDate() { return issueDate; }
    public String getExpiryDate() { return expiryDate; }
    public String getCredentialId() { return credentialId; }
}