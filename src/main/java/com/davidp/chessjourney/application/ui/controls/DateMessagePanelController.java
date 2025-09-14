package com.davidp.chessjourney.application.ui.controls;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * This controller is responsible for the date message panel control that displays
 * tournament information with a date square on the left and tournament details on the right.
 */
public class DateMessagePanelController extends Pane {

    @FXML private Pane rootPane;
    @FXML private Label lblMonthAbbrev;
    @FXML private Label lblDayNumber;
    @FXML private Label lblTournamentTitle;
    @FXML private Label lblConcejo;
    @FXML private Label lblProvincia;
    @FXML private Label lblLocal;
    @FXML private Button btClose;

    private DateMessagePanelActionListener actionListener;

    /**
     * Interface for handling date message panel button actions.
     */
    public interface DateMessagePanelActionListener {
        void onCloseButtonClicked();
        void onLocalLinkClicked();
    }

    public DateMessagePanelController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/date-message-panel.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el FXML: /com/davidp/chessjourney/date-message-panel.fxml", e);
        }
    }

    @FXML
    private void initialize() {
        // Initialize with default values if needed
        if (lblTournamentTitle.getText() == null || lblTournamentTitle.getText().isEmpty()) {
            lblTournamentTitle.setText("Próximo Torneo");
        }
    }

    /**
     * Sets the tournament date to be displayed in the date square.
     *
     * @param date The tournament start date
     */
    public void setTournamentDate(LocalDate date) {
        if (date != null) {
            // Set month abbreviation (e.g., "Sept")
            String monthAbbrev = date.format(DateTimeFormatter.ofPattern("MMM", Locale.forLanguageTag("es-ES")));
            lblMonthAbbrev.setText(monthAbbrev);
            
            // Set day number
            lblDayNumber.setText(String.valueOf(date.getDayOfMonth()));
        } else {
            lblMonthAbbrev.setText("");
            lblDayNumber.setText("");
        }
    }

    /**
     * Sets the tournament title.
     *
     * @param title The tournament title
     */
    public void setTournamentTitle(String title) {
        lblTournamentTitle.setText(title != null ? title : "");
    }

    /**
     * Sets the concejo (municipality).
     *
     * @param concejo The concejo
     */
    public void setConcejo(String concejo) {
        lblConcejo.setText(concejo != null ? concejo : "");
    }

    /**
     * Sets the provincia (province).
     *
     * @param provincia The provincia
     */
    public void setProvincia(String provincia) {
        lblProvincia.setText(provincia != null ? provincia : "");
    }

    /**
     * Sets the local (venue).
     *
     * @param local The local venue
     */
    public void setLocal(String local) {
        lblLocal.setText(local != null ? local : "");
    }

    /**
     * Gets the current tournament title.
     *
     * @return The current tournament title
     */
    public String getTournamentTitle() {
        return lblTournamentTitle.getText();
    }

    /**
     * Gets the current concejo.
     *
     * @return The current concejo
     */
    public String getConcejo() {
        return lblConcejo.getText();
    }

    /**
     * Gets the current provincia.
     *
     * @return The current provincia
     */
    public String getProvincia() {
        return lblProvincia.getText();
    }

    /**
     * Gets the current local.
     *
     * @return The current local
     */
    public String getLocal() {
        return lblLocal.getText();
    }

    /**
     * Sets the action listener for button events.
     *
     * @param listener The action listener
     */
    public void setDateMessagePanelActionListener(DateMessagePanelActionListener listener) {
        this.actionListener = listener;
    }

    @FXML
    void buttonAction(ActionEvent event) {
        if (actionListener != null) {
            if (isCloseButtonClicked(event)) {
                actionListener.onCloseButtonClicked();
            }
        }
    }

    @FXML
    void localLinkAction(ActionEvent event) {
        if (actionListener != null) {
            actionListener.onLocalLinkClicked();
        }
    }

    private boolean isCloseButtonClicked(ActionEvent event) {
        return event.getSource() == btClose;
    }
}