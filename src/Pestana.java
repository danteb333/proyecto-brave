import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class Pestana extends JPanel {
    private final String titulo;
    private final JTabbedPane contenedor;
    private final JLabel lblEstado = new JLabel("");
    private boolean esModoOscuro = false;
    private RenderAvanzado renderizador;

    // El constructor configura el contenido visual de la pestaña
    public Pestana(String titulo, JTabbedPane contenedor) {
        this.titulo = titulo;
        this.contenedor = contenedor;
        this.setLayout(new BorderLayout());

        // 1. Inicializar componentes internos
        lblEstado.setText("Esperando búsqueda...");
        JTextField barraFalsa = new JTextField(); // Referencia necesaria para el Renderizador

        Historial historial=new Historial();
        BarraNavegacion barraNavegacion = new BarraNavegacion(lblEstado, contenedor);
        this.renderizador = new RenderAvanzado(lblEstado, barraFalsa, this, historial, barraNavegacion);
        barraNavegacion.setRenderizador(renderizador);
        NavegaAvanzada navegaAvanzada = new NavegaAvanzada(barraNavegacion.getBtnAtras(), barraNavegacion.getBtnAdelante());
        renderizador.setNavegaAvanzada(navegaAvanzada);

        // 2. Configurar la lógica de los botones de la barra de navegación
        configurarBotonTema(barraNavegacion.getBtnTema(), renderizador);
        configurarMenuHistorial(barraNavegacion.getBtnHistorial(),renderizador,historial,barraNavegacion);

        //accion boton IA
        barraNavegacion.getBtnAsistIA().addActionListener(e -> renderizador.alternarIA());
        barraNavegacion.getBtnMotorBusqueda().addActionListener(e -> renderizador.alternarMotor());

        //accion de boton atrás
        barraNavegacion.getBtnAtras().addActionListener(e -> {
            String urlPrevia = navegaAvanzada.irAtras();
            if (urlPrevia != null) {
                new Thread(() -> {
                    renderizador.cargarURL(urlPrevia, lblEstado, false);
                }).start();
            }
        });
        //accion de boton adelante
        barraNavegacion.getBtnAdelante().addActionListener(e -> {
            String urlSiguiente = navegaAvanzada.irAdelante();
            if (urlSiguiente != null) {
                new Thread(() -> {
                    renderizador.cargarURL(urlSiguiente, lblEstado, false);
                }).start();
            }
        });

        // 3. Ensamblar el layout interno
        JPanel pie = new JPanel(new BorderLayout());
        pie.add(lblEstado, BorderLayout.LINE_START);

        this.add(barraNavegacion, BorderLayout.NORTH);
        this.add(renderizador, BorderLayout.CENTER);
        this.add(pie, BorderLayout.SOUTH);
    }

    public static Pestana agregarNueva(JTabbedPane panel) {
        int index = panel.getTabCount() - 1; // Posición antes del botón "+"
        String nombrePestana = "Nueva Pestaña";

        // Creamos la instancia de esta clase
        Pestana contenido = new Pestana(nombrePestana, panel);

        // La insertamos en el panel de pestañas
        panel.insertTab(null, null, contenido, null, index);

        // Le ponemos su cabecera personalizada (Título + Botón X)
        panel.setTabComponentAt(index, contenido.crearCabecera(panel));

        // La seleccionamos automáticamente
        panel.setSelectedIndex(index);

        return contenido;
    }

    /**
     * Crea el pequeño panel que va en la "ceja" de la pestaña
     */
    private JPanel crearCabecera(JTabbedPane panel) {
        JPanel pnlHeader = new JPanel(new BorderLayout(5, 0));
        pnlHeader.setOpaque(false);

        JLabel lbl = new JLabel(this.titulo);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnX = new JButton("X");
        btnX.setFont(new Font("Arial", Font.BOLD, 12));
        btnX.setMargin(new Insets(0, 0, 0, 0));
        btnX.setBorderPainted(false);
        btnX.setContentAreaFilled(false);
        btnX.setFocusable(false);

        // ACCIÓN: Cerrar solo ESTA pestaña
        //btnX.addActionListener(e -> contenedor.remove(this));
        btnX.addActionListener(e ->{
            if (panel.indexOfTabComponent(pnlHeader) < panel.getTabCount()-2){
                panel.remove(this);
            } else {
                JOptionPane.showMessageDialog(null,"La ultima pestaña no se puede cerrar",
                        "Advertencia",JOptionPane.WARNING_MESSAGE);
            }
        });

        // Efecto hover para la X de la pestaña
        btnX.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnX.setForeground(Color.RED); }
            public void mouseExited(MouseEvent e) { btnX.setForeground(Color.BLACK); }
        });

        pnlHeader.add(lbl, BorderLayout.CENTER);
        pnlHeader.add(btnX, BorderLayout.EAST);

        return pnlHeader;
    }

    // --- MÉTODOS DE CONFIGURACIÓN DE MENÚS

    private void configurarBotonTema(JButton btnTema, RenderAvanzado renderizador) {
        btnTema.addActionListener(e -> {
            esModoOscuro = !esModoOscuro;
            if (esModoOscuro) {
                btnTema.setText("🌙");
                btnTema.setToolTipText("Cambiar a Modo Claro");
                renderizador.cambiarTema(new Color(45, 45, 45), "#FFFFFF");
            } else {
                btnTema.setText("🔆");
                btnTema.setToolTipText("Cambiar a Modo Oscuro");
                renderizador.cambiarTema(Color.WHITE, "#000000");
            }
        });
    }




    public void configurarMenuHistorial(JButton btnHistorial,RenderAvanzado renderizador,Historial historial,BarraNavegacion barraNavegacion){
        JPopupMenu menu = new JPopupMenu();
        btnHistorial.addActionListener(e -> {
            menu.removeAll();
            JMenuItem borrar=new JMenuItem("\uD83D\uDDD1 Limpiar Historial");
            borrar.addActionListener(a -> {
                historial.limpiarHistorial();
                menu.setVisible(false);
            });
            menu.add(borrar);
            menu.addSeparator();
            LinkedList<String> registros=historial.getRegistros();
            if (registros.isEmpty()){
                menu.add("Sin historial");
            } else{
                for (String url : registros){
                    //vemos si es favorito
                    String urlPura = url.substring(url.indexOf("- ") + 2);

                    String textoItem = (historial.esFavorito(urlPura) ? "★ " : "") + url;
                    JMenuItem item=new JMenuItem(textoItem);
                    item.addActionListener(i -> {
                        new Thread(() ->{

                            renderizador.cargarURL(urlPura, lblEstado);
                        }).start();
                    });
                    menu.add(item);
                }
            }
            menu.pack();
            int posx = btnHistorial.getWidth() - menu.getPreferredSize().width;
            menu.show(btnHistorial, posx, btnHistorial.getHeight());
        });
    }

    public JTabbedPane getContenedor() {return contenedor;}
    public RenderAvanzado getRenderizador() { return renderizador; }
    public JLabel getLblEstado() { return lblEstado; }
}
