import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class BarraNavegacion extends JPanel {
    private final JButton btnOnline;
    private final JButton btnFavorito;
    private final JTextField barra;
    private final JButton btnBuscar;
    private final JButton btnColor;
    private final JButton btnTexto;
    private final JButton btnHistorial;


    public BarraNavegacion(Renderizador renderizador, JLabel estado, JTabbedPane panelPestanas) {
        this.panelPestanas = panelPestanas;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);

        //Boton para modo Online/Offline
        btnOnline = new JButton("●");
        btnOnline.setForeground(new Color(0, 204, 0));
        btnOnline.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        btnOnline.setPreferredSize(new Dimension(30, 30));
        btnOnline.setMargin(new Insets(0,0,0,0));
        btnOnline.setBorderPainted(false);
        btnOnline.setFocusPainted(false);
        btnOnline.setContentAreaFilled(false);
        btnOnline.setToolTipText("Modo Online");

        btnOnline.addActionListener(e ->{
            Offline off = renderizador.getOffline();
            boolean nuevoEstado = !off.isModoOnline();
            off.setModoOnline(nuevoEstado);

            if(nuevoEstado){
                btnOnline.setText("●");
                btnOnline.setForeground(new Color(0, 204, 0));
                btnOnline.setToolTipText("Modo Actual: Online");
                estado.setText("Conectado a la red.");
            }else{
                btnOnline.setText("●");
                btnOnline.setForeground(new Color(255, 0, 0));
                btnOnline.setToolTipText("Modo Actual: Off");
                estado.setText("Desconectado de la red.");
            }
        });

    private final JButton btnAtras;
    private final JButton btnAdelante;

    private Renderizador renderizador;


    public BarraNavegacion(JLabel estado, JTabbedPane panelPestanas) {
        this.setLayout(new BorderLayout(5, 0));
        //setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);
        JPanel pIzquierdo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        JPanel pDerecho = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));

        //boton favoritos
        btnFavorito = new JButton("☆");
        btnFavorito.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        btnFavorito.setPreferredSize(new Dimension(30, 30));
        btnFavorito.setMargin(new Insets(0, 0, 0, 0));
        btnFavorito.setForeground(new Color(218, 165, 32));
        btnFavorito.setBorderPainted(false);
        btnFavorito.setFocusPainted(false);
        btnFavorito.setContentAreaFilled(false);

        barra = new JTextField();
        barra.setPreferredSize(new Dimension(400, 30));
        // Aplicar a la barra de búsqueda para que no se pegue a los bordes
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), // Borde redondeado
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Espacio interno (Padding)
        ));
        //boton recargar
        JButton btnRecargar = new JButton("↺");
        btnRecargar.setPreferredSize(new Dimension(30,30));
        btnRecargar.setMargin(new Insets(0, 0, 0, 0));
        btnRecargar.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnRecargar.setBorderPainted(false);
        btnRecargar.setFocusPainted(false);
        btnRecargar.setContentAreaFilled(false);

<<<<<<< Updated upstream
=======
        //boton borrar historial

        btnBuscar = new JButton("Ir");
        btnBuscar.setEnabled(false);

        btnColor = new JButton("Fondo");
        btnTexto = new JButton("Texto");


        //boton atras
        btnAtras = new JButton("◀");
        btnAtras.setPreferredSize(new Dimension(30,30));
        btnAtras.setMargin(new Insets(0, 0, 0, 0));
        btnAtras.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnAtras.setBorderPainted(false);
        btnAtras.setFocusPainted(false);
        btnAtras.setContentAreaFilled(false);

        //boton adelante
        btnAdelante = new JButton("▶");
        btnAdelante.setPreferredSize(new Dimension(30,30));
        btnAdelante.setMargin(new Insets(0, 0, 0, 0));
        btnAdelante.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnAdelante.setBorderPainted(false);
        btnAdelante.setFocusPainted(false);
        btnAdelante.setContentAreaFilled(false);

