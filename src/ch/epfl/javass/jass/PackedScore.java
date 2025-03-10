package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * plusieurs méthodes utiles pour représenter un score dans un seul long
 * 
 * @author erwan serandour (296100)
 *
 */
// 2 équipes 2 fois 32 bits
// pour une équipe,
// bits : 0 à 3 nb Plis gagné
// bits : 4 à 12 points du tour
// bits : 12 à 23 points de la partie
// bits : 24 32 des zéro
public final class PackedScore {
    /**
     * le score initiale
     */
    public static final long INITIAL = 0;

    private static final int START_OF_TEAM1 = 0;
    private static final int START_OF_TEAM2 = 32;
    private static final int NB_BITS_PER_TEAM = Integer.SIZE;

    private static final int BIT_SIZE_OF_TRICKS = 4;
    private static final int BIT_SIZE_OF_TURN = 9;
    private static final int BIT_SIZE_OF_GAME = 11;

    private static final int START_OF_TRICK = 0;
    private static final int START_OF_TURN = BIT_SIZE_OF_TRICKS + START_OF_TRICK;
    private static final int START_OF_GAME = START_OF_TURN + BIT_SIZE_OF_TURN;

    private static final int MAX_TRICK_PER_TURN = Jass.TRICKS_PER_TURN;
    private static final int MAX_POINT_PER_TURN = 257;
    private static final int MAX_POINT_PER_GAME = 2000;

    private PackedScore() {
    }

    // teste si le score d'une équipe est correct sur 32 bits
    private static boolean isValid(int pkScore) {
        if (Bits32.extract(pkScore, START_OF_TRICK,BIT_SIZE_OF_TRICKS) 
                > MAX_TRICK_PER_TURN) {
            return false;
        }

        if (Bits32.extract(pkScore, START_OF_TURN,BIT_SIZE_OF_TURN) 
                > MAX_POINT_PER_TURN) {
            return false;
        }

        if (Bits32.extract(pkScore, START_OF_GAME,BIT_SIZE_OF_GAME) 
                > MAX_POINT_PER_GAME) {
            return false;
        }
        // le 8 dernier bits sont nuls
        if (Bits32.extract(pkScore, 24, 8) != 0) {
            return false;
        }

        return true;
    }

    /**
     * retourne vraie si les champ de pkScore contiennent des valeurs valides
     * (pli<=9,point<=257,le totale des points<=2000)
     * 
     * @param pkScore
     *            le score empaqueté dans un long
     * @return vraie si les champ de pkScore contiennent des valeurs valides
     *         (pli<=9,point<=257,le totale des points<=2000)
     */
    public static boolean isValid(long pkScore) {
        int B0To31 = (int) Bits64.extract(pkScore, START_OF_TEAM1, NB_BITS_PER_TEAM);
        int B32To63 = (int) Bits64.extract(pkScore, START_OF_TEAM2, NB_BITS_PER_TEAM);
        return isValid(B0To31) && isValid(B32To63);
    }

    
    private static int oneTeamScorePk(int turnTricks, int turnPoints, int gamePoints) {
        assert (turnTricks >= 0 && turnTricks <= MAX_TRICK_PER_TURN);
        assert (turnPoints >= 0 && turnPoints <= MAX_POINT_PER_TURN);
        assert (gamePoints >= 0 && gamePoints <= MAX_POINT_PER_GAME);
        return Bits32.pack(turnTricks, BIT_SIZE_OF_TRICKS, turnPoints,
                BIT_SIZE_OF_TURN, gamePoints, BIT_SIZE_OF_GAME);
    }

