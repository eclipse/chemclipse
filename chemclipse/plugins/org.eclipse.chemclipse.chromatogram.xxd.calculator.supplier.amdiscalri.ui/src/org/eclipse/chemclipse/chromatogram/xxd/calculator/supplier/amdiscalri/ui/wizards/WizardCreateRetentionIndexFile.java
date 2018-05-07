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
	private org.eclipse.chemclipse.ux.extension.msd.ui.wizards.ChromatogramInputEntriesWizardPage pageChromatogramInputEntriesMSD;
	private org.eclipse.chemclipse.ux.extension.csd.ui.wizards.ChromatogramInputEntriesWizardPage pageChromatogramInputEntriesCSD;
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
		pageCalibrationSettings = new PageCalibrationSettings(wizardElements);
		pageChromatogramInputEntriesMSD = new org.eclipse.chemclipse.ux.extension.msd.ui.wizards.ChromatogramInputEntriesWizardPage(wizardElements.getChromatogramWizardElementsMSD());
		pageChromatogramInputEntriesCSD = new org.eclipse.chemclipse.ux.extension.csd.ui.wizards.ChromatogramInputEntriesWizardPage(wizardElements.getChromatogramWizardElementsCSD());
		pagePeakSelection = new PagePeakSelection(wizardElements);
		pagePeakAssignment = new PagePeakAssignment(wizardElements);
		pageCalibrationTable = new PageCalibrationTable(wizardElements);
		//
		addPage(pageCalibrationSettings);
		addPage(pageChromatogramInputEntriesMSD);
		addPage(pageChromatogramInputEntriesCSD);
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
				nextPage = pageChromatogramInputEntriesMSD;
				pageChromatogramInputEntriesMSD.expandTree(PreferenceSupplier.getFilterPathModelsMSD());
			} else {
				nextPage = pageChromatogramInputEntriesCSD;
				pageChromatogramInputEntriesCSD.expandTree(PreferenceSupplier.getFilterPathModelsCSD());
			}
		} else if(page == pageChromatogramInputEntriesMSD) {
			nextPage = pagePeakSelection;
		} else if(page == pageChromatogramInputEntriesCSD) {
			nextPage = pagePeakSelection;
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
			pagePeakSelection.setPreviousPage(pageChromatogramInputEntriesMSD);
		} else {
			pagePeakSelection.setPreviousPage(pageChromatogramInputEntriesCSD);
		}
		//
		pageCalibrationTable.setPreviousPage(pagePeakAssignment);
		pagePeakAssignment.setPreviousPage(pagePeakSelection);
		//
		pageChromatogramInputEntriesMSD.setPreviousPage(pageCalibrationSettings);
		pageChromatogramInputEntriesCSD.setPreviousPage(pageCalibrationSettings);
	}

	@Override
	public boolean canFinish() {

		boolean canFinish = pageCalibrationSettings.canFinish();
		if(canFinish) {
			if(wizardElements.isUseMassSpectrometryData()) {
				canFinish = (wizardElements.getChromatogramWizardElementsMSD().getSelectedChromatograms().size() > 0) ? true : false;
			} else {
				canFinish = (wizardElements.getChromatogramWizardElementsCSD().getSelectedChromatograms().size() > 0) ? true : false;
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
			IChromatogramSelection chromatogramSelection = wizardElements.getChromatogramSelection();
			if(wizardElements.isUseMassSpectrometryData()) {
				if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
					IChromatogramMSD chromatogramMSD = ((IChromatogramSelectionMSD)chromatogramSelection).getChromatogramMSD();
					ChromatogramConverterMSD.convert(chromatogramFile, chromatogramMSD, CHROMATOGRAM_CONVERTER_ID, monitor);
				}
			} else {
				if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
					IChromatogramCSD chromatogramCSD = ((IChromatogramSelectionCSD)chromatogramSelection).getChromatogramCSD();
					ChromatogramConverterCSD.convert(chromatogramFile, chromatogramCSD, CHROMATOGRAM_CONVERTER_ID, monitor);
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
