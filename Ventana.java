import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Ventana extends JFrame {
    private JTabbedPane panelPestanas;

    public Ventana() {
        setTitle("Navegador Local");
        setUndecorated(true); // Quita los bordes de Windows/macOS
        setMinimumSize(new Dimension(400, 300));

        // 1. Configuramos lo que es propio de la VENTANA
        configurarBarraSuperior();
        configurarEstructuraBase();
        configurarRedimensionamiento(this, panelPestanas);

        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void configurarBarraSuperior() {
        JPanel top = new JPanel(new BorderLayout());

        // Título e Icono (Propio de la ventana)
        ImageIcon icono = new ImageIcon("icono.jpg");
        Image imagenRedimensionada = icono.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon iconoFinal = new ImageIcon(imagenRedimensionada);
        JLabel titulo = new JLabel("Brave 2", iconoFinal, JLabel.LEFT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // PANEL DE BOTONES DE CONTROL (Min, Max, Salir)
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botones.setOpaque(false);

        JButton btnMin = crearBoton("—", new Color(128, 128, 128));
        JButton btnMax = crearBoton("❒", new Color(128, 128, 128));
        JButton btnSalir = crearBoton("X", new Color(232, 17, 35));

        // Acciones que afectan all al Jframe
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

    private void configurarEstructuraBase() {
        panelPestanas = new JTabbedPane();
        panelPestanas.addTab("+", new JPanel());

        panelPestanas.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (panelPestanas.getSelectedIndex() == panelPestanas.getTabCount() - 1) {
                    // Aquí llamamos a la otra clase
                    Pestana.agregarNueva(panelPestanas);
                }
            }
        });

        add(panelPestanas, BorderLayout.CENTER);
        Pestana.agregarNueva(panelPestanas);
    }

    private void cerrarNavegador() {
        int tabs = panelPestanas.getTabCount() - 1;
        if (tabs > 0) {
            if (JOptionPane.showConfirmDialog(this, "¿Cerrar " + tabs + " pestañas?", "Salir", JOptionPane.YES_NO_OPTION) == 0) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
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

    private void configurarRedimensionamiento(Component... componentes) {
        MouseAdapter resizer = new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                Component c = e.getComponent();
                // Verificamos si estamos en la esquina inferior derecha del componente actual
                if (e.getX() > c.getWidth() - 15 && e.getY() > c.getHeight() - 15) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
            public void mouseDragged(MouseEvent e) {
                if (getCursor().getType() == Cursor.SE_RESIZE_CURSOR) {
                    Point pos = e.getLocationOnScreen();
                    int w = pos.x - getX();
                    int h = pos.y - getY();
                    if (w >= 200 && h >= 200) {
                        setSize(w, h);
                    }
                }
            }
        };

        // Le agregamos el listener a todos los componentes que pasemos (la ventana y las pestañas)
        for (Component comp : componentes) {
            comp.addMouseListener(resizer);
            comp.addMouseMotionListener(resizer);
        }
    }
}