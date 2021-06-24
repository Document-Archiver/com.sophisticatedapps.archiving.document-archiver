package com.sophisticatedapps.archiving.documentarchiver.controller;

import com.sophisticatedapps.archiving.documentarchiver.api.ApplicationContext;
import com.sophisticatedapps.archiving.documentarchiver.type.Tenant;
import com.sophisticatedapps.archiving.documentarchiver.util.PropertiesUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.StringUtil;
import com.sophisticatedapps.archiving.documentarchiver.util.TenantUtil;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ManageTenantsPaneController extends BaseController {

    private static final String ICON_DELETE = "🗑";
    private static final String ICON_FORBIDDEN = "🚫";

    private final List<Tenant> formerTenantList = new ArrayList<>();

    @FXML
    private TableView<Tenant> tenantsTableView;

    @FXML
    private TableColumn<Tenant, String> nameColumn;

    @FXML
    private TableColumn<Tenant, String> deleteColumn;

    @FXML
    private TextField newTenantNameTextField;

    @FXML
    private Button createTenantButton;

    @Override
    public void rampUp(ApplicationContext anApplicationContext) {

        super.rampUp(anApplicationContext);

        // Cell value factories & cell factories
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        deleteColumn.setCellValueFactory(aCellDataFeatures -> {
            boolean tmpTenantActive = PropertiesUtil.ACTIVE_TENANT.equals(aCellDataFeatures.getValue());
            return (new ReadOnlyStringWrapper(tmpTenantActive ? ICON_FORBIDDEN : ICON_DELETE));
        });

        deleteColumn.setCellFactory(aTenantStringTableColumn -> {
            TableCell<Tenant, String> tmpCell = new TableCell<>() { // NOSONAR
                @Override
                public void updateItem(String aString, boolean anIsEmpty) {
                    setText(aString);
                }
            };
            tmpCell.setOnMouseClicked(aMouseEvent -> {
                if (ICON_DELETE.equals(tmpCell.getText())) {
                    deleteTenant(tmpCell.getTableRow().getItem());
                }
            });
            return tmpCell;
        });

        List<Tenant> tmpAvailableTenants = TenantUtil.getAvailableTenants();
        formerTenantList.addAll(tmpAvailableTenants);
        tenantsTableView.getItems().addAll(tmpAvailableTenants);

        // Listener
        newTenantNameTextField.textProperty().addListener((anObservable, anOldValue, aNewValue) ->
                createTenantButton.setDisable(aNewValue.isEmpty()));
    }

    @Override
    public void rampDown() {

        super.rampDown();

        formerTenantList.clear();
    }

    @FXML
    protected void handleCreateTenantButtonAction() {

        Tenant tmpNewTenant = new Tenant(StringUtil.retrieveFilenameSafeString(newTenantNameTextField.getText()));

        if (!tenantsTableView.getItems().contains(tmpNewTenant)) {

            try {

                TenantUtil.persistTenant(tmpNewTenant);
                tenantsTableView.getItems().add(tmpNewTenant);
                newTenantNameTextField.setText(StringUtil.EMPTY_STRING);
            }
            catch (IOException | RuntimeException e) {

                dialogProvider.provideExceptionAlert(e).showAndWait();
            }
        }
    }

    @SuppressWarnings("idea: OptionalGetWithoutIsPresent")
    private void deleteTenant(Tenant aTenant) {

        if (Objects.isNull(aTenant)) {

            return;
        }

        // Create and show Dialog
        Optional<ButtonType> tmpConfirmationResult =
                dialogProvider.provideConfirmTenantDeletionAlert(aTenant.getName()).showAndWait();

        // Deal with result
        if (ButtonBar.ButtonData.YES == tmpConfirmationResult.get().getButtonData()) { // NOSONAR

            try {

                TenantUtil.deleteTenant(aTenant);
                tenantsTableView.getItems().remove(aTenant);
            }
            catch (IOException | RuntimeException e) {

                dialogProvider.provideExceptionAlert(e).showAndWait();
            }
        }
    }

    public boolean isTenantsChanged() {

        List<Tenant> tmpCurrentTenantList = tenantsTableView.getItems();
        boolean tmpEqual = ((tmpCurrentTenantList.size() == formerTenantList.size())
                && tmpCurrentTenantList.containsAll(formerTenantList)
                && formerTenantList.containsAll(tmpCurrentTenantList));
        return (!tmpEqual);
    }

}
