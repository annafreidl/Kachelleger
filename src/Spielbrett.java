import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Spielbrett {
	

    private int width, height;
    private Platz[][] platzfelder;
    private Pixelvergleich p_vergleich;
 
    public Spielbrett(int width, int height, Pixelvergleich kachelTool) {
        this.width = width;
        this.height = height;
        this.p_vergleich = kachelTool;

        // Lege zweidimensionales Array an
        this.platzfelder = new Platz[this.width][this.height];
       
        // F�lle Array an jeder Position mit Platz-Objekt
        for (int x=0;x<this.width;x++) {
            for (int y=0;y<this.height;y++) {
                this.platzfelder[x][y] = new Platz(x, y, this);
            }
        }
        System.out.println("Feld initialisiert.");
    }

    
    public int getWidth(){
        return this.width;
    }
   
    public int getHeight(){
        return this.height;
    }

    public boolean istAufBrett(int x, int y) {
        if ((x >= 0) && (x < this.width) && (y >= 0) && (y < this.height)){
            return true;
        }
        return false;
    }
   
    public Platz getPlatz(int x, int y) {
        return this.platzfelder[x][y];
    }

    // Kachel f�r den neuen Platz ausw�hlen
    public void randomizePlatz(Platz p) {
        List<Kachel> geladeneKacheln = this.p_vergleich.getKacheln();
        if (p.getStatus() == PlatzStatus.FREI) {
            List<Kachel> passendeKacheln = getPossibleKachel(p);
            if (passendeKacheln.size() == 0) {
                System.err.println("Feld hat keine passenden Kacheln, aber ist nicht blockiert.");
            } else {
                // W�hle neue Kachel aus, die passt...
                int randomKachelIndex = new Random().nextInt(passendeKacheln.size());
                platziereKachel(p, passendeKacheln.get(randomKachelIndex));
                // �berpr�fe, ob durch die neue Kachel neue "BLOCKIERT"-Felder entstanden sind
                for (Platz nachbar: p.getFreieNachbarn()) {
                    if (getPossibleKachel(nachbar).size() == 0) {
                        platziereBlockiert(nachbar);
                    }
                }
            }
        }
    }

    // wird bei Dr�cken der Leertaste aufgerufen
    public void erweitere() {
        List<Kachel> geladeneKacheln = this.p_vergleich.getKacheln();
        List<Platz> gesetztePlaetze = getGesetztePlaetze();
        List<Platz> freie_nachbarn = new ArrayList<Platz>(gesetztePlaetze.size()*4);
        for (Platz gesetzt: gesetztePlaetze) {
            for (Platz freierPlatz: gesetzt.getFreieNachbarn()) {
                // Überprüfe, ob schon vorhanden, um Duplikate zu vermeiden
                if (!freie_nachbarn.contains(freierPlatz)) {
                    freie_nachbarn.add(freierPlatz);
                }
            }
        }

        for (Platz frei:freie_nachbarn) {
            randomizePlatz(frei);
        }

    }

    // wird bei dr�cken der Enter Taste aufgerufen
    public void leere() {
        for (int x=0; x<this.width; x++) {
            for (int y=0; y<this.height; y++ ){
                Platz p = getPlatz(x,y);
                platziereFrei(p);
            }
        }

    }

    // Wenn Kachel entfernt wird, muss gepr�ft werden ob evtl. blockierte Felder wieder frei werden
    public void entferneKachel(Platz p) {
        if (p.getStatus() == PlatzStatus.BESETZT) {
            List<Kachel> geladeneKacheln = this.p_vergleich.getKacheln();
            platziereFrei(p);
            for (Platz nachbar: p.getBlockierteNachbarn()) {
                List<Kachel> passendeKacheln = getPossibleKachel(nachbar);
                if (passendeKacheln.size() > 0) {
                    // �ndere blockierten Nachbarn zu frei
                    platziereFrei(nachbar);
                }
            }
        }
    }

    // Kachel platzieren und Status erneuern
    public void platziereKachel(Platz p, Kachel k) {
        if (p.getStatus() == PlatzStatus.FREI) {
            p.setStatus(PlatzStatus.BESETZT);
            p.setKachel(k);
        } else {
            System.err.println("Irgendwas lief schief beim SETZEN. Die Kachel ist FREI");
        }
    }

    // evtl. Status von Platz auf Status Blockiert setzen
    public void platziereBlockiert(Platz p) {
        if (p.getStatus() == PlatzStatus.FREI) {
            p.setStatus(PlatzStatus.BLOCKIERT);
            p.setKachel(null);
        } else {
            System.err.println("Irgendwas lief schief beim BLOCKIEREN. Die Kachel ist FREI");
        }
    }
    
    public void platziereFrei(Platz p) {
        p.setStatus(PlatzStatus.FREI);
        p.setKachel(null);
    }

    
    public List<Kachel> getPossibleKachel(Platz p) {
        // Idee:
        // Starte mit leerer Liste an passenden Kacheln
        // Laufe alle besetzten Nachbarn ab, und schreibe alle Kacheln heraus, die nicht passen.
        // Dann: Laufe durch ALLE Kacheln und werfe die Kacheln heraus,
        // die in der Liste mit unpassenden Kacheln stehen.

        List<Kachel> geladeneKacheln = this.p_vergleich.getKacheln();
        List<Kachel> verboteneKacheln = new ArrayList<>(geladeneKacheln.size());

        // Filtere Kacheln
        List<Platz> nachbarn = p.getBesetzteNachbarn();
        for (Platz nachbar: nachbarn) {
            for (Kachel k: geladeneKacheln) {
                // �berpr�fe, ob schon ausgeschlossen
                if (!verboteneKacheln.contains(k)) {
                    // Nicht ausgeschlossen
                    // -> Vergleiche, ob Kachel nicht passt
                    if (!this.p_vergleich.vergleicheKacheln(p, k, nachbar)) {
                        // Kachel passt nicht -> verbiete Kachel
                        verboteneKacheln.add(k);
                    }
                }
            }
        }
        
        // Lade alle nicht-verbotenen Kacheln in die Liste
        List<Kachel> passendeKacheln = new ArrayList<>(geladeneKacheln.size());
        for (Kachel k: geladeneKacheln) {
            if (!verboteneKacheln.contains(k)) {
                passendeKacheln.add(k);
            }
        }
        return passendeKacheln;
    }

    private List<Platz> getGesetztePlaetze() {
        List<Platz> besetztePlaetze = new ArrayList<>(this.height*this.width);
        Platz p;
        // Suche alle Felder auf gesetzte Pl�tze
        for (int x=0;x<this.width;x++){
            for (int y=0;y<this.height;y++) {
                p = this.getPlatz(x,y);
                if (p.getStatus() == PlatzStatus.BESETZT) {
                    besetztePlaetze.add(p);
                }
            }
        }
        return besetztePlaetze;
    }
}