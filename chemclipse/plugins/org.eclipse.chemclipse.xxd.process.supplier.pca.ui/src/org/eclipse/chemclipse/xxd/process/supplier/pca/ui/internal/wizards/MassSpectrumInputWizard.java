/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - adjustments for mass spectra
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IPreprocessingSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.PcaExtractionMassSpectra;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class MassSpectrumInputWizard extends Wizard implements IInputWizard {

	private MassSpectrumSettingsWizardPage massSpectrumSettingsWizardPage = new MassSpectrumSettingsWizardPage();
	private MassSpectrumFilesWizardPage massSpectrumFilesWizardPage = new MassSpectrumFilesWizardPage();
	private PreprocessingWizardPage preprocessingWizardPage = new PreprocessingWizardPage();
	private FilterWizardPage filterWizardPage = new FilterWizardPage();
	/*
	 * Will be created when finishing the report.
	 */
	private PcaExtractionMassSpectra pcaExtractionData;

	@Override
	public void addPages() {

		addPage(massSpectrumSettingsWizardPage);
		addPage(massSpectrumFilesWizardPage);
		addPage(preprocessingWizardPage);
		addPage(filterWizardPage);
	}

	@Override
	public IExtractionData getExtractionData() {

		return pcaExtractionData;
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return massSpectrumFilesWizardPage.getUniqueDataInputEnties();
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return massSpectrumSettingsWizardPage.getAnalysisSettings();
	}

	@Override
	public IPreprocessingSettings getPreprocessingSettings() {

		return preprocessingWizardPage.getPreprocessingSettings();
	}

	@Override
	public IFilterSettings getFilterSettings() {

		return filterWizardPage.getFilterSettings();
	}

	@Override
	public boolean performFinish() {

		List<IDataInputEntry> dataInputs = getDataInputEntries();
		pcaExtractionData = new PcaExtractionMassSpectra(dataInputs);
		//
		return true;
	}
}