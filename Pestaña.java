import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Pestaña {

    public Pestaña(){
        configurarPestanas();
    }

    private void configurarPestanas() {
        JTabbedPane panelPestanas = new JTabbedPane();
        panelPestanas.setBackground(Color.WHITE);

        JPanel btnNuevaPestana = new JPanel();
        panelPestanas.addTab("+", btnNuevaPestana);

        panelPestanas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (panelPestanas.getSelectedIndex() == panelPestanas.getTabCount() - 1) {
                    agregarNuevaPestana();
                }
            }
        });

        panelPestanas.add(panelPestanas, BorderLayout.CENTER);
        agregarNuevaPestana(); // Agregar la primera pestaña por defecto
    }

    private void agregarNuevaPestana() {
        int index = panelPestanas.getTabCount() - 1;
        String titulo = "Pestaña nueva";

        JPanel contenido = new JPanel(new BorderLayout());
        JLabel estado = new JLabel("Esperando búsqueda...");

        // Aquí instanciamos nuestras nuevas clases
        JTextField barraFalsa = new JTextField(); // Usado solo para referencia cruzada
        Renderizador renderizador = new Renderizador(estado, barraFalsa);
        BarraNavegacion barraNavegacion = new BarraNavegacion(renderizador, estado, panelPestanas);

        configurarMenuColores(barraNavegacion.getBtnColor(), renderizador);
        configurarMenuTexto(barraNavegacion.getBtnTexto(), renderizador);

        JPanel pie = new JPanel(new BorderLayout());
        pie.add(estado, BorderLayout.LINE_START);

        contenido.add(barraNavegacion, BorderLayout.NORTH);
        contenido.add(renderizador, BorderLayout.CENTER);
        contenido.add(pie, BorderLayout.SOUTH);

        panelPestanas.insertTab(titulo, null, contenido, null, index);

        // Custom Tab Header con botón cerrar
        JPanel pnlHeader = new JPanel(new BorderLayout(5, 0));
        pnlHeader.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pnlHeader.add(lblTitulo, BorderLayout.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        lblTitulo.setMaximumSize(new Dimension(80, 20));
        JButton btnX = new JButton("X");
        btnX.setFont(new Font("Arial", Font.BOLD, 14));
        btnX.setPreferredSize(new Dimension(15, 25));
        btnX.setMargin(new Insets(0, 0, 0, 0));
        btnX.setBorderPainted(false);
        btnX.setContentAreaFilled(false);
        btnX.addActionListener(e -> panelPestanas.remove(contenido));
        pnlHeader.add(btnX, BorderLayout.EAST);
        pnlHeader.setPreferredSize(new Dimension(110, 25));
        pnlHeader.setMaximumSize(new Dimension(110, 25));

        panelPestanas.setTabComponentAt(index, pnlHeader);
        panelPestanas.setSelectedIndex(index);
    }
}
