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

    private void configurarMenuColores(JButton btnColor, Renderizador renderizador) {
        JPopupMenu menu = new JPopupMenu();
        String[] temas = {"Modo Oscuro", "Modo Claro", "Modo Sepia"};

        // Tus colores actuales están perfectos
        Color[] fondos = {
                new Color(45, 45, 45), // Gris oscuro
                Color.WHITE,            // Blanco
                new Color(250, 240, 230) // Sepia/Crema
        };

        String[] textos = {
                "#FFFFFF", // Blanco sobre oscuro
                "#000000", // Negro sobre claro
                "#5D4037"  // Café oscuro sobre sepia
        };

        for (int i = 0; i < temas.length; i++) {
            final int idx = i;
            JMenuItem item = new JMenuItem(temas[i]);
            item.addActionListener(e -> {
                // Llamamos al método actualizado
                renderizador.cambiarTema(fondos[idx], textos[idx]);
            });
            menu.add(item);
        }

        btnColor.addActionListener(e -> menu.show(btnColor, 0, btnColor.getHeight()));
    }

    private void configurarMenuTexto(JButton btnTexto, Renderizador renderizador) {
        JPopupMenu menu = new JPopupMenu();

        // Nombres, Colores de fondo (opcional) y Colores de texto
        String[] nombres = {"Blanco", "Negro", "Azul"};
        String[] coloresHex = {"#FFFFFF", "#000000", "#0000FF"};

        for (int i = 0; i < nombres.length; i++) {
            final int idx = i;
            JMenuItem item = new JMenuItem(nombres[i]);
            item.addActionListener(e -> {
                renderizador.cambiarColorTexto(coloresHex[idx]);
            });
            menu.add(item);
        }

        btnTexto.addActionListener(e -> menu.show(btnTexto, 0, btnTexto.getHeight()));
    }

    private void cerrarNavegador() {
        int tabs = panelPestanas.getTabCount() - 1;
        if (tabs > 0) {
            if (JOptionPane.showConfirmDialog(this, "¿Cerrar " + tabs + " pestañas abiertas?", "Salir", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        } else {
            System.exit(0);
        }
    }

    // Metodo actualizado para recibir múltiples componentes
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
                    if (w >= 400 && h >= 300) {
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