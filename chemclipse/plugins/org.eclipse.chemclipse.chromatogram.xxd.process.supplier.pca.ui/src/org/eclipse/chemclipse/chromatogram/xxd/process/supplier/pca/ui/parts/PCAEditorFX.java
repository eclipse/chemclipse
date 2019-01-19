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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers.CreatePcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.controllers.PCAEditorController;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.PCAController;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class PCAEditorFX {

	public static final String CONTRIBUTION_URI = "bundleclass://org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui/org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.PCAEditorFX";
	public static final String ICON_URI = "platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/chromatogram.gif";
	public static final String ID = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts.PCAEditorFX";
	public static final String LABEL = "PCA Editor";
	public static final String TOOL_TIPS = "PCA Editor";
	private final static Logger logger = Logger.getLogger(PCAEditorFX.class);
	private PCAEditorController controller;
	/**
	 * View controller. Will be only available after {@link #createScene(Composite)} was called.
	 */
	private FXCanvas fxCanvas;
	@Inject
	private Composite parent;
	private PCAController pcaController;
	@Inject
	private MPart part;
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSamples managerSamples;
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSample managerSample;
	@SuppressWarnings("restriction")
	@Inject
	private EHandlerService handlerService;
	private static String ID_COMMAND_SETTINGS = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.command.settingspcaeditor";

	public PCAEditorFX() {

	}

	@SuppressWarnings("restriction")
	@PostConstruct
	public void createControl() {

		init(parent);
		handlerService.activateHandler(ID_COMMAND_SETTINGS, new Object() {

			@Execute
			private void execute(Display display) {

				pcaController.openSettingsDialog(display);
			}
		});
	}

	protected void createScene(final Composite parent) {

		try {
			final URL location = getClass().getResource("/fxml/PCAEditor.fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			controller = fXMLLoader.getController();
			controller.setSelectionManagerSamples(getSelectionManagerSamples());
			controller.setSamplesConsumer((s) -> {
				pcaController.setSamples(s);
			});
			final Scene scene = new Scene(root);
			fxCanvas.setScene(scene);
			Object object = part.getTransientData().get(CreatePcaEvaluation.DATA_SAMPLES);
			if(object instanceof ISamplesVisualization) {
				ISamplesVisualization samples = (ISamplesVisualization)object;
				controller.setSamples(samples);
			}
			Object laodData = part.getTransientData().get(CreatePcaEvaluation.ALLOW_DATALOAD);
			if(laodData instanceof Boolean) {
				controller.setLoadButtonVisible((Boolean)laodData);
			}
			setFocus();
		} catch(final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void init(final Composite parent) {

		// this will initialize the FX Toolkit
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		pcaController = new PCAController(composite, new GridData(GridData.FILL_HORIZONTAL));
		pcaController.setSelectionManagerSamples(getSelectionManagerSamples());
		fxCanvas = new FXCanvas(composite, SWT.NONE);
		fxCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(parent));
	}

	@PreDestroy
	public void preDestroy() {

		Optional<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> samples = controller.getSamples();
		if(samples.isPresent()) {
			boolean contains = getSelectionManagerSamples().getSelection().remove(samples.get());
			if(contains) {
				getSelectionManagerSample().getSelection().clear();
			}
			getSelectionManagerSamples().getElements().remove(samples.get());
		}
		controller.preDestroy();
	}

	private SelectionManagerSample getSelectionManagerSample() {

		if(managerSample != null) {
			return managerSample;
		}
		return SelectionManagerSample.getInstance();
	}

	private SelectionManagerSamples getSelectionManagerSamples() {

		if(managerSamples != null) {
			return managerSamples;
		}
		return SelectionManagerSamples.getInstance();
	}

	@Focus
	public void setFocus() {

		fxCanvas.setFocus();
		if(controller != null && controller.getSamples().isPresent()) {
			ISampleVisualization sample = controller.getSelectedSamples();
			getSelectionManagerSamples().getSelection().setAll(controller.getSamples().get());
			if(sample != null) {
				getSelectionManagerSample().getSelection().setAll(sample);
			}
		} else {
			getSelectionManagerSamples().getSelection().clear();
		}
	}
}
