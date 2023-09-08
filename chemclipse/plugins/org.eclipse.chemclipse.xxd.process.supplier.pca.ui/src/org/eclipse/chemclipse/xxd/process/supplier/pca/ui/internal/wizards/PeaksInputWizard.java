/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - group by retention index
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.ExtractionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IPreprocessingSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.PcaExtractionPeaks;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.ValueOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.extraction.ExtractionSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.DescriptionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class PeaksInputWizard extends Wizard implements IInputWizard {

	private PeakSettingsWizardPage peakSettingsWizardPage = new PeakSettingsWizardPage();
	private PeakFilesWizardPage peakFilesWizardPage = new PeakFilesWizardPage();
	private GroupNamesWizardPage groupNamesWizardPage = new GroupNamesWizardPage();
	private PreprocessingWizardPage preprocessingWizardPage = new PreprocessingWizardPage();
	private FilterWizardPage filterWizardPage = new FilterWizardPage();
	/*
	 * Will be created when finishing the report.
	 */
	private PcaExtractionPeaks pcaExtractionData;

	@Override
	public void addPages() {

		addPage(peakSettingsWizardPage);
		addPage(peakFilesWizardPage);
		addPage(groupNamesWizardPage);
		addPage(preprocessingWizardPage);
		addPage(filterWizardPage);
	}

	@Override
	public IExtractionData getExtractionData() {

		return pcaExtractionData;
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return peakFilesWizardPage.getUniqueDataInputEnties();
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return peakSettingsWizardPage.getAnalysisSettings();
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
		DescriptionOption descriptionOption = peakSettingsWizardPage.getDescriptionOption();
		ExtractionOption extractionOption = peakSettingsWizardPage.getExtractionOption();
		ValueOption valueOption = peakSettingsWizardPage.getValueOption();
		int groupValueWindow = peakSettingsWizardPage.getGroupValueWindow();
		ExtractionSettings extractionSettings = new ExtractionSettings(descriptionOption, extractionOption, valueOption, groupValueWindow);
		pcaExtractionData = new PcaExtractionPeaks(dataInputs, extractionSettings);
		//
		return true;
	}
}