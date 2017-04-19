/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

import org.eclipse.chemclipse.logging.core.Logger;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

	private final static Logger logger = Logger.getLogger(ProgressForm.class);
	public final static int DEFAULT_WIDTH = 300;
	public final static int DEFAULT_HEIGHT = 100;

	public static void showAndRunInBackground(final String text, final Task<?> t) {

		Platform.runLater(() -> {
			final ProgressForm f = new ProgressForm(text);
			final Service<?> s = new Service() {

				@Override
				protected Task createTask() {

					return t;
				}
			};
			s.setOnSucceeded(e -> {
				f.dialogStage.close();
			});
			s.setOnFailed(e -> {
				logger.error(t.getException().getLocalizedMessage(), t.getException());
				f.label.setText(t.getException().getLocalizedMessage());
				f.dialogStage.setOnCloseRequest(event -> event.isConsumed());
			});
			f.pb.progressProperty().bind(s.progressProperty());
			f.dialogStage.show();
			s.start();
		});
	}

	public static void showAndRunInFX(final String text, final CheckedRunnable r) {

		Platform.runLater(() -> {
			final ProgressForm f = new ProgressForm(text);
			try {
				f.pb.progressProperty().unbind();
				f.dialogStage.show();
				r.run();
				f.dialogStage.close();
			} catch(final Exception e) {
				logger.error(e.getLocalizedMessage(), e);
				f.label.setText(e.getLocalizedMessage());
				f.dialogStage.setOnCloseRequest(event -> event.isConsumed());
			}
		});
	}

	private final Stage dialogStage;
	private final ProgressBar pb = new ProgressBar();
	private final Label label = new Label();

	private ProgressForm(final String text) {
		this(text, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	private ProgressForm(final String text, final int width, final int height) {
		// Form
		dialogStage = new Stage();
		dialogStage.setWidth(width);
		dialogStage.setHeight(height);
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.centerOnScreen();
		dialogStage.setResizable(false);
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		dialogStage.setOnCloseRequest(event -> event.consume());
		// progress bar
		label.setText(text);
		pb.setProgress(-1F);
		pb.setMaxWidth(Double.MAX_VALUE);
		pb.setMaxHeight(Double.MAX_VALUE);
		final VBox hb = new VBox();
		hb.setPadding(new Insets(10, 10, 10, 10));
		hb.setSpacing(10);
		hb.setFillWidth(true);
		hb.setMaxWidth(Double.MAX_VALUE);
		hb.setMaxHeight(Double.MAX_VALUE);
		hb.setSpacing(10);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(label, pb);
		final Scene scene = new Scene(hb);
		dialogStage.setScene(scene);
	}

	protected Stage getDialogStage() {

		return dialogStage;
	}

	public void showAndRunInBackground(final CheckedRunnable r) {

		showAndRunInBackground(() -> r.run());
	}
}
