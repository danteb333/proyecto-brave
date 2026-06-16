import javax.swing.*;
import java.io.*;
import java.net.*;

public class ClienteHTTP {
    private String contenido;
    private String firstLine = "";

    public boolean conectar(String host, JLabel barraestado, String path, int puerto) throws Exception {
        barraestado.setText("Cargando..........");
        firstLine = "";
        try {
            // Para puertos HTTP usamos socket directo
            if (puerto == 80) {
                Socket socket = new Socket();
                InetSocketAddress direccion = new InetSocketAddress(host, puerto);
                socket.connect(direccion, 10000);
                socket.setSoTimeout(10000);
                GetContenido(socket, host, path, barraestado, puerto, false);
            } else {
                // Para 443, 3000 u otros, intentamos HTTPS primero, si falla HTTP
                GetContenido(null, host, path, barraestado, puerto, true);
            }
            return !firstLine.isEmpty();
        } catch (SocketTimeoutException e) {
            barraestado.setText("Tiempo de espera agotado");
            return false;
        } catch (UnknownHostException e) {
            barraestado.setText("No se encontró el servidor");
            return false;
        } catch (IOException e) {
            barraestado.setText("Revise su conexión a internet");
            return false;
        }
    }

    public void GetContenido(Socket socket, String host, String path, JLabel barraestado, int puerto, boolean usarHTTPS) throws IOException {
        if (usarHTTPS) {
            // Primero intentamos HTTPS
            boolean exito = intentarConexion(host, path, puerto, true);
            // Si falla, intentamos HTTP plano
            if (!exito) {
                intentarConexion(host, path, puerto, false);
            }
        } else {
            // Socket directo puerto 80
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("GET " + path + " HTTP/1.1\r\n");
            writer.write("Host: " + host + "\r\n");
            writer.write("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.0.0\r\n");
            writer.write("Connection: close\r\n");
            writer.write("\r\n");
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String statusLine = reader.readLine();
            if (statusLine != null) {
                firstLine = statusLine.replace("HTTP/1.1 ", "");
                barraestado.setText(firstLine);
            }

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

    // Retorna true si la conexión fue exitosa
    private boolean intentarConexion(String host, String path, int puerto, boolean https) {
        try {
            String esquema = https ? "https" : "http";
            URL url = URI.create(esquema + "://" + host + ":" + puerto + path).toURL();

            URLConnection raw = url.openConnection();

            if (raw instanceof javax.net.ssl.HttpsURLConnection httpsConn) {
                httpsConn.setHostnameVerifier((h, s) -> true);
                try {
                    javax.net.ssl.SSLContext ctx = javax.net.ssl.SSLContext.getInstance("TLS");
                    ctx.init(null, new javax.net.ssl.TrustManager[]{
                            new javax.net.ssl.X509TrustManager() {
                                public void checkClientTrusted(java.security.cert.X509Certificate[] c, String a) {}
                                public void checkServerTrusted(java.security.cert.X509Certificate[] c, String a) {}
                                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                    return new java.security.cert.X509Certificate[0];
                                }
                            }
                    }, null);
                    httpsConn.setSSLSocketFactory(ctx.getSocketFactory());
                } catch (Exception ignored) {}
            }

            HttpURLConnection connection = (HttpURLConnection) raw;
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
                char[] buffer = new char[8192];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    cuerpo.append(buffer, 0, read);
                }
                this.contenido = cuerpo.toString();
                reader.close();
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public String getContenido() { return contenido; }
    public String getFirstLine() { return firstLine; }
}
