package gui.component.variable.table;

import gui.utility.CssClasses;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.*;

public class VariableTableController implements Initializable {
    @FXML
    private TableColumn<VariableEntry, String> nameColumn;

    @FXML
    private TableColumn<VariableEntry, Long> valueColumn;

    @FXML
    private TableView<VariableEntry> variableTable;

    // to preserve put order
    private final Map<String, Long> variableMap = new LinkedHashMap<>();

    private record VariableEntry(String name, Long value) {}
    private final ObservableList<VariableEntry> variableEntries = FXCollections.observableArrayList();

    private String highlightedVariableName = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        variableTable.setPlaceholder(new Label("No data"));

        nameColumn.setCellValueFactory(cellData ->
                        new ReadOnlyStringWrapper(cellData.getValue().name())
        );
        valueColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().value())
        );

        variableTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(VariableEntry item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove(CssClasses.DEBUG_HIGHLIGHTED); // remove if exists
                if (!empty && item != null && highlightedVariableName.equals(item.name())) {
                    getStyleClass().add(CssClasses.DEBUG_HIGHLIGHTED);
                }
            }
        });
    }

    public void clear() {
        this.variableEntries.clear();
        variableTable.setItems(this.variableEntries);
    }

    public void setVariableEntries(Map<String, Long> variableValues) {
        this.variableMap.clear();
        addVariableEntries(variableValues);
    }

    public void addVariableEntries(Map<String, Long> variableValues) {
        this.variableMap.putAll(variableValues);
        setEntriesFromMap();
    }

    // highlights the line with the variable if exists
    public void highlightVariable(String varName){
        highlightedVariableName = varName;
        variableTable.refresh();
    }

    public void resetHighlight(){
        highlightedVariableName = "";
        variableTable.refresh();
    }

    private void setEntriesFromMap() {
        variableEntries.clear();
        variableMap.forEach((name, value) -> this.variableEntries.add(new VariableEntry(name, value)));
        variableTable.setItems(this.variableEntries);
    }
}
