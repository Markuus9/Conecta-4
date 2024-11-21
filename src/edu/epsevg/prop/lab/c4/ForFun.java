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
    /**
     * Propietats
     */
    private String name = "4Fun";
    private int depth = 6;
    private Integer MaxInfinity = Integer.MAX_VALUE;
    private Integer MinInfinity = Integer.MIN_VALUE;
    
    /**
     * Constructor
     **/
    public ForFun(){}

    /**
     * Contrueix un Player amb la depth pasada per parametre
     * @param depth
     */
    public ForFun(int depth) {
        this.depth = depth;
    }

    /**
     * @return
     */
    @Override
    public String nom() {
        return this.name;
    }

    /**
     * @param t Tauler actual
     * @param color Color de la fitxa a posar
     * @return La millor columna on posar la fitxa
     */
    @Override
    public int moviment(Tauler t, int color) {
        int bestCol = 0, value, best = MinInfinity;
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
                value = min(nextMove, color, depth, MinInfinity, MaxInfinity);
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
     * Minimitza el value de la funcio alfabeta
     * @param t
     * @param alfa
     * @param beta
     * @param depth
     * @param jugador
     * @return el value corresponent a min
     */
    private int min(Tauler t,int player, int depth, int alpha, int beta){
        if(depth == 0 || !t.espotmoure()) {
            beta = heuristica(t, player);
        } else {
            for(int i = 0; i < t.getMida(); i++){
                if(t.movpossible(i)){
                    Tauler nextMove = new Tauler(t);
                    nextMove.afegeix(i, -player);
                    if (nextMove.solucio(i, -player)) {
                        beta = MinInfinity;
                        break;
                    }
                    int maxValue = max(nextMove, player, depth-1, alpha, beta);
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
     * Maximitza el value de la funcio alfabeta
     * @param t
     * @param alfa
     * @param beta
     * @param depth
     * @param jugador
     * @return el value corresponent a min
     */
    // min(Tauler t,int player, int depth, int alpha, int beta)
    private int max(Tauler t, int player, int depth, int alpha, int beta){
        if(depth == 0 || !t.espotmoure()){
            return heuristica(t,player);
        }
        for(int i = 0 ;i < t.getMida(); i++){
            if(t.movpossible(i)){
                Tauler nextMove = new Tauler (t);
                nextMove.afegeix(i, player);
                if(nextMove.solucio(i,player)) {
                    alpha = MaxInfinity;
                    break;
                }
                int minValue = min(nextMove, player, depth-1, alpha, beta);
                if (alpha < minValue){
                    alpha = minValue;
                }
                if(alpha >= beta){
                    break;
                }
            }
        }
        return alpha;
    }

    /**
     * Evalua cada posicio del tauler i computa una heuristica
     * @param t
     * @param jugador
     * @return la heuristica
     */
    private int heuristica(Tauler t, int jugador) {
        int heuristica = 0;
        for (int i = 0; i < t.getMida(); i++) {
            for (int j = 0; j < t.getMida(); j++) {
                heuristica += puntuarCasella(t, i, j, jugador);
            }
        }
        return heuristica;
    }

    /**
     * Calcula els espais restants que queden per completar el 4 en ratlla en la fila
     * @param fila
     * @param col
     * @param t
     * @return
     */
    private int espaisRestants(int fila, int col, Tauler t){
        int espais = 0;
        for(int i = 0; i < 4 && fila >= 0; ++i){
            if(t.getColor(fila,col) == 0){
                ++espais;
            } else break;

            fila -= i;
        }
        return espais;
    }

    private int puntuarCasella(Tauler t, int fil, int col, int jug) {
        int heuristica = 0;

        // Direcciones de búsqueda: vertical, horizontal, diagonal derecha y diagonal izquierda
        int[][] direcciones = {
            {1, 0},  // Vertical
            {0, 1},  // Horizontal
            {1, 1},  // Diagonal hacia abajo derecha
            {1, -1}  // Diagonal hacia abajo izquierda
        };

        for (int[] direccion : direcciones) {
            heuristica += calcularHeuristicaDireccion(t, fil, col, jug, direccion[0], direccion[1]);
        }

        return heuristica;
    }

    /**
     * Calcula la heurística para una dirección específica (vertical, horizontal, diagonal)
     * @param t Tablero
     * @param fil Fila inicial
     * @param col Columna inicial
     * @param jug Jugador actual
     * @param dirFil Dirección en la fila (incremento)
     * @param dirCol Dirección en la columna (incremento)
     * @return Valor heurístico de la dirección
     */
    private int calcularHeuristicaDireccion(Tauler t, int fil, int col, int jug, int dirFil, int dirCol) {
        int jugador = t.getColor(fil, col);
        int contador = 1, contadorBlancs = 0;

        for (int paso = 1; paso <= 3; paso++) {
            int nuevaFila = fil + paso * dirFil;
            int nuevaColumna = col + paso * dirCol;

            if (nuevaFila < 0 || nuevaFila >= t.getMida() || nuevaColumna < 0 || nuevaColumna >= t.getMida()) {
                break; // Nos salimos del tablero
            }

            if (t.getColor(nuevaFila, nuevaColumna) == jugador) {
                contador++;
            } else if (t.getColor(nuevaFila, nuevaColumna) == 0) {
                contadorBlancs = espaisRestants(nuevaFila, nuevaColumna, t);
                break;
            } else {
                break;
            }
        }

        if (jugador == jug) {
            return calcularPuntuacio(contador, contadorBlancs);
        } else {
            return -calcularPuntuacio(contador, contadorBlancs);
        }
    }

    /**
    * Calcula la puntuación de la casilla según las fichas conectadas y los movimientos restantes.
    * @param fichasConectadas Número de fichas conectadas
    * @param movimientosRestantes Espacios vacíos restantes
    * @return Puntuación calculada
    */
   int calcularPuntuacio(int fichasConectadas, int movimientosRestantes) {
       int factorMovimiento = 4 - movimientosRestantes;

       switch (fichasConectadas) {
           case 0: 
               return 0; // Sin fichas conectadas, no hay puntuación
           case 1:
               return factorMovimiento; // Una ficha conectada, peso mínimo
           case 2:
               return 10 * factorMovimiento; // Dos fichas conectadas, peso medio
           case 3:
               return 100 * factorMovimiento; // Tres fichas conectadas, peso alto
           case 4:
               return 1000; // Cuatro fichas conectadas, ganadora
           default:
               return 0; // Caso no esperado
       }
   }
}
