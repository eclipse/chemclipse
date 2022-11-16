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
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IPreprocessingSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.PcaExtractionScans;
import org.eclipse.chemclipse.xxd.process.supplier.pca.extraction.ScanExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class ScansInputWizard extends Wizard implements IInputWizard {

	private ScanSettingsWizardPage scanSettingsWizardPage = new ScanSettingsWizardPage();
	private ScanFilesWizardPage scanFilesWizardPage = new ScanFilesWizardPage();
	private GroupNamesWizardPage groupNamesWizardPage = new GroupNamesWizardPage();
	private PreprocessingWizardPage preprocessingWizardPage = new PreprocessingWizardPage();
	private FilterWizardPage filterWizardPage = new FilterWizardPage();
	/*
	 * Will be created when finishing the report.
	 */
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

		return scanSettingsWizardPage.getAnalysisSettings();
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
		int retentionTimeWindow = scanSettingsWizardPage.getRetentionTimeWindow();
		boolean useDefaultProperties = scanSettingsWizardPage.isUseDefaultProperties();
		ExtractionType scanAlignment = scanSettingsWizardPage.getExtractionType();
		int maximalNumberScans = scanSettingsWizardPage.getMaximalNumberScans();
		pcaExtractionData = new PcaExtractionScans(retentionTimeWindow, maximalNumberScans, dataInputs, scanAlignment, useDefaultProperties);
		return true;
	}
}
