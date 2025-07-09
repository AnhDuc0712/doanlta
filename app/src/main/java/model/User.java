package model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String fullName;
    public String username;
    public String email;
    public String phone;
    public String photo;

    // 👇 Constructor rỗng cần thiết cho Room
    public User() {
    }

    // Constructor đầy đủ cho bạn sử dụng khi tạo mới
    public User(String fullName, String username, String email, String phone, String photo) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
    }

    // Getters & Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
}
