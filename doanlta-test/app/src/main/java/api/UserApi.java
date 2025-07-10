package api;

import android.util.Log;

import org.json.JSONObject;

import io.socket.client.Socket;
import model.User;

public class UserApi {
    private static final String TAG = "UserApi";
    private final Socket socket;

    public interface UserApiCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public UserApi(Socket socket) {
        this.socket = socket;
    }

    public void login(String username, String password, UserApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            socket.emit("login", data);

            socket.once("login_response", args -> {
                JSONObject resp = (JSONObject) args[0];
                boolean success = resp.optBoolean("success", false);
                if (success) {
                    JSONObject userJson = resp.optJSONObject("user");
                    User user = new User();
                    user.setUsername(userJson.optString("username", ""));
                    user.setFullName(userJson.optString("name", ""));
                    user.setEmail(userJson.optString("email", ""));
                    user.setPhone(userJson.optString("phone", ""));
                    user.setPhoto(userJson.optString("photo", ""));
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(resp.optString("message", "Đăng nhập thất bại"));
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void register(User user, String password, UserApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", user.getUsername());
            data.put("password", password);
            data.put("email", user.getEmail());
            data.put("name", user.getFullName());
            data.put("phone", user.getPhone());
            data.put("photo", user.getPhoto());

            socket.emit("register", data);

            socket.once("register_response", args -> {
                JSONObject resp = (JSONObject) args[0];
                boolean success = resp.optBoolean("success", false);
                if (success) {
                    JSONObject userJson = resp.optJSONObject("user");
                    User newUser = new User();
                    newUser.setUsername(userJson.optString("username", ""));
                    newUser.setFullName(userJson.optString("name", ""));
                    newUser.setEmail(userJson.optString("email", ""));
                    newUser.setPhone(userJson.optString("phone", ""));
                    newUser.setPhoto(userJson.optString("photo", ""));
                    callback.onSuccess(newUser);
                } else {
                    callback.onFailure(resp.optString("message", "Đăng ký thất bại"));
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void checkUsernameExists(String username, UserApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);
            socket.emit("check_username", data);

            socket.once("check_username_response", args -> {
                JSONObject resp = (JSONObject) args[0];
                boolean exists = resp.optBoolean("exists", false);
                if (exists) {
                    // Gửi User để báo là đã tồn tại (có thể tạo User giả)
                    callback.onSuccess(new User());
                } else {
                    callback.onFailure(resp.optString("message", "Tên đăng nhập chưa tồn tại"));
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void updateUser(User user, UserApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", user.getUsername());
            data.put("name", user.getFullName());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("photo", user.getPhoto());

            socket.emit("update_user", data);

            socket.once("update_user_response", args -> {
                JSONObject resp = (JSONObject) args[0];
                boolean success = resp.optBoolean("success", false);
                if (success) {
                    JSONObject userJson = resp.optJSONObject("user");
                    User updatedUser = new User();
                    updatedUser.setUsername(userJson.optString("username", ""));
                    updatedUser.setFullName(userJson.optString("name", ""));
                    updatedUser.setEmail(userJson.optString("email", ""));
                    updatedUser.setPhone(userJson.optString("phone", ""));
                    updatedUser.setPhoto(userJson.optString("photo", ""));
                    callback.onSuccess(updatedUser);
                } else {
                    callback.onFailure(resp.optString("message", "Cập nhật thất bại"));
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }

    public void syncUserData(String username, UserApiCallback callback) {
        try {
            JSONObject data = new JSONObject();
            data.put("username", username);

            socket.emit("sync_user", data);

            socket.once("sync_user_response", args -> {
                JSONObject resp = (JSONObject) args[0];
                boolean success = resp.optBoolean("success", false);
                if (success) {
                    JSONObject userJson = resp.optJSONObject("user");
                    User user = new User();
                    user.setUsername(userJson.optString("username", ""));
                    user.setFullName(userJson.optString("name", ""));
                    user.setEmail(userJson.optString("email", ""));
                    user.setPhone(userJson.optString("phone", ""));
                    user.setPhoto(userJson.optString("photo", ""));
                    callback.onSuccess(user);
                } else {
                    callback.onFailure(resp.optString("message", "Không lấy được user từ server"));
                }
            });
        } catch (Exception e) {
            callback.onFailure(e.getMessage());
        }
    }
}
