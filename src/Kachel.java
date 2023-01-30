import javafx.scene.image.Image;

public class Kachel  {
    private Image bild;

    // Kachel mit jeweiligem Bild deklarieren
    public Kachel(String path) {
        this.bild = new Image("file:" + path);
    }

    public Image getImage() {
        return this.bild;
    }
}