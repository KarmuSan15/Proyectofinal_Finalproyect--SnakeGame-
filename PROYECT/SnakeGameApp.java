package PROYECT;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class SnakeGameApp {
    private JFrame frame;
    private JTable table;
    private JScrollPane scrollPane;

    // Método principal
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                SnakeGameApp app = new SnakeGameApp();
                app.createAndShowGUI();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error connecting to database: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Método para crear y mostrar la GUI
    public void createAndShowGUI() throws SQLException {
        frame = new JFrame("Snake Scores");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.getContentPane().setBackground(Color.BLACK); // Establece el fondo negro

        // Inicializamos el ScoreEditor y ejecutamos una consulta para obtener los datos de la tabla 'scores'
        ScoreEditor scoreEditor = new ScoreEditor();
        Statement stmt = scoreEditor.getConnection().createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, name, score FROM scores");

        // Definimos los nombres de las columnas para la tabla
        String[] columnNames = {"ID", "Name", "Score"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Rellenamos el modelo de la tabla con los datos obtenidos de la consulta
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int score = rs.getInt("score");

            Object[] row = {id, name, score};
            model.addRow(row);
        }

        // Creamos la tabla y le añadimos un MouseListener para manejar el evento de doble clic
        table = new JTable(model);
        table.setForeground(Color.WHITE); // Establece el color del texto en blanco
        table.setBackground(Color.BLACK); // Establece el color de fondo en negro
        table.getTableHeader().setForeground(Color.WHITE); // Establece el color del encabezado en blanco
        table.getTableHeader().setBackground(Color.BLACK); // Establece el color de fondo del encabezado en negro
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Si se hace doble clic en una fila
                    int rowIndex = table.rowAtPoint(e.getPoint());
                    if (rowIndex >= 0) {
                        int id = (int) table.getValueAt(rowIndex, 0);
                        String name = (String) JOptionPane.showInputDialog(frame, "Enter new name:", "Change Name", JOptionPane.PLAIN_MESSAGE, null, null, (String) table.getValueAt(rowIndex, 1));
                        if (name != null) {
                            try (PreparedStatement pstmt = scoreEditor.getConnection().prepareStatement("UPDATE scores SET name = ? WHERE id = ?")) {
                                pstmt.setString(1, name);
                                pstmt.setInt(2, id);
                                pstmt.executeUpdate();

                                table.setValueAt(name, rowIndex, 1);
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(frame, "Error changing name: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        // Añadimos la tabla a un JScrollPane
        scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.BLACK); // Establece el color de fondo del viewport en negro
        frame.add(scrollPane, BorderLayout.CENTER);

        // Botón para añadir un nuevo registro
        JButton addButton = new JButton("AÑADIR");
        addButton.setForeground(Color.WHITE); // Establece el color del texto en blanco
        addButton.setBackground(Color.BLACK); // Establece el color de fondo en negro
        addButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(frame, "Introduce nombre:");
            int score = Integer.parseInt(JOptionPane.showInputDialog(frame, "Introduce record:"));

            try (PreparedStatement pstmt = scoreEditor.getConnection().prepareStatement("INSERT INTO scores (name, score) VALUES (?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setInt(2, score);
                pstmt.executeUpdate();

                model.addRow(new Object[]{model.getRowCount() + 1, name, score});
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error adding score: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Botón para eliminar un registro seleccionado
        JButton deleteButton = new JButton("ELIMINAR");
        deleteButton.setForeground(Color.WHITE); // Establece el color del texto en blanco
        deleteButton.setBackground(Color.BLACK); // Establece el color de fondo en negro
        deleteButton.addActionListener(e -> {
            int rowIndex = table.getSelectedRow();

            if (rowIndex >= 0) {
                int id = (int) table.getValueAt(rowIndex, 0);

                try (PreparedStatement pstmt = scoreEditor.getConnection().prepareStatement("DELETE FROM scores WHERE id = ?")) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();

                    model.removeRow(rowIndex);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(frame, "Error deleting score: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
   
        // Etiqueta para informar al usuario sobre el doble clic
        JLabel changeNameLabel = new JLabel("Doble click para cambiar el nombre");
        changeNameLabel.setForeground(Color.WHITE); // Establece el color del texto en blanco
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.BLACK); // Establece el color de fondo en negro
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(changeNameLabel);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    // Clase interna para manejar la conexión a la base de datos
    private static class ScoreEditor {
        private Connection connection;

        public ScoreEditor() throws SQLException {
            String url = "jdbc:mysql://127.0.0.1:3306/snake_game";
            String user = "root";
            String password = "";

            connection = DriverManager.getConnection(url, user, password);
        }

        public Connection getConnection() {
            return connection;
        }
    }
}
