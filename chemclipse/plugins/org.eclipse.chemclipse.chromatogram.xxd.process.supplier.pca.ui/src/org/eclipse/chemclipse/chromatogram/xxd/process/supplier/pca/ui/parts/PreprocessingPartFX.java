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

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers.PreprocessingController;
import org.eclipse.chemclipse.logging.core.Logger;
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

public class PreprocessingPartFX extends GeneralPcaPart {

	private final static Logger logger = Logger.getLogger(PreprocessingPartFX.class);
	private PreprocessingController controller;
	/**
	 * View controller. Will be only available after {@link #createScene(Composite)} was called.
	 */
	private FXCanvas fxCanvas;

	@Inject
	public PreprocessingPartFX() {
		super();
	}

	@PostConstruct
	public void postConstruct(Composite composite) {

		init(composite);
		inicializeHandler();
	}

	protected void createScene(final Composite parent) {

		try {
			final URL location = getClass().getResource("/fxml/Preprocessing.fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			controller = fXMLLoader.getController();
			setSamples();
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
		fxCanvas = new FXCanvas(composite, SWT.NONE);
		fxCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(parent));
	}

	@Override
	protected void variablesHasBeenUpdated() {

	}

	@Override
	protected void samplesHasBeenUpdated() {

	}

	@Override
	protected void samplesHasBeenSet() {

		setSamples();
	}

	@Override
	protected void settingsHasBeenChanged() {

	}

	private void setSamples() {

		ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples = getSamples();
		if(controller != null) {
			if(samples != null) {
				controller.setUpdate(getSelectionManagerSamples().getPreprocessingData(samples), samples, getSelectionManagerSamples().getPcaSettings(samples));
				controller.dataPreprocessing();
			} else {
				controller.setUpdate(new PcaPreprocessingData(), new PcaSettingsVisualization());
			}
		}
	}
}
