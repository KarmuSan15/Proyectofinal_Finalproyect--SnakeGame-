package PROYECT;

import javax.swing.*;

class SnakeFrame extends JFrame{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    LobbyPanel lobbyPanel = new LobbyPanel(this);
    GamePanel gamePanel = new GamePanel(this);
    LeaderboardPanel leaderboardPanel = new LeaderboardPanel(this);
    private boolean addedLeaderboard = false;
    
    public SnakeFrame() {
        // Añadir el panel del lobby
        this.add(lobbyPanel);

        // Configuración de la ventana principal
        this.setTitle("Java Snakey"); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // Añadir el panel del juego y cargar la lista de puntajes
        this.add(gamePanel);
        this.loadScoreList();
    }
    
    void loadScoreList() {
        // Cargar la lista de puntajes (a implementar)
    }

    // Método para cambiar al panel del juego
    public void switchToGamePanel(){
        lobbyPanel.setVisible(false);
        gamePanel.setVisible(true);
        gamePanel.requestFocus();
        gamePanel.startGame();
    }
    
    // Método para volver del panel del juego al panel del lobby
    public void switchToLobbyPanel(){
        gamePanel.gameOver();
        gamePanel.setVisible(false);
        lobbyPanel.setVisible(true);
        lobbyPanel.requestFocus();
    }

    // Método para cambiar del juego al panel de la tabla de líderes
    public void gameToLeaderboard(){
        gamePanel.setVisible(false);
        leaderboardPanel.loadScoreList();
        leaderboardPanel.setVisible(true);
        leaderboardPanel.requestFocus();
    }
    
    // Método para cambiar del panel de la tabla de líderes al juego
    public void leaderboardToGame(){
        leaderboardPanel.setVisible(false);
        gamePanel.setVisible(true);
        gamePanel.requestFocus();
        gamePanel.startGame();
    }
    
    // Método para cambiar del panel de la tabla de líderes al lobby
    public void leaderboardToLobby(){
        leaderboardPanel.setVisible(false);
        lobbyPanel.setVisible(true);
        lobbyPanel.requestFocus();
    }
    
    // Método para cambiar al panel de la tabla de líderes desde el lobby
    public void switchToLeaderboardPanel(){
        if(addedLeaderboard){
            lobbyPanel.setVisible(false);
            leaderboardPanel.loadScoreList();
            leaderboardPanel.setVisible(true);
            leaderboardPanel.requestFocus();
        } else {
            lobbyPanel.setVisible(false);
            gamePanel.setVisible(false);
            this.add(leaderboardPanel);
            leaderboardPanel.loadScoreList();
            leaderboardPanel.requestFocus();
            addedLeaderboard = true;
        }
    }
}
