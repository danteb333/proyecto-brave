import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class Renderizador extends JPanel {
    private JEditorPane visorHTML;

    public Renderizador(JLabel estado, JTextField barra) {
        setLayout(new BorderLayout());
        visorHTML = new JEditorPane();
        visorHTML.setEditable(false);
        visorHTML.setCursor(new Cursor(Cursor.HAND_CURSOR));
        visorHTML.setContentType("text/html");

        // Contenido por defecto
        visorHTML.setText("<html><body style='text-align:center; font-family:Arial;'>"
                + "<h1>ola, mira un gatito</h1>"
                + "<img src='https://i.redd.it/meu-gato-%C3%A9-praticamente-o-gato-do-meme-v0-n9dleoj8dgfc1.jpg?width=720&format=pjpg&auto=webp&s=426eb562eddf7898e5ba777104b7065552be8114'>"
                + "</body></html>");

        configurarEventos(estado, barra);

        JScrollPane scroll = new JScrollPane(visorHTML);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void configurarEventos(JLabel estado, JTextField barra) {
        visorHTML.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                try {
                    String nombreArchivo = "";

                    // Intentamos obtener el nombre del archivo del link
                    if (e.getURL() != null) {
                        nombreArchivo = e.getURL().getFile();
                        // Si la ruta es absoluta (trae C:/), extraemos solo el nombre
                        if (nombreArchivo.contains("/")) {
                            nombreArchivo = nombreArchivo.substring(nombreArchivo.lastIndexOf('/') + 1);
                        }
                    } else {
                        // Si el URL es nulo por el cambio de estilo, extraemos la descripción del link
                        nombreArchivo = e.getDescription();
                    }

                    // Limpiamos espacios y caracteres raros
                    nombreArchivo = nombreArchivo.replace("%20", " ");

                    // Actualizamos la barra y cargamos
                    barra.setText(nombreArchivo);
                    cargarURL(nombreArchivo, estado);

                } catch (Exception ex) {
                    estado.setText("Error al abrir vínculo: " + ex.getMessage());
                }
            }
            // ... (el resto de tus eventos ENTERED y EXITED se mantienen igual)
        });
    }

    public void cargarURL(String nombreArchivo, JLabel estado) {
        try {
            // 1. Obtener la carpeta nativa del proyecto
            String carpetaNativa = System.getProperty("user.dir");
            File archivo = new File(carpetaNativa, nombreArchivo);

            // Si el usuario puso una ruta completa, File la reconocerá automáticamente
            if (!archivo.exists()) {
                archivo = new File(nombreArchivo);
            }

            if (archivo.exists() && archivo.isFile()) {
                // 2. Leer el código HTML como texto
                String codigo = new String(Files.readAllBytes(archivo.toPath()));

                // 3. Configurar el visor para que acepte HTML y links
                visorHTML.setContentType("text/html");

                // 4. ESTABLECER LA BASE (Crucial para que funcionen los links relativos)
                visorHTML.setText(codigo); // Primero inyectamos el texto
                HTMLDocument doc = (HTMLDocument) visorHTML.getDocument();
                doc.setBase(archivo.getParentFile().toURI().toURL()); // Luego fijamos la carpeta

                estado.setText("\u2713 " + archivo.getName() + " cargado");
                estado.setForeground(new Color(0, 102, 0)); // Verde éxito
            } else {
                estado.setText("Error: Archivo no encontrado");
                estado.setForeground(Color.RED);
            }
        } catch (Exception ex) {
            estado.setText("Error al procesar el archivo");
            ex.printStackTrace();
        }
    }

    public void cambiarTema(Color fondo, String colorTexto) {
        try {
            // 1. Aplicar fondo al componente
            visorHTML.setBackground(fondo);
            String fondoHex = String.format("#%02x%02x%02x", fondo.getRed(), fondo.getGreen(), fondo.getBlue());

            // 2. Obtener el código y preparar el estilo inyectado
            String codigoHmtl = visorHTML.getText();
            String estiloInyectado = "<style>" +
                    "body { background-color: " + fondoHex + " !important; color: " + colorTexto + " !important; }" +
                    "* { color: " + colorTexto + " !important; }" +
                    "</style>";

            // Insertar estilo
            if (codigoHmtl.contains("<html>")) {
                codigoHmtl = codigoHmtl.replace("<html>", "<html>" + estiloInyectado);
            }

            // 3. Obtener la carpeta actual para no perder los links
            String carpetaNativa = System.getProperty("user.dir");
            File directorioBase = new File(carpetaNativa);

            // 4. Cargar el contenido y RE-ESTABLECER la base
            visorHTML.setContentType("text/html");
            visorHTML.setText(codigoHmtl);

            // ESTO ES LO QUE ARREGLA EL ERROR DE CARGA:
            HTMLDocument doc = (HTMLDocument) visorHTML.getDocument();
            doc.setBase(directorioBase.toURI().toURL());

            visorHTML.repaint();

        } catch (Exception ex) {
            System.out.println("Error al aplicar tema y mantener links: " + ex.getMessage());
        }
    }

    public void cambiarColorTexto(String colorHex) {
        try {
            // 1. Obtenemos el kit y el estilo
            HTMLEditorKit kit = (HTMLEditorKit) visorHTML.getEditorKit();
            StyleSheet estilo = kit.getStyleSheet();

            // 2. Aplicamos la regla al cuerpo, párrafos, listas y etiquetas de texto comunes
            // Usamos !important para asegurar que ignore estilos previos
            String regla = String.format("body, p, li, div, h1, h2, h3 { color: %s !important; }", colorHex);
            estilo.addRule(regla);

            // 3. REPASO CRÍTICO: Para que el texto que ya está cargado cambie,
            // a veces es necesario refrescar el modelo de texto completamente:
            String contenidoActual = visorHTML.getText();
            visorHTML.setDocument(kit.createDefaultDocument()); // Reinicia el documento
            visorHTML.setText(contenidoActual); // Reinyecta el texto con el nuevo estilo aplicado

            visorHTML.repaint();
            visorHTML.revalidate();

        } catch (Exception e) {
            System.out.println("Error al cambiar color: " + e.getMessage());
        }
    }
}