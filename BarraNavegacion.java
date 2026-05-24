import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class BarraNavegacion extends JPanel {
    private final JButton btnFavorito;
    private final JTextField barra;
    private final JButton btnBuscar;
    private final JButton btnColor;
    private final JButton btnTexto;
    private final JButton btnHistorial;
    private Renderizador renderizador;

    public BarraNavegacion(JLabel estado, JTabbedPane panelPestanas) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);

        //boton favoritos
        btnFavorito = new JButton("☆");
        btnFavorito.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 28));
        btnFavorito.setPreferredSize(new Dimension(30, 30));
        btnFavorito.setMargin(new Insets(0, 0, 0, 0));
        btnFavorito.setForeground(new Color(218, 165, 32));
        btnFavorito.setBorderPainted(false);
        btnFavorito.setContentAreaFilled(false);
        barra = new JTextField();
        barra.setPreferredSize(new Dimension(400, 30));
        // Aplicar a la barra de búsqueda para que no se pegue a los bordes
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), // Borde redondeado
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Espacio interno (Padding)
        ));

        JButton btnRecargar = new JButton("↺");
        btnRecargar.setPreferredSize(new Dimension(30,30));
        btnRecargar.setMargin(new Insets(0, 0, 0, 0));
        btnRecargar.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 24));
        btnRecargar.setBorderPainted(false);
        btnRecargar.setContentAreaFilled(false);
        //boton borrar historial
        btnBuscar = new JButton("Ir");
        btnBuscar.setEnabled(false);
        btnBuscar.setBackground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        // Botón de Fondo (Color)
        btnColor = new JButton("🎨");
        btnColor.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnColor.setPreferredSize(new Dimension(40, 30));
        btnColor.setMargin(new Insets(0, 0, 0, 0));
        btnColor.setBorderPainted(false);
        btnColor.setContentAreaFilled(false);
        btnColor.setFocusPainted(false);
        btnColor.setToolTipText("Cambiar tema de fondo");

        // Botón de Texto
        btnTexto = new JButton("🅰");
        btnTexto.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        btnTexto.setPreferredSize(new Dimension(40, 30));
        btnTexto.setMargin(new Insets(0, 0, 0, 0));
        btnTexto.setBorderPainted(false);
        btnTexto.setContentAreaFilled(false);
        btnTexto.setFocusPainted(false);
        btnTexto.setToolTipText("Cambiar color de texto");

        // Botón de Historial
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
        //accion de boton favoritos
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
        //boton buscar
        btnBuscar.addActionListener(e -> {
            new Thread(() ->{
                renderizador.cargarURL(barra.getText(),estado);
            }).start();
        });

        add(btnRecargar);
        add(btnFavorito);
        add(barra);
        add(btnBuscar);
        add(btnColor);
        add(btnTexto);
        add(btnHistorial);
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

    public void setRenderizador(Renderizador renderizador) {
        this.renderizador = renderizador;
    }

    public JTextField getBarra() {
        return barra;
    }
}