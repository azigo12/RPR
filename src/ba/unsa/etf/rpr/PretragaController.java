package ba.unsa.etf.rpr;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class PretragaController {
    private SimpleStringProperty uneseniUzorak, slika;
    private String odabranaPutanja;

    @FXML
    public TableView<String> tabelaPutanja;
    public TableColumn<String, String> colPutanje;
    public TextField uzorak;

    @FXML
    public void initialize() {
        colPutanje.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue()));

        tabelaPutanja.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 1) {
                promjena();
            }
        });
    }

    private void promjena() {
        if (tabelaPutanja.getSelectionModel().getSelectedItem() != null) {
            odabranaPutanja = tabelaPutanja.getSelectionModel().getSelectedItem();
            Stage stage = (Stage) tabelaPutanja.getScene().getWindow();
            stage.close();
        }
    }

    public String getPutanja() {
        return odabranaPutanja;
    }

    public PretragaController() {
    }

    public void pretraziFajlove() {
        //ovo radi samo za fajlove u Users\Amila, pa mozda zatreba nekad hehe
        /*if(uzorak.getText().length() != 0) {
            String p = System.getProperty("user.home");
            File[] files = new File(p).listFiles();
            for(File f : files)
                if(f.getAbsolutePath().contains(uzorak.getText())) listaPutanja.add(f.getAbsolutePath());

            for (String f : listaPutanja)
                System.out.println(f);
        }*/

        //a ovo radi isto samo preko streama
        /*try {
            Path dir = FileSystems.getDefault().getPath(System.getProperty("user.home"));
            DirectoryStream<Path> stream = null;
            stream = Files.newDirectoryStream(dir);
            for (Path path : stream)
                System.out.println( path.toAbsolutePath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        //System.getProperty("user.home") -> dolje ispod naravno trebamo stavit ovo kao parametar kako bi se pretražili svi folderi na računaru a ne samo ovaj jedan
        //al pošto to traje vječnost ostavljam ipak samo folder sa slikama i u pretragu kucam jpg

        new Thread(() -> {
            ArrayList<File> files = new ArrayList<>();
            addfiles(new File("C:\\Users\\Amila\\Desktop"), files);

            ObservableList<String> svePutanje = FXCollections.observableArrayList();
            files.stream().forEach(f -> svePutanje.add(f.getAbsolutePath()));
            Platform.runLater(() -> {
                tabelaPutanja.setItems(svePutanje);
                tabelaPutanja.refresh();
            });
        }).start();
    }

    private void addfiles (File input, ArrayList<File> files)
    {
        if(input.isDirectory()) {
            if (input.listFiles() != null) {
                ArrayList<File> path = new ArrayList<File>(Arrays.asList(input.listFiles()));
                for (int i = 0; i < path.size(); ++i) {
                    if (path.get(i).isDirectory()) {
                        if (files != null)
                            addfiles(path.get(i), files);
                    }
                    if (path.get(i).isFile() && path.get(i).getAbsolutePath().contains(uzorak.getText()))
                        files.add(path.get(i));
                }
            }
        }

        if(input.isFile() && input.getAbsolutePath().contains(uzorak.getText()))
            files.add(input);
    }
}
