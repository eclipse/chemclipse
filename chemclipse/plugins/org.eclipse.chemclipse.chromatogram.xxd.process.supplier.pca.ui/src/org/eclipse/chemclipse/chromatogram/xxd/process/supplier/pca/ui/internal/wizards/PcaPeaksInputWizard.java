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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionPeaks;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.jface.wizard.Wizard;

public class PcaPeaksInputWizard extends Wizard implements IPcaInputWizard {

	private PeakFilesInputPageWizard dataInputFromPeakFilesPage;
	private FiltrationDataWizardPage filtrationDataPage;
	private MainPropertiesPeaksInputWizardPage mainPropertiesPage;
	private MassSetGroupNamesWizardPage massSetGroupNamesWizardPage;
	private PreprocessingDataWizardPage preprocessingData;
	private PcaExtractionPeaks pcaExtractionData;

	public PcaPeaksInputWizard() {

		super();
		mainPropertiesPage = new MainPropertiesPeaksInputWizardPage("MainProperites");
		dataInputFromPeakFilesPage = new PeakFilesInputPageWizard("DataInputFiles");
		massSetGroupNamesWizardPage = new MassSetGroupNamesWizardPage("MassSetGroup");
		preprocessingData = new PreprocessingDataWizardPage("NormalizationData");
		filtrationDataPage = new FiltrationDataWizardPage("FiltrationData");
	}

	@Override
	public void addPages() {

		addPage(mainPropertiesPage);
		addPage(dataInputFromPeakFilesPage);
		addPage(massSetGroupNamesWizardPage);
		addPage(preprocessingData);
		addPage(filtrationDataPage);
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputFromPeakFilesPage.getUniqueDataInputEnties();
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

		return preprocessingData.getPcaPreprocessingData();
	}

	@Override
	public boolean performFinish() {

		List<IDataInputEntry> dataInputs = getDataInputEntries();
		int retentionTimeWindow = mainPropertiesPage.getRetentionTimeWindow();
		pcaExtractionData = new PcaExtractionPeaks(dataInputs, retentionTimeWindow);
		return true;
	}

	@Override
	public IPcaSettingsVisualization getPcaSettingsVisualization() {

		return preprocessingData.getPcaSettings();
	}
}
