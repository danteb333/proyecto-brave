import javax.swing.*;
import java.io.*;
import java.net.*;

public class ClienteHTTP {
    private String contenido;
    private String firstLine;

    public boolean conectar(String host, JLabel barraestado,String path,int puerto) throws Exception{
        barraestado.setText("Cargando..........");
        //intenta conectarse
        try{
            InetAddress ipadress=InetAddress.getByName(host);
            host=ipadress.getHostName();
            System.out.println(host);
            Socket socket=null;
            System.out.println("cliente: "+ puerto);
            if (puerto != 3000 && puerto != 443) {
                socket = new Socket();
                //limita el tiempo de conexion al servidor a 10 segundos
                InetSocketAddress direccion= new InetSocketAddress(host,puerto);
                socket.connect(direccion,10000);
                //timeout de 10 segundos para la carga
                socket.setSoTimeout(10000);
            }
            GetContenido(socket,host, path, barraestado,puerto);
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
        System.out.println(puerto);
        if (puerto == 3000 || puerto == 443) {
            URL url = URI.create("https://" + host + path).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //((javax.net.ssl.HttpsURLConnection)connection).setHostnameVerifier((h, s) -> true);

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0");
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();
            firstLine = responseCode + " " + connection.getResponseMessage();

            InputStream is = (responseCode >= 400) ? connection.getErrorStream() : connection.getInputStream();

            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder cuerpo = new StringBuilder();
                char[] buffer = new char[8192]; // Leer por trozos de memoria, no por líneas
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    cuerpo.append(buffer, 0, read);
                }
                this.contenido = cuerpo.toString();
                reader.close();
            }
        } else {
            // logica puerto 80
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("GET " + path + " HTTP/1.1\r\n");
            writer.write("Host: " + host + "\r\n");
            writer.write("User-Agent: Java-Browser/1.0\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String statusLine = reader.readLine();
            if (statusLine != null) {
                firstLine = statusLine.replace("HTTP/1.1 ", "");
                barraestado.setText(firstLine);
            }

            // Saltamos las cabeceras hasta encontrar la línea vacía
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {}

            StringBuilder cuerpo = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                cuerpo.append(line).append("\n");
            }
            this.contenido = cuerpo.toString();
            socket.close();
        }
    }

    public String getContenido() {
        return contenido;
    }

    public String getFirstLine() {
        return firstLine;
    }
}