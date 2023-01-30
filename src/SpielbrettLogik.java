
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class SpielbrettLogik {
	
    private Image emptyImage, blockedImage;
    private Pixelvergleich p_vergleich;
    private GridPane kachelGrid;
    private ImageView [][] kachelImageViews;
    private Spielbrett brett;

    
    public SpielbrettLogik(Stage primaryStage, Spielbrett brett, int pxImgBreite, int pxImgHoehe) {
        
    	this.brett = brett;
        this.kachelGrid = new GridPane();
        this.kachelGrid.setGridLinesVisible(true);

        // Berechne Fenstergr��e
        int pxWidth, pxHeight;
        pxWidth = (int)(pxImgBreite * brett.getWidth());
        pxHeight = (int)(pxImgHoehe * brett.getHeight());

        // F�lle kachelGrid f�r ImageView
        kachelImageViews = new ImageView[brett.getWidth()][brett.getHeight()];
        for (int x=0; x < brett.getWidth(); x++) {
            for (int y=0; y < brett.getHeight(); y++) {
                final int x_pos = x;
                final int y_pos = y;

                ImageView img = new ImageView();
                
                // Skaliere Bild
                img.setFitWidth(pxImgBreite);
                img.setFitHeight(pxImgHoehe);

                // MouseEvents setzen f�r Links- und Rechtsklick
                img.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        // Linksklick
                        this.klickAufFeld_links(x_pos,y_pos);
                    } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                        // Rechtsklick
                        this.klickAufFeld_rechts(x_pos,y_pos);
                    }
                });
                
                kachelImageViews[x_pos][y_pos] = img;
                kachelGrid.add(img, x_pos, y_pos);
            }
        }

        // Tastendr�cke �ber Szene abfragen
        Scene scene = new Scene(kachelGrid, pxWidth, pxHeight);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event->{
            if (event.getCode() == KeyCode.SPACE) {
                this.druckAufLeertaste();
            } else if (event.getCode() == KeyCode.ENTER) {
                this.druckAufEscape();
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

        // lade besondere Bilder
        emptyImage = new Image("file:kachel_empty.png");
        blockedImage = new Image("file:kachel_blocked.png");
        zeichneKacheln();
    }

    // wird bei Linksklick ausgef�hrt
    private void klickAufFeld_links(int feld_x, int feld_y) {
        System.out.println("Linksklick auf: " + String.valueOf(feld_x) + ", " + String.valueOf((feld_y)));

        Platz platz = this.brett.getPlatz(feld_x, feld_y);
        if (platz.getStatus() == PlatzStatus.BLOCKIERT) {
            // Mache nichts, weil Feld blockiert ist.
        } else if ((platz.getStatus() == PlatzStatus.FREI) || (platz.getStatus() == PlatzStatus.BESETZT)) {
            brett.randomizePlatz(platz);
        } else{
            // Unbekannter Fehlerzustand
        }
        zeichneKacheln();
    }

    // wird bei Rechtsklick ausgef�hrt
    private void klickAufFeld_rechts(int feld_x, int feld_y) {
        System.out.println("Rechtsklick auf: " + String.valueOf(feld_x) + ", " + String.valueOf((feld_y)));

        Platz platz = this.brett.getPlatz(feld_x, feld_y);
        System.out.println(platz.getStatus());
        if (platz.getStatus() == PlatzStatus.BESETZT) {
            brett.entferneKachel(platz);
        } else if (platz.getStatus() == PlatzStatus.BLOCKIERT) {

        } else if (platz.getStatus() == PlatzStatus.FREI) {
            // Leeres Feld kann man nicht mehr l�schen
        } else{
            // Unbekannter Fehlerzustand
        }
        zeichneKacheln();
    }

    // Brett erweitern bei Druck auf Enter
    private void druckAufLeertaste(){
        System.out.println("Leertaste gedr�ckt");
        brett.erweitere();
        zeichneKacheln();
    }

    // Brett leeren bei Druck auf Leertaste
    private void druckAufEscape(){
        System.out.println("Escape gedr�ckt");
        brett.leere();
        zeichneKacheln();
    }

    // Kacheln je nach Platzstatus zuweisen
    public void zeichneKacheln() {
        for (int x=0;x< this.brett.getWidth();x++) {
            for (int y=0;y< brett.getHeight();y++) {

                Platz platz = this.brett.getPlatz(x,y);
                ImageView img = this.kachelImageViews[x][y];

                if (platz.getStatus() == PlatzStatus.FREI) {
                    img.setImage(emptyImage);
                } else if (platz.getStatus() == PlatzStatus.BLOCKIERT) {
                    img.setImage(blockedImage);
                } else if (platz.getStatus() == PlatzStatus.BESETZT) {
                    // Lade besondere Kachel (leer oder blockiert)
                    img.setImage(platz.getKachel().getImage());
                } else {
                   
                }

            }
        }

    }
}
