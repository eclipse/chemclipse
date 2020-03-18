/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.quickstart;

import java.lang.reflect.InvocationTargetException;

import javax.inject.Named;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.handlers.CreatePcaEvaluation;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.SamplesExtractor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IInputWizard;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public abstract class WizardTile implements TileDefinition {

	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, MApplication application, EModelService modelService, EPartService partService, IEclipseContext context) throws InvocationTargetException, InterruptedException {

		IInputWizard wizard = createWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		//
		int status = wizardDialog.open();
		if(status == Window.OK) {
			/*
			 * Settings
			 */
			IAnalysisSettings analysisSettings = wizard.getAnalysisSettings();
			PreprocessingSettings preprocessingSettings = wizard.getPreprocessingSettings();
			FilterSettings filterSettings = wizard.getFilterSettings();
			IExtractionData extractionData = wizard.getExtractionData();
			/*
			 * Run the process.
			 */
			SamplesExtractor runnable = new SamplesExtractor(extractionData, analysisSettings, filterSettings, preprocessingSettings);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(shell);
			monitor.run(true, true, runnable);
			/*
			 * Calculate the results and show the score plot page.
			 */
			ISamplesPCA<?, ?> samples = runnable.getSamples();
			if(samples != null) {
				CreatePcaEvaluation.createPart(samples, context, null);
			} else {
				MessageDialog.openInformation(shell, "PCA", "No sample(s) have been processed, hence the editor is not started.");
			}
		}
	}

	@Override
	public String getPreferredPerspective() {

		return CreatePcaEvaluation.PCA_PERSPECTIVE;
	}

	@Override
	public String getContext() {

		return "pca-quickstart-default-wizard";
	}

	protected abstract IInputWizard createWizard();
}
