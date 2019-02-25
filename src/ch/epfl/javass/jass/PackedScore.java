package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * plusieurs méthodes utiles pour représenter un score dans un seul long
 * 
 * @author erwan serandour (296100)
 *
 */

public final class PackedScore {
    public static final long INITIAL=0;
    
    private PackedScore() {}
    
    private static boolean isValid(int pkScore) {
        if(Bits32.extract(pkScore, 0, 4)>9) {
            return false;
        }
        
        if(Bits32.extract(pkScore, 4, 9)>257) {
            return false;
        }
        
        if(Bits32.extract(pkScore, 13, 11)>2000) {
            return false;
        }
        
        if(Bits32.extract(pkScore, 24, 8)!=0) {
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
     * @return vraie si les champ de pkScore contiennent des valeurs
     *         valides (pli<=9,point<=257,le totale des points<=2000)
     */
    public static boolean isValid(long pkScore) {
        int B0To31=(int)Bits64.extract(pkScore, 0, Integer.SIZE);
        int B32To63=(int)Bits64.extract(pkScore, 32, Integer.SIZE);
        return isValid(B0To31)&&isValid(B32To63);
    }
    
    private static int oneTeamScorePk(int turnTricks, int turnPoints, int gamePoints) {
        assert (turnTricks>=0 && turnTricks<=9);
        assert (turnPoints>=0 && turnPoints<=257);
        assert (gamePoints>=0 && gamePoints<=2000);
        return Bits32.pack(turnTricks, 4, turnPoints,9, gamePoints, 11);
        
    }
    
    /**
     * retourne un score empaqueté dans un long
     * 
     * @param turnTricks1
     *            le nombre de pli de l'équipe 1
     * @param turnPoints1
     *            le nombre de points (durant le tour) de l'équipe 1
     * @param gamePoints1
     *            le nombre de points (durant la partie) de l'équipe 1
     * @param turnTricks2
     *            le nombre de pli de l'équipe 2
     * @param turnPoints2
     *            le nombre de points (durant le tour) de l'équipe 2
     * @param gamePoints2
     *            le nombre de points (durant la partie) de l'équipe 1
     * @return un score empaqueté dans un long
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
       long B0To31= oneTeamScorePk( turnTricks1,  turnPoints1,  gamePoints1);
       long B32To63= oneTeamScorePk( turnTricks2,  turnPoints2,  gamePoints2);
       return Bits64.pack(B0To31, Integer.SIZE,B32To63, Integer.SIZE);
    }
    
    /**
     * @param pkScore
     * @param t
     * @return
     */
    private static int extractPkTeam(long pkScore, TeamId t) {
        assert isValid( pkScore);
        if(t==TeamId.TEAM_1) {
            return (int)Bits64.extract(pkScore,0, Integer.SIZE);
        }
        return (int)Bits64.extract(pkScore,Integer.SIZE, Integer.SIZE);
    } 
    
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid( pkScore);
        return Bits32.extract(extractPkTeam( pkScore, t), 0, 4);
    }
    
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid( pkScore);
        return Bits32.extract(extractPkTeam( pkScore, t), 4, 9);
    }
    
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid( pkScore);
        return Bits32.extract(extractPkTeam( pkScore, t), 13, 11);
    }
    
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid( pkScore);
        return gamePoints( pkScore,  t)+turnPoints( pkScore,  t);
    }
    
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert isValid( pkScore);
        assert trickPoints<257;
        
        int turnTricks=turnTricks( pkScore,  winningTeam)+1;
        assert turnTricks<=9;
        int turnPoints =turnPoints( pkScore,  winningTeam)+trickPoints;
        int totalPoints =totalPoints( pkScore, winningTeam);
        
        if(turnTricks==9) {
            turnPoints+=100;
        }
         
        int winningTeamPk =oneTeamScorePk(turnTricks,  turnPoints, totalPoints);
        int losingTeamPk = extractPkTeam( pkScore, winningTeam.other());
        
        if(winningTeam==TeamId.TEAM_1) {
            return Bits64.pack(winningTeamPk, 32,losingTeamPk , 32);
        }
        return Bits64.pack(losingTeamPk, 32,winningTeamPk , 32);
    }
    
    public static long nextTurn(long pkScore) {
        assert isValid( pkScore);
        
        int team1Pk = oneTeamScorePk(0, 0,  totalPoints( pkScore,TeamId.TEAM_1 ));
        int team2Pk = oneTeamScorePk(0, 0,  totalPoints( pkScore,TeamId.TEAM_2 ));
        return Bits64.pack(team1Pk, 32, team2Pk, 32);
    }
    
    private static String teamToString(long pkScore,TeamId t) {
        return "plie: "+turnTricks(pkScore, t)+" tour: "+turnPoints(pkScore,t)+" partie: "+gamePoints(pkScore,  t);
    }
    
    public static String toString(long pkScore) {
        assert isValid( pkScore);
        return teamToString( pkScore,TeamId.TEAM_1)+" / "+teamToString( pkScore,TeamId.TEAM_2);
    }

}
