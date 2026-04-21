import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Ventana extends JFrame {
    private JTabbedPane panelPestanas;

    public Ventana() {
        setTitle("Navegador Local");
        setUndecorated(true);
        setMinimumSize(new Dimension(400, 300));

        configurarBarraSuperior();
        configurarPestanas(); // <- Importante: inicializar las pestañas primero

        // Pasamos tanto la ventana (this) como el panel de pestañas
        configurarRedimensionamiento(this, panelPestanas);

        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configurarBarraSuperior() {
        JPanel top = new JPanel(new BorderLayout());
        ImageIcon icono = new ImageIcon("icono.jpg");
        Image imagenRedimensionada = icono.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon iconoFinal = new ImageIcon(imagenRedimensionada);
        JLabel titulo = new JLabel("Brave 2", iconoFinal, JLabel.LEFT);
        titulo.setIconTextGap(10);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botones.setOpaque(false);

        JButton btnMin = crearBoton("—", new Color(128, 128, 128));
        JButton btnMax = crearBoton("❒", new Color(128, 128, 128));
        JButton btnSalir = crearBoton("X", new Color(232, 17, 35));

        btnMin.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        btnMax.addActionListener(e -> setExtendedState(getExtendedState() == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL : JFrame.MAXIMIZED_BOTH));
        btnSalir.addActionListener(e -> cerrarNavegador());

        botones.add(btnMin);
        botones.add(btnMax);
        botones.add(btnSalir);

        top.add(titulo, BorderLayout.LINE_START);
        top.add(botones, BorderLayout.LINE_END);
        add(top, BorderLayout.PAGE_START);
    }

    private JButton crearBoton(String texto, Color hover) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }

    private void configurarPestanas() {
        panelPestanas = new JTabbedPane();
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

        add(panelPestanas, BorderLayout.CENTER);
        agregarNuevaPestana(); // Agregar la primera pestaña por defecto
    }

    
}