    /**
     * retourne un score empaqueté dans un long
     * 
     * @param turnTricks1
     *            le nombre de pli gagné par l'équipe 1
     * @param turnPoints1
     *            le nombre de points (durant le tour) de l'équipe 1
     * @param gamePoints1
     *            le nombre de points (durant la partie) de l'équipe 1
     * @param turnTricks2
     *            le nombre de pli gagné par l'équipe 2
     * @param turnPoints2
     *            le nombre de points (durant le tour) de l'équipe 2
     * @param gamePoints2
     *            le nombre de points (durant la partie) de l'équipe 1
     * @return un score empaqueté dans un long
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {
        long B0To31 = oneTeamScorePk(turnTricks1, turnPoints1, gamePoints1);
        long B32To63 = oneTeamScorePk(turnTricks2, turnPoints2, gamePoints2);
        return Bits64.pack(B0To31, NB_BITS_PER_TEAM, B32To63, NB_BITS_PER_TEAM);
    }

    private static int extractPkTeam(long pkScore, TeamId t) {
        assert isValid(pkScore);
        if (t == TeamId.TEAM_1) {
            return (int) Bits64.extract(pkScore, START_OF_TEAM1, NB_BITS_PER_TEAM);
        }
        return (int) Bits64.extract(pkScore, START_OF_TEAM2, NB_BITS_PER_TEAM);
    }

    /**
     * retourne le nombre de pli gagné par l'équipe donnée
     * 
     * @param pkScore
     *            le score empaqueté (valide)
     * @param t
     *            l'équipe
     * @return le nombre de pli gagné par l'équipe donnée
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return Bits32.extract(extractPkTeam(pkScore, t), START_OF_TRICK, BIT_SIZE_OF_TRICKS);
    }

    /**
     * retourne le nombre de points (durant le tour) gagné par l'équipe donnée
     * 
     * @param pkScore
     *            le score empaqueté (valide)
     * @param t
     *            l'équipe
     * @return le nombre de point (durant le tour) gagné par l'équipe donnée
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return Bits32.extract(extractPkTeam(pkScore, t), START_OF_TURN, BIT_SIZE_OF_TURN);
    }

    /**
     * retourne le nombre de points (durant la partie) gagné par l'équipe donnée
     * 
     * 
     * @param pkScore
     *            le score empaqueté (valide)
     * @param t
     *            l'équipe
     * @return le nombre de points (durant la partie) gagné par l'équipe donnée
     * 
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return Bits32.extract(extractPkTeam(pkScore, t), START_OF_GAME, BIT_SIZE_OF_GAME);
    }

    /**
     * retourne le nombre de points gagné au totale durant la partie par
     * l'équipe donnée (ceux du tour + partie)
     * 
     * @param pkScore
     *            le score empaqueté (valide)
     * @param t
     *            l'équipe
     * @return le nombre de points gagné au totale durant la partie par l'équipe
     *         donnée (ceux du tour + partie)
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * retourne un score empqueté avec les champs mis à jour
     * 
     * @param pkScore
     *            le score empaqueté de départ (valide)
     * @param winningTeam
     *            l'équipe gagante
     * @param trickPoints
     *            le nombre de points supplémentaire pour l'équipe gagnante
     * @return un score empqueté avec les champs mis à jour en tenant compte que
     *         l'équipe gagante à gagner trickPoints
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert isValid(pkScore);
        assert trickPoints <= MAX_POINT_PER_TURN;

        int turnTricks = turnTricks(pkScore, winningTeam) + 1;
        int turnPoints = turnPoints(pkScore, winningTeam) + trickPoints;
        int totalPoints = gamePoints(pkScore, winningTeam);

        if (turnTricks == Jass.TRICKS_PER_TURN) {
            turnPoints += Jass.MATCH_ADDITIONAL_POINTS;
        }

        int winningTeamPk = oneTeamScorePk(turnTricks, turnPoints, totalPoints);
        int losingTeamPk = extractPkTeam(pkScore, winningTeam.other());

        if (winningTeam == TeamId.TEAM_1) {
            return Bits64.pack(winningTeamPk, NB_BITS_PER_TEAM, losingTeamPk, NB_BITS_PER_TEAM);
        }
        return Bits64.pack(losingTeamPk, NB_BITS_PER_TEAM, winningTeamPk, NB_BITS_PER_TEAM);
    }

    /**
     * retourne les scores empaquetés donnés mis à jour pour le tour prochain
     * 
     * @param pkScore
     *            le score empqueté
     * @return les scores empaquetés donnés mis à jour pour le tour prochain
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore);
        // les points du tour et le nb plis gagné sont mis à zéro
        int team1Pk = oneTeamScorePk(0, 0, totalPoints(pkScore, TeamId.TEAM_1));
        int team2Pk = oneTeamScorePk(0, 0, totalPoints(pkScore, TeamId.TEAM_2));
        return Bits64.pack(team1Pk, NB_BITS_PER_TEAM, team2Pk, NB_BITS_PER_TEAM);
    }

    private static String teamToString(long pkScore, TeamId t) {
        return "plie: " + turnTricks(pkScore, t) + " tour: "
                + turnPoints(pkScore, t) + " partie: " + gamePoints(pkScore, t);
    }

    /**
     * retourne une chaine de caractère représantant le score
     * 
     * @param pkScore
     *            le score empaqueté
     * @return retourne une chaine de caractère représantant le score
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);
        return teamToString(pkScore, TeamId.TEAM_1) + " / "
                + teamToString(pkScore, TeamId.TEAM_2);
    }

}
