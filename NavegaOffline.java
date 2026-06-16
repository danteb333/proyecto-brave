import javax.swing.*;
import java.io.*;
import java.nio.file.*;

public class NavegaOffline{
    private String contenido;
    private String statusLine;

    public boolean leerArchivoLocal(String ruta){
        File archivo = new File(ruta);

        if (!archivo.exists() || archivo.isDirectory()) {
            this.statusLine = "404 Not Found (Local)";
            this.contenido = "<html><body><h2 style='color:red;'>Error 404: Archivo no encontrado</h2><p>La ruta especificada no existe en el equipo.</p></body></html>";
            return false;
        }

        try{
            // Leer todos los bytes del archivo HTML de forma nativa
            byte[] bytes= Files.readAllBytes(archivo.toPath());
            this.contenido = new String(bytes, "UTF-8");
            this.statusLine = "200 OK (Local File)";
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public String getContenido(){
        return contenido;
    }

    public String getFirstLine(){
        return statusLine;
    }


}
