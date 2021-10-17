package morales.david.desktop.controllers.teachers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import morales.david.desktop.controllers.modals.CredentialModalController;
import morales.david.desktop.interfaces.Controller;
import morales.david.desktop.managers.DataManager;
import morales.david.desktop.ClientManager;
import morales.david.desktop.models.Credential;
import morales.david.desktop.models.packets.Packet;
import morales.david.desktop.models.packets.PacketBuilder;
import morales.david.desktop.models.packets.PacketType;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CredentialsController implements Initializable, Controller {

    @FXML
    private TableView<Credential> credentialsTable;

    @FXML
    private TableColumn<Credential, String> usernameColumn;

    @FXML
    private TableColumn<Credential, String> roleColumn;

    @FXML
    private TableColumn<Credential, String> teacherColumn;

    @FXML
    private TextField filterField;

    @FXML
    private Button newButton;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        showTable();

        Platform.runLater(() -> {

            Packet credentialsRequestPacket = new PacketBuilder()
                    .ofType(PacketType.CREDENTIALS.getRequest())
                    .build();

            ClientManager.getInstance().sendPacketIO(credentialsRequestPacket);

        });

        if(ClientManager.getInstance().getClientSession().isTeacherRole()) {
            newButton.setVisible(false);
            editButton.setVisible(false);
            deleteButton.setVisible(false);
        }

    }

    @FXML
    public void handleButtonAction(MouseEvent event) {

        if(event.getSource() == newButton) {

            newCredential();

        } else if(event.getSource() == editButton) {

            Credential selected = credentialsTable.getSelectionModel().getSelectedItem();

            editCredential(selected);

        } else if(event.getSource() == deleteButton) {

            deleteCredential();

        }

    }

    public void showTable() {

        ObservableList<Credential> list = DataManager.getInstance().getCredentials();

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        teacherColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTeacher() != null ? cellData.getValue().getTeacher().getName() : "No asignado"));

        credentialsTable.setItems(list);

        credentialsTable.setRowFactory( tv -> {

            TableRow<Credential> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !row.isEmpty()) {

                    Credential selected = row.getItem();

                    editCredential(selected);

                }
            });

            return row ;
            
        });

        filterField.textProperty().addListener(observable -> {

            if(filterField.textProperty().get().isEmpty()) {
                credentialsTable.setItems(list);
                return;
            }

            ObservableList<Credential> tableItems = FXCollections.observableArrayList();
            ObservableList<TableColumn<Credential, ?>> cols = credentialsTable.getColumns();

            for(int i=0; i< list.size(); i++) {

                for(int j=0; j<cols.size(); j++) {
                    TableColumn col = cols.get(j);
                    String cellValue = col.getCellData(list.get(i)).toString();
                    cellValue = cellValue.toLowerCase();
                    if(cellValue.contains(filterField.textProperty().get().toLowerCase())) {
                        tableItems.add(list.get(i));
                        break;
                    }
                }
            }

            credentialsTable.setItems(tableItems);


        });

        credentialsTable.setPlaceholder(new Label("No existe ningun credencial registrado"));

    }

    private void deleteCredential() {

        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;

        Credential credential = credentialsTable.getSelectionModel().getSelectedItem();

        Packet removeCredentialRequestPacket = new PacketBuilder()
                .ofType(PacketType.REMOVECREDENTIAL.getRequest())
                .addArgument("credential", credential)
                .build();

        ClientManager.getInstance().sendPacketIO(removeCredentialRequestPacket);

    }

    private void editCredential(Credential credential) {

        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;

        if(credential == null)
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/editCredentialModal.fxml"));
        try {

            DialogPane parent = loader.load();
            CredentialModalController controller = loader.getController();

            controller.setData(credential, true);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType updateBtn = new ButtonType("Actualizar", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);

            dialog.getDialogPane().getButtonTypes().addAll(updateBtn, cancelBtn);

            Button updateButton = (Button) dialog.getDialogPane().lookupButton(updateBtn);
            updateButton.getStyleClass().addAll("dialogButton", "updateButton");

            updateButton.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        if (!controller.validateInputs()) {
                            event.consume();
                        }
                    }
            );

            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
            cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == updateBtn) {

                Credential updatedCredential = controller.getData();

                Packet updateCredentialRequestPacket = new PacketBuilder()
                        .ofType(PacketType.UPDATECREDENTIAL.getRequest())
                        .addArgument("credential", updatedCredential)
                        .build();

                ClientManager.getInstance().sendPacketIO(updateCredentialRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void newCredential() {

        if(ClientManager.getInstance().getClientSession().isTeacherRole())
            return;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/modals/newCredentialModal.fxml"));
        try {

            DialogPane parent = loader.load();
            CredentialModalController controller = loader.getController();

            controller.setData(new Credential(), false);

            Dialog<ButtonType> dialog = new Dialog<>();

            dialog.setDialogPane(parent);
            dialog.setTitle("");

            ButtonType addBtn = new ButtonType("Añadir", ButtonBar.ButtonData.YES);
            ButtonType cancelBtn = new ButtonType("Cancelar", ButtonBar.ButtonData.NO);

            dialog.getDialogPane().getButtonTypes().addAll(addBtn, cancelBtn);

            Button addButton = (Button) dialog.getDialogPane().lookupButton(addBtn);
            addButton.getStyleClass().addAll("dialogButton", "addButton");

            addButton.addEventFilter(
                    ActionEvent.ACTION,
                    event -> {
                        if (!controller.validateInputs()) {
                            event.consume();
                        }
                    }
            );

            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(cancelBtn);
            cancelButton.getStyleClass().addAll("dialogButton", "cancelButton");

            ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("/images/schedule-icon-inverted.png"));

            Optional<ButtonType> clickedButton = dialog.showAndWait();

            if(clickedButton.get() == addBtn) {

                Credential credential = controller.getData();

                Packet addCredentialRequestPacket = new PacketBuilder()
                        .ofType(PacketType.ADDCREDENTIAL.getRequest())
                        .addArgument("credential", credential)
                        .build();

                ClientManager.getInstance().sendPacketIO(addCredentialRequestPacket);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
