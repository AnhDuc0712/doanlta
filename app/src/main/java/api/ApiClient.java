package api;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ApiClient {
    private static final String TAG = "ApiClient";
    public static final String BASE_URL = "http://10.0.2.2:5000";
    private static final String SERVER_URL = "ws://10.0.2.2:5000";
    private static OkHttpClient client;
    private static WebSocket webSocket;

    // Initialize OkHttpClient with custom configurations if needed
    private static synchronized OkHttpClient getClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .build();
        }
        return client;
    }

    public static synchronized void connectSocket() {
        if (webSocket != null) {
            Log.d(TAG, "🔗 WebSocket already connected");
            return;
        }
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = getClient().newWebSocket(request, new EchoWebSocketListener());
        Log.d(TAG, "🔌 Connecting to: " + SERVER_URL);
    }

    public static synchronized WebSocket getSocket() {
        if (webSocket == null) {
            connectSocket();
        }
        return webSocket;
    }

    public static synchronized void disconnectSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnected");
            webSocket = null;
            client = null; // Reset client to ensure fresh instance
            Log.d(TAG, "🚫 WebSocket closed");
        }
    }

    private static class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            Log.d(TAG, "✅ WebSocket connected");
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            Log.d(TAG, "📩 Received: " + text);
            // TODO: Forward message to appropriate handler if needed
        }

        @Override
        public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            Log.w(TAG, "⚠️ WebSocket closing: " + reason);
        }

        @Override
        public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
            Log.w(TAG, "⚠️ WebSocket closed: " + reason);
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
            Log.e(TAG, "❌ WebSocket failure: " + t.getMessage(), t);
        }
    }
}