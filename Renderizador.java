import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class Renderizador extends JPanel {
    private final JEditorPane visorHTML;
    private final ClienteHTTP clienteHTTP;
    private final Pestana pestana;
    private final Historial historial;
    private final BarraNavegacion barraNavegacion;
    private final NavegaOffline navegaOffline = new NavegaOffline();

    public Renderizador(JLabel estado, JTextField barra,Pestana pestana, Historial historial,BarraNavegacion barraNavegacion) {
        setLayout(new BorderLayout());
        visorHTML = new JEditorPane();
        visorHTML.setEditable(false);
        visorHTML.setCursor(new Cursor(Cursor.HAND_CURSOR));
        visorHTML.setContentType("text/html");
        this.clienteHTTP=new ClienteHTTP();
        this.historial=historial;
        this.pestana=pestana;
        this.barraNavegacion=barraNavegacion;

        // Contenido por defecto
        visorHTML.setText("<html><body style='text-align:center; font-family:Arial;'>"
                + "<h1>Hola, bienvenidos a nuestro navegador</h1>"
                + "</body></html>");

        configurarEventos(estado, barra);

        JScrollPane scroll = new JScrollPane(visorHTML);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void configurarEventos(JLabel estado, JTextField barra) {
        visorHTML.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                URL urlBase = visorHTML.getPage();
                String urlClickeada = (e.getURL() != null) ? e.getURL().toString() : e.getDescription();
                if (urlClickeada.startsWith("http://")) {
                    urlClickeada = urlClickeada.replace("http://", "");
                } else if (urlClickeada.startsWith("https://")) {
                    urlClickeada = urlClickeada.replace("https://", "");
                }

                final String urlFinalAProcesar = urlClickeada;

                new Thread(() -> {
                    try {
                        cargarURL(urlFinalAProcesar, estado);
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() ->
                                estado.setText("Error: " + ex.getMessage())
                        );
                    }
                }).start();
            }
        });
    }

    public void cargarURL(String nombreArchivo, JLabel estado) {
        if(Ventana.Modo()){
            String rutaLocal = nombreArchivo;

            boolean leido = navegaOffline.leerArchivoLocal(rutaLocal);
            SwingUtilities.invokeLater(()->{
                barraNavegacion.getBarra().setText(rutaLocal);
                estado.setText(navegaOffline.getFirstLine());

            });

            // Configurar la base del documento para que busque fotos locales en la misma carpeta
            try {
                File f = new File(rutaLocal);
                if (f.exists()) {
                    visorHTML.getDocument().putProperty(javax.swing.text.Document.StreamDescriptionProperty, f.toURI().toURL());
                }
            } catch (Exception l){}

            String html = navegaOffline.getContenido();
            if (html != null) {
                // Quitamos scripts pesados
                html = html.replaceAll("(?i)<script[\\s\\S]*?></script>", "");
                html = html.replaceAll("(?i)on\\w+=\"[^\"]*\"", "");
                visorHTML.setText(html);
            }

            // Actualizar la pestaña con el nombre del archivo
            File archivo = new File(rutaLocal);
            String nombrePestana = archivo.getName().isEmpty() ? "Archivo Local" : archivo.getName();
            JTabbedPane panel = pestana.getContenedor();
            int index = panel.getSelectedIndex();
            if (index != -1) {
                panel.setTitleAt(index, nombrePestana);
                Component c = panel.getTabComponentAt(index);
                if (c instanceof JPanel) {
                    for (Component child : ((JPanel) c).getComponents()) {
                        if (child instanceof JLabel) {
                            ((JLabel) child).setText(nombrePestana);
                            break;
                        }
                    }
                }
            }
            System.out.println("Entro");

        }else{

            if (!nombreArchivo.startsWith("http://") && !nombreArchivo.startsWith("https://")) {
                nombreArchivo = "http://" + nombreArchivo;
            }

            String urlFinal = nombreArchivo;
            try {
                URL url = URI.create(nombreArchivo).toURL();
                String host = url.getHost();
                String path = (url.getPath() == null || url.getPath().isEmpty()) ? "/" : url.getPath();

                String[] nuevaurl = host.split("\\.");
                int[] puertos = {80, 443};
                boolean conectado = false;

                for (int puertoEscogido : puertos) {
                    if (clienteHTTP.conectar(host, estado, path, puertoEscogido)) {
                        String status = clienteHTTP.getFirstLine();

                        // evaluamos puerto 80 y si fue redireccionada a link https en ese puerto
                        if (puertoEscogido == 80 && (status.contains("301") || status.contains("302"))) {
                            continue;
                        }

                        // detectamos si la pagina se cargo bien o fue redireccionada (301 o 302)
                        if (status.contains("200") || status.contains("301") || status.contains("302")) {
                            conectado = true;
                            urlFinal = (puertoEscogido == 443 ? "https://" : "http://") + host + path;
                            break;
                        }
                    }
                }

                if (!conectado) {
                    SwingUtilities.invokeLater(() -> estado.setText("No se pudo acceder a la pagina"));
                    return;
                }
                final String urlHistorial = urlFinal;

                SwingUtilities.invokeLater(() -> {
                    estado.setText(clienteHTTP.getFirstLine());
                    barraNavegacion.getBarra().setText(urlHistorial);
                    String urlfnl = nuevaurl[0];
                    if (nuevaurl[0].contains("www"))
                        urlfnl = nuevaurl[1];
                    historial.agregarVisita(urlHistorial, urlfnl);
                    visorHTML.setContentType("text/html");
                    //con soporte de fotos
                    try {
                        URL urlBase = URI.create(urlHistorial).toURL();
                        visorHTML.getDocument().putProperty(Document.StreamDescriptionProperty, urlBase);
                    } catch (MalformedURLException _) {
                    }
                    HTMLEditorKit kit = new HTMLEditorKit();
                    //carga de imagenes en segundo plano
                    kit.setAutoFormSubmission(false);
                    visorHTML.setEditorKit(kit);

                    String html = clienteHTTP.getContenido();
                    if (html != null) {
                        //buscar imagenes en la misma pagina (urlHistorial)
                        if (!html.contains("<base")) {
                            html = html.replace("<head>", "<head><base href=\"" + urlHistorial + "\">");
                        }

                        //eliminar funciones javascript para facilitar carga del navegador
                        html = html.replaceAll("(?i)<script[\\s\\S]*?></script>", "");
                        html = html.replaceAll("(?i)on\\w+=\"[^\"]*\"", "");

                        visorHTML.setText(html);
                    }

                    //actualiza boton de favoritos
                    barraNavegacion.getBtnFavorito().setText(historial.esFavorito(urlHistorial) ? "★" : "☆");
                    //cambiar nombre a pestaña
                    String nombre = host;
                    if (nombre.startsWith("www.")) {
                        nombre = nombre.substring(4);
                    }
                    int punto = nombre.indexOf(".");
                    if (punto != -1) {
                        nombre = nombre.substring(0, punto);
                    }
                    JTabbedPane panel = pestana.getContenedor();
                    int index = panel.getSelectedIndex();
                    if (index != -1) {
                        panel.setTitleAt(index, nombre);
                        Component c = panel.getTabComponentAt(index);
                        if (c instanceof JPanel) {
                            for (Component child : ((JPanel) c).getComponents()) {
                                if (child instanceof JLabel) {
                                    ((JLabel) child).setText(nombre);
                                    break;
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> estado.setText(e.getMessage()));
            }
        }
    }

    public void cambiarTema(java.awt.Color fondo, String colorTexto) {
        try {
            visorHTML.setBackground(fondo);
            String fondoHex = String.format("#%02x%02x%02x", fondo.getRed(), fondo.getGreen(), fondo.getBlue());

            HTMLEditorKit kit = (HTMLEditorKit) visorHTML.getEditorKit();
            StyleSheet hojaEstilos = kit.getStyleSheet();
            String reglaFondo = "body, div, table, tr, td, span, center, form, p, input {" +
                    " background-color: " + fondoHex + " !important;" +
                    " background: " + fondoHex + " !important;" +
                    " color: " + colorTexto + " !important;" +
                    "}";

            String reglaTexto = "* { color: " + colorTexto + " !important; }";

            hojaEstilos.addRule(reglaFondo);
            hojaEstilos.addRule(reglaTexto);

            java.net.URL urlActual = visorHTML.getPage();
            String codigoHtml = visorHTML.getText();

            visorHTML.setContentType("text/html");
            visorHTML.setText(codigoHtml);

            javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument) visorHTML.getDocument();
            if (urlActual != null) {
                doc.setBase(urlActual);
            } else {
                String carpetaNativa = System.getProperty("user.dir");
                doc.setBase(new java.io.File(carpetaNativa).toURI().toURL());
            }
            visorHTML.repaint();

        } catch (Exception ex) {
            System.out.println("Error al aplicar el tema: " + ex.getMessage());
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

    public Historial getHistorial() {
        return historial;
    }
}