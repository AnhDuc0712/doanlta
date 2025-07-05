package model;

public class User {
    public String FullName;
    public String Email;
    public String Password;

    // Constructor đăng nhập
    public User(String email, String password) {
        this.Email = email;
        this.Password = password;
    }

    // Constructor đăng ký
    public User(String name, String email, String password) {
        this.FullName = name;
        this.Email = email;
        this.Password = password;
    }
}