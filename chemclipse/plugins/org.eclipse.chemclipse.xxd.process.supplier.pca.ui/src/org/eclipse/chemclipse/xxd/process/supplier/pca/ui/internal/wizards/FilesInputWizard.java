/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IExtractionData;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IFilterSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.IPreprocessingSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.PcaExtractionFileBinary;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.PcaExtractionFileText;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.jface.wizard.Wizard;

public class FilesInputWizard extends Wizard implements IInputWizard {

	private FileSettingsWizardPage fileSettingsWizardPage = new FileSettingsWizardPage();
	private PreprocessingWizardPage preprocessingWizardPage = new PreprocessingWizardPage();
	private FilterWizardPage filterWizardPage = new FilterWizardPage();
	/*
	 * Will be created when finishing the report.
	 */
	private IExtractionData extractionData;
	private File file;

	public FilesInputWizard() {

		this(null);
	}

	public FilesInputWizard(File file) {

		this.file = file;
	}

	@Override
	public void addPages() {

		fileSettingsWizardPage.setFile(file);
		//
		addPage(fileSettingsWizardPage);
		addPage(preprocessingWizardPage);
		addPage(filterWizardPage);
	}

	@Override
	public IExtractionData getExtractionData() {

		return extractionData;
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return fileSettingsWizardPage.getDataInputEntries();
	}

	@Override
	public IAnalysisSettings getAnalysisSettings() {

		return fileSettingsWizardPage.getAnalysisSettings();
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

		List<IDataInputEntry> dataInputEntries = getDataInputEntries();
		File file = getFile(dataInputEntries);
		if(file != null && file.getName().endsWith(PcaExtractionFileBinary.FILE_EXTENSION)) {
			extractionData = new PcaExtractionFileBinary(file);
		} else {
			extractionData = new PcaExtractionFileText(dataInputEntries);
		}
		return true;
	}

	private File getFile(List<IDataInputEntry> dataInputEntries) {

		for(IDataInputEntry dataInputEntry : dataInputEntries) {
			String inputFile = dataInputEntry.getInputFile();
			File file = new File(inputFile);
			if(file.exists()) {
				return file;
			}
		}
		//
		return null;
	}
}