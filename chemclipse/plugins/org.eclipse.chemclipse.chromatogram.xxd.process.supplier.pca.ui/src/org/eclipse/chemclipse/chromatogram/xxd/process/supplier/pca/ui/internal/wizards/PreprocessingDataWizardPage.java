/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.net.URL;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers.PreprocessingController;
import org.eclipse.jface.wizard.WizardPage;
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

public class PreprocessingDataWizardPage extends WizardPage {

	private PcaPreprocessingData pcaPreprocessingData;
	private IPcaSettingsVisualization pcaSettings;
	private FXCanvas fxCanvas;
	private PreprocessingController controller;

	protected PreprocessingDataWizardPage(String pageName) {

		super(pageName);
		setTitle("Preprocess Data");
		setDescription("Data can be also preprocessed later in the process in Data Preprocessing page");
		pcaPreprocessingData = new PcaPreprocessingData();
		pcaSettings = new PcaSettingsVisualization(PreferenceSupplier.getPcaSettings());
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		fxCanvas = new FXCanvas(composite, SWT.NONE);
		fxCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(parent));
		setControl(composite);
	}

	protected void createScene(final Composite parent) {

		try {
			final URL location = getClass().getResource("/fxml/Preprocessing.fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			controller = fXMLLoader.getController();
			controller.setUpdate(pcaPreprocessingData, pcaSettings);
			final Scene scene = new Scene(root);
			fxCanvas.setScene(scene);
		} catch(final Exception e) {
		}
	}

	public PcaPreprocessingData getPcaPreprocessingData() {

		return pcaPreprocessingData;
	}

	public IPcaSettingsVisualization getPcaSettings() {

		return pcaSettings;
	}
}
