package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ImportController implements Initializable, Controller {

    @FXML
    private BorderPane fileDrop;

    @FXML
    private Button importButton;

    @FXML
    private Label messageLabel;

    @FXML
    private Label dropLabel;

    private ObservableList<File> selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        selectedFile = FXCollections.observableList(new ArrayList<>());

        Platform.runLater(() -> {

            selectedFile.addListener((ListChangeListener) change -> {

                if(change.next() && change.getAddedSize() > 0) {
                    dropLabel.setText(selectedFile.get(0).getName());
                    importButton.setDisable(false);
                }

            });

            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Microsoft Access Files", "*.accdb")
            );

            List<String> validExtensions = Arrays.asList("accdb");

            fileDrop.setOnMouseClicked(event -> {

                File file = fileChooser.showOpenDialog(ScreenManager.getInstance().getStage());

                if(file != null) {
                    selectedFile.add(0, file);
                    dropLabel.setText(file.getName());
                }

            });

            fileDrop.setOnDragOver(event -> {

                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {

                    if (!validExtensions.containsAll(
                            event.getDragboard().getFiles().stream()
                                    .map(file -> getExtension(file.getName()))
                                    .collect(Collectors.toList()))) {

                        event.consume();
                        return;
                    }

                    if(event.getDragboard().getFiles().size() > 1) {
                        event.consume();
                        dropLabel.setText("Solo puedes arrastrar 1 archivo");
                        return;
                    }

                    dropLabel.setText(db.getFiles().get(0).getName());

                    event.acceptTransferModes(TransferMode.COPY);

                } else {

                    event.consume();

                }

            });

            fileDrop.setOnDragExited(event -> {

                if(selectedFile.size() == 0)
                    dropLabel.setText("Arrastra o pulsa para elegir el fichero Access");
                else
                    dropLabel.setText(selectedFile.get(0).getName());

            });

            fileDrop.setOnDragDropped(event -> {

                Dragboard db = event.getDragboard();

                boolean success = false;

                if (db.hasFiles()) {

                    success = true;

                    selectedFile.clear();
                    selectedFile.add(0, db.getFiles().get(0));

                }

                event.setDropCompleted(success);
                event.consume();

            });

        });

    }

    private String getExtension(String fileName){
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0 && i < fileName.length() - 1)
            return fileName.substring(i + 1).toLowerCase();
        return extension;
    }

    @FXML
    void handleButtonAction(MouseEvent event) {

        if(event.getSource() == importButton) {

            importButton.setDisable(true);

        }

    }

}
