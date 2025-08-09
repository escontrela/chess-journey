package com.davidp.chessjourney.application.ui.controls;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectableCardController extends Pane {
    @FXML private Pane rootPane;
    @FXML private ImageView imgIcon;
    @FXML private Label lblTitle;
    @FXML private VBox subtitlesBox;

    private boolean selected = false;

    public SelectableCardController() {

        System.out.println("Cargando FXML: /com/davidp/chessjourney/selectable-card.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/selectable-card.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        subtitles.addListener((ListChangeListener<String>) change -> updateSubtitles());

        try {

            fxmlLoader.load();

        } catch (IOException e) {

            throw new RuntimeException("No se pudo cargar el FXML: /com/davidp/chessjourney/selectable-card.fxml", e);
        }
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onCardClicked);
    }

    @FXML
    private void initialize() {
        updateSubtitles();
    }

    public void setImage(Image image) {
        imgIcon.setImage(image);
    }

    public void setImageUrl(String url) {
        imgIcon.setImage(new Image(url));
    }
    public String getImageUrl() {

        return imgIcon.getImage() != null ? imgIcon.getImage().getUrl() : null;
    }

    public void setTitle(String title) {
        System.out.println("Setting title: " + title);
        lblTitle.setText(title);
    }

    public void setSubtitles(List<String> subtitles) {
        this.subtitles.clear();
        this.subtitles.addAll(subtitles);
        updateSubtitles();
    }

    private final ObservableList<String> subtitles = FXCollections.observableArrayList();

    public ObservableList<String> getSubtitles() {
        return subtitles;
    }

    public String getTitle() {
        return lblTitle.getText();
    }

    public boolean isSelected() {
        return selected;
    }

    private void updateSubtitles() {
        subtitlesBox.getChildren().clear();
        for (String subtitle : subtitles) {
            Label label = new Label(subtitle);
            label.getStyleClass().add("selectable-card-subtitle");
            subtitlesBox.getChildren().add(label);
        }
    }

    private void onCardClicked(MouseEvent event) {
        if (cardClickListener != null) {
            cardClickListener.onCardClicked(this);
        }

        selected = !selected;
        this.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), selected);
    }

    private CardClickListener cardClickListener;

    public interface CardClickListener {
        void onCardClicked(SelectableCardController card);
    }

    public void setCardClickListener(CardClickListener listener) {
        this.cardClickListener = listener;
    }
}