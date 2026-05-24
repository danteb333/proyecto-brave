import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.net.ssl.SSLSocketFactory;

public class ClienteHTTP {
    private String contenido;
    private String firstLine;

    public boolean conectar(String host, JLabel barraestado,String path,int puerto) throws Exception{
        barraestado.setText("Cargando..........");
        //intenta conectarse
        try{
            Socket socket;
            if (puerto==443){
                socket=SSLSocketFactory.getDefault().createSocket();
            } else{
                socket=new Socket();
            }
            //limita el tiempo de conexion al servidor a 10 segundos
            InetSocketAddress direccion= new InetSocketAddress(host,puerto);
            socket.connect(direccion,10000);
            //timeout de 10 segundos para la carga
            socket.setSoTimeout(10000);
            GetContenido(socket,host, path, barraestado,puerto);
            System.out.println("listo2");
            return true;
        } catch (SocketTimeoutException e) {
            barraestado.setText("Tiempo de espera agotado");
            return false;
        } catch (UnknownHostException e) {
            barraestado.setText("no se encontro el servidor");
            return false;
        } catch (IOException e) {
            barraestado.setText("revise su conexion a internet");
            return false;
        }
    }

    public void GetContenido(Socket socket, String host,String path, JLabel barraestado,int puerto) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        if (puerto==443){
            
            System.out.println("listo4");
        } else{
        writer.write("GET "+path+" HTTP/1.1\r\n");
        writer.write("Host: " + host + "\r\n");
        writer.write("User-Agent: Java-Browser/1.0\r\n");
        writer.write("Connection: close\r\n");
        writer.write("\r\n");
        writer.flush();
        }

        BufferedReader reader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
        firstLine=reader.readLine().replace("HTTP/1.1 ","");
        barraestado.setText(firstLine);
        String line;
        while((line= reader.readLine())!=null && !line.isEmpty()){}
        StringBuilder cuerpo=new StringBuilder();
        while((line=reader.readLine())!=null){
            cuerpo.append(line).append("\n");
        }
        this.contenido=cuerpo.toString();

    }

    public String getContenido() {
        return contenido;
    }

    public String getFirstLine() {
        return firstLine;
    }
}