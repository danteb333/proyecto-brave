import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.File;
import java.net.*;

public class Renderizador extends JPanel {
    private final JEditorPane visorHTML;
    //atributos IA
    private JPanel panelIA;
    private JTextField buscarIA;
    private boolean IAactiva = false;
    private String UltimaPag = "";
    //
    JScrollPane scroll;
    private final ClienteHTTP clienteHTTP;
    private final Pestana pestana;
    private final Historial historial;
    private final BarraNavegacion barraNavegacion;
    private NavegaAvanzada navegaAvanzada;
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
        inicializarPanelIA();

        scroll = new JScrollPane(visorHTML);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    public void setNavegaAvanzada(NavegaAvanzada navegaAvanzada) {
        this.navegaAvanzada = navegaAvanzada;
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

    public void cargarURL(String nombreArchivo, JLabel estado, boolean registrarEnHistorial) {

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

        }else {

            if (!nombreArchivo.startsWith("http://") && !nombreArchivo.startsWith("https://")) {
                nombreArchivo = "http://" + nombreArchivo;
            }

            String urlFinal = nombreArchivo;
            try {
                URL url = URI.create(nombreArchivo).toURL();
                String host = url.getHost();
                String path = (url.getPath() == null || url.getPath().isEmpty()) ? "/" : url.getPath();

                int puertoIngresado = url.getPort();
                int[] puertos = (puertoIngresado != -1) ? new int[]{puertoIngresado} : new int[]{80, 443, 3000};

                boolean conectado = false;

                for (int puertoEscogido : puertos) {
                    if (clienteHTTP.conectar(host, estado, path, puertoEscogido)) {
                        String status = clienteHTTP.getFirstLine();

                        if (status == null || status.isEmpty()) continue;

                        if (puertoEscogido == 80 && (status.contains("301") || status.contains("302"))) {
                            continue;
                        }
                        if (status.contains("200") || status.contains("301") || status.contains("302")) {
                            conectado = true;
                            String protocolo = (puertoEscogido == 443) ? "https://" : (puertoEscogido == 80) ? "http://" : "http://";
                            String stringPuerto = (puertoEscogido != 80 && puertoEscogido != 443) ? (":" + puertoEscogido) : "";
                            urlFinal = protocolo + host + stringPuerto + path;
                            break;
                        }
                    }
                }

                if (!conectado) {
                    SwingUtilities.invokeLater(() -> estado.setText("No se pudo acceder a la página"));
                    return;
                }

                //cambio nombre para historial en caso de ser ip o dominio
                String hostResuelto = host;
                try {
                    //convertir la IP a su nombre de dominio real
                    InetAddress inetAddr = InetAddress.getByName(host);
                    hostResuelto = inetAddr.getHostName();
                } catch (UnknownHostException _) {
                }

                final String urlHistorial = urlFinal.replaceFirst(host, hostResuelto);
                final String hostVisual = hostResuelto;

                if (registrarEnHistorial && navegaAvanzada != null) {
                    navegaAvanzada.registrarVisitaNueva(urlHistorial);
                }

                SwingUtilities.invokeLater(() -> {
                    estado.setText(clienteHTTP.getFirstLine());
                    barraNavegacion.getBarra().setText(urlHistorial);
                    //mejora para ip
                    String urlfnl = hostVisual;
                    if (!esIP(hostVisual)) {
                        if (hostVisual.contains("www.")) {
                            urlfnl = hostVisual.replace("www.", "");
                        } else if (hostVisual.split("\\.").length > 2) {
                            urlfnl = hostVisual.split("\\.")[1];
                        }
                    }

                    if (registrarEnHistorial) {
                        historial.agregarVisita(urlHistorial, urlfnl);
                    }
                    //historial.agregarVisita(urlHistorial,urlfnl);
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

                        html = filtrarYLimpiarHTML(html);
                        html = procesarEtiquetasNoSoportadas(html);
                        //eliminar funciones javascript para facilitar carga del navegador
                    /*html = html.replaceAll("(?i)<script[\\s\\S]*?></script>", "");
                    html = html.replaceAll("(?i)on\\w+=\"[^\"]*\"", "");*/

                        visorHTML.setText(html);
                    }

                    //actualiza boton de favoritos
                    barraNavegacion.getBtnFavorito().setText(historial.esFavorito(urlHistorial) ? "★" : "☆");
                    //cambiar nombre a pestaña
                    String nombre;
                    if (esIP(hostVisual)) {
                        nombre = hostVisual;
                    } else {
                        nombre = hostVisual.replace("www.", "");
                        int punto = nombre.indexOf(".");
                        if (punto != -1) {
                            nombre = nombre.substring(0, punto);
                        }
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

    public void cargarURL(String nombreArchivo, JLabel estado) {
        cargarURL(nombreArchivo, estado, true);
    }

    private String filtrarYLimpiarHTML(String htmlOriginal) {
        if (htmlOriginal == null) return "";
        String htmlFiltrado = htmlOriginal;

        htmlFiltrado = htmlFiltrado.replaceAll("(?i)<script[\\s\\S]*?></script>", "");
        htmlFiltrado = htmlFiltrado.replaceAll("(?i)on\\w+=\"[^\"]*\"", "");
        htmlFiltrado = htmlFiltrado.replaceAll("(?i)<head>[\\s\\S]*?</head>", "");
        htmlFiltrado = htmlFiltrado.replaceAll("(?i)<meta[^>]*>", "");

        return htmlFiltrado;
    }

    private String procesarEtiquetasNoSoportadas(String html) {
        if (html == null) return "";

        String[] etiquetasIncompatibles = {"video", "audio", "canvas", "iframe", "svg"};

        for (String etiqueta : etiquetasIncompatibles) {
            String regex = "(?i)<" + etiqueta + "[^>]*>[\\s\\S]*?</" + etiqueta + ">|<" + etiqueta + "[^>]*/>";
            String mensajeError = "<b style='color: red; font-family: Arial;'>"
                    + "[Este elemento no se puede renderizar - " + etiqueta.toUpperCase() + "]"
                    + "</b>";
            html = html.replaceAll(regex, mensajeError);
        }

        return html;
    }
    //metodos IA
    private void inicializarPanelIA() {
        final boolean[] altbtn = {false};
        //panel de contenido IA
        panelIA=new JPanel(new FlowLayout(FlowLayout.LEFT));
        //campo de texto
        buscarIA = new JTextField();
        buscarIA.setPreferredSize(new Dimension(600, 28));
        buscarIA.putClientProperty("placeholder", "Escribe tu pregunta...");
        buscarIA.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        //boton buscar IA
        JButton btnBuscarIA = new JButton("⬆");
        btnBuscarIA.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        btnBuscarIA.setMargin(new Insets(7, 0, 0, 0));
        btnBuscarIA.setPreferredSize(new Dimension(40,40));
        btnBuscarIA.setBorderPainted(false);
        btnBuscarIA.setContentAreaFilled(false);
        btnBuscarIA.setFocusPainted(false);
        btnBuscarIA.setToolTipText("Presione para buscar");

        buscarIA.addActionListener(e -> {
            if (altbtn[0])
                return;
            else altbtn[0] =true;
            String pregunta=buscarIA.getText().trim();
            if (pregunta.isEmpty())
                return;

            buscarIA.setEnabled(false);
            new Thread(() -> {
                try {
                    String respuesta = AsistenteIA.callGeminiAPI(pregunta);
                    // Convierte saltos de línea a HTML
                    String html = "<html><body style='font-family:Segoe UI; padding:20px; font-size:14px; background-color: lightblue'>"
                            + "<b>Pregunta:</b> " + pregunta + "<br><br>"
                            + "<b>Respuesta:</b><br><br>"
                            + respuesta.replace("\n", "<br>")
                            + "</body></html>";
                    SwingUtilities.invokeLater(() -> visorHTML.setText(html));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            visorHTML.setText("<html><body style='padding:20px; color:red'>"
                                    + "Error: " + ex.getMessage() + "</body></html>")
                    );
                }
            }).start();
        });
        btnBuscarIA.addActionListener(e -> {
            if (altbtn[0])
                return;
            else altbtn[0] =true;
            String pregunta=buscarIA.getText().trim();
            if (pregunta.isEmpty())
                return;
            buscarIA.setEnabled(false);
            new Thread(() -> {
                try {
                    String respuesta = AsistenteIA.callGeminiAPI(pregunta);
                    // Convierte saltos de línea a HTML
                    String html = "<html><body style='font-family:Segoe UI; padding:20px; font-size:14px; background-color: lightblue'>"
                            + "<b>Pregunta:</b> " + pregunta + "<br><br>"
                            + "<b>Respuesta:</b><br><br>"
                            + respuesta.replace("\n", "<br>")
                            + "</body></html>";
                    SwingUtilities.invokeLater(() -> visorHTML.setText(html));
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            visorHTML.setText("<html><body style='padding:20px; color:red'>"
                                    + "Error: " + ex.getMessage() + "</body></html>")
                    );
                }
            }).start();
        });


        panelIA.add(buscarIA);
        panelIA.add(btnBuscarIA);
        panelIA.setVisible(false);
        add(panelIA,BorderLayout.SOUTH);

    }
    public void alternarIA() {
        if (!IAactiva) {
            // Guarda el HTML actual para restaurarlo al salir
            UltimaPag = visorHTML.getText();
            barraNavegacion.getBarra().setEnabled(false);
            panelIA.setVisible(true);
            buscarIA.setText("");
            buscarIA.requestFocus();
            visorHTML.setText("<html><body style='font-family:Segoe UI; padding:100px; color:gray; font-size:30px'>"
                    + "<p>🤖 Hazme una pregunta.</p></body></html>");
        } else {
            panelIA.setVisible(false);
            barraNavegacion.getBarra().setEnabled(true);
            visorHTML.setText(UltimaPag); // restaura la última página
        }
        IAactiva = !IAactiva;
        revalidate();
        repaint();
    }

    public void cambiarTema(Color fondo, String colorTexto) {
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

            URL urlActual = visorHTML.getPage();
            String codigoHtml = visorHTML.getText();

            visorHTML.setContentType("text/html");
            visorHTML.setText(codigoHtml);

            HTMLDocument doc = (HTMLDocument) visorHTML.getDocument();
            if (urlActual != null) {
                doc.setBase(urlActual);
            } else {
                String carpetaNativa = System.getProperty("user.dir");
                doc.setBase(new File(carpetaNativa).toURI().toURL());
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

    private boolean esIP(String host) {
        return host.matches("(\\d{1,3}\\.){3}\\d{1,3}");
    }
}