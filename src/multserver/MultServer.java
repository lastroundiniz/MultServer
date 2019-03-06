package multserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultServer {
    private ServerSocket ss;

    
    ArrayList<Users> socketList = new ArrayList();
    
    public MultServer(int port) throws IOException {
        ss = new ServerSocket(port);
        String user;
        String msg;
        
        System.out.println("Aguardando conexão de clientes");        
        while (true) { // aguarda clientes, aceita e delega para thread
            
            Socket s = ss.accept(); // obtem conexao recem chegada do cliente
            InputStream istream = s.getInputStream();
            InputStreamReader reader = new InputStreamReader(istream); 
            BufferedReader br = new BufferedReader(reader);
            user = br.readLine();
            System.out.println(user + " conectou");
            System.out.println("obteve conexao de " + s);
            Users objUser = new Users(s,user);
            msg = objUser.getUsername() + " entrou na sala.";
            sendToAll(msg);
            socketList.add(objUser); // guarda socket do cliente
            new ServerThread(objUser, this); // cria thread que trata desse cliente e repete o laco
            
        }
    }
    
    // metodo que envia mensagem para os clientes
    public void sendToAll(String msg) {
        for (int i = 0; i < socketList.size(); i++) {
            try {
                OutputStream o;
                o = socketList.get(i).getS().getOutputStream();
                PrintWriter pw = new PrintWriter(o, true);
                pw.println(msg); 
            } catch (IOException ex) {
                Logger.getLogger(MultServer.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
    
    public void sendToPrivate(String eu, String user, String msg) {
        
        for (int i = 0; i < socketList.size(); i++) {
            if (socketList.get(i).getUsername().equals(user)) {
                System.out.println("aqui");
                msg = eu + ":" + msg;
                try {
                    OutputStream o;
                    o = socketList.get(i).getS().getOutputStream();
                    PrintWriter pw = new PrintWriter(o, true);
                    pw.println(msg); 
                } catch (IOException ex) {
                    Logger.getLogger(MultServer.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }   
        }
    }
    
    public class ServerThread extends Thread {
        private Users user;
        private MultServer server;
        // construtor recebe parâmetros do programa principal
        public ServerThread (Users user, MultServer server) {
            this.user = user;
            this.server = server;
            this.start();
        }
        // corpo da thread
        public void run() {
            String msg;
            String[] split;
            try {
                InputStream istream = user.getS().getInputStream();
                InputStreamReader reader = new InputStreamReader(istream); 
                BufferedReader br = new BufferedReader(reader);
                while (true) {
                    msg =  br.readLine();
                    
                    if (msg.contains("#")) {
                        split = msg.split("#");
                        System.out.println(split[0]);
                        System.out.println(split[1]);
                        server.sendToPrivate(user.getUsername(), split[0], split[1]);
                    } else {
                        msg = user.getUsername() + ": " + msg;
                        server.sendToAll(msg);
                    }
                }
            //socket.close();
            } catch (IOException ex) {
                msg = user.getUsername() + " desconectou.";
                server.sendToAll(msg);
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        new MultServer(1234);
    }
    
}
