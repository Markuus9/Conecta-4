package edu.epsevg.prop.lab.c4;
import edu.epsevg.prop.lab.c4.IAuto;
import edu.epsevg.prop.lab.c4.Jugador;
import edu.epsevg.prop.lab.c4.Tauler;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/File.java to edit this template
 */

/**
 *
 * @author Markus
 */
public class ForFun implements Jugador, IAuto {
    private class Resultat {
        public boolean esGuanyador;
        public int jugadorGuanyador;
        public int valor = 0;

        public Resultat(boolean esTaulerGuanyador, int jugadorGuanyador, int valorTauler) {
            this.esGuanyador = esTaulerGuanyador;
            this.jugadorGuanyador = jugadorGuanyador;
            this.valor = valorTauler;
        }

        @Override
        public String toString() {
            return "Resultat{" +
                    "esGuanyador=" + esGuanyador +
                    ", jugadorGuanyador=" + jugadorGuanyador +
                    '}';
        }
    }

    /**
     * Propietats
     */
    private Integer InfinitPositiu = Integer.MAX_VALUE;
    private Integer InfinitNegatiu = -InfinitPositiu;
    private String nom = "4Fun";
    private int profunditat = 6;

    /**
     * Constructor
     **/
    public ForFun() {}

    /**
     * Contrueix un Player amb la profunditat donada
     * @param profunditat
     */
    public ForFun(int profunditat) {
        this.profunditat = profunditat;
    }

    /**
     * @return
     */
    @Override
    public String nom() {
        return this.nom;
    }

    /**
     * @param t Tauler actual de joc
     * @param color Color de la peca que possara
     * @return
     */
    @Override
    public int moviment(Tauler t, int color) {
        int valorActual, millorValor = InfinitNegatiu, millor_columna = 0;
        for(int i=0; i < t.getMida(); i++){
            if(t.movpossible(i)){
                Tauler aux = new Tauler(t);
                aux.afegeix(i, color);
                if(aux.solucio(i,color)){
                    return i;
                }
                valorActual = minimitzar(aux, InfinitNegatiu, InfinitPositiu, profunditat, color);
                System.out.println("accio -> "+ i +" valor accio = "+valorActual);
                if(valorActual > millorValor || !t.movpossible(millor_columna)){
                    millor_columna = i;
                    millorValor = valorActual;
                }
            }
        }
        System.out.println();
        return millor_columna;
    }

    /**
     * Fitxa del oponent
     * @param jugador
     * @return la peca del oponent
     */
    private int oponent(int jugador) {
        return -jugador;
    }

    /**
     * Minimitza el valor de la funcio alfabeta
     * @param t
     * @param alfa
     * @param beta
     * @param profunditat
     * @param jugador
     * @return el valor corresponent a minimitzar
     */
    private int minimitzar(Tauler t, int alfa, int beta, int profunditat, int jugador){
        if(profunditat == 0 || !t.espotmoure()) return evaluarTauler(t, jugador);
        for(int i = 0; i < t.getMida(); i++){
            if(t.movpossible(i)){
                Tauler aux = new Tauler(t);
                aux.afegeix(i, oponent(jugador));
                if (aux.solucio(i, oponent(jugador))) {
                    return InfinitNegatiu;
                }
                beta = Math.min(beta, maximitzar(aux, alfa, beta, profunditat-1, jugador));
                if(beta <= alfa) return beta;
            }
        }
        return beta;
    }

    /**
     * Maximitza el valor de la funcio alfabeta
     * @param t
     * @param alfa
     * @param beta
     * @param profunditat
     * @param jugador
     * @return el valor corresponent a minimitzar
     */
    private int maximitzar(Tauler t, int alfa, int beta, int profunditat, int jugador){
        if(profunditat == 0 || !t.espotmoure()) return evaluarTauler(t,jugador);
        for(int i = 0 ;i < t.getMida(); i++){
            if(t.movpossible(i)){
                Tauler aux = new Tauler (t);
                aux.afegeix(i, jugador);
                if(aux.solucio(i,jugador)) {
                    return InfinitPositiu;
                }
                alfa = Math.max(alfa, minimitzar(aux, alfa, beta, profunditat - 1, jugador));
                if(alfa >= beta) return alfa;
            }
        }
        return alfa;
    }

