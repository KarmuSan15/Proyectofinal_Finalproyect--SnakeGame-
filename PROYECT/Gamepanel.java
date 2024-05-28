package PROYECT;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;
    // Constantes del juego
    public static final int SCREEN_WIDTH = 600;
    public static final int SCREEN_HEIGHT = 600;
    public static final int UNIT_SIZE = 25;
    public static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE);
    public static final int HORIZONTAL_UNITS = SCREEN_WIDTH / UNIT_SIZE;
    public static final int VERTICAL_UNITS = SCREEN_HEIGHT / UNIT_SIZE;
    public static final int DELAY = 100;
    public static final int INITIAL_SNAKE_SIZE = 6;

    // Variables del estado del juego
    private int appleX;
    private int appleY;
    private Timer timer = new Timer(DELAY, this);
    private char direction;
    private int[] snakeX = new int[GAME_UNITS];
    private int[] snakeY = new int[GAME_UNITS];
    private int snakeSize;
    private int applesEaten;
    SnakeFrame parentFrame;
    boolean keyInput = false;

    // Variables de puntuación
    private int lowestScore;
    private ArrayList<Score> scoreList = new ArrayList<>();
    private boolean showJTextField = false;
    private String playerName = "";
    String[] gameOverMessages = {"suerte la proxima!", "Lo sentimos Snakey!", "Dale que se puede!", "GG WP", "Hora de mudar", "Buen teclado", "Ow :(", "Uhh eso dolio!", "Que tristeza"};
    String randomGameOverMessage = "";
    private Score actualScore;
    private ScoreEditor scoreEditor;

    private static class ScoreEditor {
        private Connection connection;

        public ScoreEditor() throws SQLException {
            String url = "jdbc:mysql://127.0.0.1:3306/snake_game";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);
        }

        public void saveScore(Score score) throws SQLException {
            String query = "INSERT INTO scores (name, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = VALUES(score)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, score.name);
            statement.setInt(2, score.score);
            statement.addBatch();
            statement.executeBatch();
            statement.close();
        }

        public ArrayList<Score> loadScores() throws SQLException {
            ArrayList<Score> scores = new ArrayList<>();
            String query = "SELECT * FROM scores ORDER BY score DESC LIMIT 10";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                scores.add(new Score(name, score));
            }

            resultSet.close();
            statement.close();
            return scores;
        }
    }

    // Constructor
    GamePanel(JFrame frame) {
        parentFrame = (SnakeFrame) frame;

        // Configuración del panel de juego
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(new MyKeyAdapter());
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);

        // Inicialización del editor de puntuaciones
        try {
            scoreEditor = new ScoreEditor();
            loadScoreList();
        } catch (SQLException ex) {
            System.out.println("Error initializing the LeaderboardPanel: " + ex.getMessage());
        }
    }

    // Método para iniciar el juego
    public void startGame() {
        snakeSize = INITIAL_SNAKE_SIZE;
        applesEaten = 0;
        for (int i = 0; i < snakeSize; i++) {
            snakeX[i] = 0;
            snakeY[i] = 0;
        }
        direction = 'R';
        timer.start();
        newApple();
        System.out.println("Initialized game panel startGame()");
        loadScoreList();
        loadLowestScore();
        randomGameOverMessage = gameOverMessages[random(gameOverMessages.length)];
    }

    // Método para cargar la lista de puntuaciones
    public void loadScoreList() {
        try {
            scoreList = scoreEditor.loadScores();
            System.out.println("Scores loaded successfully");
            System.out.println(scoreList);
        } catch (SQLException ex) {
            System.out.println("Error trying to read scores from the database: " + ex.getMessage());
        }
    }

    // Método para cargar la puntuación más baja
    public void loadLowestScore() {
        lowestScore = scoreList.get(9).getScore();
        System.out.println("lowestScore: " + lowestScore);
    }

    public void actionPerformed(ActionEvent ev) {
        move();
        checkCollision();
        eatApple();
        repaint();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar manzana
        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        // Dibujar cabeza de la serpiente
        g.setColor(Color.green);
        g.fillRect(snakeX[0], snakeY[0], UNIT_SIZE, UNIT_SIZE);

        // Dibujar cuerpo de la serpiente
        for (int i = 1; i < snakeSize; i++) {
            g.fillRect(snakeX[i], snakeY[i], UNIT_SIZE, UNIT_SIZE);
        }

        // Dibujar puntuación
        g.setColor(Color.white);
        g.setFont(new Font("MS Gothic", Font.PLAIN, 25));
        FontMetrics fontSize = g.getFontMetrics();
        int fontX = SCREEN_WIDTH - fontSize.stringWidth("Puntaje: " + applesEaten) - 10;
        int fontY = fontSize.getHeight();
        g.drawString("Puntaje: " + applesEaten, fontX, fontY);

        if (!timer.isRunning()) {
            // Pantalla de game over
            g.setColor(Color.white);
            g.setFont(new Font("MS Gothic", Font.PLAIN, 58));

            String message = randomGameOverMessage;
            fontSize = g.getFontMetrics();
            fontX = (SCREEN_WIDTH - fontSize.stringWidth(message)) / 2;
            fontY = (SCREEN_HEIGHT - fontSize.getHeight()) / 2;
            g.drawString(message, fontX, fontY);

            g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
            message = "Presiona F2 para reiniciar";
            fontSize = g.getFontMetrics();
            fontX = (SCREEN_WIDTH - fontSize.stringWidth(message)) / 2;
            fontY = fontY + fontSize.getHeight() + 20;
            g.drawString(message, fontX, fontY);

            if (showJTextField) {
                drawJTextField(g);
                drawPlayerName(g);
            }
        }
    }

    public void drawJTextField(Graphics g) {
        g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        String message = "Ingresa tu nombre:";
        FontMetrics fontSize = g.getFontMetrics();
        int fontX = (SCREEN_WIDTH - fontSize.stringWidth(message)) / 2;
        g.drawString(message, fontX, 350);
    }

    public void drawPlayerName(Graphics g) {
        g.setFont(new Font("MS Gothic", Font.PLAIN, 24));
        FontMetrics fontSize = g.getFontMetrics();
        int fontX = (SCREEN_WIDTH - fontSize.stringWidth(playerName)) / 2;
        g.drawString(playerName, fontX, 400);
    }

    public void newApple() {
        int x = random(HORIZONTAL_UNITS) * UNIT_SIZE;
        int y = random(VERTICAL_UNITS) * UNIT_SIZE;
        Point provisional = new Point(x, y);
        Point snakePos = new Point();
        boolean newApplePermission = true;
        for (int i = 0; i < snakeSize; i++) {
            snakePos.setLocation(snakeX[i], snakeY[i]);
            if (provisional.equals(snakePos)) {
                newApplePermission = false;
            }
        }

        if (newApplePermission) {
            appleX = x;
            appleY = y;
        } else {
            newApple();
        }
    }

    public void checkCollision() {
        if (snakeX[0] >= SCREEN_WIDTH || snakeX[0] < 0 || snakeY[0] >= SCREEN_HEIGHT || snakeY[0] < 0) {
            gameOver();
        }
        for (int i = 1; i < snakeSize; i++) {
            if ((snakeX[0] == snakeX[i]) && (snakeY[0] == snakeY[i])) {
                gameOver();
            }
        }
    }

    public void eatApple() {
        if (snakeX[0] == appleX && snakeY[0] == appleY) {
            snakeSize++;
            applesEaten++;
            newApple();
        }
    }

    public void move() {
        for (int i = snakeSize; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case 'R':
                snakeX[0] += UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] -= UNIT_SIZE;
                break;
            case 'U':
                snakeY[0] -= UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] += UNIT_SIZE;
                break;
        }

        keyInput = false;
    }

    public void gameOver() {
        timer.stop();
        if (applesEaten > lowestScore) {
            showJTextField = true;
        }
    }

    public int random(int range) {
        return (int) (Math.random() * range);
    }

    class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent k) {
            switch (k.getKeyCode()) {
                case KeyEvent.VK_DOWN:
                    if (direction != 'U' && !keyInput) {
                        direction = 'D';
                        keyInput = true;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D' && !keyInput) {
                        direction = 'U';
                        keyInput = true;
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != 'R' && !keyInput) {
                        direction = 'L';
                        keyInput = true;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L' && !keyInput) {
                        direction = 'R';
                        keyInput = true;
                    }
                    break;
                case KeyEvent.VK_F2:
                    if (!timer.isRunning()) {
                        startGame();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    parentFrame.switchToLobbyPanel();
                    break;
            }

            if (showJTextField) {
                if (k.getKeyCode() == KeyEvent.VK_ENTER) {
                    actualScore = new Score(playerName, applesEaten);
                    scoreList.add(actualScore);
                    Collections.sort(scoreList, Comparator.comparingInt(Score::getScore).reversed());
                    sortAndSave();
                    playerName = "";
                    showJTextField = false;
                    parentFrame.switchToLobbyPanel();
                } else if (k.getKeyCode() == KeyEvent.VK_BACK_SPACE && playerName.length() > 0) {
                    StringBuilder sb = new StringBuilder(playerName);
                    sb.deleteCharAt(sb.length() - 1);
                    playerName = sb.toString();
                } else {
                    if (!k.isActionKey() && k.getKeyCode() != KeyEvent.VK_SHIFT && k.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
                        playerName += k.getKeyChar();
                    }
                }

                repaint();
            }
        }
    }

    public void sortAndSave() {
        try {
            boolean actualScoreExists = false;
            for (Score score : scoreList) {
                if (score.getName().equals(actualScore.getName())) {
                    actualScoreExists = true;
                    if (score.getScore() < actualScore.getScore()) {
                        score.setScore(actualScore.getScore());
                    }
                    break;
                }
            }
            if (!actualScoreExists) {
                scoreList.add(actualScore);
            }

            Collections.sort(scoreList, Comparator.comparingInt(Score::getScore).reversed());

            for (Score score : scoreList) {
                scoreEditor.saveScore(score);
            }

            System.out.println("Scores sorted and saved successfully");
        } catch (SQLException ex) {
            System.out.println("Error trying to sort and save scores: " + ex.getMessage());
        }
    }

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
            System.out.println("Slept");
        } catch (Exception ex) {
            System.out.println("Fatal Error in sleep() method");
        }
    }
}
