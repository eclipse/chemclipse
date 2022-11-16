/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - formatting
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.quickstart;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.handlers.CreatePcaEvaluation;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards.IInputWizard;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public abstract class WizardTile implements TileDefinition {

	private static final Logger logger = Logger.getLogger(WizardTile.class);
	//
	public static final int DEFAULT_WIDTH = 500;
	public static final int DEFAULT_HEIGHT = 600;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext context) throws InvocationTargetException, InterruptedException {

		IInputWizard wizard = createWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(shell, wizard);
		wizardDialog.setMinimumPageSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		AtomicReference<ISamplesPCA<?, ?>> evaluation = new AtomicReference<>();
		String title = null;
		//
		try {
			if(wizardDialog.open() == Window.OK) {
				/*
				 * Settings
				 */
				IAnalysisSettings analysisSettings = wizard.getAnalysisSettings();
				analysisSettings.setPreprocessingSettings(wizard.getPreprocessingSettings());
				analysisSettings.setFilterSettings(wizard.getFilterSettings());
				IExtractionData extractionData = wizard.getExtractionData();
				title = analysisSettings.getTitle();
				//
				if(extractionData != null) {
					ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(wizardDialog.getShell());
					monitorDialog.setCancelable(true);
					monitorDialog.run(true, true, new IRunnableWithProgress() {

						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

							ISamplesPCA<?, ?> samples = extractionData.process(monitor);
							samples.setAnalysisSettings(analysisSettings);
							evaluation.set(samples);
						}
					});
				}
			}
			/*
			 * Open the editor.
			 */
			ISamplesPCA<?, ?> samples = evaluation.get();
			if(samples != null) {
				CreatePcaEvaluation.createPart(samples, context, title);
			} else {
				logger.warn("Failed to create PCA analysis.");
			}
		} finally {
			wizard.dispose();
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