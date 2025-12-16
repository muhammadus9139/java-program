package Services;

import Models.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthenticationManager {
    private Map<String, User> users;
    private Map<String, String> otpStore;
    private Map<String, Long> otpTimestamps;
    private User currentUser;
    private static final String USER_DATA_FILE = "users.dat";
    private static final long OTP_VALIDITY = 5 * 60 * 1000; // 5 minutes

    public AuthenticationManager() {
        this.users = new HashMap<>();
        this.otpStore = new HashMap<>();
        this.otpTimestamps = new HashMap<>();
        loadUsers();
    }

    // OTP generation
    public boolean sendOTP(String email) {
        String otp = generateOTP();
        otpStore.put(email, otp);
        otpTimestamps.put(email, System.currentTimeMillis());
        return EmailService.sendEmail(email, "CV Maker - Verification Code",
                "Your verification code is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nIf you didn't request this code, please ignore this email.");
    }

    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    public boolean verifyOTP(String email, String otp) {
        String storedOTP = otpStore.get(email);
        Long timestamp = otpTimestamps.get(email);

        if (storedOTP != null && timestamp != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp <= OTP_VALIDITY && storedOTP.equals(otp)) {
                otpStore.remove(email);
                otpTimestamps.remove(email);
                return true;
            }
        }
        return false;
    }

    public boolean signup(String email, String password) {
        if (users.containsKey(email)) return false;
        User newUser = new User(email, password);
        users.put(email, newUser);
        saveUsers();
        return true;
    }

    public boolean login(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() { currentUser = null;
    }

    public User getCurrentUser() { return currentUser; }

    // Save users to file
    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_DATA_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load users from file
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) { users = new HashMap<>(); return; }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_DATA_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            users = new HashMap<>();
        }
    }
}
