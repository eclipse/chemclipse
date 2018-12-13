/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.ui.wizards;

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileWriter;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputEntriesWizardPage;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.InputWizardSettings.DataType;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;

public class WizardCreateRetentionIndexFile extends AbstractFileWizard {

	/**
	 * Preferred size of the wizard.
	 */
	public static final int PREFERRED_WIDTH = 300;
	public static final int PREFERRED_HEIGHT = 600;
	//
	private static final Logger logger = Logger.getLogger(WizardCreateRetentionIndexFile.class);
	//
	private IRetentionIndexWizardElements wizardElements = new RetentionIndexWizardElements();
	//
	private static final String CALIBRATION_FILE_EXTENSION = ".cal";
	private static final String CHROMATOGRAM_FILE_EXTENSION = ".ocb";
	private static final String CHROMATOGRAM_CONVERTER_ID = "org.eclipse.chemclipse.xxd.converter.supplier.chemclipse";
	//
	private PageCalibrationSettings pageCalibrationSettings;
	private InputEntriesWizardPage pageInputEntriesMSD;
	private InputEntriesWizardPage pageInputEntriesCSD;
	private PagePeakSelection pagePeakSelection;
	private PagePeakAssignment pagePeakAssignment;
	private PageCalibrationTable pageCalibrationTable;

	public WizardCreateRetentionIndexFile() {
		super("RetentionIndices_" + new Date().getTime(), CALIBRATION_FILE_EXTENSION);
	}

	@Override
	public void addPages() {

		super.addPages();
		/*
		 * Pages must implement IExtendedWizardPage / extend AbstractExtendedWizardPage
		 */
		InputWizardSettings inputWizardSettingsMSD = new InputWizardSettings(DataType.MSD_CHROMATOGRAM);
		inputWizardSettingsMSD.setTitle("Open Chromatogram (MSD) File(s)");
		inputWizardSettingsMSD.setDescription("Select a chromatogram/chromatograms file to open.");
		inputWizardSettingsMSD.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.P_FILTER_PATH_MODELS_MSD);
		//
		InputWizardSettings inputWizardSettingsCSD = new InputWizardSettings(DataType.CSD_CHROMATOGRAM);
		inputWizardSettingsCSD.setTitle("Open Chromatogram (CSD) File(s)");
		inputWizardSettingsCSD.setDescription("Select a chromatogram/chromatograms file to open.");
		inputWizardSettingsCSD.setPathPreferences(PreferenceSupplier.INSTANCE().getPreferences(), PreferenceSupplier.P_FILTER_PATH_MODELS_CSD);
		//
		pageCalibrationSettings = new PageCalibrationSettings(wizardElements);
		pageInputEntriesMSD = new InputEntriesWizardPage(inputWizardSettingsMSD);
		pageInputEntriesCSD = new InputEntriesWizardPage(inputWizardSettingsCSD);
		pagePeakSelection = new PagePeakSelection(wizardElements);
		pagePeakAssignment = new PagePeakAssignment(wizardElements);
		pageCalibrationTable = new PageCalibrationTable(wizardElements);
		//
		addPage(pageCalibrationSettings);
		addPage(pageInputEntriesMSD);
		addPage(pageInputEntriesCSD);
		addPage(pagePeakSelection);
		addPage(pagePeakAssignment);
		addPage(pageCalibrationTable);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {

		IWizardPage nextPage = super.getNextPage(page);
		//
		if(page == pageCalibrationSettings) {
			/*
			 * Show MSD or file selection.
			 */
			if(wizardElements.isUseMassSpectrometryData()) {
				nextPage = pageInputEntriesMSD;
			} else {
				nextPage = pageInputEntriesCSD;
			}
		} else if(page == pageInputEntriesMSD) {
			nextPage = pagePeakSelection;
			wizardElements.clearSelectedChromatograms();
			wizardElements.addElements(pageInputEntriesMSD.getChromatogramWizardElements());
			pageInputEntriesMSD.saveSelectedPath();
		} else if(page == pageInputEntriesCSD) {
			nextPage = pagePeakSelection;
			wizardElements.clearSelectedChromatograms();
			wizardElements.addElements(pageInputEntriesCSD.getChromatogramWizardElements());
			pageInputEntriesCSD.saveSelectedPath();
		} else if(page == pagePeakSelection) {
			nextPage = pagePeakAssignment;
		} else if(page == pagePeakAssignment) {
			nextPage = pageCalibrationTable;
		} else if(page == pagePeakAssignment) {
			nextPage = pageCalibrationTable;
		} else if(page == pageCalibrationTable) {
			nextPage = null;
		}
		//
		setPreviousPages();
		return nextPage;
	}

	private void setPreviousPages() {

		if(wizardElements.isUseMassSpectrometryData()) {
			pagePeakSelection.setPreviousPage(pageInputEntriesMSD);
		} else {
			pagePeakSelection.setPreviousPage(pageInputEntriesCSD);
		}
		//
		pageCalibrationTable.setPreviousPage(pagePeakAssignment);
		pagePeakAssignment.setPreviousPage(pagePeakSelection);
		//
		pageInputEntriesMSD.setPreviousPage(pageCalibrationSettings);
		pageInputEntriesCSD.setPreviousPage(pageCalibrationSettings);
	}

	@Override
	public boolean canFinish() {

		boolean canFinish = pageCalibrationSettings.canFinish();
		if(canFinish) {
			if(wizardElements.isUseMassSpectrometryData()) {
				canFinish = (wizardElements.getSelectedChromatograms().size() > 0) ? true : false;
			} else {
				canFinish = (wizardElements.getSelectedChromatograms().size() > 0) ? true : false;
			}
		}
		if(canFinish) {
			canFinish = pagePeakSelection.canFinish();
		}
		if(canFinish) {
			canFinish = pagePeakAssignment.canFinish();
		}
		if(canFinish) {
			canFinish = wizardElements.isRetentionIndexDataValidated();
		}
		return canFinish;
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Create Chromatogram Evaluation", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		try {
			/*
			 * Calibration File.
			 */
			File calibrationFile = file.getLocation().toFile();
			if(!calibrationFile.getAbsolutePath().endsWith(CALIBRATION_FILE_EXTENSION)) {
				calibrationFile = new File(calibrationFile.getAbsolutePath() + CALIBRATION_FILE_EXTENSION);
			}
			CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
			calibrationFileWriter.write(calibrationFile, wizardElements.getSeparationColumnIndices());
			/*
			 * Chromatogram File
			 * Export the chromatogram.
			 */
			String path = calibrationFile.getAbsolutePath();
			File chromatogramFile = new File(path.substring(0, path.length() - CALIBRATION_FILE_EXTENSION.length()) + CHROMATOGRAM_FILE_EXTENSION);
			@SuppressWarnings("rawtypes")
			IChromatogramSelection chromatogramSelection = wizardElements.getChromatogramSelection();
			if(wizardElements.isUseMassSpectrometryData()) {
				if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					IChromatogramMSD chromatogramMSD = ((IChromatogramSelectionMSD)chromatogramSelection).getChromatogramMSD();
					ChromatogramConverterMSD.getInstance().convert(chromatogramFile, chromatogramMSD, CHROMATOGRAM_CONVERTER_ID, monitor);
				}
			} else {
				if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
					IChromatogramCSD chromatogramCSD = ((IChromatogramSelectionCSD)chromatogramSelection).getChromatogramCSD();
					ChromatogramConverterCSD.getInstance().convert(chromatogramFile, chromatogramCSD, CHROMATOGRAM_CONVERTER_ID, monitor);
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}
