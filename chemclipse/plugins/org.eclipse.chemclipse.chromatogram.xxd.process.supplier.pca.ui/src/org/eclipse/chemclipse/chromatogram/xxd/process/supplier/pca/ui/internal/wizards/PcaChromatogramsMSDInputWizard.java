/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionScans;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ScansExtractionSupport.ExtractionType;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.jface.wizard.Wizard;

public class PcaChromatogramsMSDInputWizard extends Wizard implements IPcaInputWizard {

	private ChromatogramMSDFilesPageWizard dataInputFromScanFilesPage;
	private FiltrationDataWizardPage filtrationDataPage;
	private MainPropertiesScansInputWizardPage mainPropertiesPage;
	private MassSetGroupNamesWizardPage massSetGroupNamesWizardPage;
	private PreprocessingDataWizardPage preprocessingDataWizardPage;
	private PcaExtractionScans pcaExtractionData;

	public PcaChromatogramsMSDInputWizard() {

		super();
		mainPropertiesPage = new MainPropertiesScansInputWizardPage("MainProperites");
		dataInputFromScanFilesPage = new ChromatogramMSDFilesPageWizard("DataInputFiles");
		massSetGroupNamesWizardPage = new MassSetGroupNamesWizardPage("MassSetGroup");
		preprocessingDataWizardPage = new PreprocessingDataWizardPage("NormalizationData");
		filtrationDataPage = new FiltrationDataWizardPage("FiltrationData");
	}

	@Override
	public void addPages() {

		addPage(mainPropertiesPage);
		addPage(dataInputFromScanFilesPage);
		addPage(massSetGroupNamesWizardPage);
		addPage(preprocessingDataWizardPage);
		addPage(filtrationDataPage);
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputFromScanFilesPage.getUniqueDataInputEnties();
	}

	@Override
	public IDataExtraction getPcaExtractionData() {

		return pcaExtractionData;
	}

	@Override
	public PcaFiltrationData getPcaFiltrationData() {

		return filtrationDataPage.getPcaFiltrationData();
	}

	@Override
	public PcaPreprocessingData getPcaPreprocessingData() {

		return preprocessingDataWizardPage.getPcaPreprocessingData();
	}

	@Override
	public boolean performFinish() {

		List<IDataInputEntry> dataInputs = getDataInputEntries();
		int retentionTimeWindow = mainPropertiesPage.getRetentionTimeWindow();
		boolean useDefoultProperties = mainPropertiesPage.isUseDefoultProperties();
		ExtractionType scanAlignment = mainPropertiesPage.getExtractionType();
		int maximalNumberScans = mainPropertiesPage.getMaximalNumberScans();
		pcaExtractionData = new PcaExtractionScans(retentionTimeWindow, maximalNumberScans, dataInputs, scanAlignment, useDefoultProperties);
		return true;
	}

	@Override
	public IPcaSettingsVisualization getPcaSettingsVisualization() {

		return preprocessingDataWizardPage.getPcaSettings();
	}
}
