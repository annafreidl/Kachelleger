import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

// Für Dateisuche
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;


public class Pixelvergleich {
	
	    private List<Kachel> kacheln;
	    private int tiefe;
	    private double fehlerrate;

	    public Pixelvergleich(double vergleich_fehlerrate, int vergleich_tiefe) throws IOException {
	        this.kacheln = this.loadKacheln();
	        this.tiefe = vergleich_tiefe;
	        this.fehlerrate = vergleich_fehlerrate;
	    }

	    public List<Kachel> getKacheln() {
	        return this.kacheln;
	    }

	    // Kacheln aus dem "images" Ordner laden
	    private List<Kachel> loadKacheln() throws IOException {
	        List<Path> gefunden = new ArrayList<Path>();

	        // von: https://javapapers.com/java/glob-with-java-nio/
	        // und: https://stackoverflow.com/questions/4871051/how-to-get-the-current-working-directory-in-java
	        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(
	                "glob:**/images/kachel*.png");

	        Files.walkFileTree(Paths.get(System.getProperty("user.dir")), new SimpleFileVisitor<Path>() {

	            @Override
	            public FileVisitResult visitFile(Path path,
	                                             BasicFileAttributes attrs) throws IOException {
	                if (pathMatcher.matches(path)) {
	                    gefunden.add(path);
	                }
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult visitFileFailed(Path file, IOException exc)
	                    throws IOException {
	                return FileVisitResult.CONTINUE;
	            }
	        });

	         //Gebe Dateipfad aus zur Info
	        for (Path kachel_pfad: gefunden) {
	            System.out.println("Kachelbild in: " + kachel_pfad.toString());	        }

	        // Array mit geladenen Kacheln erstellen
	        List<Kachel> gefundene_kacheln = new ArrayList<Kachel>();
	        for (Path kachel_pfad: gefunden) {
	            Kachel neue_kachel = new Kachel(kachel_pfad.toString());
	            gefundene_kacheln.add(neue_kachel);
	        }
	        return gefundene_kacheln;
	    }

	// vergleich der Farben zweier Pixel auf �hnlichkeit
	private double vergleicheFarben(Color c1, Color c2) {
        double diffRot = Math.abs(c1.getRed() - c2.getRed());
        double diffGruen = Math.abs(c1.getGreen() - c2.getGreen());
        double diffBlau = Math.abs(c1.getBlue() - c2.getBlue());

        // umso kleiner der Wert umso �hnlicher die Farben
        return (diffRot + diffGruen + diffBlau) / 3.0; 
    }

    // vertikale Kanten von 2 Kacheln vergleichen
	private boolean vergleicheBilderVertikal(Image bild_oben, Image bild_unten) {
        if (bild_oben.getWidth() != bild_unten.getWidth()) { // Abfangen falls unterschiedliche Breite
            System.err.println("Breiten zwischen Bildern sind unterschiedlich!");
        }
        
        int pxBildBreite = (int)(bild_oben.getWidth());
        int pxBildHoehe = (int)(bild_unten.getHeight());
        int pxHoehe = this.tiefe;

        PixelReader px_b_oben = bild_oben.getPixelReader();
        PixelReader px_b_unten = bild_unten.getPixelReader();
        Color c_b_oben, c_b_unten;

        
        double abweichungen = 0.0;
        for (int x=0; x<pxBildBreite; x++) {
            for (int y=0;y<pxHoehe;y++) {
                c_b_oben = px_b_oben.getColor(x, (pxBildHoehe - 1) - y) ;
                c_b_unten = px_b_unten.getColor(x, y);
                abweichungen = abweichungen + vergleicheFarben(c_b_oben, c_b_unten);
            }
        }

        double durchschnittlicher_fehler = abweichungen / (pxHoehe * pxBildBreite);
        // true = Kacheln sind sich �hnlich --> potentieller Nachbar
        return durchschnittlicher_fehler < this.fehlerrate;
    }

	// vertikale Kanten von 2 Kacheln vergleichen
	private boolean vergleicheBilderHorizontal(Image bild_links, Image bild_rechts) {
        if (bild_links.getHeight() != bild_rechts.getHeight()) { // Abfangen falls unterschiedliche H�hen
            System.err.println("H�hen zwischen Bildern sind unterschiedlich!");
        }
        int pxBildBreite = (int)(bild_links.getWidth());
        int pxBildHoehe = (int)(bild_rechts.getHeight());
        int pxBreite = this.tiefe;

        PixelReader px_b_links = bild_links.getPixelReader();
        PixelReader px_b_rechts = bild_rechts.getPixelReader();
        Color c_b_links, c_b_rechts;

        double abweichungen = 0.0;
        for (int x=0;x<pxBreite;x++) {
            for (int y=0;y<pxBildHoehe;y++) {
                c_b_links = px_b_links.getColor(pxBildBreite - 1 - x, y);
                c_b_rechts = px_b_rechts.getColor(x, y);
                abweichungen = abweichungen + vergleicheFarben(c_b_links, c_b_rechts);
            }
        }

        double durchschnittlicher_fehler = abweichungen / (pxBreite * pxBildHoehe);
        return durchschnittlicher_fehler < this.fehlerrate;
    }

    // Status von Nachbarkachel auslesen (BESETZT, BLOCKIERT oder FREI) und wenn frei, 
	// dann pr�fen ob "nachbarPlatz" ein potentieller Nachbar ist
	public boolean vergleicheKacheln(Platz neuerPlatz, Kachel neue_kachel, Platz nachbarPlatz) {
        if (nachbarPlatz.getStatus() != PlatzStatus.BESETZT){
            // Nachbar hat keine Kachel
            return true;
        }
        int neu_x = neuerPlatz.getX();
        int neu_y = neuerPlatz.getY();
        int nachbar_x = nachbarPlatz.getX();
        int nachbar_y = nachbarPlatz.getY();

        Image neuesBild = neue_kachel.getImage();
        Image nachbarBild = nachbarPlatz.getKachel().getImage();
        boolean res;
        if (neu_x == nachbar_x) {
            if ((neu_y-1) == nachbar_y) {
                // neuerPlatz unter nachbarPlatz
                System.out.println("Fall 1");
                res = vergleicheBilderVertikal(nachbarBild, neuesBild);
                System.out.println(res);
                return res;
            } else if ((neu_y+1) == nachbar_y) {
                // neuerPlatz über nachbarPlatz
                System.out.println("Fall 2");
                res = vergleicheBilderVertikal(neuesBild, nachbarBild);
                System.out.println(res);
                return res;
            } else {
                System.err.println("Kachelvergleich mit Platz außerhalb der Nachbarschaft. Irgendwas stimmt hier nicht.");
            }
        } else if (neu_y == nachbar_y) {
            if ((neu_x-1) == nachbar_x) {
                // neuerPlatz rechts von nachbarPlatz
                System.out.println("Fall 3");
                res = vergleicheBilderHorizontal(nachbarBild, neuesBild);
                System.out.println(res);
                return res;
            } else if ((neu_x+1) == nachbar_x) {
                // neuerPlatz links von nachbarPlatz
                System.out.println("Fall 4");
                res = vergleicheBilderHorizontal(neuesBild, nachbarBild);
                System.out.println(res);
                return res;
            } else {
                System.err.println("Kachelvergleich mit Platz außerhalb der Nachbarschaft. Irgendwas stimmt hier nicht.");
            }
        } else {
            System.err.println("Kachelvergleich mit Platz außerhalb der Nachbarschaft. Irgendwas stimmt hier nicht.");
        }

        // Der restliche Abschnitt sollte eigentlich nie aufgerufen werden.
        System.err.println("Kachelvergleich ist an einer unbekannten Stelle fehlgeschlagen.");
        return false;

    }
	
	
	
}
