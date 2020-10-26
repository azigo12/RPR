package ba.unsa.etf.rpr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {
    GeografijaDAO dao = GeografijaDAO.getInstance();

    ResourceBundle bundle = ResourceBundle.getBundle("Translation");

    private ObservableList<Grad> gradovi = FXCollections.observableArrayList();
    private ArrayList<Grad> listaGradova;
    private ArrayList<Drzava> listaDrzava;
    private Grad gradKojiEditujem;

    @FXML
    public TableView<Grad> tableViewGradovi;
    public TableColumn<Grad, Integer> colGradId;
    public TableColumn<Grad, String> colGradNaziv;
    public TableColumn<Grad, String> colGradStanovnika;
    public TableColumn<Grad, Drzava> colGradDrzava;
    public TableColumn<Grad, String> colSlika;
    public TableColumn<Grad, String> colPostanskiBroj;

    public GlavnaController() {
        resetujBazu();
        listaGradova = dao.gradovi();
        listaDrzava = dao.dajSveDrzave();
        gradovi.addAll(listaGradova);
        gradKojiEditujem = null;
    }

    @FXML
    public void initialize() {
        tableViewGradovi.setItems(gradovi);
        colGradId.setCellValueFactory(new PropertyValueFactory<Grad, Integer>("id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory<Grad, String>("naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory<Grad, String>("brojStanovnika"));
        colGradDrzava.setCellValueFactory(new PropertyValueFactory<Grad, Drzava>("drzava"));
        colSlika.setCellValueFactory(new PropertyValueFactory<Grad, String>("slika"));
        colPostanskiBroj.setCellValueFactory(new PropertyValueFactory<Grad,String>("postanskiBroj"));

        tableViewGradovi.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 1) {
                promjena();
            }
        });
    }

    private void promjena() {
        if (tableViewGradovi.getSelectionModel().getSelectedItem() != null)
            gradKojiEditujem = tableViewGradovi.getSelectionModel().getSelectedItem();
    }

    public void dodajDrzavu(ActionEvent actionEvent) {
        Parent root = null;
        try {
            DrzavaController ctrl = new DrzavaController(null, listaGradova);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"), bundle);
            loader.setController(ctrl);
            root = loader.load();

            Stage myStage = new Stage();
            myStage.setTitle("Država");
            myStage.setScene(new Scene(root, 300, USE_COMPUTED_SIZE));
            myStage.setWidth(300);
            myStage.setMinWidth(300);
            myStage.setOnHiding(event -> {
                dodajDrzavuUBazu(ctrl);
            });
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dodajDrzavuUBazu(DrzavaController ctrl) {
        Drzava d = ctrl.dajDrzavu();
        dao.dodajDrzavu(d);
        listaDrzava.add(d);
    }

    public void dodajGrad(ActionEvent actionEvent) {
        Parent root = null;
        try {
            GradController ctrl = new GradController(null, listaDrzava);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"), bundle);
            loader.setController(ctrl);
            root = loader.load();

            Stage myStage = new Stage();
            myStage.setTitle("Grad");
            myStage.setScene(new Scene(root, 300, USE_COMPUTED_SIZE));
            myStage.setWidth(300);
            myStage.setMinWidth(300);
            myStage.setOnHiding(event -> {
                dodajGradUBazu(ctrl);
            });
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obrisiGrad(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Brisanje grada");
        alert.setHeaderText(null);
        alert.setContentText("Da li ste sigurni da želite obrisati grad?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            dao.obrisiGrad(gradKojiEditujem);
            gradKojiEditujem = null;
            refreshTable();
        }

    }

    public void izmijeniGrad(ActionEvent actionEvent) {
        Parent root = null;
        try {

            GradController ctrl = new GradController(gradKojiEditujem, listaDrzava);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"), bundle);
            loader.setController(ctrl);
            root = loader.load();

            Stage myStage = new Stage();
            myStage.setTitle("Grad");
            myStage.setScene(new Scene(root, 300, USE_COMPUTED_SIZE));
            myStage.setWidth(300);
            myStage.setMinWidth(300);
            myStage.setOnHiding(event -> {
                izmijeniGradUBazi(ctrl);
            });
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stampajIzvjestaj() {
        try {
            new GradoviReport().showReport(dao.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void promijeniJezik() {
        Parent root = null;

        ChoiceDialog<String> jezici = new ChoiceDialog<>("Bosanski");
        ObservableList<String> list = jezici.getItems();
        list.add("Engleski");
        list.add("Njemački");
        list.add("Francuski");

        jezici.setTitle("Jezici");
        jezici.setHeaderText("Jezici");
        jezici.setContentText("Odaberite jezik koji želite: ");

        Optional<String> result = jezici.showAndWait();
        if (result.isPresent())
        {
            switch (result.get()) {
                case "Bosanski":
                    bundle = ResourceBundle.getBundle("Translation_bs");
                    break;
                case "Engleski":
                    bundle = ResourceBundle.getBundle("Translation_en");
                    break;
                case "Njemački":
                    bundle = ResourceBundle.getBundle("Translation_de");
                    break;
                case "Francuski":
                    bundle = ResourceBundle.getBundle("Translation_fr");
                    break;
                default:
                    bundle = ResourceBundle.getBundle("Translation");
            }
            promijeniGlavnu();
        }
    }

    public ResourceBundle dajBundle() {
        return bundle;
    }

    private void promijeniGlavnu() {
        try {
            Stage stage = (Stage) tableViewGradovi.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"), bundle);
            loader.setController(this);
            stage.setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void izmijeniGradUBazi(GradController ctrl) {
        Grad uneseniPodaci = ctrl.getGrad();
        if(uneseniPodaci != null) {
            gradKojiEditujem.setNaziv(uneseniPodaci.getNaziv());
            gradKojiEditujem.setBrojStanovnika(uneseniPodaci.getBrojStanovnika());
            gradKojiEditujem.setDrzava(listaDrzava.stream().filter(d -> d.getNaziv().equals(uneseniPodaci.getDrzava().getNaziv())).collect(Collectors.toList()).get(0));
            System.out.println("ovdje je putanja "  + uneseniPodaci.getSlika());
            gradKojiEditujem.setSlika(uneseniPodaci.getSlika());
            dao.izmijeniGrad(gradKojiEditujem);
            refreshTable();
        }
    }

    private void refreshTable() {
        gradovi.clear();
        listaGradova = dao.gradovi(); //treba i ovo zbog toga što šaljem listu gradova u choideGrad
        gradovi.addAll(listaGradova);
        tableViewGradovi.setItems(gradovi);
        tableViewGradovi.refresh();
    }

    private void dodajGradUBazu(GradController ctrl) {
        Grad g = ctrl.getGrad();
        if(g != null) {
            Drzava d = null;
            if(g.getDrzava() != null) d = dao.nadjiDrzavu(g.getDrzava().getNaziv());
            g.setDrzava(d);
            dao.dodajGrad(g);
            refreshTable();
        }
    }

    // Metoda za potrebe testova, vraća bazu u polazno stanje
    public void resetujBazu() {
        GeografijaDAO.removeInstance();
        File dbfile = new File("baza.db");
        dbfile.delete();
        dao = GeografijaDAO.getInstance();
    }

}
