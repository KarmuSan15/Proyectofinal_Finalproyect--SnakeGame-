package PROYECT;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

class LobbyPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TITLE_MESSAGE = "Sneiky";
    public static final String CREATOR_MESSAGE = "Me dio ansiedad por eso hay dos paneles :,D";
    public static final Font TITLE_FONT = new Font("Playball", 0, 62);
    public static final Font MENU_FONT = new Font("Arial", 0, 28);
    public static final Font CREATOR_FONT = new Font("Arial", 0, 14);
    public static final int SCREEN_WIDTH = GamePanel.SCREEN_WIDTH;
    public static final int SCREEN_HEIGHT = GamePanel.SCREEN_HEIGHT;
    public static final String[] MENU_ITEMS = {"Jugar", "Mejores Snakeys", "Salir"};
    private int selectedMenuItem = 0;
	SnakeFrame parentFrame;

    // Constructor del panel de lobby
    public LobbyPanel(JFrame frame) {
		parentFrame = (SnakeFrame) frame;
        this.addKeyListener(new MyKeyAdapter());
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        // Lo hacemos focusable para que tome teclas
        this.setFocusable(true);
		this.requestFocus();
    }

    // Método para pintar el componente
    public void paintComponent(Graphics g) {
        // Llamamos al paintComponent() de super para no perder el background color seteado en JPanel.
        super.paintComponent(g);
        drawTitle(g);
        drawMenu(g);
        drawCreator(g);
    }

    // Método para dibujar el título
    private void drawTitle(Graphics g) {
        g.setColor(Color.white);
        g.setFont(TITLE_FONT);

        FontMetrics metrics = g.getFontMetrics();
        int x = (SCREEN_WIDTH - metrics.stringWidth(TITLE_MESSAGE)) / 2;
        int y = metrics.getHeight() + 100;

        g.drawString(TITLE_MESSAGE, x, y);
    }

    // Método para dibujar el menú
    private void drawMenu(Graphics g) {
        g.setColor(Color.white);
        g.setFont(MENU_FONT);

        FontMetrics metrics = g.getFontMetrics();
        for (int i = 0; i < MENU_ITEMS.length; i++) {
            int x = (SCREEN_WIDTH - metrics.stringWidth(MENU_ITEMS[i])) / 2; // Centramos horizontalmente
            int y = metrics.getHeight() + 300 + (i * (metrics.getHeight() + 20));
            g.drawString(MENU_ITEMS[i], x, y);
            if (selectedMenuItem == i) {
                drawTriangle(x - 30, y - 20, g);
            }
        }
    }

    // Método para dibujar un triángulo al lado del menú seleccionado
    private void drawTriangle(int x, int y, Graphics g) {
        g.setColor(Color.white);
        int[] xPoints = {x, x + 20, x};
        int[] yPoints = {y, y + 10, y + 20};
        g.fillPolygon(xPoints, yPoints, 3);
    }

    // Método para dibujar el mensaje del creador
    private void drawCreator(Graphics g) {
        g.setColor(Color.white);
        g.setFont(CREATOR_FONT);

        FontMetrics metrics = g.getFontMetrics();
        int x = SCREEN_WIDTH - metrics.stringWidth(CREATOR_MESSAGE) - 10;
        int y = SCREEN_WIDTH - metrics.getHeight();

        g.drawString(CREATOR_MESSAGE, x, y);
    }

    // Clase interna para manejar eventos de teclado
    private class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    decrementMenu();
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    incrementMenu();
                    repaint();
                    break;
                case KeyEvent.VK_ENTER:
                    switchPanels();
					break;
				case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                    break;
            }
        }
    }

    // Método para cambiar de panel según la opción seleccionada
    private void switchPanels() {
        switch (selectedMenuItem) {
            case 0:
                parentFrame.switchToGamePanel();
                break;
            case 1:
				parentFrame.switchToLeaderboardPanel();
                break;
            case 2:
                System.exit(0);
                break;
        }
    }

    // Método para incrementar el índice del menú
    private void incrementMenu() {
        // Siempre escribir las clases y los métodos pensando en expansión. Mientras menos toquemos el source code mejor
        int lastItemIndex = MENU_ITEMS.length - 1;
        if (selectedMenuItem < lastItemIndex) {
            selectedMenuItem++;
        } else {
            selectedMenuItem = 0;
        }
    }

    // Método para decrementar el índice del menú
    private void decrementMenu() {
        int lastItemIndex = MENU_ITEMS.length - 1;
        if (selectedMenuItem > 0) {
            selectedMenuItem--;
        } else {
            selectedMenuItem = lastItemIndex;
        }
    }

    // Método para obtener el ítem de menú seleccionado actualmente
    public int getSelectedMenuItem() {
        return selectedMenuItem;
    }
}
