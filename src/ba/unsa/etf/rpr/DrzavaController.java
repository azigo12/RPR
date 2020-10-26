package ba.unsa.etf.rpr;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DrzavaController {

    private SimpleStringProperty naziv;
    private ObservableList<Grad> gradovi = FXCollections.observableArrayList();
    private ObservableList<String> naziviGradova = FXCollections.observableArrayList();

    public TextField fieldNaziv;
    public ChoiceBox choiceGrad;

    public DrzavaController(Drzava d, ArrayList<Grad> g) {
        naziv = new SimpleStringProperty("");
        gradovi.addAll(g);
        naziviGradova = dajNazive();
    }

    private ObservableList<String> dajNazive() {
        ObservableList<String> rezultat = FXCollections.observableArrayList();
        for(Grad g : gradovi)
            rezultat.add(g.getNaziv());
        return rezultat;
    }

    @FXML
    public void initialize() {
        fieldNaziv.textProperty().bindBidirectional(naziv);
        choiceGrad.setItems(naziviGradova);

        fieldNaziv.textProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (validanNazivDrzave(newValue)) {
                        fieldNaziv.getStyleClass().removeAll("poljeNeispravno");
                        fieldNaziv.getStyleClass().add("poljeIspravno");
                    } else {
                        fieldNaziv.getStyleClass().removeAll("poljeIspravno");
                        fieldNaziv.getStyleClass().add("poljeNeispravno");
                    }
                }
        );

        if(validanNazivDrzave(naziv.get())) {
            upisiDrzavu(naziv.get());
        }
    }

    private void upisiDrzavu(String newValue) {
        int indexGrada = dajIndeksGrada(newValue);
        choiceGrad.setValue(gradovi.indexOf(indexGrada));
    }

    private int dajIndeksGrada(String newValue) {
        int rezultat = -1;
        for(Grad g : gradovi) {
            rezultat++;
            if (g.getDrzava().getNaziv().equals(newValue)) return rezultat;
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

    private boolean validanNazivDrzave(String newValue) {
        return newValue.length() > 0 && newValue.matches("^[a-zA-Z\\s]*$");
    }

    public Drzava dajDrzavu() {
        Grad grad = null;
        if(choiceGrad.getValue() != null)
            grad = gradovi.stream().filter(g -> g.getNaziv().equals(choiceGrad.getValue().toString())).collect(Collectors.toList()).get(0);
        return new Drzava(0, naziv.get(), grad);
    }

    public void zavrsiProgram(ActionEvent actionEvent) {
        Node n = (Node) actionEvent.getSource();
        Stage stage = (Stage) n.getScene().getWindow();
        stage.close();
    }

    public void potvrda(ActionEvent actionEvent) {
        if (validanNazivDrzave(naziv.get())) {
            zavrsiProgram(actionEvent);
        }
    }
}
