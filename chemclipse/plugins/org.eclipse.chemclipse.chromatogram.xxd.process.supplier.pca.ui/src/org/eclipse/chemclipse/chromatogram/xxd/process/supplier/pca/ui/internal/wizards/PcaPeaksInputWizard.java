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
import org.eclipse.jface.wizard.Wizard;

public class PcaPeaksInputWizard extends Wizard implements IPcaInputWizard {

	private DataInputFromPeakFilesPageWizard dataInputFromPeakFilesPage;
	private FiltrationDataWizardPage filtrationDataPage;
	private MainPropertiesPeaksInputWizardPage mainPropertiesPage;
	private ModificationDataWizardPage modificationDataWizardPage;
	private PcaExtractionPeaks pcaExtractionData;

	public PcaPeaksInputWizard() {
		super();
		mainPropertiesPage = new MainPropertiesPeaksInputWizardPage("MainProperites");
		dataInputFromPeakFilesPage = new DataInputFromPeakFilesPageWizard("DataInputFiles");
		modificationDataWizardPage = new ModificationDataWizardPage("NormalizationData");
		filtrationDataPage = new FiltrationDataWizardPage("FiltrationData");
	}

	@Override
	public void addPages() {

		addPage(mainPropertiesPage);
		addPage(dataInputFromPeakFilesPage);
		addPage(modificationDataWizardPage);
		addPage(filtrationDataPage);
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputFromPeakFilesPage.getUniqueDataInputEnties();
	}

	@Override
	public int getNumerOfComponents() {

		return mainPropertiesPage.getNumerOfComponents();
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

		return modificationDataWizardPage.getPcaPreprocessingData();
	}

	@Override
	public boolean performFinish() {

		List<IDataInputEntry> dataInputs = getDataInputEntries();
		int retentionTimeWindow = mainPropertiesPage.getRetentionTimeWindow();
		pcaExtractionData = new PcaExtractionPeaks(dataInputs, retentionTimeWindow, IDataExtraction.EXTRACT_PEAK);
		return true;
	}
}
