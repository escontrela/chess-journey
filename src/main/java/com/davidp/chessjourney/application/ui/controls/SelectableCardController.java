package com.davidp.chessjourney.application.ui.controls;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
    private final List<String> subtitles = new ArrayList<>();

    public SelectableCardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/davidp/chessjourney/selectable-card.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public void setTitle(String title) {
        lblTitle.setText(title);
    }

    public void setSubtitles(List<String> subtitles) {
        this.subtitles.clear();
        this.subtitles.addAll(subtitles);
        updateSubtitles();
    }

    public List<String> getSubtitles() {
        return new ArrayList<>(subtitles);
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
        selected = !selected;
        this.pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("selected"), selected);
    }
}

