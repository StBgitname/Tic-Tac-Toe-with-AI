package controller;

import model.Board;
import model.Player;
import view.GameView;
import ai.TicTacToeAI;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Steuert den Spielablauf von Tic Tac Toe.
 * Koordiniert die Interaktionen zwischen Modell, Ansicht und KI.
 */
public class GameController {
    private Board board; // Das Spielfeld
    private Player humanPlayer; // Spieler: Mensch
    private Player aiPlayer; // Spieler: KI
    private GameView view; // Ansicht
    private TicTacToeAI ai; // KI-Logik
    private List<String> stateHistory;  // Liste der Spielfeldzustände eines Spiels
    private List<Integer> moveHistory; // Liste der KI-Züge eines Spiels

    /**
     * Initialisiert das Spiel.
     */
    public GameController() {
        this.board = new Board();
        this.view = new GameView();
        this.humanPlayer = new Player("Human", 'X');
        this.aiPlayer = new Player("AI", 'O');
        this.ai = new TicTacToeAI();
        this.stateHistory = new ArrayList<>();
        this.moveHistory = new ArrayList<>();

        // laden der Q-Tabelle
        ai.loadQTable("qtable.csv");
    }

    /**
     * Startet das Spiel Tic Tac Toe.
     */
    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        Player currentPlayer = humanPlayer; // Der Mensch beginnt immer.

        while (true) {
            view.displayBoard(board);
            if (currentPlayer == humanPlayer) {
                // Menschlicher Spieler macht einen Zug.
                view.displayMessage("Dein Zug (Reihe und Spalte eingeben, z.B. 0 1):");
                int row = scanner.nextInt();
                int col = scanner.nextInt();

                if (board.makeMove(row, col, currentPlayer.getSymbol())) {
                    if (board.checkWin(currentPlayer.getSymbol())) {
                        propagateRewards(-1.0);  // negative Belohnung
                        view.displayBoard(board);
                        view.displayMessage("Herzlichen Glückwunsch, du hast gewonnen!");
                        break;
                    }
                    currentPlayer = aiPlayer;  // Spieler wechseln
                } else {
                    view.displayMessage("Ungültiger Zug, bitte erneut versuchen.");
                }
            } else {
                // KI macht einen Zug.
                String state = board.getState(); // Zustand des Spielfelds
                int move = ai.getMove(state); // KI wählt einen Zug, liefert int zwischen 0 und 8
                int row = move / 3;
                int col = move % 3;

                if (board.makeMove(row, col, currentPlayer.getSymbol())) {  // liefert false, wenn der Zug ungültig ist
                    // speichert Zustand und Zug in der history
                    stateHistory.add(state);
                    moveHistory.add(move);

                    // gewonnen?
                    if (board.checkWin(currentPlayer.getSymbol())) {
                        propagateRewards(1.0);  // positive Belohnung
                        view.displayBoard(board);
                        view.displayMessage("Die KI hat gewonnen!");
                        break;
                    }
                    currentPlayer = humanPlayer;  // Spieler wechseln (nur, falls der Zug gültig war)
                }
            }

            if (board.isFull()) {
                propagateRewards(0.1);  // leicht positive Belohnung
                view.displayBoard(board);
                view.displayMessage("Unentschieden!");
                break;
            }
        }
        ai.saveQTable("qtable.csv");   // Q-Werte in Datei speichern
        scanner.close(); // Scanner schließen
    }

    private void propagateRewards(double finalReward) {
        double reward = finalReward;

        // History rückwärts durchgehen
        for (int i = stateHistory.size() - 1; i >= 0; i--) {
            String state = stateHistory.get(i);
            int move = moveHistory.get(i);

            // Q-Wert anpassen mit Berücksichtigung zukünftiger Belohnungen
            ai.updateQValue(state, move, reward);

            // Diskontiere die Belohnung für frühere Züge
            reward *= ai.getDiscountFactor();
        }
    }

}
