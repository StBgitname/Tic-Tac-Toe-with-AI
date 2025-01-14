package view;

import controller.GameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Board;

/**
 * Stellt die grafische Benutzeroberfläche für das Tic Tac Toe Spiel bereit und verbindet sie mit dem GameController.
 */
public class GameViewGUI extends Application {
    private GameController gameController; // Verbindung zu GameController
    private Label statusLabel; // Statusanzeige
    private Button[][] gridButtons; // Spielfeld-Buttons

    @Override
    public void start(Stage primaryStage) {
        // Initialisiere den Controller
        gameController = new GameController(this); // Dependency Injection der aktuellen GUI

        // Hauptlayout
        VBox root = new VBox();
        root.setSpacing(10);

        // Spielfeld (3x3 Grid)
        GridPane grid = new GridPane();
        gridButtons = new Button[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setPrefSize(100, 100); // Größe des Buttons
                int row = i;
                int col = j;
                button.setOnAction(e -> handleButtonClick(row, col)); // Ereignis-Handler mit Zeile und Spalte
                grid.add(button, j, i);
                gridButtons[i][j] = button;
            }
        }

        // Statusanzeige
        statusLabel = new Label("Willkommen zu Tic Tac Toe!");

        // Layout zusammenfügen
        root.getChildren().addAll(grid, statusLabel);

        // Szene und Bühne
        Scene scene = new Scene(root, 400, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.show();

        // Initiales Spielfeld anzeigen
        renderBoard(gameController.getBoard());
    }

    /**
     * Aktualisiert die GUI basierend auf dem aktuellen Zustand des Boards.
     *
     * @param board Das aktuelle Spielfeld.
     */
    public void renderBoard(Board board) {
        char[][] grid = board.getBoard();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gridButtons[i][j].setText(grid[i][j] == ' ' ? "" : String.valueOf(grid[i][j]));
                gridButtons[i][j].setDisable(grid[i][j] != ' '); // Deaktivieren, wenn das Feld nicht leer ist
            }
        }
    }

    /**
     * Zeigt eine Nachricht auf der GUI an.
     *
     * @param message Die Nachricht, die angezeigt werden soll.
     */
    public void displayMessage(String message) {
        statusLabel.setText(message);
    }

    /**
     * Behandelt Klicks auf die Spielfelder.
     * Informiert den GameController über den Klick.
     *
     * @param row Die Reihe des geklickten Feldes.
     * @param col Die Spalte des geklickten Feldes.
     */
    private void handleButtonClick(int row, int col) {
        gameController.handlePlayerMove(row, col); // Controller übernimmt die Spiellogik
    }

    /**
     * Deaktiviert alle Buttons im Spielfeld.
     */
    public void disableAllButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                gridButtons[i][j].setDisable(true); // Deaktiviert den Button
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}