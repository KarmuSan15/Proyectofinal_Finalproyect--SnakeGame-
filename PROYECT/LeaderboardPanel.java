package PROYECT;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.awt.event.*;

public class LeaderboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    public static final int SCREEN_WIDTH = 600;
    public static final int SCREEN_HEIGHT = 600;
    public static final Font TITLE_FONT = new Font("Arial", 0, 42);
    public static final Font LINE_FONT = new Font("Arial", 0, 32);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 32);
    ArrayList<Score> scoreList = new ArrayList<Score>();
    SnakeFrame parentFrame;
    JPanel scores;

    private ScoreEditor scoreEditor;

    public static class ScoreEditor {
        private Connection connection;

        // Constructor para establecer la conexión a la base de datos
        public ScoreEditor() throws SQLException {
            String url = "jdbc:mysql://127.0.0.1:3306/snake_game";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);
        }

        public Connection getConnection() {
            return connection;
        }

        // Método para guardar las puntuaciones en la base de datos
        public void saveScores(ArrayList<Score> scores) throws SQLException {
            String query = "INSERT INTO scores (name, score) VALUES (?, ?) ON DUPLICATE KEY UPDATE score = VALUES(score)";
            PreparedStatement statement = connection.prepareStatement(query);

            for (Score score : scores) {
                statement.setString(1, score.name);
                statement.setInt(2, score.score);
                statement.addBatch();
            }

            statement.executeBatch();
            statement.close();
        }

        // Método para cargar las puntuaciones de la base de datos
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

    // Constructor del panel de la tabla de clasificación
    public LeaderboardPanel(JFrame frame) {
        parentFrame = (SnakeFrame) frame;

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setFocusable(true);
        this.setBackground(Color.black);
        this.setLayout(new BorderLayout());
        this.addKeyListener(new MyKeyAdapter());

        JLabel title = new JLabel("Top 10 Snakeys");
        title.setForeground(Color.white);
        title.setFont(TITLE_FONT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setPreferredSize(new Dimension(SCREEN_WIDTH, 100));

        this.add(title, BorderLayout.NORTH);

        GridLayout grid = new GridLayout(11, 2);
        scores = new JPanel(grid);
        scores.setBackground(new Color(0, 0, 0, 0));
        scores.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        try {
            scoreEditor = new ScoreEditor();
            loadScoreList();
        } catch (SQLException ex) {
            System.out.println("Error initializing the LeaderboardPanel: " + ex.getMessage());
        }
    }

    // Método para obtener un JLabel con formato específico
    private JLabel getLabelItem(String text) {
        JLabel aux = new JLabel(text, SwingConstants.CENTER);
        aux.setForeground(Color.white);
        aux.setFont(LINE_FONT);
        return aux;
    }

    // Método para cargar la lista de puntuaciones y actualizar la interfaz
    void loadScoreList() {
        try {
            scoreList = scoreEditor.loadScores();
            System.out.println("Scores loaded successfully");
        } catch (SQLException ex) {
            System.out.println("Error trying to read scores from the database");
        } finally {
            scores.removeAll();
            scores.repaint();
            JLabel nameLabel = getLabelItem("Nombre");
            JLabel scoreLabel = getLabelItem("Puntaje");
            nameLabel.setFont(HEADER_FONT);
            scoreLabel.setFont(HEADER_FONT);
            scores.add(nameLabel);
            scores.add(scoreLabel);

            Collections.sort(scoreList, Collections.reverseOrder());

            for (int i = 0; i < 10 && i < scoreList.size(); i++) {
                Score score1 = scoreList.get(i);
                JLabel nameLabel1 = getLabelItem(score1.name);
                JLabel scoreLabel1 = getLabelItem(String.valueOf(score1.score));
                scores.add(nameLabel1);
                scores.add(scoreLabel1);
            }
            this.add(scores, BorderLayout.CENTER);
        }
    }

    // Método para recargar la lista de puntuaciones
    public void reloadScoreList() {
        loadScoreList();
    }

    // Clase interna para manejar eventos de teclado
    class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent ev) {
            int keyCode = ev.getKeyCode();
            if (keyCode == KeyEvent.VK_ESCAPE) {
                parentFrame.leaderboardToLobby();
            }
        }
    }
}

class Score implements Comparable<Object> {
    String name;
    int score;

    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String toString() {
        return "name: " + this.name + " score: " + this.score;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }

    public int compareTo(Object o) {
        Score b = (Score) o;
        return this.score - b.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