>>>>>>> Stashed changes
        //boton borrar historial

        btnBuscar = new JButton("Ir");
        btnBuscar.setEnabled(false);

        btnColor = new JButton("Fondo");
        btnTexto = new JButton("Texto");


        //boton atras
        btnAtras = new JButton("◀");
        btnAtras.setPreferredSize(new Dimension(30,30));
        btnAtras.setMargin(new Insets(0, 0, 0, 0));
        btnAtras.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnAtras.setBorderPainted(false);
        btnAtras.setFocusPainted(false);
        btnAtras.setContentAreaFilled(false);

        //boton adelante
        btnAdelante = new JButton("▶");
        btnAdelante.setPreferredSize(new Dimension(30,30));
        btnAdelante.setMargin(new Insets(0, 0, 0, 0));
        btnAdelante.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnAdelante.setBorderPainted(false);
        btnAdelante.setFocusPainted(false);
        btnAdelante.setContentAreaFilled(false);

        //boton borrar historial
        btnBuscar = new JButton("\uD83D\uDD0E");
        btnBuscar.setEnabled(false);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorder(new javax.swing.border.LineBorder(Color.GRAY, 1, true));
        btnBuscar.setPreferredSize(new Dimension(30,26));

        btnColor = new JButton("🎨");
        btnColor.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnColor.setPreferredSize(new Dimension(40, 30));
        btnColor.setMargin(new Insets(0, 0, 0, 0));
        btnColor.setBorderPainted(false);
        btnColor.setContentAreaFilled(false);
        btnColor.setFocusPainted(false);
        btnColor.setToolTipText("Cambiar tema de fondo");
        btnColor.setFocusPainted(false);

        btnTexto = new JButton("🅰");
        btnTexto.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnTexto.setPreferredSize(new Dimension(40, 30));
        btnTexto.setMargin(new Insets(0, 0, 0, 0));
        btnTexto.setBorderPainted(false);
        btnTexto.setContentAreaFilled(false);
        btnTexto.setFocusPainted(false);
        btnTexto.setToolTipText("Cambiar color de texto");

        btnHistorial = new JButton("📋");
        btnHistorial.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnHistorial.setPreferredSize(new Dimension(40, 30));
        btnHistorial.setMargin(new Insets(0, 0, 0, 0));
        btnHistorial.setBorderPainted(false);
        btnHistorial.setContentAreaFilled(false);
        btnHistorial.setFocusPainted(false);
        btnHistorial.setToolTipText("Ver historial de navegación");


        // Activar/Desactivar botón
        barra.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { verificar(); }
            public void removeUpdate(DocumentEvent e) { verificar(); }
            public void changedUpdate(DocumentEvent e) { verificar(); }
            private void verificar() {
                btnBuscar.setEnabled(!barra.getText().trim().isEmpty());
            }
        });

        // Menú pegar
        JPopupMenu opcion = new JPopupMenu();
        JMenuItem pegar = new JMenuItem("Pegar");
        pegar.addActionListener(e -> barra.paste());
        opcion.add(pegar);
        barra.setComponentPopupMenu(opcion);

        btnRecargar.addActionListener(e -> {
            new Thread(() ->{
                String[] separarurl=renderizador.getHistorial().getRegistros().getFirst().split("- ");
                String historialurl=separarurl[1];
                renderizador.cargarURL(historialurl,estado);
            }).start();
        });



        btnFavorito.addActionListener(e -> {
            String textoBarra = barra.getText().trim();
            if (textoBarra.isEmpty()) return;
            if (!textoBarra.startsWith("http://") && !textoBarra.startsWith("https://")) {
                textoBarra = "http://" + textoBarra;
            }

            try {
                String url = java.net.URI.create(textoBarra).toURL().toString();
                renderizador.getHistorial().alternarFavorito(url);
                boolean esFav = renderizador.getHistorial().esFavorito(url);
                btnFavorito.setText(esFav ? "★" : "☆");

            } catch (Exception _) {
            }
        });
        btnBuscar.addActionListener(e -> {
            new Thread(() ->{
                renderizador.cargarURL(barra.getText(),estado);
            }).start();
        });


        pIzquierdo.add(btnAtras);
        pIzquierdo.add(btnAdelante);
        pIzquierdo.add(btnRecargar);
        pIzquierdo.add(btnFavorito);
        pIzquierdo.add(barra);
        pIzquierdo.add(btnBuscar);

        pDerecho.add(btnColor);
        pDerecho.add(btnTexto);
        pDerecho.add(btnHistorial);

        this.add(pIzquierdo, BorderLayout.CENTER);
        this.add(pDerecho, BorderLayout.EAST);
        //add(javax.swing.Box.createHorizontalGlue());

    }

    public JButton getBtnColor() {
        return btnColor;
    }
    public JButton getBtnTexto() {
        return btnTexto;
    }
    public JButton getBtnHistorial() {
        return btnHistorial;
    }
    public JButton getBtnFavorito() {
        return btnFavorito;
    }
    public JButton getBtnAtras() {return btnAtras;}
    public JButton getBtnAdelante() {return btnAdelante;}

    public void setRenderizador(Renderizador renderizador) {
        this.renderizador = renderizador;
    }

    public JTextField getBarra() {
        return barra;
    }
}