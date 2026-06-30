import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class BarraNavegacion extends JPanel {
    private JButton btnBuscar;
    private JButton btnColor;
    private JButton btnTexto;
    private JTabbedPane panelPestanas;

    public BarraNavegacion(Renderizador renderizador, JLabel estado, JTabbedPane panelPestanas) {
        this.panelPestanas = panelPestanas;
        setLayout(new FlowLayout(FlowLayout.LEFT));
    private final JButton btnBuscar;
    private final JButton btnAsistIA;
    private final JButton btnMotorBusqueda;
    private final JButton btnTema;
    private final JButton btnHistorial;
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

        //boton atras
        btnAtras = new JButton("◀");
        btnAtras.setPreferredSize(new Dimension(30,30));
        btnAtras.setMargin(new Insets(0, 0, 0, 0));
        btnAtras.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.setFocusPainted(false);

        //boton adelante
        btnAdelante = new JButton("▶");
        btnAdelante.setPreferredSize(new Dimension(30,30));
        btnAdelante.setMargin(new Insets(0, 0, 0, 0));
        btnAdelante.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        btnAdelante.setBorderPainted(false);
        btnAdelante.setContentAreaFilled(false);
        btnAdelante.setFocusPainted(false);
        //boton buscar
        btnBuscar = new JButton("\uD83D\uDD0E");
        //btnBuscar.setEnabled(true);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorder(new javax.swing.border.LineBorder(Color.GRAY, 1, true));
        btnBuscar.setPreferredSize(new Dimension(30,26));
        //boton cambiar tema
        btnTema = new JButton("🔆");
        btnTema.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnTema.setPreferredSize(new Dimension(40, 30));
        btnTema.setMargin(new Insets(0, 0, 0, 0));
        btnTema.setBorderPainted(false);
        btnTema.setContentAreaFilled(false);
        btnTema.setFocusPainted(false);
        btnTema.setToolTipText("Modo Oscuro / Claro");

        //boton asistenteIA
        btnAsistIA = new JButton("✨");
        btnAsistIA.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnAsistIA.setPreferredSize(new Dimension(40, 30));
        btnAsistIA.setMargin(new Insets(0, 0, 0, 0));
        btnAsistIA.setBorderPainted(false);
        btnAsistIA.setContentAreaFilled(false);
        btnAsistIA.setFocusPainted(false);
        btnAsistIA.setToolTipText("Buscar con IA");

        //boton motor de busqueda
        ImageIcon icono = new ImageIcon("coward.png");
        Image imagenRedimensionada = icono.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        ImageIcon iconoFinal = new ImageIcon(imagenRedimensionada);
        btnMotorBusqueda = new JButton(iconoFinal);
        btnMotorBusqueda.setPreferredSize(new Dimension(50,40));
        btnMotorBusqueda.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnMotorBusqueda.setPreferredSize(new Dimension(40, 30));
        btnMotorBusqueda.setMargin(new Insets(0, 0, 0, 0));
        btnMotorBusqueda.setBorderPainted(false);
        btnMotorBusqueda.setContentAreaFilled(false);
        btnMotorBusqueda.setFocusPainted(false);
        btnMotorBusqueda.setToolTipText("Ver historial de navegación");

        //boton historial
        ImageIcon icono2 = new ImageIcon("btnhistorial1.png");
        Image imagenRedimensionada2 = icono2.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
        ImageIcon iconoFinal2 = new ImageIcon(imagenRedimensionada2);


        btnHistorial = new JButton(iconoFinal2);
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
        barra.addActionListener(e ->{
            if (btnBuscar.isEnabled())
                btnBuscar.doClick();
        });

        pIzquierdo.add(btnAtras);
        pIzquierdo.add(btnAdelante);
        pIzquierdo.add(btnRecargar);
        pIzquierdo.add(btnFavorito);
        pIzquierdo.add(barra);
        pIzquierdo.add(btnBuscar);

        pDerecho.add(btnTema);
        pDerecho.add(btnAsistIA);
        pDerecho.add(btnMotorBusqueda);
        pDerecho.add(btnHistorial);

        this.add(pIzquierdo, BorderLayout.CENTER);
        this.add(pDerecho, BorderLayout.EAST);
    }

    public JButton getBtnTema() {return btnTema;}

    public JButton getBtnHistorial() {
        return btnHistorial;
    }
    public JButton getBtnFavorito() {
        return btnFavorito;
    }

    public JButton getBtnAsistIA() {
        return btnAsistIA;
    }

    public JButton getBtnMotorBusqueda() {
        return btnMotorBusqueda;
    }

    public void setRenderizador(Renderizador renderizador) {
        this.renderizador = renderizador;
    }

    public JTextField getBarra() {
        return barra;
    }

    public JButton getBtnAtras() { return btnAtras; }

    public JButton getBtnAdelante() { return btnAdelante; }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }
}