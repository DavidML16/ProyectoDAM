package morales.david.desktop.controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.ScreenManager;

import java.io.File;
import java.net.URL;
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
    private CheckBox cleanDatabase;

    @FXML
    private Label messageLabel;

    @FXML
    private Label dropLabel;

    private File selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {

            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Microsoft Access Files", "*.accdb")
            );

            List<String> validExtensions = Arrays.asList("accdb");

            fileDrop.setOnMouseClicked(event -> {

                selectedFile = fileChooser.showOpenDialog(ScreenManager.getInstance().getStage());

                if(selectedFile != null)
                    dropLabel.setText(selectedFile.getName());

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

                    event.acceptTransferModes(TransferMode.COPY);

                } else {

                    event.consume();

                }

            });

            fileDrop.setOnDragDropped(event -> {

                Dragboard db = event.getDragboard();

                boolean success = false;

                if (db.hasFiles()) {

                    success = true;

                    selectedFile = db.getFiles().get(0);

                    dropLabel.setText(selectedFile.getName());

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
