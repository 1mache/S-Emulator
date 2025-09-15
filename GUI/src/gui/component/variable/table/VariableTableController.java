package gui.component.variable.table;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class VariableTableController implements Initializable {
    @FXML
    private TableColumn<VariableEntry, String> nameColumn;

    @FXML
    private TableColumn<VariableEntry, Long> valueColumn;

    @FXML
    private TableView<VariableEntry> variableTable;

    private record VariableEntry(String name, Long value) {}
    private final ObservableList<VariableEntry> variableEntries = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        variableTable.setPlaceholder(new Label("No data"));

        nameColumn.setCellValueFactory(cellData ->
                        new ReadOnlyStringWrapper(cellData.getValue().name())
        );
        valueColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().value())
        );
    }

    public void clear() {
        this.variableEntries.clear();
        variableTable.setItems(this.variableEntries);
    }

    public void setVariableEntries(Map<String, Long> variableMap) {
        this.variableEntries.clear();
        variableMap.forEach((name, value) -> this.variableEntries.add(new VariableEntry(name, value)));
        variableTable.setItems(this.variableEntries);
    }
}
