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
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers.PreprocessingController;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PreprocessingPartFX {

	private final static Logger logger = Logger.getLogger(PreprocessingPartFX.class);
	private PreprocessingController controller;
	private ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samplesChangeListener;
	/**
	 * View controller. Will be only available after {@link #createScene(Composite)} was called.
	 */
	private FXCanvas fxCanvas;
	@Inject
	private Composite parent;
	@Inject
	private MPart part;

	public PreprocessingPartFX() {

		samplesChangeListener = new ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> c) {

				if(!c.getList().isEmpty()) {
					ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples = c.getList().get(0);
					controller.setPreprecessing(SelectionManagerSamples.getInstance().getPreprocessoringData(samples));
				} else {
					controller.reset();
				}
			}
		};
	}

	@PostConstruct
	public void createControl() {

		init(parent);
	}

	protected void createScene(final Composite parent) {

		try {
			final URL location = getClass().getResource("/fxml/Preprocessing.fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			controller = fXMLLoader.getController();
			SelectionManagerSamples.getInstance().selectionProperty().addListener(samplesChangeListener);
			if(!SelectionManagerSamples.getInstance().selectionProperty().isEmpty()) {
				ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples = SelectionManagerSamples.getInstance().selectionProperty().get(0);
				controller.setPreprecessing(SelectionManagerSamples.getInstance().getPreprocessoringData(samples));
			}
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

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().selectionProperty().removeListener(samplesChangeListener);
	}
}
