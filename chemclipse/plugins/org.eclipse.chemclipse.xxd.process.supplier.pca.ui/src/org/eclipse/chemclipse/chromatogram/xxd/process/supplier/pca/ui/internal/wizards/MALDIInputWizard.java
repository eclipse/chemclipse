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
 * Matthias Mailänder - adapted for MALDI
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IPreprocessingSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionMALDI;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction.MALDIExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class MALDIInputWizard extends Wizard implements IInputWizard {

	private MALDISettingsWizardPage maldiSettingsWizardPage = new MALDISettingsWizardPage();
	private MALDIFilesWizardPage maldiFilesWizardPage = new MALDIFilesWizardPage();
	private GroupNamesWizardPage groupNamesWizardPage = new GroupNamesWizardPage();
	private PreprocessingWizardPage preprocessingWizardPage = new PreprocessingWizardPage();
	private FilterWizardPage filterWizardPage = new FilterWizardPage();
	/*
	 * Will be created when finishing the report.
	 */
	private PcaExtractionMALDI pcaExtractionData;

	@Override
	public void addPages() {

		addPage(maldiSettingsWizardPage);
		addPage(maldiFilesWizardPage);
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

		return maldiFilesWizardPage.getUniqueDataInputEnties();
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return maldiSettingsWizardPage.getAnalysisSettings();
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
		int massWindow = maldiSettingsWizardPage.getMassWindow();
		boolean useDefaultProperties = maldiSettingsWizardPage.isUseDefaultProperties();
		ExtractionType extractionType = maldiSettingsWizardPage.getExtractionType();
		int maximalNumberScans = maldiSettingsWizardPage.getMaximalNumberPeaks();
		pcaExtractionData = new PcaExtractionMALDI(massWindow, maximalNumberScans, dataInputs, extractionType, useDefaultProperties);
		return true;
	}
}