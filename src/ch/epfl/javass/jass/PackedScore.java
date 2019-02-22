package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

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
    
    public static boolean isValid(long pkScore) {
        int B0To31=(int)Bits64.extract(pkScore, 0, Integer.SIZE);
        int B32To63=(int)Bits64.extract(pkScore, 32, Integer.SIZE);
        return isValid(B0To31)&&isValid(B32To63);
    }
    
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
       assert (turnTricks1>=0 && turnTricks1<=9) && (turnTricks2>=0 && turnTricks2<=9 );
       assert (turnPoints1>=0 && turnPoints1<=257) && (turnPoints2>=0 && turnPoints2<=257);
       assert (gamePoints1>=0 && gamePoints1<=2000) && (gamePoints2>=0 && gamePoints2<=2000);
       long B0To31= Bits32.pack(turnTricks1, 4, turnPoints1,9, gamePoints1, 11);
       long B32To63= Bits32.pack(turnTricks2, 4, turnPoints2,9, gamePoints2, 11);
       return Bits64.pack(B0To31, Integer.SIZE,B32To63, Integer.SIZE);
    }
    
    private static int extractPkTeam(long pkScore, TeamId t) {
        if(t==TeamId.TEAM_1) {
            return (int)Bits64.extract(pkScore,0, Integer.SIZE);
        }
        return (int)Bits64.extract(pkScore,32, Integer.SIZE);
    } 
    
    public static int turnTricks(long pkScore, TeamId t) {
        return Bits32.extract(extractPkTeam( pkScore, t), 0, 4);
    }
    
    public static int turnPoints(long pkScore, TeamId t) {
        return Bits32.extract(extractPkTeam( pkScore, t), 4, 9);
    }
    
    public static int gamePoints(long pkScore, TeamId t) {
        return Bits32.extract(extractPkTeam( pkScore, t), 13, 11);
    }
    
    public static int totalPoints(long pkScore, TeamId t) {
        return gamePoints( pkScore,  t)+turnPoints( pkScore,  t);
    }
    
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        int turnTricks=turnTricks( pkScore,  winningTeam)+1;
        int turnPoints =turnPoints( pkScore,  winningTeam)+trickPoints;
        int totalPoints =totalPoints( pkScore, winningTeam);
        if(turnTricks==9) {
            turnPoints+=100;
        }
         
        int winningTeamPk = Bits32.pack(turnTricks, 4, turnPoints, 9,totalPoints,11);
        int losingTeamPk = extractPkTeam( pkScore, TeamId.other(winningTeam));
        
        if(winningTeam==TeamId.TEAM_1) {
            return Bits64.pack(winningTeamPk, 32,losingTeamPk , 32);
        }
        return Bits64.pack(losingTeamPk, 32,winningTeamPk , 32);
    }
    
    public static long nextTurn(long pkScore) {
        int team1Pk = Bits32.pack(0, 4, 0, 9, totalPoints( pkScore,TeamId.TEAM_1 ), 11);
        int team2Pk = Bits32.pack(0, 4, 0, 9, totalPoints( pkScore,TeamId.TEAM_2 ), 11);
        return Bits64.pack(team1Pk, 32, team2Pk, 32);
    }
    
    public static String toString(long pkScore) {
        
    }

}
