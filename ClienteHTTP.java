import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClienteHTTP {
    private String contenido;

    public void conectar(String host){
        try(Socket socket=new Socket()){
            InetSocketAddress direccion= new InetSocketAddress(host,80);
            socket.connect(direccion,10000);
            socket.setSoTimeout(10000);
            GetContenido(socket,host);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String GetContenido(Socket socket, String host, JLabel barraestado) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        writer.write("GET / HTTP/1.1\r\n");
        writer.write("Host: " + host + "\r\n");
        writer.write("User-Agent:Navegador/1.0\r\n");
        writer.write("Connection: close\n");
        writer.write("\r\n");
        writer.flush();

        BufferedReader reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        barraestado.setText(reader.readLine());
        String line;
        while((line= reader.readLine())!=null){}
        return contenido;
    }
}
/*
String host = "ciei.utalca.cl";
        // Declara puerto 80
        int port = 80;

        try (Socket socket = new Socket(host, port);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Enviar GET HTTP/1.1
            writer.println("GET / HTTP/1.1");
            writer.println("Host: " + host);
            writer.println("Connection: close");
            writer.println();

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
 */