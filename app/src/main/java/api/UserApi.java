package api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import model.User;
import okhttp3.WebSocket;

public class UserApi {
    private static final String TAG = "UserApi";
    private final WebSocket webSocket;

    public interface UserApiCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public UserApi(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void login(String username, String password, UserApiCallback callback) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "login");
            JSONObject data = new JSONObject();
            data.put("username", username);
            data.put("password", password);
            payload.put("data", data);
            webSocket.send(payload.toString());
            Log.d(TAG, "🔐 Sent login: " + payload);
            // Note: Actual response handling should be implemented in WebSocketListener
        } catch (JSONException e) {
            Log.e(TAG, "❌ JSON error: " + e.getMessage());
            callback.onFailure("JSON error: " + e.getMessage());
        }
    }

    public void register(User user, String password, UserApiCallback callback) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "register");
            JSONObject data = new JSONObject();
            data.put("username", user.getUsername());
            data.put("password", password);
            data.put("email", user.getEmail());
            data.put("fullName", user.getFullName());
            data.put("phone", user.getPhone());
            data.put("photo", user.getPhoto());
            payload.put("data", data);
            webSocket.send(payload.toString());
            Log.d(TAG, "📝 Sent register: " + payload);
        } catch (JSONException e) {
            Log.e(TAG, "❌ JSON error: " + e.getMessage());
            callback.onFailure("JSON error: " + e.getMessage());
        }
    }

    public void checkUsernameExists(String username, UserApiCallback callback) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "checkUsername");
            JSONObject data = new JSONObject();
            data.put("username", username);
            payload.put("data", data);
            webSocket.send(payload.toString());
            Log.d(TAG, "🔍 Sent checkUsername: " + payload);
        } catch (JSONException e) {
            Log.e(TAG, "❌ JSON error: " + e.getMessage());
            callback.onFailure("JSON error: " + e.getMessage());
        }
    }

    public void updateUser(User user, UserApiCallback callback) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "updateUser");
            JSONObject data = new JSONObject();
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("fullName", user.getFullName());
            data.put("phone", user.getPhone());
            data.put("photo", user.getPhoto());
            payload.put("data", data);
            webSocket.send(payload.toString());
            Log.d(TAG, "✏️ Sent updateUser: " + payload);
        } catch (JSONException e) {
            Log.e(TAG, "❌ JSON error: " + e.getMessage());
            callback.onFailure("JSON error: " + e.getMessage());
        }
    }

    public void syncUserData(String username, UserApiCallback callback) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("type", "syncUserData");
            JSONObject data = new JSONObject();
            data.put("username", username);
            payload.put("data", data);
            webSocket.send(payload.toString());
            Log.d(TAG, "🔄 Sent syncUserData: " + payload);
        } catch (JSONException e) {
            Log.e(TAG, "❌ JSON error: " + e.getMessage());
            callback.onFailure("JSON error: " + e.getMessage());
        }
    }
}