    /**
     * Evalua cada posicio del tauler i computa una heuristica
     * @param t
     * @param jugador
     * @return la heuristica
     */
    private int evaluarTauler(Tauler t, int jugador) {

        int heuristica = 0;
        for (int i = 0; i < t.getMida(); i++) {
            for (int j = 0; j < t.getMida(); j++) {
                heuristica += puntuarCasella(t, i, j, jugador).valor;
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

    /**
     * Puntua la casella del tauler
     * @param t
     * @param fil
     * @param col
     * @param jug
     * @return la puntuacio de la casella
     */
    private Resultat puntuarCasella(Tauler t, int fil, int col, int jug) {
        int maximMenor = -1;
        int maximMajor = t.getMida();

        int jugador = t.getColor(fil, col);
        int j;
        int contador, contadorBlancs, contadorPonderat;
        int heuristicaCasella = 0;

        //Mirar adalt -> Tests OK
        contador = 1;
        contadorBlancs = 0;
        for (int i = fil+1; i < maximMajor && i <= fil+3; i++) {
            if (t.getColor(i, col) == jugador) contador++;
            else{
                break;
            }
        }

        heuristicaCasella += jugador == jug ? calcularPuntuacio(contador, contadorBlancs) : -calcularPuntuacio(contador, contadorBlancs);

        //Mirar diagonal adalt dreta -> Tests OK
        contador = 1;
        contadorBlancs = 0;
        j = col+1;
        for (int i = fil+1; i < maximMajor && j < maximMajor && i <= fil+3; i++) {
            if (t.getColor(i, j++) == jugador) contador++;
            else if (t.getColor(i, col) == 0){
                //mirar los espacios verticales para abajo
                contadorBlancs = espaisRestants(i,col,t);
            }
        }
        heuristicaCasella += jugador == jug ? calcularPuntuacio(contador, contadorBlancs) : -calcularPuntuacio(contador, contadorBlancs);

        //Mirar dreta -> Tests OK
        contador = 1;
        contadorBlancs = 0;
        for (int i = col+1; i < maximMajor && i <= col+3; i++) {
            if (t.getColor(fil, i) == jugador) contador++;
            else if (t.getColor(i, col) == 0){
                //mirar los espacios verticales para abajo
                contadorBlancs = espaisRestants(i,col,t);
            }
        }

        heuristicaCasella += jugador == jug ? calcularPuntuacio(contador, contadorBlancs) : -calcularPuntuacio(contador, contadorBlancs);

        //Mirar diagonal adalt esquerra -> Tests OK
        contador = 1;
        contadorBlancs = 0;
        j = col-1;
        for (int i = fil+1; i < maximMajor && j > maximMenor && i <= fil+3; i++) {
            if (t.getColor(i, j--) == jugador) contador++;
            else if (t.getColor(i, col) == 0){
                //mirar los espacios verticales para abajo
                contadorBlancs = espaisRestants(i,col,t);
            }
        }

        heuristicaCasella += jugador == jug ? calcularPuntuacio(contador, contadorBlancs) : -calcularPuntuacio(contador, contadorBlancs);

        return new Resultat(false, jugador, heuristicaCasella);
    }

    /**
     * Calcula el comput de la casella tenint en compte les fitxes que te i les que queden per completar el 4 en ratlla
     * @param puntuacio
     * @param moviments
     * @return la puntuacio de la casella
     */
    int calcularPuntuacio(int puntuacio, int moviments){
        int puntuacioMoviments = 4 - moviments;
        if(puntuacio==0) return 0;
        else if(puntuacio==1) return 1*puntuacioMoviments;
        else if(puntuacio==2) return 10*puntuacioMoviments;
        else if(puntuacio==3) return 100*puntuacioMoviments;
        else return 1000;
    }
}
