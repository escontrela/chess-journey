package com.davidp.chessjourney.application.ui;

import javafx.scene.layout.Pane;

/**
 * Encapsula la vista (root) + el controlador + un objeto de datos opcional.
 *
 * @param <C> Tipo del Controller
 * @param <D> Tipo del objeto de datos (opcional), si no necesitas datos, puedes usar Void o un
 *     comodín
 */
public class ScreenPanel<C, D> {

  private final Pane root;
  private final C controller;
  private D data; // si quieres almacenar algún objeto de datos

  public ScreenPanel(Pane root, C controller) {
    this.root = root;
    this.controller = controller;
  }

  public Pane getRoot() {
    return root;
  }

  public C getController() {
    return controller;
  }

  public D getData() {
    return data;
  }

  public void setData(D data) {
    this.data = data;
  }
}
