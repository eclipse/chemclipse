/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.views;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import javafx.embed.swt.FXCanvas;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class ChromatogramOverlay3D {

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, false));
		final org.eclipse.swt.widgets.Button swtButton = new org.eclipse.swt.widgets.Button(parent, SWT.PUSH);
		swtButton.setText("Press me");
		final FXCanvas fxCanvas = new FXCanvas(parent, SWT.NONE) {

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {

				getScene().getWindow().sizeToScene();
				int width = (int)getScene().getWidth();
				int height = (int)getScene().getHeight();
				return new Point(width, height);
			}
		};
		//
		Group group = new Group();
		final Button javafxButton = new Button("Curious?");
		javafxButton.setId("chemclipse");
		group.getChildren().add(javafxButton);
		Scene scene = new Scene(group, Color.rgb(parent.getBackground().getRed(), parent.getBackground().getGreen(), parent.getBackground().getBlue()));
		scene.getStylesheets().add("css/JavaFX.css");
		fxCanvas.setScene(scene);
		/*
		 * Button action listener.
		 */
		swtButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {

				javafxButton.setText("JavaFX: 3D chromatogram overlay is coming soon!");
				parent.layout();
			}
		});
		javafxButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				swtButton.setText("A SWT and JavaFX interaction is possible.");
				parent.layout();
			}
		});
	}
}