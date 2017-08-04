/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class PcaPeaksInputWizard extends Wizard {

	private DataInputFromPeakFilesPageWizard dataInputFromPeakFilesPage;
	private FiltrationDataWizardPage filtrationDataPage;
	private MainPropertiesWizardPage mainPropertiesPage;
	private NormalizationDataWizardPage normalizationDataPage;
	private PcaExtractionData pcaExtractionData;

	public PcaPeaksInputWizard() {
		super();
		mainPropertiesPage = new MainPropertiesWizardPage("MainProperites");
		dataInputFromPeakFilesPage = new DataInputFromPeakFilesPageWizard("DataInputFiles");
		normalizationDataPage = new NormalizationDataWizardPage("NormalizationData");
		filtrationDataPage = new FiltrationDataWizardPage("FiltrationData");
	}

	@Override
	public void addPages() {

		addPage(mainPropertiesPage);
		addPage(dataInputFromPeakFilesPage);
		addPage(normalizationDataPage);
		addPage(filtrationDataPage);
	}

	public int getNumerOfComponents() {

		return mainPropertiesPage.getNumerOfComponents();
	}

	public PcaExtractionData getPcaExtractionData() {

		return pcaExtractionData;
	}

	public PcaFiltrationData getPcaFiltrationData() {

		return filtrationDataPage.getPcaFiltrationData();
	}

	public PcaNormalizationData getPcaNormalizationData() {

		return normalizationDataPage.getPcaNormalizationData();
	}

	@Override
	public boolean performFinish() {

		List<IDataInputEntry> dataInputs = dataInputFromPeakFilesPage.getDataInputEntries();
		int retentionTimeWindow = mainPropertiesPage.getRetentionTimeWindow();
		pcaExtractionData = new PcaExtractionData(dataInputs, retentionTimeWindow, PcaExtractionData.EXTRACT_PEAK);
		return true;
	}
}
