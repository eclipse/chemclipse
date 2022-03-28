/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
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

	String PATH_PREFIX = "org.eclipse.chemclipse.rcp.ui.icons/";

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

	String PICTOGRAM_SUBTRACT_SCAN_ONE = PATH_PREFIX + "subtractScanOne.png";
	String PICTOGRAM_SUBTRACT_SCAN_MANY = PATH_PREFIX + "subtractScanMany.png";
	/*
	 * 7x7 (Decorator)
	 * 8x8 (Decorator)
	 */
	String IMAGE_DECORATOR_ACTIVE = PATH_PREFIX + "decorator_active.gif";
	/*
	 * Icons and Images
	 */
	String IMAGE_ADD = PATH_PREFIX + "add.gif";
	String IMAGE_COPY = PATH_PREFIX + "copy.png";
	String IMAGE_REFRESH = PATH_PREFIX + "refresh.png";
	String IMAGE_CANCEL = PATH_PREFIX + "cancel.gif";
	String IMAGE_DELETE = PATH_PREFIX + "delete.gif";
	String IMAGE_DELETE_ALL = PATH_PREFIX + "delete_all.png";;
	String IMAGE_EXECUTE_ADD = PATH_PREFIX + "execute_add.gif";
	String IMAGE_EDIT = PATH_PREFIX + "edit.gif";
	String IMAGE_EDIT_DEFAULT = PATH_PREFIX + "editDefault.gif";
	String IMAGE_EDIT_ACTIVE = PATH_PREFIX + "editActive.gif";
	String IMAGE_EDIT_DISABLED = PATH_PREFIX + "edit_disabled.gif";
	String IMAGE_EDIT_SHIFT = PATH_PREFIX + "editShift.gif";
	String IMAGE_EDIT_PROFILE = PATH_PREFIX + "editProfile.gif";
	String IMAGE_CHROMATOGRAM_PROFILE = PATH_PREFIX + "chromatogramProfile.gif";
	//
	String IMAGE_EMPTY = PATH_PREFIX + "empty.png";
	String IMAGE_LOGO = PATH_PREFIX + "logo.png";
	String IMAGE_ABOUT = PATH_PREFIX + "about.png";
	//
	String IMAGE_DESELECTED = PATH_PREFIX + "deselected.gif";
	String IMAGE_DESELECTED_INACTIVE = PATH_PREFIX + "deselected_inactive.gif";
	String IMAGE_SELECTED = PATH_PREFIX + "selected.gif";
	String IMAGE_CHECK_ALL = PATH_PREFIX + "checkAll.gif";
	String IMAGE_UNCHECK_ALL = PATH_PREFIX + "uncheckAll.gif";
	//
	String IMAGE_SHRINK_CHROMATOGRAMS = PATH_PREFIX + "shrinkChromatograms.gif";
	String IMAGE_ALIGN_CHROMATOGRAMS = PATH_PREFIX + "alignChromatograms.gif";
	String IMAGE_STRETCH_CHROMATOGRAMS = PATH_PREFIX + "stretchChromatograms.gif";
	String IMAGE_ADJUST_CHROMATOGRAMS = PATH_PREFIX + "adjustChromatograms.gif";
	//
	String IMAGE_CHROMATOGRAM = PATH_PREFIX + "chromatogram.gif";
	String IMAGE_CHROMATOGRAM_MSD = PATH_PREFIX + "chromatogram-msd.gif";
	String IMAGE_CHROMATOGRAM_CSD = PATH_PREFIX + "chromatogram-csd.gif";
	String IMAGE_CHROMATOGRAM_WSD = PATH_PREFIX + "chromatogram-wsd.gif";
	String IMAGE_CHROMATOGRAM_TSD = PATH_PREFIX + "chromatogram-tsd.gif";
	String IMAGE_SCAN_XIR = PATH_PREFIX + "scan-xir.gif"; // FTIR, NIR, ...
	String IMAGE_SCAN_XIR_RAW = PATH_PREFIX + "scan-xir-raw.gif";
	String IMAGE_SCAN_XIR_INVERTED = PATH_PREFIX + "scan-xir-inverted.gif";
	String IMAGE_SCAN_NMR = PATH_PREFIX + "scan-nmr.gif";
	String IMAGE_SCAN_FID = PATH_PREFIX + "scan-fid.png";
	//
	String IMAGE_SAMPLE = PATH_PREFIX + "sample.gif";
	String IMAGE_SAMPLE_CALIBRATION = PATH_PREFIX + "sample-calibration.gif";
	String IMAGE_SAMPLE_CALIBRATION_QUALIFIER = PATH_PREFIX + "sample-calibration-qualifier.gif";
	String IMAGE_SAMPLE_QUALIFIER = PATH_PREFIX + "sample-qualifier.gif";
	String IMAGE_SAMPLE_ISTD = PATH_PREFIX + "sample-istd.gif";
	String IMAGE_SAMPLE_ISTD_QUALIFIER = PATH_PREFIX + "sample-istd-qualifier.gif";
	String IMAGE_SAMPLE_QC = PATH_PREFIX + "sample-qc.gif";
	String IMAGE_SAMPLE_QC_QUALIFIER = PATH_PREFIX + "sample-qc-qualifier.gif";
	//
	String IMAGE_PREFERENCES = PATH_PREFIX + "preferences.gif";
	String IMAGE_SETTINGS_PULL = PATH_PREFIX + "settings_pull.png";
	String IMAGE_SETTINGS_PUSH = PATH_PREFIX + "settings_push.png";
	String IMAGE_SETTINGS_SYNCHRONIZE = PATH_PREFIX + "settings_synchronize.png";
	String IMAGE_DRIVE = PATH_PREFIX + "drive.gif";
	String IMAGE_CHROMATOGRAM_ZERO_SET = PATH_PREFIX + "chromatogramZeroSet.gif";
	String IMAGE_FOLDER_OPENED = PATH_PREFIX + "folder_opened.gif";
	String IMAGE_FOLDER_CLOSED = PATH_PREFIX + "folder_closed.gif";
	String IMAGE_FILE = PATH_PREFIX + "file.gif";
	String IMAGE_FOLDER = PATH_PREFIX + "folder.png";
	String IMAGE_OPEN_FOLDER = PATH_PREFIX + "open-folder.png";
	String IMAGE_PEAK = PATH_PREFIX + "peak.gif";
	String IMAGE_PEAK_ADD = PATH_PREFIX + "peakAdd.gif";
	String IMAGE_PEAK_REPLACE = PATH_PREFIX + "peakReplace.gif";
	String IMAGE_PEAKS = PATH_PREFIX + "peaks.gif";
	String IMAGE_ERROR = PATH_PREFIX + "error.gif";
	String IMAGE_WARN = PATH_PREFIX + "warn.gif";
	String IMAGE_INFO = PATH_PREFIX + "info.gif";
	String IMAGE_VALID = PATH_PREFIX + "valid.gif";
	String IMAGE_QUESTION = PATH_PREFIX + "question.gif";
	String IMAGE_MASS_SPECTRUM = PATH_PREFIX + "massSpectrum.gif";
	String IMAGE_MASS_SPECTRUM_FILE = PATH_PREFIX + "massSpectrumFile.gif";
	String IMAGE_MASS_SPECTRUM_DATABASE = PATH_PREFIX + "massSpectrumDatabase.gif";
	String IMAGE_TARGETS = PATH_PREFIX + "targets.gif";
	String IMAGE_CREATE_SNAPSHOT = PATH_PREFIX + "create_snapshot.png";
	String IMAGE_PERSPECTIVES = PATH_PREFIX + "perspectives.gif";
	String IMAGE_QUIT = PATH_PREFIX + "quit.gif";
	String IMAGE_SAVE = PATH_PREFIX + "save.gif";
	String IMAGE_SAVE_DISABLED = PATH_PREFIX + "save_disabled.gif";
	String IMAGE_SAVE_AS = PATH_PREFIX + "saveas.gif";
	String IMAGE_SAVE_AS_DISABLED = PATH_PREFIX + "saveas_disabled.gif";
	String IMAGE_SAVEALL = PATH_PREFIX + "saveall.gif";
	String IMAGE_SAVEALL_DISABLED = PATH_PREFIX + "saveall_disabled.gif";
	String IMAGE_VIEW = PATH_PREFIX + "view.gif";
	String IMAGE_UNKNOWN = PATH_PREFIX + "unknown.gif";
	String IMAGE_LOG = PATH_PREFIX + "log.gif";
	String IMAGE_ION = PATH_PREFIX + "ion.gif";
	String IMAGE_REPORT = PATH_PREFIX + "report.gif";
	String IMAGE_CHROMATOGRAM_REPORT = PATH_PREFIX + "chromatogramReport.gif";
	String IMAGE_CHROMATOGRAM_REPORT_ADD = PATH_PREFIX + "chromatogramReportAdd.gif";
	String IMAGE_BATCHPROCESS = PATH_PREFIX + "batchprocess.gif";
	String IMAGE_BATCHPROCESS_PEAKIDENT = PATH_PREFIX + "batchprocessPeakIdent.gif";
	String IMAGE_CONFIGURE = PATH_PREFIX + "configure.gif";
	String IMAGE_EXECUTE = PATH_PREFIX + "execute.gif";
	String IMAGE_EXECUTE_WARNING = PATH_PREFIX + "execute_warning.png";
	String IMAGE_EXECUTE_ERROR = PATH_PREFIX + "execute_error.png";
	String IMAGE_EXECUTE_AUTO_UPDATE = PATH_PREFIX + "execute_auto_update.png";
	String IMAGE_PEAK_MANUAL = PATH_PREFIX + "peak-manual.gif";
	String IMAGE_PEAK_DETECTOR = PATH_PREFIX + "peakdetector.gif";
	String IMAGE_PEAK_DETECTOR_DECONVOLUTION = PATH_PREFIX + "peakdetectorDeconvolution.gif";
	String IMAGE_DECONVOLUTION = PATH_PREFIX + "deconvolution.gif";
	String IMAGE_INTEGRATION_RESULTS = PATH_PREFIX + "integrationResults.gif";
	String IMAGE_INTEGRATOR_SUMAREA = PATH_PREFIX + "sumareaIntegrator.gif";
	String IMAGE_PEAK_INTEGRATOR_MAX = PATH_PREFIX + "peakIntegratorMax.gif";
	String IMAGE_PEAK_INTEGRATOR = PATH_PREFIX + "peakIntegrator.gif";
	String IMAGE_CHROMATOGRAM_INTEGRATOR = PATH_PREFIX + "chromatogramIntegrator.gif";
	String IMAGE_DELETE_PEAK_INTEGRATIONS = PATH_PREFIX + "deletePeakIntegrations.gif";
	String IMAGE_DELETE_PEAK_IDENTIFICATIONS = PATH_PREFIX + "delete_all_identifications.gif";
	String IMAGE_DELETE_CHROMATOGRAM_INTEGRATIONS = PATH_PREFIX + "deleteChromatogramIntegrations.gif";
	String IMAGE_COMBINED_INTEGRATOR = PATH_PREFIX + "combinedIntegrator.gif";
	//
	String IMAGE_CLASSIFIER = PATH_PREFIX + "classifier.gif";
	String IMAGE_CLASSIFIER_WNC = PATH_PREFIX + "wnc.gif";
	String IMAGE_CLASSIFIER_DW = PATH_PREFIX + "durbin_watson.gif";
	//
	String IMAGE_FILTER_BACKFOLDING = PATH_PREFIX + "backfolding.gif";
	String IMAGE_FILTER_CODA = PATH_PREFIX + "coda.gif";
	String IMAGE_FILTER_DENOISING = PATH_PREFIX + "denoising.gif";
	String IMAGE_FILTER_IONREMOVER = PATH_PREFIX + "ionremover.gif";
	String IMAGE_WORD_DOCUMENT = PATH_PREFIX + "word_document.gif";
	String IMAGE_EXCEL_DOCUMENT = PATH_PREFIX + "excel_document.gif";
	String IMAGE_BITMAP_DOCUMENT = PATH_PREFIX + "bitmap_document.png";
	String IMAGE_BACKWARD = PATH_PREFIX + "backward.gif";
	String IMAGE_FAST_BACKWARD = PATH_PREFIX + "fastbackward.gif";
	String IMAGE_FORWARD = PATH_PREFIX + "forward.gif";
	String IMAGE_FAST_FORWARD = PATH_PREFIX + "fastforward.gif";
	String IMAGE_FILTER_SAVITZKY_GOLAY = PATH_PREFIX + "savitzkygolay.gif";
	String IMAGE_CHROMATOGRAM_FILE_EXPLORER = PATH_PREFIX + "chromatogramFileExplorer.gif";
	String IMAGE_MASS_SPECTRUM_FILE_EXPLORER = PATH_PREFIX + "massSpectrumFileExplorer.gif";
	String IMAGE_MASS_SPECTRUM_LIBRARY = PATH_PREFIX + "massSpectrumLibrary.gif";
	String IMAGE_MIRRORED_MASS_SPECTRUM = PATH_PREFIX + "mirroredMassSpectrum.gif";
	String IMAGE_SHIFTED_MASS_SPECTRUM = PATH_PREFIX + "shiftedMassSpectrum.gif";
	String IMAGE_IMPORT = PATH_PREFIX + "import.png";
	String IMAGE_PREPROCESSING = PATH_PREFIX + "preprocessing.png";
	String IMAGE_IMPORT_CHROMATOGRAM = PATH_PREFIX + "importChromatogram.png";
	String IMAGE_EXPORT = PATH_PREFIX + "export.png";
	String IMAGE_GROOVY_EXECUTE = PATH_PREFIX + "groovy_execute.gif";
	String IMAGE_GROOVY_CREATE = PATH_PREFIX + "groovy_create.gif";
	String IMAGE_JYTHON_EXECUTE = PATH_PREFIX + "jython_execute.gif";
	String IMAGE_JYTHON_CREATE = PATH_PREFIX + "jython_create.gif";
	String IMAGE_BASELINE = PATH_PREFIX + "baseline.gif";
	String IMAGE_BASELINE_SNIP = PATH_PREFIX + "baselineSnip.gif";
	String IMAGE_BASELINE_DELETE = PATH_PREFIX + "baselineDelete.gif";
	String IMAGE_FILTER_SNIP_SELECTED_PEAK = PATH_PREFIX + "snipMassSpectrumPeak.gif";
	String IMAGE_FILTER_SNIP_ALL_PEAKS = PATH_PREFIX + "snipMassSpectrumPeaks.gif";
	String IMAGE_FILTER_IONREMOVER_SELECTED_PEAK = PATH_PREFIX + "ionremoverMassSpectrumPeak.gif";
	String IMAGE_FILTER_IONREMOVER_ALL_PEAKS = PATH_PREFIX + "ionremoverMassSpectrumPeaks.gif";
	//
	String IMAGE_QUANTITATION_RESULTS = PATH_PREFIX + "integrationResults.gif";
	String IMAGE_QUANTIFY_SELECTED_PEAK = PATH_PREFIX + "quantifySelectedPeak.gif";
	String IMAGE_QUANTIFY_ALL_PEAKS = PATH_PREFIX + "quantifyAllPeaks.gif";
	String IMAGE_ADD_PEAK_TO_QUANTITATION_TABLE = PATH_PREFIX + "addPeakToQuantitationTable.gif";
	String IMAGE_ADD_PEAKS_TO_QUANTITATION_TABLE = PATH_PREFIX + "addPeaksToQuantitationTable.gif";
	//
	String IMAGE_MANUAL_PEAK_IDENTIFIER = PATH_PREFIX + "peakIdentifierManual.gif";
	String IMAGE_SUBTRACT_MASS_SPECTRUM = PATH_PREFIX + "subtractMassSpectrum.gif";
	//
	String IMAGE_SUBTRACT_MASS_SPECTRUM_PEAK = PATH_PREFIX + "subtractMassSpectrumPeak.gif";
	String IMAGE_SUBTRACT_MASS_SPECTRUM_PEAKS = PATH_PREFIX + "subtractMassSpectrumPeaks.gif";
	String IMAGE_SUBTRACT_ADD_COMBINED_SCAN = PATH_PREFIX + "subtractFilterAddCombinedScan.gif";
	String IMAGE_SUBTRACT_ADD_SELECTED_SCAN = PATH_PREFIX + "subtractFilterAddSelectedScan.gif";
	String IMAGE_SUBTRACT_CLEAR_SESSION_MASS_SPECTRUM = PATH_PREFIX + "subtractFilterClearSessionMassSpectrum.gif";
	String IMAGE_SUBTRACT_LOAD_SESSION_MASS_SPECTRUM = PATH_PREFIX + "subtractFilterLoadSessionMassSpectrum.gif";
	String IMAGE_SUBTRACT_STORE_SESSION_MASS_SPECTRUM = PATH_PREFIX + "subtractFilterStoreSessionMassSpectrum.gif";
	//
	String IMAGE_SELECTED_SCAN = PATH_PREFIX + "selectedScan.gif";
	String IMAGE_SELECTED_PEAK = PATH_PREFIX + "selectedPeak.gif";
	//
	String IMAGE_IMPORT_CHROMATOGRAM_MSD = PATH_PREFIX + "importChromatogramMSD.gif";
	String IMAGE_IMPORT_CHROMATOGRAM_CSD = PATH_PREFIX + "importChromatogramCSD.gif";
	String IMAGE_IMPORT_CHROMATOGRAM_WSD = PATH_PREFIX + "importChromatogramWSD.gif";
	//
	String IMAGE_DATABASE = PATH_PREFIX + "database.gif";
	String IMAGE_ION_TRANSITION = PATH_PREFIX + "ionTransition.gif";
	//
	String IMAGE_UPDATES = PATH_PREFIX + "updates.gif";
	String IMAGE_MARKETPLACE = PATH_PREFIX + "marketplace.gif";
	//
	String IMAGE_PCA = PATH_PREFIX + "pca.gif";
	//
	String IMAGE_CDK_PEAK = PATH_PREFIX + "cdkPeak.gif";
	String IMAGE_CDK_PEAKS = PATH_PREFIX + "cdkPeaks.gif";
	String IMAGE_CDK_DELETE = PATH_PREFIX + "cdkDelete.gif";
	//
	String IMAGE_LOCK_OFFSET = PATH_PREFIX + "lockOffset.gif";
	String IMAGE_UNLOCK_OFFSET = PATH_PREFIX + "unlockOffset.gif";
	//
	String IMAGE_PIN_CHROMATOGRAM = PATH_PREFIX + "pinChromatogram.gif";
	String IMAGE_UNPIN_CHROMATOGRAM = PATH_PREFIX + "unpinChromatogram.gif";
	String IMAGE_PIN_MASS_SPECTRUM = PATH_PREFIX + "pinMassSpectrum.gif";
	String IMAGE_UNPIN_MASS_SPECTRUM = PATH_PREFIX + "unpinMassSpectrum.gif";
	String IMAGE_RETENION_INDEX = PATH_PREFIX + "retentionIndex.gif";
	//
	String IMAGE_IDENTIFY_PEAK = PATH_PREFIX + "identify_peak.gif";
	String IMAGE_IDENTIFY_PEAKS = PATH_PREFIX + "identify_peaks.gif";
	String IMAGE_IDENTIFY_MASS_SPECTRUM = PATH_PREFIX + "identify_massspectrum.gif";
	//
	String IMAGE_FILTER_MEAN_NORMALIZER = PATH_PREFIX + "normalizerMean.gif";
	String IMAGE_FILTER_MEDIAN_NORMALIZER = PATH_PREFIX + "normalizerMedian.gif";
	String IMAGE_FILTER_UNITSUM_NORMALIZER = PATH_PREFIX + "normalizerUnitSum.gif";
	String IMAGE_FILTER_NORMALIZER = PATH_PREFIX + "normalizer.gif";
	String IMAGE_FILTER_SCANREMOVER = PATH_PREFIX + "scanremover.gif";
	//
	String IMAGE_CHROMATOGRAM_OVERLAY_SUBTRACT = PATH_PREFIX + "chromatogramOverlaySubtract.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY_MIRRORED = PATH_PREFIX + "chromatogramOverlayMirrored.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY = PATH_PREFIX + "chromatogramOverlay.gif";
	//
	String IMAGE_RESET = PATH_PREFIX + "reset.gif";
	String IMAGE_RESET_EQUAL = PATH_PREFIX + "reset-equal.gif";
	String IMAGE_OFFSET_LEFT = PATH_PREFIX + "offsetLeft.gif";
	String IMAGE_OFFSET_LEFT_FAST = PATH_PREFIX + "offsetLeftFast.gif";
	String IMAGE_OFFSET_RIGHT = PATH_PREFIX + "offsetRight.gif";
	String IMAGE_OFFSET_RIGHT_FAST = PATH_PREFIX + "offsetRightFast.gif";
	String IMAGE_OFFSET_UP = PATH_PREFIX + "offsetUp.gif";
	String IMAGE_OFFSET_DOWN = PATH_PREFIX + "offsetDown.gif";
	//
	String IMAGE_NEXT = PATH_PREFIX + "next.gif";
	String IMAGE_PREVIOUS = PATH_PREFIX + "previous.gif";
	String IMAGE_NEXT_YELLOW = PATH_PREFIX + "nextYellow.gif";
	String IMAGE_PREVIOUS_YELLOW = PATH_PREFIX + "previousYellow.gif";
	//
	String IMAGE_SCRIPT_SHELL = PATH_PREFIX + "script_shell.png";
	//
	String IMAGE_NIST_MASS_SPECTRUM = PATH_PREFIX + "nist_massSpectrum.gif";
	String IMAGE_NIST_OPEN_MASS_SPECTRUM = PATH_PREFIX + "nist_open_massSpectrum.gif";
	String IMAGE_NIST_OPEN_PEAK = PATH_PREFIX + "nist_open_peak.gif";
	String IMAGE_NIST_OPEN_PEAKS = PATH_PREFIX + "nist_open_peaks.gif";
	String IMAGE_NIST_OPEN = PATH_PREFIX + "nist_open.gif";
	String IMAGE_NIST_PEAK = PATH_PREFIX + "nist_peak.gif";
	String IMAGE_NIST_PEAKS = PATH_PREFIX + "nist_peaks.gif";
	String IMAGE_NIST = PATH_PREFIX + "nist.gif";
	//
	String IMAGE_LIBRARY_DOCUMENT = PATH_PREFIX + "libraryDocument.gif";
	String IMAGE_ORIGIN_DOCUMENT = PATH_PREFIX + "originDocument.gif";
	String IMAGE_REFERENCE_PEAK = PATH_PREFIX + "referencePeak.gif";
	String IMAGE_CHROMATOGRAM_DATABASE = PATH_PREFIX + "chromatogramDatabase.gif";
	String IMAGE_CHROMATOGRAM_DATABASE_ADD = PATH_PREFIX + "chromatogramDatabaseAdd.gif";
	String IMAGE_SUBSTANCE_HIT_RESULT = PATH_PREFIX + "substanceHitResult.gif";
	String IMAGE_PLUS = PATH_PREFIX + "plus.gif";
	String IMAGE_MINUS = PATH_PREFIX + "minus.gif";
	//
	String IMAGE_ARROW_DOWN_2 = PATH_PREFIX + "arrow_down_2.gif";
	String IMAGE_ARROW_UP_2 = PATH_PREFIX + "arrow_up_2.gif";
	//
	String IMAGE_ARROW_DOWN = PATH_PREFIX + "arrow_down.gif";
	String IMAGE_ARROW_UP = PATH_PREFIX + "arrow_up.gif";
	String IMAGE_ARROW_EQUAL = PATH_PREFIX + "arrow_equal.gif";
	//
	String IMAGE_ARROW_FORWARD = PATH_PREFIX + "arrowForward.gif";
	String IMAGE_ARROW_BACKWARD = PATH_PREFIX + "arrowBackward.gif";
	String IMAGE_EXCEL = PATH_PREFIX + "excel.gif";
	String IMAGE_PDF = PATH_PREFIX + "pdf.gif";
	String IMAGE_TXT = PATH_PREFIX + "txt.gif";
	String IMAGE_CALCULATE = PATH_PREFIX + "calculate.gif";
	String IMAGE_CHECK = PATH_PREFIX + "check.gif";
	String IMAGE_SELECT_ROWS = PATH_PREFIX + "select-rows.gif";
	String IMAGE_REMOVE = PATH_PREFIX + "remove.gif";
	String IMAGE_STATUS_EMPTY = PATH_PREFIX + "status-empty.gif";
	String IMAGE_STATUS_OK = PATH_PREFIX + "status-ok.gif";
	String IMAGE_STATUS_WARN = PATH_PREFIX + "status-warn.gif";
	String IMAGE_STATUS_ERROR = PATH_PREFIX + "status-error.gif";
	//
	String IMAGE_DETECTION_BOX_BOTH = PATH_PREFIX + "detectionBoxBoth.gif";
	String IMAGE_DETECTION_BOX_LEFT = PATH_PREFIX + "detectionBoxLeft.gif";
	String IMAGE_DETECTION_BOX_RIGHT = PATH_PREFIX + "detectionBoxRight.gif";
	//
	String IMAGE_DETECTION_TYPE_BASELINE = PATH_PREFIX + "detectionTypeBaseline.gif";
	String IMAGE_DETECTION_TYPE_SCAN_BB = PATH_PREFIX + "detectionTypeScanBB.gif";
	String IMAGE_DETECTION_TYPE_SCAN_BV = PATH_PREFIX + "detectionTypeScanBV.gif";
	String IMAGE_DETECTION_TYPE_SCAN_VB = PATH_PREFIX + "detectionTypeScanVB.gif";
	String IMAGE_DETECTION_TYPE_SCAN_VV = PATH_PREFIX + "detectionTypeScanVV.gif";
	String IMAGE_DETECTION_TYPE_TANGENT = PATH_PREFIX + "detectionTypeTangent.gif";
	String IMAGE_DETECTION_TYPE_PERPENDICULAR = PATH_PREFIX + "detectionTypePerpendicular.gif";
	//
	String IMAGE_EXPAND_ALL = PATH_PREFIX + "expand_all.gif";
	String IMAGE_COLLAPSE_ALL = PATH_PREFIX + "collapse_all.gif";
	String IMAGE_SEARCH = PATH_PREFIX + "search.gif";
	String IMAGE_CASE_SENSITIVE = PATH_PREFIX + "caseSensitive.gif";
	String IMAGE_EVALUATE = PATH_PREFIX + "evaluate.gif";
	String IMAGE_EVALUATED = PATH_PREFIX + "evaluated.gif";
	String IMAGE_VALIDATE = PATH_PREFIX + "validate.gif";
	String IMAGE_SKIP = PATH_PREFIX + "skip.gif";
	String IMAGE_SKIPPED = PATH_PREFIX + "skipped.gif";
	String IMAGE_TAG = PATH_PREFIX + "tag.gif";
	String IMAGE_SHIFT = PATH_PREFIX + "shift.gif";
	String IMAGE_SHIFT_ACTIVE = PATH_PREFIX + "shiftActive.gif";
	String IMAGE_SHIFT_DEFAULT = PATH_PREFIX + "shiftDefault.gif";
	String IMAGE_SHIFT_Y = PATH_PREFIX + "shiftY.gif";
	String IMAGE_SHIFT_XY = PATH_PREFIX + "shiftXY.gif";
	String IMAGE_SHIFT_AUTO_MIRROR = PATH_PREFIX + "shiftAutoMirror.gif";
	String IMAGE_SHIFT_MIRROR = PATH_PREFIX + "shiftMirror.gif";
	String IMAGE_SCAN_RETENTION_TIME = PATH_PREFIX + "scanRetentionTime.gif";
	String IMAGE_CHART_LEGEND_MARKER = PATH_PREFIX + "chartLegendMarker.gif";
	String IMAGE_CHART_RANGE_SELECTOR = PATH_PREFIX + "chartRangeSelector.gif";
	String IMAGE_SEQUENCE_LIST = PATH_PREFIX + "sequenceListDefault.gif";
	String IMAGE_SEQUENCE_ADD = PATH_PREFIX + "sequenceAdd.gif";
	/*
	 * PICTOGRAM and Data Analysis Perspective
	 */
	String PICTOGRAM_DATA_ANALYSIS = PATH_PREFIX + "DataAnalysis.png";
	String IMAGE_CHROMATOGRAM_OVERLAY_DEFAULT = PATH_PREFIX + "chromatogramOverlayDefault.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY_ACTIVE = PATH_PREFIX + "chromatogramOverlayActive.gif";
	String IMAGE_CHROMATOGRAM_OVERVIEW_DEFAULT = PATH_PREFIX + "chromatogramOverviewDefault.gif";
	String IMAGE_CHROMATOGRAM_OVERVIEW_ACTIVE = PATH_PREFIX + "chromatogramOverviewActive.gif";
	String IMAGE_SELECTED_SCANS_DEFAULT = PATH_PREFIX + "selectedScansDefault.gif";
	String IMAGE_SELECTED_SCANS_ACTIVE = PATH_PREFIX + "selectedScansActive.gif";
	String IMAGE_SELECTED_PEAKS_DEFAULT = PATH_PREFIX + "selectedPeaksDefault.gif";
	String IMAGE_SELECTED_PEAKS_ACTIVE = PATH_PREFIX + "selectedPeaksActive.gif";
	String IMAGE_SUBTRACT_SCAN_DEFAULT = PATH_PREFIX + "subtractScanDefault.gif";
	String IMAGE_SUBTRACT_SCAN_ACTIVE = PATH_PREFIX + "subtractScanActive.gif";
	String IMAGE_COMBINED_SCAN_DEFAULT = PATH_PREFIX + "combinedScanDefault.gif";
	String IMAGE_COMBINED_SCAN_ACTIVE = PATH_PREFIX + "combinedScanActive.gif";
	String IMAGE_COMPARISON_SCAN = PATH_PREFIX + "comparisonScan.gif";
	String IMAGE_COMPARISON_SCAN_DEFAULT = PATH_PREFIX + "comparisonScanDefault.gif";
	String IMAGE_COMPARISON_SCAN_ACTIVE = PATH_PREFIX + "comparisonScanActive.gif";
	String IMAGE_QUANTITATION_DEFAULT = PATH_PREFIX + "quantitationDefault.gif";
	String IMAGE_QUANTITATION_ACTIVE = PATH_PREFIX + "quantitationActive.gif";
	String IMAGE_SCAN_PEAK_LIST_DEFAULT = PATH_PREFIX + "scanPeakListDefault.gif";
	String IMAGE_SCAN_PEAK_LIST_ACTIVE = PATH_PREFIX + "scanPeakListActive.gif";
	String IMAGE_HEATMAP_ACTIVE = PATH_PREFIX + "heatmapActive.gif";
	String IMAGE_HEATMAP_DEFAULT = PATH_PREFIX + "heatmapDefault.gif";
	String IMAGE_SEQUENCE_LIST_DEFAULT = PATH_PREFIX + "sequenceListDefault.gif";
	String IMAGE_SEQUENCE_LIST_ACTIVE = PATH_PREFIX + "sequenceListActive.gif";
	String IMAGE_PCR_ACTIVE = PATH_PREFIX + "pcrActive.gif";
	String IMAGE_PCR_DEFAULT = PATH_PREFIX + "pcrDefault.gif";
	String IMAGE_BAR_CHART = PATH_PREFIX + "barchart.png";
	String IMAGE_INTERNAL_STANDARDS_DEFAULT = PATH_PREFIX + "internalStandardsDefault.gif";
	String IMAGE_INTERNAL_STANDARDS_ACTIVE = PATH_PREFIX + "internalStandardsActive.gif";
	String IMAGE_EXTERNAL_STANDARDS_DEFAULT = PATH_PREFIX + "externalStandardsDefault.gif";
	String IMAGE_EXTERNAL_STANDARDS_ACTIVE = PATH_PREFIX + "externalStandardsActive.gif";
	String IMAGE_EDIT_ENTRY = PATH_PREFIX + "editEntry.gif";
	String IMAGE_EDIT_ENTRY_DEFAULT = PATH_PREFIX + "editEntryDefault.gif";
	String IMAGE_EDIT_ENTRY_ACTIVE = PATH_PREFIX + "editEntryActive.gif";
	String IMAGE_CHROMATOGRAM_DEFAULT = PATH_PREFIX + "chromatogramDefault.gif";
	String IMAGE_CHROMATOGRAM_ACTIVE = PATH_PREFIX + "chromatogramActive.gif";
	//
	String IMAGE_PROCESS_CONTROL = PATH_PREFIX + "processControl.gif";
	String IMAGE_START_PROCESSING = PATH_PREFIX + "startProcessing.gif";
	String IMAGE_END_PROCESSING = PATH_PREFIX + "endProcessing.gif";
	//
	String IMAGE_RESULTS = PATH_PREFIX + "measurementResults.gif";
	String IMAGE_MEASUREMENT_RESULTS_DEFAULT = PATH_PREFIX + "measurementResultsDefault.gif";
	String IMAGE_MEASUREMENT_RESULTS_ACTIVE = PATH_PREFIX + "measurementResultsActive.gif";
	//
	String IMAGE_PLATE_PCR = PATH_PREFIX + "plate-pcr.png";
	String IMAGE_METHOD = PATH_PREFIX + "method.gif";
	String IMAGE_METHOD_ADD = PATH_PREFIX + "methodAdd.gif";
	String IMAGE_METHOD_EDIT = PATH_PREFIX + "methodEdit.gif";
	String IMAGE_METHOD_DELETE = PATH_PREFIX + "methodDelete.gif";
	String IMAGE_METHOD_COPY = PATH_PREFIX + "methodCopy.gif";
	//
	String IMAGE_EXECUTE_EXTENSION = PATH_PREFIX + "execute-extension.png";
	String IMAGE_POPUP_MENU = PATH_PREFIX + "popup-menu.png";
	//
	String IMAGE_AUTH = PATH_PREFIX + "auth.png";
	String IMAGE_AUTH_LOCKED = PATH_PREFIX + "auth_locked.gif";
	String IMAGE_AUTH_UNLOCKED = PATH_PREFIX + "auth_unlocked.gif";
	//
	String IMAGE_HEADER_DATA = PATH_PREFIX + "headerdata.gif";
	String IMAGE_CHART_DATA_SHOW = PATH_PREFIX + "chartDataShow.png";
	String IMAGE_CHART_DATA_HIDE = PATH_PREFIX + "chartDataHide.png";
	//
	String IMAGE_PERSON = PATH_PREFIX + "person.gif";
	String IMAGE_PERSON_DEFAULT = PATH_PREFIX + "personDefault.gif";
	String IMAGE_PERSON_ACTIVE = PATH_PREFIX + "personActive.gif";
	//
	String IMAGE_FILE_ADD = PATH_PREFIX + "fileAdd.gif";
	String IMAGE_FOLDER_ADD = PATH_PREFIX + "folderAdd.gif";
	//
	String IMAGE_CHROMATOGRAM_BLANK = PATH_PREFIX + "chromatogramBlank.gif";
	String IMAGE_INTERPOLATE = PATH_PREFIX + "interpolate.png";
	//
	String IMAGE_OFFSET = PATH_PREFIX + "offset.gif";
	String IMAGE_OFFSET_DEFAULT = PATH_PREFIX + "offsetDefault.gif";
	String IMAGE_OFFSET_ACTIVE = PATH_PREFIX + "offsetActive.gif";
	//
	String IMAGE_MERGE = PATH_PREFIX + "merge.gif";
	String IMAGE_LABELS = PATH_PREFIX + "labels.png";
	String IMAGE_PEAK_TRACES = PATH_PREFIX + "peakTraces.gif";
	String IMAGE_COPY_CLIPBOARD = PATH_PREFIX + "copy-clipboard.png";
	String IMAGE_ZOOM_LOCKED = PATH_PREFIX + "zoomLocked.png";
	String IMAGE_ZOOM_IN = PATH_PREFIX + "zoomIn.png";
	String IMAGE_TYPES = PATH_PREFIX + "types.png";
	String IMAGE_VALUE_DECREASE = PATH_PREFIX + "valueDecrease.gif";
	String IMAGE_VALUE_INCREASE = PATH_PREFIX + "valueIncrease.gif";
	//
	String IMAGE_INSTRUMENT = PATH_PREFIX + "instrument.gif";
	String IMAGE_BASELINE_SHOW = PATH_PREFIX + "baselineShow.gif";
	String IMAGE_BASELINE_HIDE = PATH_PREFIX + "baselineHide.gif";
	String IMAGE_CHROMATOGRAM_TIC_SHOW = PATH_PREFIX + "chromatogramTicShow.gif";
	String IMAGE_CHROMATOGRAM_TIC_HIDE = PATH_PREFIX + "chromatogramTicHide.gif";
	String IMAGE_CHROMATOGRAM_XIC_SHOW = PATH_PREFIX + "chromatogramXicShow.gif";
	String IMAGE_CHROMATOGRAM_XIC_HIDE = PATH_PREFIX + "chromatogramXicHide.gif";
	String IMAGE_REVIEW_DETAILS_SHOW = PATH_PREFIX + "reviewDetailsShow.gif";
	String IMAGE_REVIEW_DETAILS_HIDE = PATH_PREFIX + "reviewDetailsHide.gif";
	String IMAGE_PLUGINS = PATH_PREFIX + "plugins.png";
	String IMAGE_XML_FILE = PATH_PREFIX + "xmldoc.gif";
	String IMAGE_ZIP_FILE = PATH_PREFIX + "zip_file.png";
	String IMAGE_TRANSFER = PATH_PREFIX + "transfer.png";
	//
	String IMAGE_FOCUS_TOP = PATH_PREFIX + "focus_top.gif";
	String IMAGE_FOCUS_BOTTOM = PATH_PREFIX + "focus_bottom.gif";
	String IMAGE_FOCUS_BOTH_HORIZONTAL = PATH_PREFIX + "focus_both_horizontal.gif";
	String IMAGE_FOCUS_LEFT = PATH_PREFIX + "focus_left.gif";
	String IMAGE_FOCUS_RIGHT = PATH_PREFIX + "focus_right.gif";
	String IMAGE_FOCUS_BOTH_VERTICAL = PATH_PREFIX + "focus_both_vertical.gif";
	//
	String IMAGE_PEAK_RANGE = PATH_PREFIX + "peak-range.gif";
	String IMAGE_PRINT = PATH_PREFIX + "print.png";
	String IMAGE_CLEAR = PATH_PREFIX + "clear.gif";
	String IMAGE_RULER = PATH_PREFIX + "ruler.gif";
	String IMAGE_SKIP_QUANTIFIED_PEAK = PATH_PREFIX + "skipQuantifiedPeak.gif";
	String IMAGE_DELETE_PEAK = PATH_PREFIX + "delete_peak.gif";
	String IMAGE_DELETE_PEAKS = PATH_PREFIX + "delete_peaks.gif";
	String IMAGE_CROSS_ZERO = PATH_PREFIX + "crossZero.gif";
	String IMAGE_INCLUDE_INTERCEPT = PATH_PREFIX + "includeIntercept.gif";
	String IMAGE_LOWER_MIN_AREA = PATH_PREFIX + "lowerMinArea.gif";
	String IMAGE_HIGHER_MAX_AREA = PATH_PREFIX + "higherMaxArea.gif";
	String IMAGE_GRID = PATH_PREFIX + "grid.png";
	String IMAGE_FILTER = PATH_PREFIX + "filter.png";
	String IMAGE_LOCK_UPDATE = PATH_PREFIX + "lockUpdate.gif";
	String IMAGE_SORT_ALPHA_ASC = PATH_PREFIX + "sort_alpha_asc.png";
	String IMAGE_SORT_ALPHA_DESC = PATH_PREFIX + "sort_alpha_desc.png";
}