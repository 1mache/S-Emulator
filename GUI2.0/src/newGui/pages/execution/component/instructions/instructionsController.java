package newGui.pages.execution.component.instructions;

import dto.InstructionPeek;
import dto.server.request.BreakpointRequest;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.HighlightInfoRequest;
import requests.UpdateBreakpointRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;

public class instructionsController {

    mainExecutionController mainExecutionController;

    // Won't Use
    @FXML private VBox left;
    @FXML private Label SelectedInstructionHistoryChain;


    // Instructions Table
    @FXML private TableView<InstructionPeek> instructionsTable;
    @FXML private TableColumn<InstructionPeek, String> colBS;
    @FXML private TableColumn<InstructionPeek, Long> colCycles;
    @FXML private TableColumn<InstructionPeek, String> colArchitecture;
    @FXML private TableColumn<InstructionPeek, String> colInstruction;
    @FXML private TableColumn<InstructionPeek, String> colLabel;
    @FXML private TableColumn<InstructionPeek, Long> colNumber;

    private final Set<Integer> highlighted = new HashSet<>();
    private final Set<Integer> breakpointIndices = new HashSet<>();



    // History Chain Table
    @FXML private TableView<InstructionPeek> instructionsHistoryTable;
    @FXML private TableColumn<InstructionPeek, String> colHistoryLabel;
    @FXML private TableColumn<InstructionPeek, String> colHistoryBS;
    @FXML private TableColumn<InstructionPeek, String> colHistoryInstruction;
    @FXML private TableColumn<InstructionPeek, String> colHistoryArchitecture;
    @FXML private TableColumn<InstructionPeek, Long> colHistoryCycles;
    @FXML private TableColumn<InstructionPeek, Long> colHistoryNumber;


    @FXML private TextField SummaryLine;

    @FXML
    private void initialize() {
        refreshRowStylesWithBreakpoints();
        installBreakpointToggleByDoubleClick();
    }

    private void refreshRowStylesWithBreakpoints() {
        colNumber.setCellFactory(col -> new TableCell<InstructionPeek, Long>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(String.valueOf(item));

                int idx = getIndex();

                if (breakpointIndices.contains(idx)) {
                    javafx.scene.shape.Circle circle =
                            new javafx.scene.shape.Circle(5, javafx.scene.paint.Color.RED);
                    setGraphic(circle);
                    setContentDisplay(ContentDisplay.LEFT); // נקודה משמאל לטקסט
                } else {
                    setGraphic(null);
                }
            }
        });

        instructionsTable.refresh();
    }

    private void installBreakpointToggleByDoubleClick() {
        instructionsTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                int idx = instructionsTable.getSelectionModel().getSelectedIndex();
                if (idx >= 0) {
                    Boolean contains = breakpointIndices.contains(idx);
                    if (contains) {
                        breakpointIndices.remove(idx);
                    } else {
                        breakpointIndices.add(idx);
                    }
                    instructionsTable.refresh();
                    if (mainExecutionController.debugModeActive()){
                        sendNewRequestOnBreakpointChange(idx,contains);
                    }
                }
            }
        });
    }

    private void sendNewRequestOnBreakpointChange(int idx, Boolean contains) {
        Request changeBreakpointRequest = UpdateBreakpointRequest.build(new BreakpointRequest(idx, contains));
        HttpClientUtil.runAsync(changeBreakpointRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                UpdateBreakpointRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                UpdateBreakpointRequest.onResponse(response);
            }
        });


    }

    public List<Integer> getBreakpointIndices() {
        return new ArrayList<>(breakpointIndices);
    }


    public void setMainExecutionController(mainExecutionController mainExecutionController) {
        this.mainExecutionController = mainExecutionController;
    }

    @FXML
    void showHistoryChain(MouseEvent event) {
        InstructionPeek instructionPeek = instructionsTable.getSelectionModel().getSelectedItem();
        if (instructionPeek == null) {
            return;
        }

        List<InstructionPeek> chain = new ArrayList<>();
        for (InstructionPeek current = instructionPeek; current != null; current = current.expandedFrom()) {
            chain.add(current);
        }
        Collections.reverse(chain);

        instructionsHistoryTable.getItems().clear();
        instructionsHistoryTable.getItems().addAll(chain);

        colHistoryInstruction.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().stringRepresentation()));

        colHistoryLabel.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().label()));

        colHistoryCycles.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().cycles()).asObject());

        colHistoryNumber.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().lineId()).asObject());

        colHistoryBS.setCellValueFactory(data -> {
            boolean isSynthetic = data.getValue().isSynthetic();
            String text = isSynthetic ? "S" : "B";
            return new SimpleStringProperty(text);
        });

        colHistoryArchitecture.setCellValueFactory(data -> new SimpleStringProperty(""));
        installRowHighlighter();

    }

    public void setProgramPeek(List<InstructionPeek> instructions) {
        instructionsTable.getItems().clear();
        instructionsTable.getItems().addAll(instructions);

        colInstruction.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().stringRepresentation()));

        colLabel.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().label()));

        colCycles.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().cycles()).asObject());

        colNumber.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().lineId()).asObject());

        colBS.setCellValueFactory(data -> {
            boolean isSynthetic = data.getValue().isSynthetic();
            String text = isSynthetic ? "S" : "B";
            return new SimpleStringProperty(text);
        });

        colArchitecture.setCellValueFactory(data -> new SimpleStringProperty(""));
        installRowHighlighter();

    }


    private void installRowHighlighter() {
        if (instructionsTable.getRowFactory() != null) return;

        instructionsTable.setRowFactory(tv -> new TableRow<InstructionPeek>() {
            @Override
            protected void updateItem(InstructionPeek item, boolean empty) {
                super.updateItem(item, empty);

                // Apply yellow background only for highlighted row indices
                if (!empty && highlighted.contains(getIndex())) {
                    setStyle("-fx-background-color: #fff59d;"); // light yellow
                } else {
                    setStyle(""); // reset
                }
            }
        });
    }


    public void updateHighlightedInstructions(List<Integer> indices) {
        highlighted.clear();
        if (indices != null) {
            highlighted.addAll(indices);
        }
        instructionsTable.refresh(); // re-render rows to apply styles
    }

    public void highlightLine(Integer stopedLineInDebug) {
        if (stopedLineInDebug != null) {
            highlighted.add(stopedLineInDebug);
        }
        instructionsTable.refresh(); // re-render rows to apply styles
    }
}