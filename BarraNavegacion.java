import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class BarraNavegacion extends JPanel {
    private JTextField barra;
    private JButton btnBuscar;
    private JButton btnColor;
    private JButton btnTexto;
    private JTabbedPane panelPestanas;

    public BarraNavegacion(Renderizador renderizador, JLabel estado, JTabbedPane panelPestanas) {
        this.panelPestanas = panelPestanas;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setOpaque(false);

        barra = new JTextField();
        barra.setPreferredSize(new Dimension(400, 30));
        // Aplicar a la barra de búsqueda para que no se pegue a los bordes
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), // Borde redondeado
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Espacio interno (Padding)
        ));

        btnBuscar = new JButton("Ir");
        btnBuscar.setEnabled(false);

        btnColor = new JButton("Fondo");
        btnTexto = new JButton("Texto");

        // Activar/Desactivar botón
        barra.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { verificar(); }
            public void removeUpdate(DocumentEvent e) { verificar(); }
            public void changedUpdate(DocumentEvent e) { verificar(); }
            private void verificar() {
                btnBuscar.setEnabled(barra.getText().trim().length() > 0);
            }
        });

        // Menú pegar
        JPopupMenu opcion = new JPopupMenu();
        JMenuItem pegar = new JMenuItem("Pegar");
        pegar.addActionListener(e -> barra.paste());
        opcion.add(pegar);
        barra.setComponentPopupMenu(opcion);

        btnBuscar.addActionListener(e -> {
            String ruta = barra.getText();
            renderizador.cargarURL(ruta, estado);

            // 1. Obtener nombre del archivo
            java.io.File archivo = new java.io.File(ruta);
            String nombre = archivo.getName();

            // 2. Quitar extensión
            int punto = nombre.lastIndexOf('.');
            if (punto != -1) {
                nombre = nombre.substring(0, punto);
            }

            // 3. Aplicar a la pestaña usando la referencia guardada
            int index = panelPestanas.getSelectedIndex();
            if (index != -1 && !nombre.isEmpty()) {
                // Obtenemos el componente del header (el JLabel que está dentro del pnlHeader)
                Component c = panelPestanas.getTabComponentAt(index);
                if (c instanceof JPanel) {
                    JPanel pnl = (JPanel) c;
                    for (Component child : pnl.getComponents()) {
                        if (child instanceof JLabel) {
                            ((JLabel) child).setText(nombre);
                            break;
                        }
                    }
                }
                // También cambiamos el título interno por seguridad
                panelPestanas.setTitleAt(index, nombre);
            }
        });

        add(barra);
        add(btnBuscar);
        add(btnColor);
        add(btnTexto);
    }

    public JButton getBtnColor() {
        return btnColor;
    }
    public JButton getBtnTexto() {
        return btnTexto;
    }
}