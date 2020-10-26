package ba.unsa.etf.rpr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        GeografijaDAO instance = GeografijaDAO.getInstance();
        GlavnaController ctrl = new GlavnaController();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle);
        loader.setController(ctrl);
        Parent root = loader.load();
        primaryStage.setTitle("Gradovi i države");
        primaryStage.setScene(new Scene(root, 840, USE_COMPUTED_SIZE));
        primaryStage.setMinWidth(840);
        primaryStage.toFront();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    /*public static void main(String[] args) {
        System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();
    }

    private static void glavniGrad() {
        System.out.println("Unesite naziv države čiji vas glavni grad zanima: ");
        Scanner ulaz = new Scanner(System.in);
        String nazivDrzave = ulaz.nextLine();

        Grad g = GeografijaDAO.getInstance().glavniGrad(nazivDrzave);

        if(g == null)
            System.out.println("Nepostojeća država");
        else
            System.out.println("Glavni grad države " + nazivDrzave + " je " + g.getNaziv());
    }

    public static String ispisiGradove() {
        ArrayList<Grad> gradovi = GeografijaDAO.getInstance().gradovi();
        String rezultat = "";

        for(Grad g : gradovi)
            rezultat += g.toString() + "\n";

        return rezultat;
    }*/
}
