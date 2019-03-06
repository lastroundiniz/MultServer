package multserver;

import java.net.Socket;

public class Users {
    Socket s;
    String username;
    public Users (Socket s, String username) {
        this.s = s;
        this.username = username;
    }

    public Socket getS() {
        return s;
    }

    public String getUsername() {
        return username;
    }
    
}
