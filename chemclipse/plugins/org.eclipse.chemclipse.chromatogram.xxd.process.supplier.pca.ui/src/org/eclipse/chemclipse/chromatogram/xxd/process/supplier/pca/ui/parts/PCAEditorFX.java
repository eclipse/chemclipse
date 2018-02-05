/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmHb.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.net.URL;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.SampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.SamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers.PCAEditorController;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.PCAController;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PCAEditorFX {

	private final static Logger logger = Logger.getLogger(PCAEditorFX.class);
	private PCAEditorController controller;
	/**
	 * View controller. Will be only available after {@link #createScene(Composite)} was called.
	 */
	private FXCanvas fxCanvas;
	@Inject
	private Composite parent;
	private PCAController pcaController;

	public PCAEditorFX() {
	}

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
			controller = fXMLLoader.getController();
			controller.setSamplesConsumer(pcaController::setSamples);
			final Scene scene = new Scene(root);
			fxCanvas.setScene(scene);
		} catch(final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void init(final Composite parent) {

		// this will initialize the FX Toolkit
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		pcaController = new PCAController(composite, new GridData(GridData.FILL_HORIZONTAL));
		fxCanvas = new FXCanvas(composite, SWT.NONE);
		fxCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(parent));
	}

	@PreDestroy
	public void preDestroy() {

		Optional<SamplesVisualization> samples = controller.getSamples();
		if(samples.isPresent()) {
			boolean contains = SelectionManagerSamples.getInstance().getSelection().remove(samples.get());
			if(contains) {
				SelectionManagerSample.getInstance().getSelection().clear();
			}
			SelectionManagerSamples.getInstance().getElements().remove(samples.get());
		}
		controller.preDestroy();
	}

	@Focus
	public void setFocus() {

		fxCanvas.setFocus();
		if(controller != null && controller.getSamples().isPresent()) {
			SampleVisualization sample = controller.getSelectedSamples();
			SelectionManagerSamples.getInstance().getSelection().setAll(controller.getSamples().get());
			if(sample != null) {
				SelectionManagerSample.getInstance().getSelection().setAll(sample);
			}
		} else {
			SelectionManagerSamples.getInstance().getSelection().clear();
		}
	}
}
