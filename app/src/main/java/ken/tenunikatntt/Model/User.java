package ken.tenunikatntt.Model;

/**
 * Created by Emilken18 on 8/2/2018.
 */

public class User {

    private String Name;
    private String Password;
    private String Phone;
    private String IsStaff;
    private String secureCode;
    private String homeAddress;
    private String balance;

    public User() {
    }

    public User(String name, String password, String secureCode) {
        Name = name;
        Password = password;
        IsStaff = "false";
        this.secureCode = secureCode;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() { return Phone; }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public String getHomeAddress() { return homeAddress; }

    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
}
