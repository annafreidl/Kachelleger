
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
		     
	public static void main(String[] args) {
		         launch(args);
	}
		     
	@Override
	public void start(Stage primaryStage) {
		// Parameter setzen
		
		         // Gr��e des Feldes
		         int grid_x, grid_y;
		         grid_x = 16;
		         grid_y = 9;
		         
		         // Aufl�sung eines Platzes
		         int px_x, px_y;
		         px_x = 64;
		         px_y = 64;
		         
		         // Tiefe und Fehlerrate individuell festlegbar
		         int tiefe = 3;
		         double fehlerrate = 0.01;

		         // Lade Logik/Kacheln aus dem sourcefolder "images"
		         Pixelvergleich kachelTool = null;
		         try {
		             kachelTool = new Pixelvergleich(fehlerrate, tiefe);
		             if (kachelTool.getKacheln().size() <= 0) {
		                 throw new Exception();
		             }
		         } catch(Exception e) {
		             System.out.println("Fehler bei der Kachelsuche.");
		         }

		         // neues Spielbrett und Spielbrettlogik erzeugen
		         Spielbrett brett = new Spielbrett(grid_x, grid_y, kachelTool);
		         SpielbrettLogik brettGui = new SpielbrettLogik(primaryStage, brett, px_x, px_y);

		     }
		}