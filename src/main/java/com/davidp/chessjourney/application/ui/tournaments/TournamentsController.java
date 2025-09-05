package com.davidp.chessjourney.application.ui.tournaments;

import com.davidp.chessjourney.application.factories.UseCaseFactory;
import com.davidp.chessjourney.application.ui.ScreenController;
import com.davidp.chessjourney.application.ui.settings.InputScreenData;
import com.davidp.chessjourney.application.usecases.ManageTournamentsUseCase;
import com.davidp.chessjourney.domain.Tournament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the tournaments screen.
 */
public class TournamentsController implements ScreenController, Initializable {

    @FXML
    private Button btClose;

    @FXML
    private Button btRefresh;

    @FXML
    private TableView<Tournament> tournamentTable;

    @FXML
    private TableColumn<Tournament, String> colProvincia;

    @FXML
    private TableColumn<Tournament, String> colConcejo;

    @FXML
    private TableColumn<Tournament, String> colTorneo;

    @FXML
    private TableColumn<Tournament, LocalDate> colInicio;

    @FXML
    private TableColumn<Tournament, LocalDate> colFin;

    @FXML
    private TableColumn<Tournament, String> colRitmo;

    private ManageTournamentsUseCase manageTournamentsUseCase;
    private ScreenController.ScreenStatus status;

    @Override
    public void setData(InputScreenData inputData) {
        // No specific data needed for tournaments screen
    }

    @Override
    public void setLayout(double layoutX, double layoutY) {
        if (tournamentTable.getParent() instanceof Pane parent) {
            parent.setLayoutX(layoutX);
            parent.setLayoutY(layoutY);
        }
    }

    @Override
    public void show() {
        if (tournamentTable.getParent() instanceof Pane parent) {
            parent.setVisible(true);
            status = ScreenController.ScreenStatus.VISIBLE;
        }
    }

    @Override
    public void show(InputScreenData inputData) {
        setData(inputData);
        show();
    }

    @Override
    public void hide() {
        if (tournamentTable.getParent() instanceof Pane parent) {
            parent.setVisible(false);
            status = ScreenController.ScreenStatus.HIDDEN;
        }
    }

    @Override
    public Pane getRootPane() {
        return (Pane) tournamentTable.getParent();
    }

    @Override
    public boolean isInitialized() {
        return status == ScreenController.ScreenStatus.INITIALIZED;
    }

    @Override
    public boolean isVisible() {
        return tournamentTable.getParent() != null && tournamentTable.getParent().isVisible();
    }

    @Override
    public boolean isHidden() {
        return !isVisible();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize use case
        this.manageTournamentsUseCase = UseCaseFactory.createManageTournamentsUseCase();

        // Set up table columns
        colProvincia.setCellValueFactory(new PropertyValueFactory<>("provincia"));
        colConcejo.setCellValueFactory(new PropertyValueFactory<>("concejo"));
        colTorneo.setCellValueFactory(new PropertyValueFactory<>("torneo"));
        colInicio.setCellValueFactory(new PropertyValueFactory<>("inicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("fin"));
        colRitmo.setCellValueFactory(new PropertyValueFactory<>("ritmo"));

        // Load initial data
        loadTournaments();
    }

    @FXML
    void buttonAction(MouseEvent event) {
        if (event.getSource() == btClose) {
            closeWindow();
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

    private void closeWindow() {
        Stage stage = (Stage) btClose.getScene().getWindow();
        stage.close();
    }

    @Override
    public ScreenController.ScreenStatus getStatus() {
        return status;
    }

    public void setStatus(ScreenController.ScreenStatus status) {
        this.status = status;
    }
}