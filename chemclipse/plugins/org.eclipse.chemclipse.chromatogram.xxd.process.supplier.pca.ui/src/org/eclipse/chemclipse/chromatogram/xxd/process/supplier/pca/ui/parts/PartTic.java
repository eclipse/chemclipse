/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Alexander Kerner - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.net.URL;

import javax.annotation.PostConstruct;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PartTic {

	private static final Logger logger = Logger.getLogger(PartTic.class);
	private FXCanvas fxCanvas;

	@PostConstruct
	public void createComposite(Composite parent) {

		init(parent);
	}

	private void createScene() {

		try {
			IPath path = new Path("fxml/FXML.fxml");
			URL location = FileLocator.find(Activator.getDefault().getBundle(), path, null);
			FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			Parent root = fXMLLoader.load(location.openStream());
			Scene scene = new Scene(root);
			scene.getStylesheets().add("css/fxml.css");
			fxCanvas.setScene(scene);
			fXMLLoader.getController();
			logger.debug("Initializing FX successful");
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void init(Composite parent) {

		fxCanvas = new FXCanvas(parent, SWT.NONE) {

			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {

				getScene().getWindow().sizeToScene();
				int width = (int)getScene().getWidth();
				int height = (int)getScene().getHeight();
				return new Point(width, height);
			}
		};
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene());
	}
}
