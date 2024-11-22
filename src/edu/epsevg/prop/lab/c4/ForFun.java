package edu.epsevg.prop.lab.c4;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

/**
 *
 * @author Markus
 */
public class ForFun implements Jugador, IAuto {
    private int depth = 5;
    
    /**
     * Constructor
     **/
    public ForFun(){}

    /**
     * Contrueix un Player amb la profunditat pasada per parametre
     * @param depth
     */
    public ForFun(int depth) {
        this.depth = depth;
    }

    @Override
    public String nom() {
        return "4Fun";
    }

    @Override
    public int moviment(Tauler t, int color) {
        int bestCol = 0, value, best = Integer.MIN_VALUE;
        for(int i=0; i < t.getMida(); i++){
            if(t.movpossible(i)){
                Tauler nextMove = new Tauler(t);
                nextMove.afegeix(i, color);
                //Comprovem si amb el pròxim moviment guanyem
                if(nextMove.solucio(i,color)){
                    bestCol = i;
                    break;
                }
                // Calculem la heurística i decidim en quina columna posar la fitxa 
                value = min(nextMove, color, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if(!t.movpossible(bestCol) || value > best){
                    bestCol = i;
                    best = value;
                }
                System.out.println("La columna: "+ i +", te un valor heuristic = "+ value);
            }
        }
        return bestCol;
    }

    /**
    * Calcula el valor minim mitjançant l'algoritme Minimax amb poda alfa-beta.
    * @param t Tauler actual.
    * @param color Color del jugador actual.
    * @param depth Profunditat restant de la cerca.
    * @param alpha Valor alfa actual.
    * @param beta Valor beta actual.
    * @return El valor màxim calculat.
    */
    private int min(Tauler t, int color, int depth, int alpha, int beta){
        if(depth == 0 || !t.espotmoure()) {
            beta = calcHeuristica(t, color);
        } else {
            for(int i = 0; i < t.getMida(); i++){
                if(t.movpossible(i)){
                    Tauler nextMove = new Tauler(t);
                    nextMove.afegeix(i, -color);
                    if (nextMove.solucio(i, -color)) {
                        beta = Integer.MIN_VALUE;
                        break;
                    }
                    int maxValue = max(nextMove, color, depth-1, alpha, beta);
                    if (beta > maxValue){
                        beta = maxValue;
                    }
                    if(beta <= alpha) {
                        break;
                    }
                }
            }
        }
        return beta;
    }

    /**
    * Calcula el valor màxim mitjançant l'algoritme Minimax amb poda alfa-beta.
    * @param t Tauler actual.
    * @param color Color del jugador actual.
    * @param depth Profunditat restant de la cerca.
    * @param alpha Valor alfa actual.
    * @param beta Valor beta actual.
    * @return El valor màxim calculat.
    */
    private int max(Tauler t, int color, int depth, int alpha, int beta){
        if(depth == 0 || !t.espotmoure()){
            return calcHeuristica(t,color);
        } else {
            for(int i = 0; i < t.getMida(); i++){
                if(t.movpossible(i)){
                    Tauler nextMove = new Tauler (t);
                    nextMove.afegeix(i, color);
                    if(nextMove.solucio(i,color)) {
                        alpha = Integer.MAX_VALUE;
                        break;
                    }
                    int minValue = min(nextMove, color, depth-1, alpha, beta);
                    if (alpha < minValue){
                        alpha = minValue;
                    }
                    if(alpha >= beta){
                        break;
                    }
                }
            }
        }
        return alpha;
    }

    /**
    * Calcula els espais buits restants per completar una línia de 4 en una direcció específica.
    * @param row Fila inicial.
    * @param col Columna inicial.
    * @param t Tauler de joc.
    * @return Nombre d'espais buits restants.
    */
   private int empty(int row, int col, Tauler t) {
        int emptySpaces = 0; // Inicialitza el comptador d'espais buits.
        int steps = 0; // Comptador de passos.

        // Recorre fins a un màxim de 4 passos o fins a trobar un límit.
        while (steps < 4 && row >= 0) {
            if (t.getColor(row, col) == 0) {
                emptySpaces++; // Incrementa si la casella està buida.
            } else {
                break; // Es para si es troba una casella ocupada.
            }
            steps++;
            row--; // Es mou cap amunt en la fila
        }
        return emptySpaces;
    }
   
    /**
    * Calcula la heurística total del tauler per a un color específic.
    * Suma les puntuacions heurístiques de totes les caselles del tauler.
    * @param t Tauler de joc.
    * @param color Color del jugador actual.
    * @return Valor heurístic total del tauler.
    */
    private int calcHeuristica(Tauler t, int color) {
        int value = 0;
        for (int i = 0; i < t.getMida(); i++) {
            for (int j = 0; j < t.getMida(); j++) {
                value += heuristica(i, j, color, t);
            }
        }
        return value;
    }

    /**
    * Calcula la heurística total per a una casella específica del tauler.
    * Explora totes les direccions possibles (vertical, horitzontal, diagonals).
    * @param i Fila inicial de la casella.
    * @param j Columna inicial de la casella.
    * @param color Color del jugador actual.
    * @param t Tauler de joc.
    * @return Valor heurístic total per a la casella.
    */
    private int heuristica(int i, int j, int color,Tauler t) {
        int heuristica = 0;

        // Direccions de cerca: vertical, horitzontal, diagonal dreta i diagonal esquerra
        int[][] direccions = {
            {1, 0},  // Vertical
            {0, 1},  // Horizontal
            {1, 1},  // Diagonal cap avall dreta
            {1, -1}  // Diagonal cap avall esquerra
        };

        // Calcula la heurística per a cada direcció.
        for (int[] direccio : direccions) {
            heuristica += calcSections(t, i, j, color, direccio[0], direccio[1]);
        }

        return heuristica;
    }

    /**
    * Calcula la heurística per a una direcció específica (vertical, horitzontal, diagonal).
    * @param t Tauler actual.
    * @param startRow Fila inicial.
    * @param startCol Columna inicial.
    * @param color Color del jugador actual.
    * @param rowDirection Direcció en la fila (increment).
    * @param colDirection Direcció en la columna (increment).
    * @return Valor heurístic de la direcció.
    */
    private int calcSections(Tauler t, int startRow, int startCol, int color, int rowDirection, int colDirection) {
        int cellColor = t.getColor(startRow, startCol); // Color de la casella inicial.
        int count = 1; // Comptador de fitxes consecutives.
        int emptySpaces = 0; // Comptador d'espais buits.

        // Recorre fins a 3 posicions en la direcció especificada.
        for (int step = 1; step <= 3; step++) {
            int newRow = startRow + step * rowDirection; // Calcula la nova fila.
            int newCol = startCol + step * colDirection; // Calcula la nova columna.

            // Si la posició surt del tauler, es para.
            if (newRow < 0 || newRow >= t.getMida() || newCol < 0 || newCol >= t.getMida()) {
               break;
            }

            // Comprova el color de la nova casella.
            if (t.getColor(newRow, newCol) == cellColor) {
               count++; // Incrementa el comptador de fitxes consecutives.
            } else if (t.getColor(newRow, newCol) == 0) {
               // Si la casella està buida, compta els espais disponibles i para.
               emptySpaces = empty(newRow, newCol, t);
               break;
            }
        }

        // Calcula la puntuació heurística basada en les fitxes consecutives i els espais buits.
        if (cellColor == color) {
           return (4 - emptySpaces) * (count * count * count); // Positiu si és del jugador actual.
        } else {
           return -(4 - emptySpaces) * (count * count * count); // Negatiu si és del contrincant.
        }
    }
}
