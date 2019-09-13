/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * A FX-based progress form.
 *
 * @author Alexander Kerner
 *
 */
public class ProgressForm {

	public ProgressBar getProgressBar() {

		return progressBar;
	}

	public Label getLabel() {

		return label;
	}

	public Button getCancelButton() {

		return cancelButton;
	}

	public final static int DEFAULT_WIDTH = 500;
	public final static int DEFAULT_HEIGHT = 120;

	/**
	 * Shows a simple progress bar, as long as given task is running. The task
	 * is executed in the background, after success, {@link Consumer} {@code c}
	 * is called on UI thread.
	 *
	 * @param text
	 *            Text to display as long as task is running
	 * @param t
	 *            {@link Task} to execute in the background
	 * @param c
	 *            {@link Consumer} to execute on UI thread
	 */
	@Deprecated
	public static <T> void showAndRunInBackground(final Task<T> t, final Consumer<T> c) {

		Platform.runLater(() -> {
			final ProgressForm f = new ProgressForm("working..");
			f.label.textProperty().bind(t.messageProperty());
			f.cancelButton.setOnMouseClicked(event -> {
				t.cancel();
				f.cancelButton.setDisable(true);
			});
			final Service<T> s = new Service<T>() {

				@Override
				protected Task<T> createTask() {

					return t;
				}
			};
			s.setOnSucceeded(e -> {
				if(c != null) {
					c.accept(s.getValue());
				}
				f.dialogStage.close();
			});
			s.setOnFailed(e -> {
				f.label.textProperty().unbind();
				f.label.setText("An unexpected error occoured");
				f.dialogStage.setOnCloseRequest(event -> event.isConsumed());
				f.progressBar.progressProperty().unbind();
				f.progressBar.setProgress(100);
				f.cancelButton.setText("OK");
				f.cancelButton.setOnAction(event -> f.dialogStage.close());
			});
			s.setOnCancelled(e -> {
				f.dialogStage.close();
			});
			f.progressBar.progressProperty().bind(s.progressProperty());
			f.dialogStage.show();
			s.start();
		});
	}

	@Deprecated
	public static <T> void showAndRunInFX(final String text, final Task<T> t, final Consumer<T> c) {

		Platform.runLater(() -> {
			final ProgressForm f = new ProgressForm(text);
			f.cancelButton.setOnMouseClicked(event -> {
				t.cancel();
				f.cancelButton.setDisable(true);
			});
			final Service<T> s = new Service<T>() {

				@Override
				protected Task<T> createTask() {

					return t;
				}
			};
			s.setOnSucceeded(e -> {
				if(c != null) {
					c.accept(s.getValue());
				}
				f.dialogStage.close();
			});
			s.setOnFailed(e -> {
				f.label.setText(t.getException().getLocalizedMessage());
				f.dialogStage.setOnCloseRequest(event -> event.isConsumed());
			});
			s.setOnCancelled(e -> {
				f.dialogStage.close();
			});
			f.progressBar.progressProperty().bind(s.progressProperty());
			f.dialogStage.show();
			s.start();
		});
	}

	@Deprecated
	public static void showAndRunInFX(final String text, final CheckedRunnable r) {

		Platform.runLater(() -> {
			final ProgressForm f = new ProgressForm(text);
			try {
				f.progressBar.progressProperty().unbind();
				f.dialogStage.show();
				r.run();
				f.dialogStage.close();
			} catch(final Throwable e) {
				f.label.setText(e.getLocalizedMessage());
				f.dialogStage.setOnCloseRequest(event -> event.isConsumed());
			} finally {
			}
		});
	}

	final Stage dialogStage;
	final ProgressBar progressBar = new ProgressBar();
	final Label label = new Label();
	final Button cancelButton = new Button();

	public ProgressForm(final String text) {
		this(text, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	private ProgressForm(final String text, final int width, final int height) {
		// Form
		dialogStage = new Stage();
		dialogStage.setTitle(text);
		dialogStage.setWidth(width);
		dialogStage.setHeight(height);
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.centerOnScreen();
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.setOnCloseRequest(event -> event.consume());
		// progress bar
		progressBar.setProgress(-1F);
		progressBar.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(progressBar, Priority.ALWAYS);
		cancelButton.setAlignment(Pos.CENTER);
		cancelButton.setText("Cancel");
		final HBox box = new HBox();
		box.setAlignment(Pos.CENTER);
		box.setMaxWidth(Double.MAX_VALUE);
		box.setSpacing(2);
		box.getChildren().addAll(progressBar, cancelButton);
		final VBox hb = new VBox();
		hb.setPadding(new Insets(10, 10, 10, 10));
		hb.setSpacing(10);
		hb.setFillWidth(true);
		hb.setMaxWidth(Double.MAX_VALUE);
		hb.setMaxHeight(Double.MAX_VALUE);
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(label, box);
		final Scene scene = new Scene(hb);
		dialogStage.setScene(scene);
	}

	public Stage getDialogStage() {

		return dialogStage;
	}

	@Deprecated
	public void showAndRunInBackground(final CheckedRunnable r) {

		showAndRunInBackground(() -> r.run());
	}
}
