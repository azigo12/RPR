package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GradController {

    private SimpleStringProperty naziv, brojStanovnika, postanskiBroj;
    private boolean cancel;
    private ObservableList<Drzava> drzave = FXCollections.observableArrayList();
    private Grad gradKojiEditujem;
    private SimpleStringProperty putanjaSlike = new SimpleStringProperty();
    ResourceBundle bundle = ResourceBundle.getBundle("Translation");

    @FXML
    public TextField fieldNaziv, fieldPostanskiBroj;
    public TextField fieldBrojStanovnika;
    public ChoiceBox choiceDrzava;
    public ImageView slika;

    public GradController(Grad g, ArrayList<Drzava> d) {
        naziv = new SimpleStringProperty("");
        brojStanovnika = new SimpleStringProperty("");
        postanskiBroj = new SimpleStringProperty("");
        if (d != null) drzave.addAll(d);
        gradKojiEditujem = g;
        cancel = false;
    }

    @FXML
    public void initialize() {
        fieldNaziv.textProperty().bindBidirectional(naziv);
        fieldBrojStanovnika.textProperty().bindBidirectional(brojStanovnika);
        fieldPostanskiBroj.textProperty().bindBidirectional(postanskiBroj);

        choiceDrzava.setItems(drzave);

        fieldNaziv.textProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (validanNazivGrada(newValue)) {
                        fieldNaziv.getStyleClass().removeAll("poljeNeispravno");
                        fieldNaziv.getStyleClass().add("poljeIspravno");
                    } else {
                        fieldNaziv.getStyleClass().removeAll("poljeIspravno");
                        fieldNaziv.getStyleClass().add("poljeNeispravno");
                    }
                }
        );

        fieldBrojStanovnika.textProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (validanBrojStanovnika(newValue)) {
                        fieldBrojStanovnika.getStyleClass().removeAll("poljeNeispravno");
                        fieldBrojStanovnika.getStyleClass().add("poljeIspravno");
                    } else {
                        fieldBrojStanovnika.getStyleClass().removeAll("poljeIspravno");
                        fieldBrojStanovnika.getStyleClass().add("poljeNeispravno");
                    }
                }
        );

        fieldPostanskiBroj.textProperty().addListener(
                (obs, oldValue, newValue) -> {
                    new Thread(() -> {
                        try {
                            URL url = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + newValue);
                            BufferedReader ulaz = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
                            String json = "", line = null;
                            while ((line = ulaz.readLine()) != null)
                                json = json + line;
                            if (json.contains("NOT OK")) {
                                Platform.runLater(() -> {
                                    fieldPostanskiBroj.getStyleClass().removeAll("poljeIspravno");
                                    fieldPostanskiBroj.getStyleClass().add("poljeNeispravno");
                                });
                            }
                            else {
                                Platform.runLater(() -> {
                                    fieldPostanskiBroj.getStyleClass().removeAll("poljeNeispravno");
                                    fieldPostanskiBroj.getStyleClass().add("poljeIspravno");
                                });
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
        );

        if (gradKojiEditujem != null) {
            fieldNaziv.setText(gradKojiEditujem.getNaziv());
            fieldBrojStanovnika.setText(Integer.toString(gradKojiEditujem.getBrojStanovnika()));
            fieldPostanskiBroj.setText(Integer.toString(gradKojiEditujem.getPostanskiBroj()));
            //zasto mi ovdje ne radi drzave.indexOf() kad mu proslijedim gradKojiEditujem,getDrzava()?
            int indexDrzave = dajIndeksDrzave();
            if (indexDrzave != -1) choiceDrzava.setValue(drzave.get(indexDrzave));
            putanjaSlike.set(gradKojiEditujem.getSlika());
            prikaziSliku(gradKojiEditujem.getSlika());
            gradKojiEditujem = null;
        }
    }

    private int dajIndeksDrzave() {
        int rezultat = -1;
        for (Drzava d : drzave) {
            rezultat++;
            if (gradKojiEditujem.getDrzava() != null && d.getNaziv().equals(gradKojiEditujem.getDrzava().getNaziv()))
                return rezultat;
        }
        return -1;
    }

    public String getNaziv() {
        return naziv.get();
    }

    public SimpleStringProperty nazivProperty() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv.set(naziv);
    }

    public String getBrojStanovnika() {
        return brojStanovnika.get();
    }

    public SimpleStringProperty brojStanovnikaProperty() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(String brojStanovnika) {
        this.brojStanovnika.set(brojStanovnika);
    }

    private boolean validanBrojStanovnika(String newValue) {
        return newValue.length() > 0 && newValue.charAt(0) != '0' && newValue.matches("^[0-9]*$");
    }

    private boolean validanNazivGrada(String newValue) {
        return newValue.length() > 0 && newValue.matches("^[a-zA-Z\\s]*$");
    }

    public void zavrsiProgram(ActionEvent actionEvent) {
        cancel = true;
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void zavrsiProgramSaOK(ActionEvent actionEvent) {
        if (validanBrojStanovnika(brojStanovnika.get()) && validanNazivGrada(naziv.get())) {
            Node n = (Node) actionEvent.getSource();
            Stage stage = (Stage) n.getScene().getWindow();
            stage.close();
        }
    }

    public void promijeniSliku(ActionEvent actionEvent) {
        /*TextInputDialog dialog = new TextInputDialog("C:\\Users\\Amila\\Desktop\\slikeGradova\\");
        dialog.setTitle("Promjena slike");
        dialog.setHeaderText("Promjena slike grada");
        dialog.setContentText("Molimo vas unesite lokaciju na disku gdje se nalazi fotografija grada: ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            putanjaSlike.set(result.get());
            if(gradKojiEditujem != null) gradKojiEditujem.setSlika(putanjaSlike.get());
            prikaziSliku();
        }*/
        Parent root = null;
        try {
            PretragaController ctrl = new PretragaController();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/pretraga.fxml"), bundle);
            loader.setController(ctrl);
            root = loader.load();

            Stage myStage = new Stage();
            myStage.setTitle("Pretraga");
            myStage.setScene(new Scene(root, 300, USE_COMPUTED_SIZE));
            myStage.setMinWidth(350);
            myStage.setOnHiding(event -> {
                prikaziSliku(ctrl);
            });
            myStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void prikaziSliku(PretragaController ctrl) {
        File f = new File(ctrl.getPutanja());
        putanjaSlike.set(ctrl.getPutanja());
        URI u = f.toURI();
        slika.setImage(new Image(u.toString()));
    }

    public void prikaziSliku(String putanja) {
        File f = new File(putanja);
        URI u = f.toURI();
        slika.setImage(new Image(u.toString()));
    }

    public Grad getGrad() {
        Grad g = new Grad();
        if (validanBrojStanovnika(brojStanovnika.get()) && validanNazivGrada(naziv.get()) && fieldPostanskiBroj.getStyleClass().contains("poljeIspravno") && !cancel) {
            g.setId(0);
            g.setNaziv(naziv.get());
            g.setBrojStanovnika(Integer.parseInt(brojStanovnika.get()));
            //ovo šaljem samo radi naziva drzave
            if (choiceDrzava.getValue() != null)
                g.setDrzava(new Drzava(0, choiceDrzava.getValue().toString(), null));
            else g.setDrzava(null);
            g.setSlika(putanjaSlike.get());
            g.setPostanskiBroj(Integer.parseInt(postanskiBroj.get()));
            return g;
        }
        cancel = false;
        return null;
    }
}