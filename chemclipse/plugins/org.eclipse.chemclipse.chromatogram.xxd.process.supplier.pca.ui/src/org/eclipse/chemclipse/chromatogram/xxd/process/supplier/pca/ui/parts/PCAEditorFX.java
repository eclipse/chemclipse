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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers.PCAEditorController;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.PCAController;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PCAEditorFX {

	private final static Logger logger = Logger.getLogger(PCAEditorFX.class);
	private ListChangeListener<ISample<? extends ISampleData>> actualSelectionChangeListener;
	private PCAEditorController controller;
	/**
	 * View controller. Will be only available after {@link #createScene(Composite)} was called.
	 */
	private FXCanvas fxCanvas;
	@Inject
	private Composite parent;
	private PCAController pcaController;

	public PCAEditorFX() {
		actualSelectionChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				if(controller != null) {
					if(controller.getSamples().isPresent()) {
						ObservableList<ISample<? extends ISampleData>> selection = SelectionManagerSample.getInstance().getSelection();
						if(!selection.isEmpty()) {
							ISample<? extends ISampleData> s = selection.get(0);
							controller.seletedSample((Sample)s);
						}
					}
				}
			}
		};
	}

	@PostConstruct
	public void createControl() {

		init(parent);
		SelectionManagerSample.getInstance().getSelection().addListener(actualSelectionChangeListener);
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

		SelectionManagerSample.getInstance().getSelection().remove(actualSelectionChangeListener);
	}

	@Focus
	public void setFocus() {

		fxCanvas.setFocus();
		if(controller != null) {
			if(controller.getSamples().isPresent()) {
				SelectionManagerSamples.getInstance().getSelection().setAll(controller.getSamples().get());
				SelectionManagerSample.getInstance().getSelection().setAll(controller.getSelectedSamples());
			}
		}
	}
}
