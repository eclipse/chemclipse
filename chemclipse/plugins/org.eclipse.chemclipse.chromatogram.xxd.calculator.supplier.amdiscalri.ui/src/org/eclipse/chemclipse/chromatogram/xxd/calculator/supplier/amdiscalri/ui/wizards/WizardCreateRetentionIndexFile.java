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
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.CalibrationFileWriter;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.IRetentionIndexEntry;
import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
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
	private PagePeakSelectionMSD pagePeakSelectionMSD;
	private PagePeakSelectionCSD pagePeakSelectionCSD;
	private PagePeakAssignmentMSD pagePeakAssignmentMSD;
	private PagePeakAssignmentCSD pagePeakAssignmentCSD;
	private PageCalibrationTableMSD pageCalibrationTableMSD;
	private PageCalibrationTableCSD pageCalibrationTableCSD;

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
		pagePeakSelectionMSD = new PagePeakSelectionMSD(wizardElements);
		pagePeakSelectionCSD = new PagePeakSelectionCSD(wizardElements);
		pagePeakAssignmentMSD = new PagePeakAssignmentMSD(wizardElements);
		pagePeakAssignmentCSD = new PagePeakAssignmentCSD(wizardElements);
		pageCalibrationTableMSD = new PageCalibrationTableMSD(wizardElements);
		pageCalibrationTableCSD = new PageCalibrationTableCSD(wizardElements);
		//
		addPage(pageCalibrationSettings);
		addPage(pageChromatogramInputEntriesMSD);
		addPage(pageChromatogramInputEntriesCSD);
		addPage(pagePeakSelectionMSD);
		addPage(pagePeakSelectionCSD);
		addPage(pagePeakAssignmentMSD);
		addPage(pagePeakAssignmentCSD);
		addPage(pageCalibrationTableMSD);
		addPage(pageCalibrationTableCSD);
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
			} else {
				nextPage = pageChromatogramInputEntriesCSD;
			}
		} else if(page == pageChromatogramInputEntriesMSD) {
			nextPage = pagePeakSelectionMSD;
		} else if(page == pageChromatogramInputEntriesCSD) {
			nextPage = pagePeakSelectionCSD;
		} else if(page == pagePeakSelectionMSD) {
			nextPage = pagePeakAssignmentMSD;
		} else if(page == pagePeakSelectionCSD) {
			nextPage = pagePeakAssignmentCSD;
		} else if(page == pagePeakAssignmentMSD) {
			nextPage = pageCalibrationTableMSD;
		} else if(page == pagePeakAssignmentCSD) {
			nextPage = pageCalibrationTableCSD;
		} else if(page == pageCalibrationTableMSD || page == pageCalibrationTableCSD) {
			nextPage = null;
		}
		//
		setPreviousPages();
		return nextPage;
	}

	private void setPreviousPages() {

		if(wizardElements.isUseMassSpectrometryData()) {
			pageCalibrationTableMSD.setPreviousPage(pagePeakAssignmentMSD);
		} else {
			pageCalibrationTableCSD.setPreviousPage(pagePeakAssignmentCSD);
		}
		//
		pagePeakAssignmentMSD.setPreviousPage(pagePeakSelectionMSD);
		pagePeakAssignmentCSD.setPreviousPage(pagePeakSelectionCSD);
		//
		pagePeakSelectionMSD.setPreviousPage(pageChromatogramInputEntriesMSD);
		pagePeakSelectionCSD.setPreviousPage(pageChromatogramInputEntriesCSD);
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
			canFinish = pagePeakSelectionMSD.canFinish();
		}
		if(canFinish) {
			canFinish = pagePeakAssignmentMSD.canFinish();
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
			 * Export
			 */
			List<IRetentionIndexEntry> retentionIndexEntries = wizardElements.getExtractedRetentionIndexEntries();
			/*
			 * Calibration File.
			 */
			File calibrationFile = file.getLocation().toFile();
			if(!calibrationFile.getAbsolutePath().endsWith(CALIBRATION_FILE_EXTENSION)) {
				calibrationFile = new File(calibrationFile.getAbsolutePath() + CALIBRATION_FILE_EXTENSION);
			}
			CalibrationFileWriter calibrationFileWriter = new CalibrationFileWriter();
			calibrationFileWriter.write(calibrationFile, retentionIndexEntries);
			/*
			 * Chromatogram File
			 * Export the chromatogram.
			 */
			String path = calibrationFile.getAbsolutePath();
			File chromatogramFile = new File(path.substring(0, path.length() - CALIBRATION_FILE_EXTENSION.length()) + CHROMATOGRAM_FILE_EXTENSION);
			if(wizardElements.isUseMassSpectrometryData()) {
				IChromatogramMSD chromatogramMSD = wizardElements.getChromatogramSelectionMSD().getChromatogramMSD();
				ChromatogramConverterMSD.convert(chromatogramFile, chromatogramMSD, CHROMATOGRAM_CONVERTER_ID, monitor);
			} else {
				IChromatogramCSD chromatogramCSD = wizardElements.getChromatogramSelectionCSD().getChromatogramCSD();
				ChromatogramConverterCSD.convert(chromatogramFile, chromatogramCSD, CHROMATOGRAM_CONVERTER_ID, monitor);
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
