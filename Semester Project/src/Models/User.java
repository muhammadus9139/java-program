package Models;

// User.java
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String email;
    private String password;
    private List<CV> savedCVs;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.savedCVs = new ArrayList<>();
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public List<CV> getSavedCVs() { return savedCVs; }
    public void addCV(CV cv) { savedCVs.add(cv); }
    public void removeCV(int index) {
        if (index >= 0 && index < savedCVs.size()) {
            savedCVs.remove(index);
        }
    }
}