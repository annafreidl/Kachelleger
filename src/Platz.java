
import java.util.ArrayList;
import java.util.List;

// Plätze können den Status frei, besetzt oder blockiert einnehmen
enum PlatzStatus {
    FREI, BESETZT, BLOCKIERT
}

// ein Platz kann vier Nachbarn haben...
enum PlatzNachbarTyp {
    LINKS, RECHTS, OBEN, UNTEN
}

public class Platz {
    private int x, y;
    private Spielbrett brett;
    private PlatzStatus status;
    private Kachel kachel;

    
    // Zu Anfang ist jeder Platz frei
    public Platz(int x, int y, Spielbrett brett) {
        this.x = x;
        this.y = y;
        this.brett = brett;
        this.status = PlatzStatus.FREI;
        this.kachel = null;
        System.out.println("Platz an Koordinate: " + String.valueOf(this.x) + ", " + String.valueOf(this.y));
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public PlatzStatus getStatus() {
        return this.status;
    }

    public Kachel getKachel() {
        return this.kachel;
    }
    public void setKachel(Kachel kachel) {
        this.kachel = kachel;
    }
    public void setStatus(PlatzStatus status) {
        this.status = status;
    }

    // je nachdem wo der aktuelle Kachel hin soll, muss der Pixelverlgleich an der oberen/unteren,
    // linken oder rechten Kante stattfinden
    public Platz getNachbarPlatz(PlatzNachbarTyp nachbar) {
        int nachbar_x = this.x;
        int nachbar_y = this.y;

        switch (nachbar) {
            case LINKS:
                nachbar_x = this.x - 1;
                break;
            case RECHTS:
                nachbar_x = this.x + 1;
                break;
            case OBEN:
                // Koordinatensystem ist "umgedreht" (x: 2, y: 2) ist über (x: 2, y: 3)
                nachbar_y = this.y - 1;
                break;
            case UNTEN:
                // Koordinatensystem ist "umgedreht" (x: 2, y: 3) ist unter (x: 2, y: 2)
                nachbar_y = this.y + 1;
                break;
        }

        if (brett.istAufBrett(nachbar_x,nachbar_y)) {
            return brett.getPlatz(nachbar_x, nachbar_y);
        }
        return null;
    }

    
    public List<Platz> getBesetzteNachbarn() {
        // Wir brauchen maximal 4 Plätze weil wir maximal 4 Nachbarn haben
        List<Platz> nachbarn = new ArrayList<>(4);
        Platz p;
        // Sammle Nachbarn die besetzt sind
        p = getNachbarPlatz(PlatzNachbarTyp.LINKS);
        if ((p != null) && p.getStatus() == PlatzStatus.BESETZT)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.RECHTS);
        if ((p != null) && p.getStatus() == PlatzStatus.BESETZT)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.OBEN);
        if ((p != null) && p.getStatus() == PlatzStatus.BESETZT)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.UNTEN);
        if ((p != null) && p.getStatus() == PlatzStatus.BESETZT)
            nachbarn.add(p);

        return nachbarn;
    }
    public List<Platz> getBlockierteNachbarn() {
        // Wir brauchen maximal 4 Plätze weil wir maximal 4 Nachbarn haben
        List<Platz> nachbarn = new ArrayList<>(4);
        Platz p;
        // Sammle Nachbarn die gesperrt sind
        p = getNachbarPlatz(PlatzNachbarTyp.LINKS);
        if ((p != null) && p.getStatus() == PlatzStatus.BLOCKIERT)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.RECHTS);
        if ((p != null) && p.getStatus() == PlatzStatus.BLOCKIERT)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.OBEN);
        if ((p != null) && p.getStatus() == PlatzStatus.BLOCKIERT)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.UNTEN);
        if ((p != null) && p.getStatus() == PlatzStatus.BLOCKIERT)
            nachbarn.add(p);

        return nachbarn;
    }

    public List<Platz> getFreieNachbarn() {
        // Wir brauchen maximal 4 Plätze weil wir maximal 4 Nachbarn haben
        List<Platz> nachbarn = new ArrayList<>(4);
        Platz p;
        // Sammle Nachbarn die nicht besetzt sind
        p = getNachbarPlatz(PlatzNachbarTyp.LINKS);
        if ((p != null) && p.getStatus() == PlatzStatus.FREI)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.RECHTS);
        if ((p != null) && p.getStatus() == PlatzStatus.FREI)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.OBEN);
        if ((p != null) && p.getStatus() == PlatzStatus.FREI)
            nachbarn.add(p);
        p = getNachbarPlatz(PlatzNachbarTyp.UNTEN);
        if ((p != null) && p.getStatus() == PlatzStatus.FREI)
            nachbarn.add(p);

        return nachbarn;
    }
}

