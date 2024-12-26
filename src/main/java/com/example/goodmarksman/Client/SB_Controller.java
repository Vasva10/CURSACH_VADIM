package com.example.goodmarksman.Client;

import com.example.goodmarksman.objects.Score;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class SB_Controller {
    @FXML
    TableView tableView;
    @FXML
    TableColumn idColumn;
    @FXML
    TableColumn playerNameColumn;
    @FXML
    TableColumn hitsColumn;
    @FXML
    TableColumn shotsColumn;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<Score, Integer>("id"));
        playerNameColumn.setCellValueFactory(new PropertyValueFactory<Score, String>("playerName"));
        hitsColumn.setCellValueFactory(new PropertyValueFactory<Score, Integer>("scoreValue"));
        shotsColumn.setCellValueFactory(new PropertyValueFactory<Score, Integer>("shotCountValue"));
    }

    public void setTable(ArrayList<Score> data) {
        ObservableList<Score> scoreData = FXCollections.observableArrayList(data);

        System.err.println(scoreData);
        Platform.runLater(() -> tableView.setItems(scoreData));
    }
}
