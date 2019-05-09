package ch.epfl.javass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PlayersKind {
    
    h( "h:<nom>  un joueur humain nommé <nom>"),
    s( "s:<nom>  un joueur simulé nommé <nom>,"
            + " [<graine> la graine du joueur <graine>],"
            + " [<iterations> un int plus grand où égal à 9  <iterations>]"),
    r( "r:<nom>  un joueur distant nommé <nom>,"
            + " <ip> l'adrresse ip du joueur <ip>");
    
    
    public static final List<PlayersKind> ALL = Collections.unmodifiableList(Arrays.asList(values()));
    

    private final String description;
    
    private PlayersKind( String descr) {
        this.description=descr;
    }
    
 
    
    public String getDescription() {
        return description;
    }

}
