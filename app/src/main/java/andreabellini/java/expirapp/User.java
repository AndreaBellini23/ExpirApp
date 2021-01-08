package andreabellini.java.expirapp;

public class User {

    private String longName;
    private String email;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String longName, String email) {
        this.longName = longName;
        this.email = email;
    }
}
