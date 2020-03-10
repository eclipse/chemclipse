/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.FilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionScans;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ScansExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class ScansInputWizard extends Wizard implements IInputWizard {

	private IAnalysisSettings analysisSettings = new AnalysisSettings();
	//
	private ScanSettingsWizardPage scanSettingsWizardPage = new ScanSettingsWizardPage();
	private ScanFilesWizardPage scanFilesWizardPage = new ScanFilesWizardPage();
	private GroupNamesWizardPage groupNamesWizardPage = new GroupNamesWizardPage();
	private PreprocessingWizardPage preprocessingWizardPage = new PreprocessingWizardPage();
	private FilterWizardPage filterWizardPage = new FilterWizardPage();
	//
	private PcaExtractionScans pcaExtractionData;

	@Override
	public void addPages() {

		addPage(scanSettingsWizardPage);
		addPage(scanFilesWizardPage);
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

		return scanFilesWizardPage.getUniqueDataInputEnties();
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return analysisSettings;
	}

	@Override
	public PreprocessingSettings getPreprocessingSettings() {

		return preprocessingWizardPage.getPreprocessingSettings();
	}

	@Override
	public FilterSettings getFilterSettings() {

		return filterWizardPage.getFilterSettings();
	}

	@Override
	public boolean performFinish() {

		List<IDataInputEntry> dataInputs = getDataInputEntries();
		int retentionTimeWindow = scanSettingsWizardPage.getRetentionTimeWindow();
		boolean useDefaultProperties = scanSettingsWizardPage.isUseDefaultProperties();
		ExtractionType scanAlignment = scanSettingsWizardPage.getExtractionType();
		int maximalNumberScans = scanSettingsWizardPage.getMaximalNumberScans();
		pcaExtractionData = new PcaExtractionScans(retentionTimeWindow, maximalNumberScans, dataInputs, scanAlignment, useDefaultProperties);
		return true;
	}
}
