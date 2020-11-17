/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - prefix images with bundle name
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import org.eclipse.chemclipse.rcp.ui.icons.Activator;

public interface IApplicationImage extends IApplicationImageProvider {

	/**
	 * platform:/plugin/org.eclipse.chemclipse.rcp.ui.icons/icons/16x16/comparisonScanDefault.gif
	 * 
	 * @param fileName
	 * @param size
	 * @return String
	 */
	public static String getLocation(String fileName, String size) {

		String bundleName = Activator.getDefault().getBundle().getSymbolicName();
		StringBuilder builder = new StringBuilder();
		String[] values = fileName.split("/");
		String imageName = values.length == 2 ? values[1] : "info.gif";
		//
		builder.append("platform:/plugin/");
		builder.append(bundleName);
		builder.append("/icons/");
		builder.append(size);
		builder.append("/");
		builder.append(imageName);
		//
		return builder.toString();
	}

	String PICTOGRAM_SUBTRACT_SCAN_ONE = "org.eclipse.chemclipse.rcp.ui.icons/subtractScanOne.png";
	String PICTOGRAM_SUBTRACT_SCAN_MANY = "org.eclipse.chemclipse.rcp.ui.icons/subtractScanMany.png";
	/*
	 * 7x7 (Decorator)
	 * 8x8 (Decorator)
	 */
	String IMAGE_DECORATOR_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/decorator_active.gif";
	/*
	 * Icons and Images
	 */
	String IMAGE_ADD = "org.eclipse.chemclipse.rcp.ui.icons/add.gif";
	String IMAGE_COPY = "org.eclipse.chemclipse.rcp.ui.icons/copy.png";
	String IMAGE_CANCEL = "org.eclipse.chemclipse.rcp.ui.icons/cancel.gif";
	String IMAGE_DELETE = "org.eclipse.chemclipse.rcp.ui.icons/delete.gif";
	String IMAGE_DELETE_ALL = "org.eclipse.chemclipse.rcp.ui.icons/delete_all.png";;
	String IMAGE_EXECUTE_ADD = "org.eclipse.chemclipse.rcp.ui.icons/execute_add.gif";
	String IMAGE_EDIT = "org.eclipse.chemclipse.rcp.ui.icons/edit.gif";
	String IMAGE_EDIT_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/editDefault.gif";
	String IMAGE_EDIT_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/editActive.gif";
	String IMAGE_EDIT_DISABLED = "org.eclipse.chemclipse.rcp.ui.icons/edit_disabled.gif";
	String IMAGE_EDIT_SHIFT = "org.eclipse.chemclipse.rcp.ui.icons/editShift.gif";
	String IMAGE_EDIT_PROFILE = "org.eclipse.chemclipse.rcp.ui.icons/editProfile.gif";
	String IMAGE_CHROMATOGRAM_PROFILE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramProfile.gif";
	//
	String IMAGE_EMPTY = "org.eclipse.chemclipse.rcp.ui.icons/empty.png";
	String IMAGE_LOGO = "org.eclipse.chemclipse.rcp.ui.icons/logo.png";
	String IMAGE_ABOUT = "org.eclipse.chemclipse.rcp.ui.icons/about.png";
	//
	String IMAGE_DESELECTED = "org.eclipse.chemclipse.rcp.ui.icons/deselected.gif";
	String IMAGE_DESELECTED_INACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/deselected_inactive.gif";
	String IMAGE_SELECTED = "org.eclipse.chemclipse.rcp.ui.icons/selected.gif";
	String IMAGE_CHECK_ALL = "org.eclipse.chemclipse.rcp.ui.icons/checkAll.gif";
	String IMAGE_UNCHECK_ALL = "org.eclipse.chemclipse.rcp.ui.icons/uncheckAll.gif";
	//
	String IMAGE_SHRINK_CHROMATOGRAMS = "org.eclipse.chemclipse.rcp.ui.icons/shrinkChromatograms.gif";
	String IMAGE_ALIGN_CHROMATOGRAMS = "org.eclipse.chemclipse.rcp.ui.icons/alignChromatograms.gif";
	String IMAGE_STRETCH_CHROMATOGRAMS = "org.eclipse.chemclipse.rcp.ui.icons/stretchChromatograms.gif";
	String IMAGE_ADJUST_CHROMATOGRAMS = "org.eclipse.chemclipse.rcp.ui.icons/adjustChromatograms.gif";
	//
	String IMAGE_CHROMATOGRAM = "org.eclipse.chemclipse.rcp.ui.icons/chromatogram.gif";
	String IMAGE_CHROMATOGRAM_MSD = "org.eclipse.chemclipse.rcp.ui.icons/chromatogram-msd.gif";
	String IMAGE_CHROMATOGRAM_CSD = "org.eclipse.chemclipse.rcp.ui.icons/chromatogram-csd.gif";
	String IMAGE_CHROMATOGRAM_WSD = "org.eclipse.chemclipse.rcp.ui.icons/chromatogram-wsd.gif";
	String IMAGE_SCAN_XIR = "org.eclipse.chemclipse.rcp.ui.icons/scan-xir.gif"; // FTIR, NIR, ...
	String IMAGE_SCAN_NMR = "org.eclipse.chemclipse.rcp.ui.icons/scan-nmr.gif";
	String IMAGE_SCAN_FID = "org.eclipse.chemclipse.rcp.ui.icons/scan-fid.png";
	//
	String IMAGE_SAMPLE = "org.eclipse.chemclipse.rcp.ui.icons/sample.gif";
	String IMAGE_SAMPLE_QUALIFIER = "org.eclipse.chemclipse.rcp.ui.icons/sample-qualifier.gif";
	String IMAGE_SAMPLE_ISTD = "org.eclipse.chemclipse.rcp.ui.icons/sample-istd.gif";
	String IMAGE_SAMPLE_ISTD_QUALIFIER = "org.eclipse.chemclipse.rcp.ui.icons/sample-istd-qualifier.gif";
	String IMAGE_SAMPLE_QC = "org.eclipse.chemclipse.rcp.ui.icons/sample-qc.gif";
	String IMAGE_SAMPLE_QC_QUALIFIER = "org.eclipse.chemclipse.rcp.ui.icons/sample-qc-qualifier.gif";
	//
	String IMAGE_PREFERENCES = "org.eclipse.chemclipse.rcp.ui.icons/preferences.gif";
	String IMAGE_SETTINGS_PULL = "org.eclipse.chemclipse.rcp.ui.icons/settings_pull.png";
	String IMAGE_SETTINGS_PUSH = "org.eclipse.chemclipse.rcp.ui.icons/settings_push.png";
	String IMAGE_DRIVE = "org.eclipse.chemclipse.rcp.ui.icons/drive.gif";
	String IMAGE_CHROMATOGRAM_ZERO_SET = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramZeroSet.gif";
	String IMAGE_FOLDER_OPENED = "org.eclipse.chemclipse.rcp.ui.icons/folder_opened.gif";
	String IMAGE_FOLDER_CLOSED = "org.eclipse.chemclipse.rcp.ui.icons/folder_closed.gif";
	String IMAGE_FILE = "org.eclipse.chemclipse.rcp.ui.icons/file.gif";
	String IMAGE_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/peak.gif";
	String IMAGE_PEAK_ADD = "org.eclipse.chemclipse.rcp.ui.icons/peakAdd.gif";
	String IMAGE_PEAK_REPLACE = "org.eclipse.chemclipse.rcp.ui.icons/peakReplace.gif";
	String IMAGE_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/peaks.gif";
	String IMAGE_WARN = "org.eclipse.chemclipse.rcp.ui.icons/warn.gif";
	String IMAGE_INFO = "org.eclipse.chemclipse.rcp.ui.icons/info.gif";
	String IMAGE_QUESTION = "org.eclipse.chemclipse.rcp.ui.icons/question.gif";
	String IMAGE_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/massSpectrum.gif";
	String IMAGE_MASS_SPECTRUM_FILE = "org.eclipse.chemclipse.rcp.ui.icons/massSpectrumFile.gif";
	String IMAGE_MASS_SPECTRUM_DATABASE = "org.eclipse.chemclipse.rcp.ui.icons/massSpectrumDatabase.gif";
	String IMAGE_TARGETS = "org.eclipse.chemclipse.rcp.ui.icons/targets.gif";
	String IMAGE_CREATE_SNAPSHOT = "org.eclipse.chemclipse.rcp.ui.icons/create_snapshot.png";
	String IMAGE_PERSPECTIVES = "org.eclipse.chemclipse.rcp.ui.icons/perspectives.gif";
	String IMAGE_QUIT = "org.eclipse.chemclipse.rcp.ui.icons/quit.gif";
	String IMAGE_SAVE = "org.eclipse.chemclipse.rcp.ui.icons/save.gif";
	String IMAGE_SAVE_DISABLED = "org.eclipse.chemclipse.rcp.ui.icons/save_disabled.gif";
	String IMAGE_SAVE_AS = "org.eclipse.chemclipse.rcp.ui.icons/saveas.gif";
	String IMAGE_SAVE_AS_DISABLED = "org.eclipse.chemclipse.rcp.ui.icons/saveas_disabled.gif";
	String IMAGE_SAVEALL = "org.eclipse.chemclipse.rcp.ui.icons/saveall.gif";
	String IMAGE_SAVEALL_DISABLED = "org.eclipse.chemclipse.rcp.ui.icons/saveall_disabled.gif";
	String IMAGE_VIEW = "org.eclipse.chemclipse.rcp.ui.icons/view.gif";
	String IMAGE_ERROR = "org.eclipse.chemclipse.rcp.ui.icons/error.gif";
	String IMAGE_UNKNOWN = "org.eclipse.chemclipse.rcp.ui.icons/unknown.gif";
	String IMAGE_LOG = "org.eclipse.chemclipse.rcp.ui.icons/log.gif";
	String IMAGE_ION = "org.eclipse.chemclipse.rcp.ui.icons/ion.gif";
	String IMAGE_REPORT = "org.eclipse.chemclipse.rcp.ui.icons/report.gif";
	String IMAGE_CHROMATOGRAM_REPORT = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramReport.gif";
	String IMAGE_CHROMATOGRAM_REPORT_ADD = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramReportAdd.gif";
	String IMAGE_BATCHPROCESS = "org.eclipse.chemclipse.rcp.ui.icons/batchprocess.gif";
	String IMAGE_BATCHPROCESS_PEAKIDENT = "org.eclipse.chemclipse.rcp.ui.icons/batchprocessPeakIdent.gif";
	String IMAGE_CONFIGURE = "org.eclipse.chemclipse.rcp.ui.icons/configure.gif";
	String IMAGE_EXECUTE = "org.eclipse.chemclipse.rcp.ui.icons/execute.gif";
	String IMAGE_EXECUTE_WARNING = "org.eclipse.chemclipse.rcp.ui.icons/execute_warning.png";
	String IMAGE_EXECUTE_ERROR = "org.eclipse.chemclipse.rcp.ui.icons/execute_error.png";
	String IMAGE_EXECUTE_AUTO_UPDATE = "org.eclipse.chemclipse.rcp.ui.icons/execute_auto_update.png";
	String IMAGE_PEAK_MANUAL = "org.eclipse.chemclipse.rcp.ui.icons/peak-manual.gif";
	String IMAGE_PEAK_DETECTOR = "org.eclipse.chemclipse.rcp.ui.icons/peakdetector.gif";
	String IMAGE_PEAK_DETECTOR_DECONVOLUTION = "org.eclipse.chemclipse.rcp.ui.icons/peakdetectorDeconvolution.gif";
	String IMAGE_DECONVOLUTION = "org.eclipse.chemclipse.rcp.ui.icons/deconvolution.gif";
	String IMAGE_INTEGRATION_RESULTS = "org.eclipse.chemclipse.rcp.ui.icons/integrationResults.gif";
	String IMAGE_INTEGRATOR_SUMAREA = "org.eclipse.chemclipse.rcp.ui.icons/sumareaIntegrator.gif";
	String IMAGE_PEAK_INTEGRATOR_MAX = "org.eclipse.chemclipse.rcp.ui.icons/peakIntegratorMax.gif";
	String IMAGE_PEAK_INTEGRATOR = "org.eclipse.chemclipse.rcp.ui.icons/peakIntegrator.gif";
	String IMAGE_CHROMATOGRAM_INTEGRATOR = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramIntegrator.gif";
	String IMAGE_DELETE_PEAK_INTEGRATIONS = "org.eclipse.chemclipse.rcp.ui.icons/deletePeakIntegrations.gif";
	String IMAGE_DELETE_CHROMATOGRAM_INTEGRATIONS = "org.eclipse.chemclipse.rcp.ui.icons/deleteChromatogramIntegrations.gif";
	String IMAGE_COMBINED_INTEGRATOR = "org.eclipse.chemclipse.rcp.ui.icons/combinedIntegrator.gif";
	//
	String IMAGE_CLASSIFIER = "org.eclipse.chemclipse.rcp.ui.icons/classifier.gif";
	String IMAGE_CLASSIFIER_WNC = "org.eclipse.chemclipse.rcp.ui.icons/wnc.gif";
	String IMAGE_CLASSIFIER_DW = "org.eclipse.chemclipse.rcp.ui.icons/durbin_watson.gif";
	//
	String IMAGE_FILTER_BACKFOLDING = "org.eclipse.chemclipse.rcp.ui.icons/backfolding.gif";
	String IMAGE_FILTER_CODA = "org.eclipse.chemclipse.rcp.ui.icons/coda.gif";
	String IMAGE_FILTER_DENOISING = "org.eclipse.chemclipse.rcp.ui.icons/denoising.gif";
	String IMAGE_FILTER_IONREMOVER = "org.eclipse.chemclipse.rcp.ui.icons/ionremover.gif";
	String IMAGE_WORD_DOCUMENT = "org.eclipse.chemclipse.rcp.ui.icons/word_document.gif";
	String IMAGE_EXCEL_DOCUMENT = "org.eclipse.chemclipse.rcp.ui.icons/excel_document.gif";
	String IMAGE_BITMAP_DOCUMENT = "org.eclipse.chemclipse.rcp.ui.icons/bitmap_document.png";
	String IMAGE_BACKWARD = "org.eclipse.chemclipse.rcp.ui.icons/backward.gif";
	String IMAGE_FAST_BACKWARD = "org.eclipse.chemclipse.rcp.ui.icons/fastbackward.gif";
	String IMAGE_FORWARD = "org.eclipse.chemclipse.rcp.ui.icons/forward.gif";
	String IMAGE_FAST_FORWARD = "org.eclipse.chemclipse.rcp.ui.icons/fastforward.gif";
	String IMAGE_FILTER_SAVITZKY_GOLAY = "org.eclipse.chemclipse.rcp.ui.icons/savitzkygolay.gif";
	String IMAGE_CHROMATOGRAM_FILE_EXPLORER = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramFileExplorer.gif";
	String IMAGE_MASS_SPECTRUM_FILE_EXPLORER = "org.eclipse.chemclipse.rcp.ui.icons/massSpectrumFileExplorer.gif";
	String IMAGE_MASS_SPECTRUM_LIBRARY = "org.eclipse.chemclipse.rcp.ui.icons/massSpectrumLibrary.gif";
	String IMAGE_MIRRORED_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/mirroredMassSpectrum.gif";
	String IMAGE_SHIFTED_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/shiftedMassSpectrum.gif";
	String IMAGE_IMPORT = "org.eclipse.chemclipse.rcp.ui.icons/import.png";
	String IMAGE_PREPROCESSING = "org.eclipse.chemclipse.rcp.ui.icons/preprocessing.png";
	String IMAGE_IMPORT_CHROMATOGRAM = "org.eclipse.chemclipse.rcp.ui.icons/importChromatogram.png";
	String IMAGE_EXPORT = "org.eclipse.chemclipse.rcp.ui.icons/export.png";
	String IMAGE_GROOVY_EXECUTE = "org.eclipse.chemclipse.rcp.ui.icons/groovy_execute.gif";
	String IMAGE_GROOVY_CREATE = "org.eclipse.chemclipse.rcp.ui.icons/groovy_create.gif";
	String IMAGE_JYTHON_EXECUTE = "org.eclipse.chemclipse.rcp.ui.icons/jython_execute.gif";
	String IMAGE_JYTHON_CREATE = "org.eclipse.chemclipse.rcp.ui.icons/jython_create.gif";
	String IMAGE_BASELINE = "org.eclipse.chemclipse.rcp.ui.icons/baseline.gif";
	String IMAGE_BASELINE_SNIP = "org.eclipse.chemclipse.rcp.ui.icons/baselineSnip.gif";
	String IMAGE_BASELINE_DELETE = "org.eclipse.chemclipse.rcp.ui.icons/baselineDelete.gif";
	String IMAGE_FILTER_SNIP_SELECTED_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/snipMassSpectrumPeak.gif";
	String IMAGE_FILTER_SNIP_ALL_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/snipMassSpectrumPeaks.gif";
	String IMAGE_FILTER_IONREMOVER_SELECTED_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/ionremoverMassSpectrumPeak.gif";
	String IMAGE_FILTER_IONREMOVER_ALL_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/ionremoverMassSpectrumPeaks.gif";
	//
	String IMAGE_QUANTITATION_RESULTS = "org.eclipse.chemclipse.rcp.ui.icons/integrationResults.gif";
	String IMAGE_QUANTIFY_SELECTED_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/quantifySelectedPeak.gif";
	String IMAGE_QUANTIFY_ALL_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/quantifyAllPeaks.gif";
	String IMAGE_ADD_PEAK_TO_QUANTITATION_TABLE = "org.eclipse.chemclipse.rcp.ui.icons/addPeakToQuantitationTable.gif";
	String IMAGE_ADD_PEAKS_TO_QUANTITATION_TABLE = "org.eclipse.chemclipse.rcp.ui.icons/addPeaksToQuantitationTable.gif";
	//
	String IMAGE_MANUAL_PEAK_IDENTIFIER = "org.eclipse.chemclipse.rcp.ui.icons/peakIdentifierManual.gif";
	String IMAGE_SUBTRACT_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/subtractMassSpectrum.gif";
	//
	String IMAGE_SUBTRACT_MASS_SPECTRUM_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/subtractMassSpectrumPeak.gif";
	String IMAGE_SUBTRACT_MASS_SPECTRUM_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/subtractMassSpectrumPeaks.gif";
	String IMAGE_SUBTRACT_ADD_COMBINED_SCAN = "org.eclipse.chemclipse.rcp.ui.icons/subtractFilterAddCombinedScan.gif";
	String IMAGE_SUBTRACT_ADD_SELECTED_SCAN = "org.eclipse.chemclipse.rcp.ui.icons/subtractFilterAddSelectedScan.gif";
	String IMAGE_SUBTRACT_CLEAR_SESSION_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/subtractFilterClearSessionMassSpectrum.gif";
	String IMAGE_SUBTRACT_LOAD_SESSION_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/subtractFilterLoadSessionMassSpectrum.gif";
	String IMAGE_SUBTRACT_STORE_SESSION_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/subtractFilterStoreSessionMassSpectrum.gif";
	//
	String IMAGE_SELECTED_SCAN = "org.eclipse.chemclipse.rcp.ui.icons/selectedScan.gif";
	String IMAGE_SELECTED_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/selectedPeak.gif";
	//
	String IMAGE_IMPORT_CHROMATOGRAM_MSD = "org.eclipse.chemclipse.rcp.ui.icons/importChromatogramMSD.gif";
	String IMAGE_IMPORT_CHROMATOGRAM_CSD = "org.eclipse.chemclipse.rcp.ui.icons/importChromatogramCSD.gif";
	String IMAGE_IMPORT_CHROMATOGRAM_WSD = "org.eclipse.chemclipse.rcp.ui.icons/importChromatogramWSD.gif";
	//
	String IMAGE_DATABASE = "org.eclipse.chemclipse.rcp.ui.icons/database.gif";
	String IMAGE_ION_TRANSITION = "org.eclipse.chemclipse.rcp.ui.icons/ionTransition.gif";
	//
	String IMAGE_UPDATES = "org.eclipse.chemclipse.rcp.ui.icons/updates.gif";
	String IMAGE_MARKETPLACE = "org.eclipse.chemclipse.rcp.ui.icons/marketplace.gif";
	//
	String IMAGE_PCA = "org.eclipse.chemclipse.rcp.ui.icons/pca.gif";
	//
	String IMAGE_CDK_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/cdkPeak.gif";
	String IMAGE_CDK_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/cdkPeaks.gif";
	String IMAGE_CDK_DELETE = "org.eclipse.chemclipse.rcp.ui.icons/cdkDelete.gif";
	//
	String IMAGE_LOCK_OFFSET = "org.eclipse.chemclipse.rcp.ui.icons/lockOffset.gif";
	String IMAGE_UNLOCK_OFFSET = "org.eclipse.chemclipse.rcp.ui.icons/unlockOffset.gif";
	//
	String IMAGE_PIN_CHROMATOGRAM = "org.eclipse.chemclipse.rcp.ui.icons/pinChromatogram.gif";
	String IMAGE_UNPIN_CHROMATOGRAM = "org.eclipse.chemclipse.rcp.ui.icons/unpinChromatogram.gif";
	String IMAGE_PIN_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/pinMassSpectrum.gif";
	String IMAGE_UNPIN_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/unpinMassSpectrum.gif";
	String IMAGE_RETENION_INDEX = "org.eclipse.chemclipse.rcp.ui.icons/retentionIndex.gif";
	//
	String IMAGE_IDENTIFY_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/identify_peak.gif";
	String IMAGE_IDENTIFY_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/identify_peaks.gif";
	String IMAGE_IDENTIFY_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/identify_massspectrum.gif";
	//
	String IMAGE_FILTER_MEAN_NORMALIZER = "org.eclipse.chemclipse.rcp.ui.icons/normalizerMean.gif";
	String IMAGE_FILTER_MEDIAN_NORMALIZER = "org.eclipse.chemclipse.rcp.ui.icons/normalizerMedian.gif";
	String IMAGE_FILTER_UNITSUM_NORMALIZER = "org.eclipse.chemclipse.rcp.ui.icons/normalizerUnitSum.gif";
	String IMAGE_FILTER_NORMALIZER = "org.eclipse.chemclipse.rcp.ui.icons/normalizer.gif";
	String IMAGE_FILTER_SCANREMOVER = "org.eclipse.chemclipse.rcp.ui.icons/scanremover.gif";
	//
	String IMAGE_CHROMATOGRAM_OVERLAY_SUBTRACT = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverlaySubtract.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY_MIRRORED = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverlayMirrored.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverlay.gif";
	//
	String IMAGE_RESET = "org.eclipse.chemclipse.rcp.ui.icons/reset.gif";
	String IMAGE_RESET_EQUAL = "org.eclipse.chemclipse.rcp.ui.icons/reset-equal.gif";
	String IMAGE_OFFSET_LEFT = "org.eclipse.chemclipse.rcp.ui.icons/offsetLeft.gif";
	String IMAGE_OFFSET_LEFT_FAST = "org.eclipse.chemclipse.rcp.ui.icons/offsetLeftFast.gif";
	String IMAGE_OFFSET_RIGHT = "org.eclipse.chemclipse.rcp.ui.icons/offsetRight.gif";
	String IMAGE_OFFSET_RIGHT_FAST = "org.eclipse.chemclipse.rcp.ui.icons/offsetRightFast.gif";
	String IMAGE_OFFSET_UP = "org.eclipse.chemclipse.rcp.ui.icons/offsetUp.gif";
	String IMAGE_OFFSET_DOWN = "org.eclipse.chemclipse.rcp.ui.icons/offsetDown.gif";
	//
	String IMAGE_NEXT = "org.eclipse.chemclipse.rcp.ui.icons/next.gif";
	String IMAGE_PREVIOUS = "org.eclipse.chemclipse.rcp.ui.icons/previous.gif";
	String IMAGE_NEXT_YELLOW = "org.eclipse.chemclipse.rcp.ui.icons/nextYellow.gif";
	String IMAGE_PREVIOUS_YELLOW = "org.eclipse.chemclipse.rcp.ui.icons/previousYellow.gif";
	//
	String IMAGE_SCRIPT_SHELL = "org.eclipse.chemclipse.rcp.ui.icons/script_shell.png";
	//
	String IMAGE_NIST_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/nist_massSpectrum.gif";
	String IMAGE_NIST_OPEN_MASS_SPECTRUM = "org.eclipse.chemclipse.rcp.ui.icons/nist_open_massSpectrum.gif";
	String IMAGE_NIST_OPEN_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/nist_open_peak.gif";
	String IMAGE_NIST_OPEN_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/nist_open_peaks.gif";
	String IMAGE_NIST_OPEN = "org.eclipse.chemclipse.rcp.ui.icons/nist_open.gif";
	String IMAGE_NIST_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/nist_peak.gif";
	String IMAGE_NIST_PEAKS = "org.eclipse.chemclipse.rcp.ui.icons/nist_peaks.gif";
	String IMAGE_NIST = "org.eclipse.chemclipse.rcp.ui.icons/nist.gif";
	//
	String IMAGE_LIBRARY_DOCUMENT = "org.eclipse.chemclipse.rcp.ui.icons/libraryDocument.gif";
	String IMAGE_ORIGIN_DOCUMENT = "org.eclipse.chemclipse.rcp.ui.icons/originDocument.gif";
	String IMAGE_REFERENCE_PEAK = "org.eclipse.chemclipse.rcp.ui.icons/referencePeak.gif";
	String IMAGE_CHROMATOGRAM_DATABASE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramDatabase.gif";
	String IMAGE_CHROMATOGRAM_DATABASE_ADD = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramDatabaseAdd.gif";
	String IMAGE_SUBSTANCE_HIT_RESULT = "org.eclipse.chemclipse.rcp.ui.icons/substanceHitResult.gif";
	String IMAGE_PLUS = "org.eclipse.chemclipse.rcp.ui.icons/plus.gif";
	String IMAGE_MINUS = "org.eclipse.chemclipse.rcp.ui.icons/minus.gif";
	//
	String IMAGE_ARROW_DOWN_2 = "org.eclipse.chemclipse.rcp.ui.icons/arrow_down_2.gif";
	String IMAGE_ARROW_UP_2 = "org.eclipse.chemclipse.rcp.ui.icons/arrow_up_2.gif";
	//
	String IMAGE_ARROW_DOWN = "org.eclipse.chemclipse.rcp.ui.icons/arrow_down.gif";
	String IMAGE_ARROW_UP = "org.eclipse.chemclipse.rcp.ui.icons/arrow_up.gif";
	String IMAGE_ARROW_EQUAL = "org.eclipse.chemclipse.rcp.ui.icons/arrow_equal.gif";
	//
	String IMAGE_ARROW_FORWARD = "org.eclipse.chemclipse.rcp.ui.icons/arrowForward.gif";
	String IMAGE_ARROW_BACKWARD = "org.eclipse.chemclipse.rcp.ui.icons/arrowBackward.gif";
	String IMAGE_EXCEL = "org.eclipse.chemclipse.rcp.ui.icons/excel.gif";
	String IMAGE_PDF = "org.eclipse.chemclipse.rcp.ui.icons/pdf.gif";
	String IMAGE_TXT = "org.eclipse.chemclipse.rcp.ui.icons/txt.gif";
	String IMAGE_CALCULATE = "org.eclipse.chemclipse.rcp.ui.icons/calculate.gif";
	String IMAGE_CHECK = "org.eclipse.chemclipse.rcp.ui.icons/check.gif";
	String IMAGE_SELECT_ROWS = "org.eclipse.chemclipse.rcp.ui.icons/select-rows.gif";
	String IMAGE_REMOVE = "org.eclipse.chemclipse.rcp.ui.icons/remove.gif";
	String IMAGE_STATUS_EMPTY = "org.eclipse.chemclipse.rcp.ui.icons/status-empty.gif";
	String IMAGE_STATUS_OK = "org.eclipse.chemclipse.rcp.ui.icons/status-ok.gif";
	String IMAGE_STATUS_WARN = "org.eclipse.chemclipse.rcp.ui.icons/status-warn.gif";
	String IMAGE_STATUS_ERROR = "org.eclipse.chemclipse.rcp.ui.icons/status-error.gif";
	//
	String IMAGE_DETECTION_BOX_BOTH = "org.eclipse.chemclipse.rcp.ui.icons/detectionBoxBoth.gif";
	String IMAGE_DETECTION_BOX_LEFT = "org.eclipse.chemclipse.rcp.ui.icons/detectionBoxLeft.gif";
	String IMAGE_DETECTION_BOX_RIGHT = "org.eclipse.chemclipse.rcp.ui.icons/detectionBoxRight.gif";
	//
	String IMAGE_DETECTION_TYPE_BASELINE = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypeBaseline.gif";
	String IMAGE_DETECTION_TYPE_SCAN_BB = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypeScanBB.gif";
	String IMAGE_DETECTION_TYPE_SCAN_BV = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypeScanBV.gif";
	String IMAGE_DETECTION_TYPE_SCAN_VB = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypeScanVB.gif";
	String IMAGE_DETECTION_TYPE_SCAN_VV = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypeScanVV.gif";
	String IMAGE_DETECTION_TYPE_TANGENT = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypeTangent.gif";
	String IMAGE_DETECTION_TYPE_PERPENDICULAR = "org.eclipse.chemclipse.rcp.ui.icons/detectionTypePerpendicular.gif";
	//
	String IMAGE_EXPAND_ALL = "org.eclipse.chemclipse.rcp.ui.icons/expand_all.gif";
	String IMAGE_COLLAPSE_ALL = "org.eclipse.chemclipse.rcp.ui.icons/collapse_all.gif";
	String IMAGE_SEARCH = "org.eclipse.chemclipse.rcp.ui.icons/search.gif";
	String IMAGE_EVALUATE = "org.eclipse.chemclipse.rcp.ui.icons/evaluate.gif";
	String IMAGE_EVALUATED = "org.eclipse.chemclipse.rcp.ui.icons/evaluated.gif";
	String IMAGE_VALIDATE = "org.eclipse.chemclipse.rcp.ui.icons/validate.gif";
	String IMAGE_SKIP = "org.eclipse.chemclipse.rcp.ui.icons/skip.gif";
	String IMAGE_SKIPPED = "org.eclipse.chemclipse.rcp.ui.icons/skipped.gif";
	String IMAGE_TAG = "org.eclipse.chemclipse.rcp.ui.icons/tag.gif";
	String IMAGE_SHIFT = "org.eclipse.chemclipse.rcp.ui.icons/shift.gif";
	String IMAGE_SHIFT_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/shiftActive.gif";
	String IMAGE_SHIFT_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/shiftDefault.gif";
	String IMAGE_SHIFT_Y = "org.eclipse.chemclipse.rcp.ui.icons/shiftY.gif";
	String IMAGE_SHIFT_XY = "org.eclipse.chemclipse.rcp.ui.icons/shiftXY.gif";
	String IMAGE_SHIFT_AUTO_MIRROR = "org.eclipse.chemclipse.rcp.ui.icons/shiftAutoMirror.gif";
	String IMAGE_SHIFT_MIRROR = "org.eclipse.chemclipse.rcp.ui.icons/shiftMirror.gif";
	String IMAGE_SCAN_RETENTION_TIME = "org.eclipse.chemclipse.rcp.ui.icons/scanRetentionTime.gif";
	String IMAGE_CHART_LEGEND_MARKER = "org.eclipse.chemclipse.rcp.ui.icons/chartLegendMarker.gif";
	String IMAGE_CHART_RANGE_SELECTOR = "org.eclipse.chemclipse.rcp.ui.icons/chartRangeSelector.gif";
	String IMAGE_SEQUENCE_LIST = "org.eclipse.chemclipse.rcp.ui.icons/sequenceListDefault.gif";
	String IMAGE_SEQUENCE_ADD = "org.eclipse.chemclipse.rcp.ui.icons/sequenceAdd.gif";
	/*
	 * PICTOGRAM and Data Analysis Perspective
	 */
	String PICTOGRAM_DATA_ANALYSIS = "org.eclipse.chemclipse.rcp.ui.icons/DataAnalysis.png";
	String IMAGE_CHROMATOGRAM_OVERLAY_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverlayDefault.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverlayActive.gif";
	String IMAGE_CHROMATOGRAM_OVERVIEW_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverviewDefault.gif";
	String IMAGE_CHROMATOGRAM_OVERVIEW_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramOverviewActive.gif";
	String IMAGE_SELECTED_SCANS_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/selectedScansDefault.gif";
	String IMAGE_SELECTED_SCANS_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/selectedScansActive.gif";
	String IMAGE_SELECTED_PEAKS_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/selectedPeaksDefault.gif";
	String IMAGE_SELECTED_PEAKS_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/selectedPeaksActive.gif";
	String IMAGE_SUBTRACT_SCAN_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/subtractScanDefault.gif";
	String IMAGE_SUBTRACT_SCAN_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/subtractScanActive.gif";
	String IMAGE_COMBINED_SCAN_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/combinedScanDefault.gif";
	String IMAGE_COMBINED_SCAN_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/combinedScanActive.gif";
	String IMAGE_COMPARISON_SCAN_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/comparisonScanDefault.gif";
	String IMAGE_COMPARISON_SCAN_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/comparisonScanActive.gif";
	String IMAGE_QUANTITATION_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/quantitationDefault.gif";
	String IMAGE_QUANTITATION_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/quantitationActive.gif";
	String IMAGE_SCAN_PEAK_LIST_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/scanPeakListDefault.gif";
	String IMAGE_SCAN_PEAK_LIST_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/scanPeakListActive.gif";
	String IMAGE_HEATMAP_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/heatmapActive.gif";
	String IMAGE_HEATMAP_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/heatmapDefault.gif";
	String IMAGE_SEQUENCE_LIST_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/sequenceListDefault.gif";
	String IMAGE_SEQUENCE_LIST_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/sequenceListActive.gif";
	String IMAGE_PCR_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/pcrActive.gif";
	String IMAGE_PCR_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/pcrDefault.gif";
	String IMAGE_INTERNAL_STANDARDS_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/internalStandardsDefault.gif";
	String IMAGE_INTERNAL_STANDARDS_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/internalStandardsActive.gif";
	String IMAGE_EXTERNAL_STANDARDS_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/externalStandardsDefault.gif";
	String IMAGE_EXTERNAL_STANDARDS_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/externalStandardsActive.gif";
	String IMAGE_EDIT_ENTRY = "org.eclipse.chemclipse.rcp.ui.icons/editEntry.gif";
	String IMAGE_EDIT_ENTRY_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/editEntryDefault.gif";
	String IMAGE_EDIT_ENTRY_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/editEntryActive.gif";
	String IMAGE_CHROMATOGRAM_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramDefault.gif";
	String IMAGE_CHROMATOGRAM_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramActive.gif";
	//
	String IMAGE_PROCESS_CONTROL = "org.eclipse.chemclipse.rcp.ui.icons/processControl.gif";
	String IMAGE_START_PROCESSING = "org.eclipse.chemclipse.rcp.ui.icons/startProcessing.gif";
	String IMAGE_END_PROCESSING = "org.eclipse.chemclipse.rcp.ui.icons/endProcessing.gif";
	//
	String IMAGE_RESULTS = "org.eclipse.chemclipse.rcp.ui.icons/measurementResults.gif";
	String IMAGE_MEASUREMENT_RESULTS_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/measurementResultsDefault.gif";
	String IMAGE_MEASUREMENT_RESULTS_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/measurementResultsActive.gif";
	//
	String IMAGE_PLATE_PCR = "org.eclipse.chemclipse.rcp.ui.icons/plate-pcr.gif";
	String IMAGE_METHOD = "org.eclipse.chemclipse.rcp.ui.icons/method.gif";
	String IMAGE_METHOD_ADD = "org.eclipse.chemclipse.rcp.ui.icons/methodAdd.gif";
	String IMAGE_METHOD_EDIT = "org.eclipse.chemclipse.rcp.ui.icons/methodEdit.gif";
	String IMAGE_METHOD_DELETE = "org.eclipse.chemclipse.rcp.ui.icons/methodDelete.gif";
	String IMAGE_METHOD_COPY = "org.eclipse.chemclipse.rcp.ui.icons/methodCopy.gif";
	//
	String IMAGE_EXECUTE_EXTENSION = "org.eclipse.chemclipse.rcp.ui.icons/execute-extension.png";
	String IMAGE_POPUP_MENU = "org.eclipse.chemclipse.rcp.ui.icons/popup-menu.png";
	//
	String IMAGE_AUTH = "org.eclipse.chemclipse.rcp.ui.icons/auth.png";
	String IMAGE_AUTH_LOCKED = "org.eclipse.chemclipse.rcp.ui.icons/auth_locked.gif";
	String IMAGE_AUTH_UNLOCKED = "org.eclipse.chemclipse.rcp.ui.icons/auth_unlocked.gif";
	//
	String IMAGE_HEADER_DATA = "org.eclipse.chemclipse.rcp.ui.icons/headerdata.gif";
	String IMAGE_CHART_DATA_SHOW = "org.eclipse.chemclipse.rcp.ui.icons/chartDataShow.png";
	String IMAGE_CHART_DATA_HIDE = "org.eclipse.chemclipse.rcp.ui.icons/chartDataHide.png";
	//
	String IMAGE_PERSON_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/personDefault.gif";
	String IMAGE_PERSON_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/personActive.gif";
	//
	String IMAGE_FILE_ADD = "org.eclipse.chemclipse.rcp.ui.icons/fileAdd.gif";
	String IMAGE_FOLDER_ADD = "org.eclipse.chemclipse.rcp.ui.icons/folderAdd.gif";
	//
	String IMAGE_CHROMATOGRAM_BLANK = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramBlank.gif";
	String IMAGE_INTERPOLATE = "org.eclipse.chemclipse.rcp.ui.icons/interpolate.png";
	//
	String IMAGE_OFFSET_DEFAULT = "org.eclipse.chemclipse.rcp.ui.icons/offsetDefault.gif";
	String IMAGE_OFFSET_ACTIVE = "org.eclipse.chemclipse.rcp.ui.icons/offsetActive.gif";
	//
	String IMAGE_MERGE = "org.eclipse.chemclipse.rcp.ui.icons/merge.gif";
	String IMAGE_LABELS = "org.eclipse.chemclipse.rcp.ui.icons/labels.png";
	String IMAGE_PEAK_TRACES = "org.eclipse.chemclipse.rcp.ui.icons/peakTraces.gif";
	String IMAGE_COPY_CLIPBOARD = "org.eclipse.chemclipse.rcp.ui.icons/copy-clipboard.png";
	String IMAGE_ZOOM_LOCKED = "org.eclipse.chemclipse.rcp.ui.icons/zoomLocked.png";
	String IMAGE_TYPES = "org.eclipse.chemclipse.rcp.ui.icons/types.png";
	String IMAGE_VALUE_DECREASE = "org.eclipse.chemclipse.rcp.ui.icons/valueDecrease.gif";
	String IMAGE_VALUE_INCREASE = "org.eclipse.chemclipse.rcp.ui.icons/valueIncrease.gif";
	//
	String IMAGE_INSTRUMENT = "org.eclipse.chemclipse.rcp.ui.icons/instrument.gif";
	String IMAGE_BASELINE_SHOW = "org.eclipse.chemclipse.rcp.ui.icons/baselineShow.gif";
	String IMAGE_BASELINE_HIDE = "org.eclipse.chemclipse.rcp.ui.icons/baselineHide.gif";
	String IMAGE_CHROMATOGRAM_TIC_SHOW = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramTicShow.gif";
	String IMAGE_CHROMATOGRAM_TIC_HIDE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramTicHide.gif";
	String IMAGE_CHROMATOGRAM_XIC_SHOW = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramXicShow.gif";
	String IMAGE_CHROMATOGRAM_XIC_HIDE = "org.eclipse.chemclipse.rcp.ui.icons/chromatogramXicHide.gif";
	String IMAGE_REVIEW_DETAILS_SHOW = "org.eclipse.chemclipse.rcp.ui.icons/reviewDetailsShow.gif";
	String IMAGE_REVIEW_DETAILS_HIDE = "org.eclipse.chemclipse.rcp.ui.icons/reviewDetailsHide.gif";
	String IMAGE_PLUGINS = "org.eclipse.chemclipse.rcp.ui.icons/plugins.png";
	String IMAGE_ZIP_FILE = "org.eclipse.chemclipse.rcp.ui.icons/zip_file.png";
	String IMAGE_TRANSFER = "org.eclipse.chemclipse.rcp.ui.icons/transfer.png";
	//
	String IMAGE_FOCUS_TOP = "org.eclipse.chemclipse.rcp.ui.icons/focus_top.gif";
	String IMAGE_FOCUS_BOTTOM = "org.eclipse.chemclipse.rcp.ui.icons/focus_bottom.gif";
	String IMAGE_FOCUS_BOTH_HORIZONTAL = "org.eclipse.chemclipse.rcp.ui.icons/focus_both_horizontal.gif";
	String IMAGE_FOCUS_LEFT = "org.eclipse.chemclipse.rcp.ui.icons/focus_left.gif";
	String IMAGE_FOCUS_RIGHT = "org.eclipse.chemclipse.rcp.ui.icons/focus_right.gif";
	String IMAGE_FOCUS_BOTH_VERTICAL = "org.eclipse.chemclipse.rcp.ui.icons/focus_both_vertical.gif";
	//
	String IMAGE_PEAK_RANGE = "org.eclipse.chemclipse.rcp.ui.icons/peak-range.gif";
}