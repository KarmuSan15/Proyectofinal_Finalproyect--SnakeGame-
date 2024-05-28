package PROYECT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BackgroundImageFrame extends JFrame {/*añadir la lectura de un archivo txt*/
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Ruta a la imagen de fondo
    private static final String BACKGROUND_IMAGE_FILE = "F:\\CURSO\\PROYECTO\\FONDOO.jpg";

    public BackgroundImageFrame() {
    	
    	//lee archivo txt
        String saludoText = "";
        try (BufferedReader br = new BufferedReader(new FileReader("F:\\CURSO\\PROYECTO\\saludo.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                saludoText += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // dialogo
        JFrame ventana = new JFrame();
        
        JOptionPane.showMessageDialog(ventana, "Perdon por el susto, no te preocupes no soy un virus.");
        
        String userName = JOptionPane.showInputDialog(ventana, saludoText + "\n¿Como te llamas?");

        // ingreso nombre
      
        
        JOptionPane.showMessageDialog(ventana, "Hola, " + userName + " encantado de conocerte!");
        
        JOptionPane.showMessageDialog(ventana, "Espero que te guste bienvenid@ a 'EL JUEGO', " + userName + " diviertete!");
        
        JOptionPane.showMessageDialog(ventana, "Ahora aparecera el inicio, Adios nos vemos," + userName);
       
    	// Configuración del título, cierre y tamaño de la ventana
        setTitle("EL JUEGO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);

        // Creación e inserción de la imagen de fondo
        ImageIcon backgroundImageIcon = new ImageIcon(BACKGROUND_IMAGE_FILE);
        Image backgroundImage = backgroundImageIcon.getImage().getScaledInstance(900, 600, Image.SCALE_SMOOTH);
        ImageIcon scaledBackgroundImageIcon = new ImageIcon(backgroundImage);
        JLabel backgroundLabel = new JLabel(scaledBackgroundImageIcon);
        backgroundLabel.setLayout(new GridBagLayout());
        getContentPane().add(backgroundLabel, BorderLayout.CENTER);

        // Creación e inserción del panel de botones
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        ImageButtonLauncher imageButtonLauncher = new ImageButtonLauncher();
        panelBotones.add(imageButtonLauncher);

        // Añadir el panel de botones a la ventana
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        backgroundLabel.add(panelBotones, constraints);

        // Posicionamiento y visualización de la ventana
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new BackgroundImageFrame();
    }

    // Clase interna para los botones con imagen y funcionalidad
    static class ImageButtonLauncher extends JPanel {
    	
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Rutas a las imágenes de los botones y las URL correspondientes
        private static final String IMAGE1_FILE ="F:\\CURSO\\PROYECTO\\play.png";
     /*   private static final String IMAGE2_FILE ="F:\\CURSO\\PROYECTO\\setting.png";  */
        private static final String IMAGE3_FILE =  "F:\\CURSO\\PROYECTO\\record.png";

        private JButton button1;
      /*  private JButton button2;*/
        private JButton button3;

        public ImageButtonLauncher() {
            setLayout(new GridBagLayout());
            setOpaque(false);

            // Creación e inserción de los botones con imagen
            button1 = new JButton(new ImageIcon(IMAGE1_FILE));
            button1.setPreferredSize(new Dimension(298, 100));
            button1.setBorder(BorderFactory.createEmptyBorder());
            button1.setContentAreaFilled(false);

          /*  button2 = new JButton(new ImageIcon(IMAGE2_FILE));
            button2.setPreferredSize(new Dimension(298, 100));
            button2.setBorder(BorderFactory.createEmptyBorder());
            button2.setContentAreaFilled(false); */

            button3 = new JButton(new ImageIcon(IMAGE3_FILE));
            button3.setPreferredSize(new Dimension(298, 100));
            button3.setBorder(BorderFactory.createEmptyBorder());
            button3.setContentAreaFilled(false);

            // Añadir los botones al panel
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(250, 0, 0, 0);
            add(button1, constraints);

          /*  constraints.insets = new Insets(20, 0, 0, 0);
            constraints.gridy = 1;
            add(button2, constraints); */

            constraints.insets = new Insets(20, 0, 0, 0);
            constraints.gridy = 2;
            add(button3, constraints);
            

            // Añadir el listener de eventos a los botones
            ButtonActionListener listener = new ButtonActionListener();
            button1.addActionListener(listener);
          /*  button2.addActionListener(listener);*/
            button3.addActionListener(listener);
        }

        // Clase interna para el listener de eventos de los botones
        private class ButtonActionListener implements ActionListener {
            public ButtonActionListener() {
            }
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Cerrar el marco actual
                    SwingUtilities.getWindowAncestor((Component) e.getSource()).dispose();

                    // Abrir el marco del juego Snake en un nuevo hilo
                    SwingUtilities.invokeLater(() -> {
                        try {
                            if (e.getSource() == button1) {
                                new SnakeFrame().setVisible(true);
                            } else if (e.getSource() == button3) {
                                new SnakeGameApp().createAndShowGUI();
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ImageButtonLauncher.this.getRootPane(), "Error al abrir el juego: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ImageButtonLauncher.this.getRootPane(), "Error al cerrar la ventana: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
