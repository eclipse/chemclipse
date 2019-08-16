/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.fx.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class FXUtils {

	public static void showError(final String msg, final Throwable e) {

		Platform.runLater(() -> {
			final Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(msg);
			if(e != null) {
				alert.setContentText(e.getLocalizedMessage());
				// Create expandable Exception.
				final StringWriter sw = new StringWriter();
				final PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				final String exceptionText = sw.toString();
				final Label label = new Label("The exception stacktrace was:");
				final TextArea textArea = new TextArea(exceptionText);
				textArea.setEditable(false);
				textArea.setWrapText(true);
				textArea.setMaxWidth(Double.MAX_VALUE);
				textArea.setMaxHeight(Double.MAX_VALUE);
				GridPane.setVgrow(textArea, Priority.ALWAYS);
				GridPane.setHgrow(textArea, Priority.ALWAYS);
				final GridPane expContent = new GridPane();
				expContent.setMaxWidth(Double.MAX_VALUE);
				expContent.add(label, 0, 0);
				expContent.add(textArea, 0, 1);
				// Set expandable Exception into the dialog pane.
				alert.getDialogPane().setExpandableContent(expContent);
			}
			alert.showAndWait();
		});
	}

	public static void showError(final Throwable e) {

		showError(e.getLocalizedMessage(), e);
	}
}
