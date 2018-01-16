/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmHb.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * jan - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PCAEditorFX {

	private final static Logger logger = Logger.getLogger(PCAEditorFX.class);
	/**
	 * View controller. Will be only available after {@link #createScene(Composite)} was called.
	 */
	private FXCanvas fxCanvas;
	@Inject
	private Composite parent;

	@PostConstruct
	public void createControl() {

		init(parent);
	}

	protected void createScene(final Composite parent) {

		try {
			final URL location = getClass().getResource("/fxml/PCAEditor.fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			final Scene scene = new Scene(root);
			fxCanvas.setScene(scene);
		} catch(final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void init(final Composite parent) {

		// this will initialize the FX Toolkit
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		parent.setLayout(new FillLayout());
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(parent));
	}

	@PreDestroy
	public void preDestroy() {

	}

	@Focus
	public void setFocus() {

		fxCanvas.setFocus();
	}
}
