package api;

import android.util.Log;
import java.net.URISyntaxException;
import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    private static Socket socket;

    public static Socket getSocket() {
        if (socket == null) {
            try {
                IO.Options options = new IO.Options();
                options.forceNew = true;
                options.reconnection = true;
                socket = IO.socket("http://10.0.2.2:5000", options); // dùng http, KHÔNG dùng ws://
            } catch (URISyntaxException e) {
                Log.e("SocketManager", "Socket URI syntax error", e);
            }
        }
        return socket;
    }
}
