import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.net.URI;

public class Main {

    private static void aplicarEfectoHover(JButton boton, Color colorNormal, Color colorHover) {
        boton.setBackground(colorNormal);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(true); // Permite que se vea el color de fondo

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(colorHover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(colorNormal);
            }
        });
    }
    // VALIDACION DE ARCHIVO LOCAL
    private static void validarArchivoLocal(String ruta, JLabel labelEstado) {
        if (ruta == null || ruta.trim().isEmpty()) {
            labelEstado.setText("Estado: La ruta está vacía.");
            labelEstado.setForeground(Color.GRAY);
            return;
        }

        try {
            File archivo;

            // Verifica si el texto ingresado tiene formato de URL local (file://)
            if (ruta.toLowerCase().startsWith("file://")) {
                archivo = new File(new URI(ruta));
            } else {
                // Si no, lo trata como una ruta de archivo normal del sistema operativo
                archivo = new File(ruta);
            }

            // Valida que exista y que sea un archivo
            if (archivo.exists() && archivo.isFile()) {
                labelEstado.setText("Estado: Cargado");
                labelEstado.setForeground(new Color(0, 153, 0)); // Verde oscuro

                JOptionPane.showMessageDialog(null,"Este archivo es valido");

                //Aqui se puede agregar la logica para cargar o leer el archivo

            } else {
                labelEstado.setText("Cargado.");
                JOptionPane.showMessageDialog(null,"Este archivo no existe");
                labelEstado.setForeground(Color.GREEN);
            }
        } catch (Exception ex) {
            labelEstado.setText("Cargado.");
            labelEstado.setForeground(Color.GREEN);
        }
    }
    public static void createinterface() {
        JFrame frame = new JFrame("layout");
        frame.setUndecorated(true);
        //
        frame.setMinimumSize(new Dimension(400, 300));

        MouseAdapter resizer = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Component c = e.getComponent();
                if (e.getX() > c.getWidth() - 15 && e.getY() > c.getHeight() - 15) {
                    frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    frame.setCursor(Cursor.getDefaultCursor());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (frame.getCursor().getType() == Cursor.SE_RESIZE_CURSOR) {
                    Point mousePos = e.getLocationOnScreen();
                    int nuevoAncho = mousePos.x - frame.getX();
                    int nuevoAlto = mousePos.y - frame.getY();

                    if (nuevoAncho >= 400 && nuevoAlto >= 300) {
                        frame.setSize(nuevoAncho, nuevoAlto);
                    }
                }
            }
        };
        frame.addMouseListener(resizer);
        frame.addMouseMotionListener(resizer);

        //mostrar tamaño ventana
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int ancho = e.getComponent().getWidth();
                int alto = e.getComponent().getHeight();
                System.out.println("Nuevo tamaño: " + ancho + "x" + alto);

            }
        });

        JPanel top = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Mi Aplicación");
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        titulo.setForeground(Color.BLACK);
        titulo.setFont(new Font("Arial", Font.BOLD, 12));

        //botones min max cerrar
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        botones.setOpaque(false);
        Color gris = new Color(128, 128, 128);
        Color rojoCerrar = new Color(232, 17, 35);
        JButton minimizar=new JButton("—");
        JButton maximizar=new JButton("❒");
        JButton salir=new JButton("X");
        aplicarEfectoHover(minimizar, Color.white, gris);
        aplicarEfectoHover(maximizar, Color.white, gris);
        aplicarEfectoHover(salir, Color.white, rojoCerrar);
        minimizar.addActionListener(e -> frame.setExtendedState(JFrame.ICONIFIED));
        maximizar.addActionListener(e -> {
            if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) frame.setExtendedState(JFrame.NORMAL);
            else frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });


        //panel de pestañas
        JTabbedPane panelpestañas=new JTabbedPane();
        panelpestañas.addMouseListener(resizer);
        panelpestañas.addMouseMotionListener(resizer);
        panelpestañas.setBorder(BorderFactory.createEmptyBorder());
        panelpestañas.setBackground(Color.WHITE);
        panelpestañas.setFocusable(false);
        
        //agregar pestañas
        JPanel tab1=new JPanel();
        tab1.add(new JLabel("pruebas"));
        panelpestañas.add("tab1",tab1);
        panelpestañas.insertTab("+",null,null,null,0);
        panelpestañas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (panelpestañas.getSelectedIndex() == 0) {
                    String titulo = "pestaña " + panelpestañas.getTabCount();
                    JPanel contenido = new JPanel(new BorderLayout());

                    //barra busqueda
                    JTextField barra= new JTextField();
                    barra.setPreferredSize(new Dimension(400,30));
                    barra.setBackground(Color.WHITE);
                    barra.setFont(new Font("Arial", Font.BOLD, 12));

                    //boton buscar
                    JButton btbuscar= new JButton("Ir");
                    btbuscar.setEnabled(false);
                    btbuscar.setFont(new Font("Arial", Font.BOLD, 12));
                    btbuscar.setBackground(Color.WHITE);
                    //invalidar boton buscar
                    barra.getDocument().addDocumentListener(new DocumentListener() {
                        @Override
                        public void insertUpdate(DocumentEvent e) { verificar(); }
                        @Override
                        public void removeUpdate(DocumentEvent e) { verificar(); }
                        @Override
                        public void changedUpdate(DocumentEvent e) { verificar(); }

                        private void verificar() {
                            boolean tieneTexto = barra.getText().trim().length() > 0;
                            btbuscar.setEnabled(tieneTexto);
                        }
                    });
                    //Opcion emergente de pegar
                    JPopupMenu opcion = new JPopupMenu();
                    JMenuItem pegar=new JMenuItem("Pegar");
                    opcion.add(pegar);
                    pegar.addActionListener(_ -> {
                        barra.paste();
                    });
                    //panel busqueda
                    JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
                    panelBusqueda.setOpaque(false);
                    panelBusqueda.add(barra);
                    panelBusqueda.add(btbuscar);

                    contenido.setBackground(Color.white);
                    panelpestañas.addTab(titulo, contenido);

                    //
                    int index = panelpestañas.getTabCount() - 1;
                    JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                    pnl.setOpaque(false);
                    pnl.add(new JLabel(titulo));
                    
                    //boton "X"
                    JButton btnX = new JButton("x");
                    btnX.setFont(new Font("Arial", Font.BOLD, 14));
                    btnX.setForeground(Color.GRAY);
                    btnX.setBorder(null);
                    btnX.setContentAreaFilled(false);
                    btnX.addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) { btnX.setForeground(Color.RED); }
                        public void mouseExited(MouseEvent e) { btnX.setForeground(Color.GRAY); }
                    });

                    btnX.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int result = JOptionPane.showConfirmDialog(null,"seguro que quieres cerrar esta pestaña?", "advertencia",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE);
                            if (result==JOptionPane.YES_OPTION){
                                int i = panelpestañas.indexOfTabComponent(pnl);
                                if (i != -1) panelpestañas.remove(i);
                            }
                        }

                    });

                    pnl.add(btnX);
                    panelpestañas.setTabComponentAt(index, pnl);
                    panelpestañas.setSelectedIndex(index);

                    //footer
                    JPanel pie= new JPanel(new BorderLayout());
                    JLabel estado= new JLabel("Esperando búsqueda...");
                    estado.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                    estado.setForeground(Color.BLACK);
                    estado.setFont(new Font("Arial", Font.BOLD, 12));

                    pie.add(estado, BorderLayout.LINE_START);
                    contenido.add(panelBusqueda, BorderLayout.NORTH);
                    contenido.add(pie,BorderLayout.SOUTH);
                    //Listener para que aparezca la opcion
                    barra.addMouseListener(new MouseAdapter() {
                        public void mousePressed(MouseEvent e) {
                            if (e.isPopupTrigger()) {
                                opcion.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }

                        public void mouseReleased(MouseEvent e) {
                            if (e.isPopupTrigger()) {
                                opcion.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    });
                    //EVENTO DEL BOTON BUSQUEDA
                    btbuscar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {

                            String textoIngresado = barra.getText();
                            validarArchivoLocal(textoIngresado, estado);
                        }

                    });
                }
            }
        });

        //btn salir
        salir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i=panelpestañas.getTabCount()-1;
                int result = JOptionPane.showConfirmDialog(null,"hay "+i+" pestañas abiertas,quieres cerrar?", "advertencia",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result==JOptionPane.YES_OPTION){
                    System.exit(0);
                }
            }

        });
        botones.add(minimizar);
        botones.add(maximizar);
        botones.add(salir);
        top.add(titulo, BorderLayout.LINE_START);
        top.add(botones, BorderLayout.LINE_END);
        frame.add(panelpestañas);
        frame.getContentPane().add(top, BorderLayout.PAGE_START);
        frame.pack();
        frame.setSize(new Dimension(800, 600));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createinterface();
            }
        });
    }
}