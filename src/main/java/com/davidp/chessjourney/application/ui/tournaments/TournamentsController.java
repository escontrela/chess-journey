package com.davidp.chessjourney.application.ui.tournaments;

import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.usecases.ManageTournamentsUseCase;
import com.davidp.chessjourney.domain.Tournament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the tournaments screen.
 */
public class TournamentsController implements ScreenController {

    private ManageTournamentsUseCase manageTournamentsUseCase;
    private ScreenStatus status;

    @FXML private Button btClose;
    @FXML private Button btRefresh;
    @FXML private Pane rootPane;
    @FXML private TableView<Tournament> tournamentTable;
    @FXML private TableColumn<Tournament, String> colProvincia;
    @FXML private TableColumn<Tournament, String> colConcejo;
    @FXML private TableColumn<Tournament, String> colTorneo;
    @FXML private TableColumn<Tournament, LocalDate> colInicio;
    @FXML private TableColumn<Tournament, LocalDate> colFin;
    @FXML private TableColumn<Tournament, String> colRitmo;

    @Override
    public void setData(InputScreenData inputData) {
        if (inputData != null) {
            setLayout(inputData.getLayoutX(), inputData.getLayoutY());
        }
    }

    @Override
    public void setLayout(double layoutX, double layoutY) {
        rootPane.setLayoutX(layoutX);
        rootPane.setLayoutY(layoutY);
    }

    @Override
    public void show() {
        rootPane.setVisible(true);
        status = ScreenStatus.VISIBLE;
    }

    @Override
    public void show(InputScreenData inputData) {
        setData(inputData);
        show();
    }

    @Override
    public void hide() {
        rootPane.setVisible(false);
        status = ScreenStatus.HIDDEN;
    }

    @Override
    public Pane getRootPane() {
        return rootPane;
    }

    @Override
    public boolean isInitialized() {
        return status == ScreenStatus.INITIALIZED;
    }

    @Override
    public boolean isVisible() {
        return rootPane != null && rootPane.isVisible();
    }

    @Override
    public boolean isHidden() {
        return !isVisible();
    }

    public void initialize() {
        // Initialize use case
        this.manageTournamentsUseCase = UseCaseFactory.createManageTournamentsUseCase();

        // Set up table columns
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colConcejo.setCellValueFactory(new PropertyValueFactory<>("concejo"));
        colTorneo.setCellValueFactory(new PropertyValueFactory<>("torneo"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("inicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("fin"));
        colRitmo.setCellValueFactory(new PropertyValueFactory<>("ritmo"));

        // Set status
        status = ScreenStatus.INITIALIZED;

        // Load initial data
        loadTournaments();
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (event.getSource() == btClose) {
            hide();
        } else if (event.getSource() == btRefresh) {
            refreshTournaments();
        }
    }

    private void loadTournaments() {
        try {
            List<Tournament> tournaments = manageTournamentsUseCase.getUpcomingTournaments(10);
            ObservableList<Tournament> tournamentData = FXCollections.observableArrayList(tournaments);
            tournamentTable.setItems(tournamentData);
            
            System.out.println("Loaded " + tournaments.size() + " upcoming tournaments");
        } catch (Exception e) {
            System.err.println("Error loading tournaments: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void refreshTournaments() {
        try {
            btRefresh.setDisable(true);
            btRefresh.setText("Refreshing...");
            
            // Force refresh from website
            manageTournamentsUseCase.refreshTournaments();
            
            // Reload table
            loadTournaments();
            
        } catch (Exception e) {
            System.err.println("Error refreshing tournaments: " + e.getMessage());
            e.printStackTrace();
        } finally {
            btRefresh.setDisable(false);
            btRefresh.setText("Refresh");
        }
    }

    @Override
    public ScreenStatus getStatus() {
        return status;
    }
}