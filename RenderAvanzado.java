import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.File;
import java.net.*;

public class RenderAvanzado extends JPanel {
    private final JEditorPane visorHTML;
    //atributos IA
    private JPanel panelIA;
    private JTextField buscarIA;
    private boolean IAactiva = false;
    private String UltimaPag = "";
    //atributos motor
    private JPanel panelMotor;
    private JTextField buscarMotor;
    private boolean MotorActivo=false;
    private String ultimaPagMotor="";
    JScrollPane scroll;
    private final ClienteHTTP clienteHTTP;
    private final Pestana pestana;
    private final Historial historial;
    private final BarraNavegacion barraNavegacion;
    private NavegaAvanzada navegaAvanzada;
    private final NavegaOffline navegaOffline = new NavegaOffline();
    private final JPanel inferiores;

    public RenderAvanzado(JLabel estado, JTextField barra,Pestana pestana, Historial historial,BarraNavegacion barraNavegacion) {
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
        inicializarMotor();

        inferiores=new JPanel(new BorderLayout());
        inferiores.add(panelIA,BorderLayout.SOUTH);
        inferiores.add(panelMotor,BorderLayout.NORTH);
        add(inferiores,BorderLayout.SOUTH);

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
                String urlClickeada = "";

                if(Ventana.Modo()){
                    try{
                        if (e.getURL() != null) {
                            urlClickeada = e.getURL().getFile();
                            // Si la ruta es absoluta (trae C:/), extraemos solo el nombre
                            if (urlClickeada.contains("/")) {
                                urlClickeada = urlClickeada.substring(1).replace("%20"," ").replace("/","\\");
                            }
                        } else {
                            // Si el URL es nulo por el cambio de estilo, extraemos la descripción del link
                            urlClickeada = e.getDescription();
                        }

                        // Solo actualizamos la barra visualmente.
                        barra.setText(urlClickeada.replace("%20", " "));

                    }catch(Exception ex) {
                        estado.setText("Error al abrir vínculo: " + ex.getMessage());
                        return; // Cortamos la ejecución en caso de error
                    }

                } else {
                    URL urlBase = visorHTML.getPage();
                    urlClickeada = (e.getURL() != null) ? e.getURL().toString() : e.getDescription();
                    if (urlClickeada.startsWith("http://")) {
                        urlClickeada = urlClickeada.replace("http://", "");
                    } else if (urlClickeada.startsWith("https://")) {
                        urlClickeada = urlClickeada.replace("https://", "");
                    }
                }

                // El hilo universal procesará la carga (Tanto para Online como Offline)

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

        if(Ventana.Modo()) {
            String rutaLocal = nombreArchivo;

            boolean leido = navegaOffline.leerArchivoLocal(rutaLocal);

            // Extraemos el nombre del archivo para usarlo como título
            File archivo = new File(rutaLocal);
            String nombrePestana = archivo.getName().isEmpty() ? "Archivo Local" : archivo.getName();

            //Registrar visita para los botones Atrás y Adelante
            if (registrarEnHistorial && navegaAvanzada != null) {
                navegaAvanzada.registrarVisitaNueva(rutaLocal);
            }

            SwingUtilities.invokeLater(() -> {
                barraNavegacion.getBarra().setText(rutaLocal);
                estado.setText(navegaOffline.getFirstLine());

                //Guardar en el menú del Historial
                if (registrarEnHistorial && leido) {
                    historial.agregarVisita(rutaLocal, nombrePestana);
                }
            });

            // Configurar la base del documento para que busque fotos locales en la misma carpeta
            try {
                File f = new File(rutaLocal);
                if (f.exists()) {
                    visorHTML.getDocument().putProperty(javax.swing.text.Document.StreamDescriptionProperty, f.toURI().toURL());
                }
            } catch (Exception l) {
            }

            String html = navegaOffline.getContenido();
            if (html != null) {
                // Quitamos scripts pesados
                html = html.replaceAll("(?i)<script[\\s\\S]*?></script>", "");
                html = html.replaceAll("(?i)on\\w+=\"[^\"]*\"", "");
                visorHTML.setText(html);
            }

            // Actualizar la pestaña con el nombre del archivo
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
        } else {

            // MOTOR DE BUSQUEDA INTERNO
            if (nombreArchivo.startsWith("coward://search/")) {
                // Limpiamos la URL para recuperar la palabra original
                String busqueda = nombreArchivo.replace("coward://search/", "").replace("+", " ");

                // 1. Lo registramos en las pilas de Adelante/Atras
                if (registrarEnHistorial && navegaAvanzada != null) {
                    navegaAvanzada.registrarVisitaNueva(nombreArchivo);
                }

                // 2. Generamos el HTML usando la clase MotorBusqueda
                MotorBusqueda motor = new MotorBusqueda();
                String htmlResultados = "<html><body style='font-family:Segoe UI; padding:20px; font-size:14px;'>"
                        + "<h2>Resultados de Búsqueda para: <i>" + busqueda + "</i></h2><hr>"
                        + motor.buscarDatos(busqueda)
                        + "</body></html>";

                final String urlBuscadorFinal = nombreArchivo;

                //Dibujamos en pantalla y actualizamos la interfaz
                SwingUtilities.invokeLater(() -> {
                    // Usamos nuestra copia 'urlBuscadorFinal' aquí adentro
                    barraNavegacion.getBarra().setText(urlBuscadorFinal);
                    estado.setText("Búsqueda completada");
                    visorHTML.setText(htmlResultados);

                    // Lo guardamos en el menú desplegable del reloj (Historial)
                    if (registrarEnHistorial) {
                        historial.agregarVisita(urlBuscadorFinal, "Buscador: " + busqueda);
                    }

                    // Le cambiamos el nombre a la ceja de la pestaña
                    JTabbedPane panel = pestana.getContenedor();
                    int index = panel.getSelectedIndex();
                    if (index != -1) {
                        panel.setTitleAt(index, "🔎 " + busqueda);
                        Component c = panel.getTabComponentAt(index);
                        if (c instanceof JPanel) {
                            for (Component child : ((JPanel) c).getComponents()) {
                                if (child instanceof JLabel) {
                                    ((JLabel) child).setText("🔎 " + busqueda);
                                    break;
                                }
                            }
                        }
                    }
                });
                return;
            }


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
            if (btnBuscarIA.isEnabled())
                btnBuscarIA.doClick();
        });
        btnBuscarIA.addActionListener(e -> {
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

    }

    public void alternarIA() {
        if (!IAactiva) {
            UltimaPag = visorHTML.getText();
            //verificar si esta activo el panelMotor
            if (MotorActivo) {
                panelMotor.setVisible(false);
                MotorActivo = false;
            }
            barraNavegacion.getBarra().setEnabled(false);
            barraNavegacion.getBtnBuscar().setEnabled(false);
            buscarIA.setEnabled(true);
            panelIA.setVisible(true);
            buscarIA.setText("");
            buscarIA.requestFocus();
            visorHTML.setText("<html><body style='font-family:Segoe UI; padding:100px; color:gray; font-size:30px'>"
                    + "<p>🤖 Hazme una pregunta.</p></body></html>");
        } else {
            panelIA.setVisible(false);
            barraNavegacion.getBarra().setEnabled(true);
            barraNavegacion.getBtnBuscar().setEnabled(true);
            //si el historial esta vacio va a home, sino a la ultima busqueda
            if (historial.getRegistros().isEmpty()) {
                visorHTML.setText("<html><body style='text-align:center; font-family:Arial;'>"
                        + "<h1>Hola, bienvenidos a nuestro navegador</h1>"
                        + "</body></html>");
            } else {
                visorHTML.setText(UltimaPag);
            }
        }
        IAactiva = !IAactiva;
        revalidate();
        repaint();
    }

    //motor de busqueda
    public void inicializarMotor(){
        panelMotor=new JPanel(new FlowLayout(FlowLayout.LEFT));
        //campo de texto
        buscarMotor = new JTextField();
        buscarMotor.setPreferredSize(new Dimension(600, 28));
        buscarMotor.putClientProperty("placeholder", "Escribe tu pregunta...");
        buscarMotor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        //boton buscar motor
        JButton btnBuscarMotor = new JButton("⬆");
        btnBuscarMotor.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        btnBuscarMotor.setMargin(new Insets(7, 0, 0, 0));
        btnBuscarMotor.setPreferredSize(new Dimension(40,40));
        btnBuscarMotor.setBorderPainted(false);
        btnBuscarMotor.setContentAreaFilled(false);
        btnBuscarMotor.setFocusPainted(false);
        btnBuscarMotor.setToolTipText("Presione para buscar");

        btnBuscarMotor.addActionListener(e -> {
            String busqueda = buscarMotor.getText().trim();
            if (busqueda.isEmpty()) return;

            buscarMotor.setEnabled(false); // Bloqueamos temporalmente

            // 1. Creamos la nueva pestaña (Esto ahora funciona gracias al Paso 1)
            Pestana nuevaPestana = Pestana.agregarNueva(pestana.getContenedor());

            // 2. Disparamos la búsqueda hacia esa nueva pestaña en un hilo
            new Thread(() -> {
                String urlFalsa = "coward://search/" + busqueda.replace(" ", "+");
                nuevaPestana.getRenderizador().cargarURL(urlFalsa, nuevaPestana.getLblEstado());
            }).start();

            // 3. Reseteamos el buscador actual y lo cerramos
            buscarMotor.setEnabled(true);
            buscarMotor.setText("");
            alternarMotor();
        });

        buscarMotor.addActionListener(e ->{
            if (btnBuscarMotor.isEnabled())
                btnBuscarMotor.doClick();
        });

        panelMotor.add(buscarMotor);
        panelMotor.add(btnBuscarMotor);
        panelMotor.setVisible(false);
    }

    public void alternarMotor(){
        if (!MotorActivo) {
            ultimaPagMotor = visorHTML.getText();
            if (IAactiva){
                panelIA.setVisible(false);
                IAactiva=false;
            }
            barraNavegacion.getBarra().setEnabled(false);
            barraNavegacion.getBtnBuscar().setEnabled(false);
            buscarMotor.setEnabled(true);
            panelMotor.setVisible(true);
            buscarMotor.setText("");
            buscarMotor.requestFocus();
            visorHTML.setText("<html><body style='font-family:Segoe UI; padding:100px; color:gray; font-size:30px'>"
                    + "<p><img src='coward.png' width='100' height='100'>COWARD</p></body></html>");
        } else {
            panelMotor.setVisible(false);
            barraNavegacion.getBarra().setEnabled(true);
            barraNavegacion.getBtnBuscar().setEnabled(true);
            // Si historial vacío → home, sino → última página
            if (historial.getRegistros().isEmpty()) {
                visorHTML.setText("<html><body style='text-align:center; font-family:Arial;'>"
                        + "<h1>Hola, bienvenidos a nuestro navegador</h1>"
                        + "</body></html>");
            } else {
                visorHTML.setText(ultimaPagMotor);
            }
        }
        MotorActivo = !MotorActivo;
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