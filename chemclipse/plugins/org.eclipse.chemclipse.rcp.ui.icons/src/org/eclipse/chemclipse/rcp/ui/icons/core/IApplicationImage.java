/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

public interface IApplicationImage extends IApplicationImageProvider {

	/*
	 * Icons and Images
	 */
	String IMAGE_ADD = "add.gif";
	String IMAGE_CANCEL = "cancel.gif";
	String IMAGE_DELETE = "delete.gif";
	String IMAGE_EXECUTE_ADD = "execute_add.gif";
	String IMAGE_EDIT = "edit.gif";
	String IMAGE_EDIT_DEFAULT = "editDefault.gif";
	String IMAGE_EDIT_ACTIVE = "editActive.gif";
	String IMAGE_EDIT_DISABLED = "edit_disabled.gif";
	String IMAGE_EDIT_ENTRY = "edit_entry.gif";
	String IMAGE_EDIT_SHIFT = "editShift.gif";
	String IMAGE_EDIT_PROFILE = "editProfile.gif";
	String IMAGE_CHROMATOGRAM_PROFILE = "chromatogramProfile.gif";
	//
	String IMAGE_EMPTY = "empty.png";
	String IMAGE_LOGO = "logo.png";
	String IMAGE_ABOUT = "about.png";
	//
	String IMAGE_DESELECTED = "deselected.gif";
	String IMAGE_DESELECTED_INACTIVE = "deselected_inactive.gif";
	String IMAGE_SELECTED = "selected.gif";
	String IMAGE_CHECK_ALL = "checkAll.gif";
	String IMAGE_UNCHECK_ALL = "uncheckAll.gif";
	//
	String IMAGE_SHRINK_CHROMATOGRAMS = "shrinkChromatograms.gif";
	String IMAGE_ALIGN_CHROMATOGRAMS = "alignChromatograms.gif";
	String IMAGE_STRETCH_CHROMATOGRAMS = "stretchChromatograms.gif";
	String IMAGE_ADJUST_CHROMATOGRAMS = "adjustChromatograms.gif";
	//
	String IMAGE_CHROMATOGRAM = "chromatogram.gif";
	String IMAGE_CHROMATOGRAM_MSD = "chromatogram-msd.gif";
	String IMAGE_CHROMATOGRAM_CSD = "chromatogram-csd.gif";
	String IMAGE_CHROMATOGRAM_WSD = "chromatogram-wsd.gif";
	String IMAGE_SCAN_XIR = "scan-xir.gif"; // FTIR, NIR, ...
	String IMAGE_SCAN_NMR = "scan-nmr.gif";
	String IMAGE_SCAN_FID = "scan-fid.png";
	//
	String IMAGE_SAMPLE = "sample.gif";
	String IMAGE_SAMPLE_ISTD = "sample-istd.gif";
	//
	String IMAGE_PREFERENCES = "preferences.gif";
	String IMAGE_DRIVE = "drive.gif";
	String IMAGE_CHROMATOGRAM_ZERO_SET = "chromatogramZeorSet.gif";
	String IMAGE_FOLDER_OPENED = "folder_opened.gif";
	String IMAGE_FOLDER_CLOSED = "folder_closed.gif";
	String IMAGE_FILE = "file.gif";
	String IMAGE_PEAK = "peak.gif";
	String IMAGE_PEAKS = "peaks.gif";
	String IMAGE_WARN = "warn.gif";
	String IMAGE_INFO = "info.gif";
	String IMAGE_QUESTION = "question.gif";
	String IMAGE_MASS_SPECTRUM = "massSpectrum.gif";
	String IMAGE_MASS_SPECTRUM_FILE = "massSpectrumFile.gif";
	String IMAGE_MASS_SPECTRUM_DATABASE = "massSpectrumDatabase.gif";
	String IMAGE_TARGETS = "targets.gif";
	String IMAGE_CREATE_SNAPSHOT = "create_snapshot.png";
	String IMAGE_PERSPECTIVES = "perspectives.gif";
	String IMAGE_QUIT = "quit.gif";
	String IMAGE_SAVE = "save.gif";
	String IMAGE_SAVE_DISABLED = "save_disabled.gif";
	String IMAGE_SAVE_AS = "saveas.gif";
	String IMAGE_SAVE_AS_DISABLED = "saveas_disabled.gif";
	String IMAGE_SAVEALL = "saveall.gif";
	String IMAGE_SAVEALL_DISABLED = "saveall_disabled.gif";
	String IMAGE_VIEW = "view.gif";
	String IMAGE_ERROR = "error.gif";
	String IMAGE_UNKNOWN = "unknown.gif";
	String IMAGE_LOG = "log.gif";
	String IMAGE_ION = "ion.gif";
	String IMAGE_REPORT = "report.gif";
	String IMAGE_CHROMATOGRAM_REPORT = "chromatogramReport.gif";
	String IMAGE_CHROMATOGRAM_REPORT_ADD = "chromatogramReportAdd.gif";
	String IMAGE_BATCHPROCESS = "batchprocess.gif";
	String IMAGE_BATCHPROCESS_PEAKIDENT = "batchprocessPeakIdent.gif";
	String IMAGE_CONFIGURE = "configure.gif";
	String IMAGE_EXECUTE = "execute.gif";
	String IMAGE_EXECUTE_WARNING = "execute_warning.png";
	String IMAGE_EXECUTE_ERROR = "execute_error.png";
	String IMAGE_EXECUTE_AUTO_UPDATE = "execute_auto_update.png";
	String IMAGE_PEAK_MANUAL = "peak-manual.gif";
	String IMAGE_PEAK_DETECTOR = "peakdetector.gif";
	String IMAGE_PEAK_DETECTOR_DECONVOLUTION = "peakdetectorDeconvolution.gif";
	String IMAGE_DECONVOLUTION = "deconvolution.gif";
	String IMAGE_INTEGRATION_RESULTS = "integrationResults.gif";
	String IMAGE_INTEGRATOR_SUMAREA = "sumareaIntegrator.gif";
	String IMAGE_PEAK_INTEGRATOR_MAX = "peakIntegratorMax.gif";
	String IMAGE_PEAK_INTEGRATOR = "peakIntegrator.gif";
	String IMAGE_CHROMATOGRAM_INTEGRATOR = "chromatogramIntegrator.gif";
	String IMAGE_DELETE_PEAK_INTEGRATIONS = "deletePeakIntegrations.gif";
	String IMAGE_DELETE_CHROMATOGRAM_INTEGRATIONS = "deleteChromatogramIntegrations.gif";
	String IMAGE_COMBINED_INTEGRATOR = "combinedIntegrator.gif";
	//
	String IMAGE_CLASSIFIER = "classifier.gif";
	String IMAGE_CLASSIFIER_WNC = "wnc.gif";
	String IMAGE_CLASSIFIER_DW = "durbin_watson.gif";
	//
	String IMAGE_FILTER_BACKFOLDING = "backfolding.gif";
	String IMAGE_FILTER_CODA = "coda.gif";
	String IMAGE_FILTER_DENOISING = "denoising.gif";
	String IMAGE_FILTER_IONREMOVER = "ionremover.gif";
	String IMAGE_WORD_DOCUMENT = "word_document.gif";
	String IMAGE_EXCEL_DOCUMENT = "excel_document.gif";
	String IMAGE_BACKWARD = "backward.gif";
	String IMAGE_FAST_BACKWARD = "fastbackward.gif";
	String IMAGE_FORWARD = "forward.gif";
	String IMAGE_FAST_FORWARD = "fastforward.gif";
	String IMAGE_FILTER_SAVITZKY_GOLAY = "savitzkygolay.gif";
	String IMAGE_CHROMATOGRAM_FILE_EXPLORER = "chromatogramFileExplorer.gif";
	String IMAGE_MASS_SPECTRUM_FILE_EXPLORER = "massSpectrumFileExplorer.gif";
	String IMAGE_MASS_SPECTRUM_LIBRARY = "massSpectrumLibrary.gif";
	String IMAGE_MIRRORED_MASS_SPECTRUM = "mirroredMassSpectrum.gif";
	String IMAGE_SHIFTED_MASS_SPECTRUM = "shiftedMassSpectrum.gif";
	String IMAGE_IMPORT = "import.png";
	String IMAGE_EXPORT = "export.png";
	String IMAGE_GROOVY_EXECUTE = "groovy_execute.gif";
	String IMAGE_GROOVY_CREATE = "groovy_create.gif";
	String IMAGE_JYTHON_EXECUTE = "jython_execute.gif";
	String IMAGE_JYTHON_CREATE = "jython_create.gif";
	String IMAGE_BASELINE = "baseline.gif";
	String IMAGE_BASELINE_SNIP = "baselineSnip.gif";
	String IMAGE_BASELINE_DELETE = "baselineDelete.gif";
	String IMAGE_FILTER_SNIP_SELECTED_PEAK = "snipMassSpectrumPeak.gif";
	String IMAGE_FILTER_SNIP_ALL_PEAKS = "snipMassSpectrumPeaks.gif";
	String IMAGE_FILTER_IONREMOVER_SELECTED_PEAK = "ionremoverMassSpectrumPeak.gif";
	String IMAGE_FILTER_IONREMOVER_ALL_PEAKS = "ionremoverMassSpectrumPeaks.gif";
	//
	String IMAGE_QUANTITATION_RESULTS = "integrationResults.gif";
	String IMAGE_QUANTIFY_SELECTED_PEAK = "quantifySelectedPeak.gif";
	String IMAGE_QUANTIFY_ALL_PEAKS = "quantifyAllPeaks.gif";
	String IMAGE_ADD_PEAK_TO_QUANTITATION_TABLE = "addPeakToQuantitationTable.gif";
	String IMAGE_ADD_PEAKS_TO_QUANTITATION_TABLE = "addPeaksToQuantitationTable.gif";
	//
	String IMAGE_MANUAL_PEAK_IDENTIFIER = "peakIdentifierManual.gif";
	String IMAGE_SUBTRACT_MASS_SPECTRUM = "subtractMassSpectrum.gif";
	//
	String IMAGE_SUBTRACT_MASS_SPECTRUM_PEAK = "subtractMassSpectrumPeak.gif";
	String IMAGE_SUBTRACT_MASS_SPECTRUM_PEAKS = "subtractMassSpectrumPeaks.gif";
	String IMAGE_SUBTRACT_ADD_COMBINED_SCAN = "subtractFilterAddCombinedScan.gif";
	String IMAGE_SUBTRACT_ADD_SELECTED_SCAN = "subtractFilterAddSelectedScan.gif";
	String IMAGE_SUBTRACT_CLEAR_SESSION_MASS_SPECTRUM = "subtractFilterClearSessionMassSpectrum.gif";
	String IMAGE_SUBTRACT_LOAD_SESSION_MASS_SPECTRUM = "subtractFilterLoadSessionMassSpectrum.gif";
	String IMAGE_SUBTRACT_STORE_SESSION_MASS_SPECTRUM = "subtractFilterStoreSessionMassSpectrum.gif";
	//
	String IMAGE_SELECTED_SCAN = "selectedScan.gif";
	String IMAGE_SELECTED_PEAK = "selectedPeak.gif";
	//
	String IMAGE_IMPORT_CHROMATOGRAM_MSD = "importChromatogramMSD.gif";
	String IMAGE_IMPORT_CHROMATOGRAM_CSD = "importChromatogramCSD.gif";
	String IMAGE_IMPORT_CHROMATOGRAM_WSD = "importChromatogramWSD.gif";
	//
	String IMAGE_DATABASE = "database.gif";
	String IMAGE_ION_TRANSITION = "ionTransition.gif";
	//
	String IMAGE_UPDATES = "updates.gif";
	String IMAGE_MARKETPLACE = "marketplace.gif";
	//
	String IMAGE_PCA = "pca.gif";
	//
	String IMAGE_CDK_PEAK = "cdkPeak.gif";
	String IMAGE_CDK_PEAKS = "cdkPeaks.gif";
	String IMAGE_CDK_DELETE = "cdkDelete.gif";
	//
	String IMAGE_LOCK_OFFSET = "lockOffset.gif";
	String IMAGE_UNLOCK_OFFSET = "unlockOffset.gif";
	//
	String IMAGE_PIN_CHROMATOGRAM = "pinChromatogram.gif";
	String IMAGE_UNPIN_CHROMATOGRAM = "unpinChromatogram.gif";
	String IMAGE_PIN_MASS_SPECTRUM = "pinMassSpectrum.gif";
	String IMAGE_UNPIN_MASS_SPECTRUM = "unpinMassSpectrum.gif";
	String IMAGE_RETENION_INDEX = "retentionIndex.gif";
	//
	String IMAGE_IDENTIFY_PEAK = "identify_peak.gif";
	String IMAGE_IDENTIFY_PEAKS = "identify_peaks.gif";
	String IMAGE_IDENTIFY_MASS_SPECTRUM = "identify_massspectrum.gif";
	//
	String IMAGE_FILTER_MEAN_NORMALIZER = "normalizerMean.gif";
	String IMAGE_FILTER_MEDIAN_NORMALIZER = "normalizerMedian.gif";
	String IMAGE_FILTER_UNITSUM_NORMALIZER = "normalizerUnitSum.gif";
	String IMAGE_FILTER_NORMALIZER = "normalizer.gif";
	String IMAGE_FILTER_SCANREMOVER = "scanremover.gif";
	//
	String IMAGE_CHROMATOGRAM_OVERLAY_SUBTRACT = "chromatogramOverlaySubtract.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY_MIRRORED = "chromatogramOverlayMirrored.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY = "chromatogramOverlay.gif";
	//
	String IMAGE_RESET = "reset.gif";
	String IMAGE_OFFSET_LEFT = "offsetLeft.gif";
	String IMAGE_OFFSET_LEFT_FAST = "offsetLeftFast.gif";
	String IMAGE_OFFSET_RIGHT = "offsetRight.gif";
	String IMAGE_OFFSET_RIGHT_FAST = "offsetRightFast.gif";
	String IMAGE_OFFSET_UP = "offsetUp.gif";
	String IMAGE_OFFSET_DOWN = "offsetDown.gif";
	//
	String IMAGE_NEXT = "next.gif";
	String IMAGE_PREVIOUS = "previous.gif";
	String IMAGE_NEXT_YELLOW = "nextYellow.gif";
	String IMAGE_PREVIOUS_YELLOW = "previousYellow.gif";
	//
	String IMAGE_SCRIPT_SHELL = "script_shell.png";
	//
	String IMAGE_NIST_MASS_SPECTRUM = "nist_massSpectrum.gif";
	String IMAGE_NIST_OPEN_MASS_SPECTRUM = "nist_open_massSpectrum.gif";
	String IMAGE_NIST_OPEN_PEAK = "nist_open_peak.gif";
	String IMAGE_NIST_OPEN_PEAKS = "nist_open_peaks.gif";
	String IMAGE_NIST_OPEN = "nist_open.gif";
	String IMAGE_NIST_PEAK = "nist_peak.gif";
	String IMAGE_NIST_PEAKS = "nist_peaks.gif";
	String IMAGE_NIST = "nist.gif";
	//
	String IMAGE_LIBRARY_DOCUMENT = "libraryDocument.gif";
	String IMAGE_ORIGIN_DOCUMENT = "originDocument.gif";
	String IMAGE_REFERENCE_PEAK = "referencePeak.gif";
	String IMAGE_CHROMATOGRAM_DATABASE = "chromatogramDatabase.gif";
	String IMAGE_CHROMATOGRAM_DATABASE_ADD = "chromatogramDatabaseAdd.gif";
	String IMAGE_SUBSTANCE_HIT_RESULT = "substanceHitResult.gif";
	String IMAGE_PLUS = "plus.gif";
	String IMAGE_MINUS = "minus.gif";
	//
	String IMAGE_ARROW_DOWN_2 = "arrow_down_2.gif";
	String IMAGE_ARROW_UP_2 = "arrow_up_2.gif";
	//
	String IMAGE_ARROW_DOWN = "arrow_down.gif";
	String IMAGE_ARROW_UP = "arrow_up.gif";
	String IMAGE_ARROW_EQUAL = "arrow_equal.gif";
	//
	String IMAGE_ARROW_FORWARD = "arrowForward.gif";
	String IMAGE_ARROW_BACKWARD = "arrowBackward.gif";
	String IMAGE_EXCEL = "excel.gif";
	String IMAGE_PDF = "pdf.gif";
	String IMAGE_TXT = "txt.gif";
	String IMAGE_CALCULATE = "calculate.gif";
	String IMAGE_CHECK = "check.gif";
	String IMAGE_SELECT_ROWS = "select-rows.gif";
	String IMAGE_REMOVE = "remove.gif";
	String IMAGE_STATUS_EMPTY = "status-empty.gif";
	String IMAGE_STATUS_OK = "status-ok.gif";
	String IMAGE_STATUS_WARN = "status-warn.gif";
	String IMAGE_STATUS_ERROR = "status-error.gif";
	//
	String IMAGE_DETECTION_BOX_BOTH = "detectionBoxBoth.gif";
	String IMAGE_DETECTION_BOX_LEFT = "detectionBoxLeft.gif";
	String IMAGE_DETECTION_BOX_RIGHT = "detectionBoxRight.gif";
	//
	String IMAGE_DETECTION_TYPE_BASELINE = "detectionTypeBaseline.gif";
	String IMAGE_DETECTION_TYPE_SCAN_BB = "detectionTypeScanBB.gif";
	String IMAGE_DETECTION_TYPE_SCAN_BV = "detectionTypeScanBV.gif";
	String IMAGE_DETECTION_TYPE_SCAN_VB = "detectionTypeScanVB.gif";
	String IMAGE_DETECTION_TYPE_SCAN_VV = "detectionTypeScanVV.gif";
	String IMAGE_DETECTION_TYPE_TANGENT = "detectionTypeTangent.gif";
	String IMAGE_DETECTION_TYPE_PERPENDICULAR = "detectionTypePerpendicular.gif";
	//
	String IMAGE_EXPAND_ALL = "expand_all.gif";
	String IMAGE_COLLAPSE_ALL = "collapse_all.gif";
	String IMAGE_SEARCH = "search.gif";
	String IMAGE_EVALUATE = "evaluate.gif";
	String IMAGE_EVALUATED = "evaluated.gif";
	String IMAGE_VALIDATE = "validate.gif";
	String IMAGE_SKIP = "skip.gif";
	String IMAGE_SKIPPED = "skipped.gif";
	String IMAGE_TAG = "tag.gif";
	String IMAGE_SHIFT_Y = "shiftY.gif";
	String IMAGE_SHIFT_XY = "shiftXY.gif";
	String IMAGE_SHIFT_AUTO_MIRROR = "shiftAutoMirror.gif";
	String IMAGE_SHIFT_MIRROR = "shiftMirror.gif";
	String IMAGE_SCAN_RETENTION_TIME = "scanRetentionTime.gif";
	String IMAGE_CHART_LEGEND_MARKER = "chartLegendMarker.gif";
	String IMAGE_CHART_RANGE_SELECTOR = "chartRangeSelector.gif";
	String IMAGE_SEQUENCE_LIST = "sequenceListDefault.gif";
	/*
	 * PICTOGRAM and Data Analysis Perspective
	 */
	String PICTOGRAM_DATA_ANALYSIS = "DataAnalysis.png";
	String IMAGE_CHROMATOGRAM_OVERLAY_DEFAULT = "chromatogramOverlayDefault.gif";
	String IMAGE_CHROMATOGRAM_OVERLAY_ACTIVE = "chromatogramOverlayActive.gif";
	String IMAGE_CHROMATOGRAM_OVERVIEW_DEFAULT = "chromatogramOverviewDefault.gif";
	String IMAGE_CHROMATOGRAM_OVERVIEW_ACTIVE = "chromatogramOverviewActive.gif";
	String IMAGE_SELECTED_SCANS_DEFAULT = "selectedScansDefault.gif";
	String IMAGE_SELECTED_SCANS_ACTIVE = "selectedScansActive.gif";
	String IMAGE_SELECTED_PEAKS_DEFAULT = "selectedPeaksDefault.gif";
	String IMAGE_SELECTED_PEAKS_ACTIVE = "selectedPeaksActive.gif";
	String IMAGE_SUBTRACT_SCAN_DEFAULT = "subtractScanDefault.gif";
	String IMAGE_SUBTRACT_SCAN_ACTIVE = "subtractScanActive.gif";
	String IMAGE_COMBINED_SCAN_DEFAULT = "combinedScanDefault.gif";
	String IMAGE_COMBINED_SCAN_ACTIVE = "combinedScanActive.gif";
	String IMAGE_COMPARISON_SCAN_DEFAULT = "comparisonScanDefault.gif";
	String IMAGE_COMPARISON_SCAN_ACTIVE = "comparisonScanActive.gif";
	String IMAGE_QUANTITATION_DEFAULT = "quantitationDefault.gif";
	String IMAGE_QUANTITATION_ACTIVE = "quantitationActive.gif";
	String IMAGE_SCAN_PEAK_LIST_DEFAULT = "scanPeakListDefault.gif";
	String IMAGE_SCAN_PEAK_LIST_ACTIVE = "scanPeakListActive.gif";
	String IMAGE_HEATMAP_ACTIVE = "heatmapActive.gif";
	String IMAGE_HEATMAP_DEFAULT = "heatmapDefault.gif";
	String IMAGE_SEQUENCE_LIST_DEFAULT = "sequenceListDefault.gif";
	String IMAGE_SEQUENCE_LIST_ACTIVE = "sequenceListActive.gif";
	String IMAGE_PCR_ACTIVE = "pcrActive.gif";
	String IMAGE_PCR_DEFAULT = "pcrDefault.gif";
	String IMAGE_INTERNAL_STANDARDS_DEFAULT = "internalStandardsDefault.gif";
	String IMAGE_INTERNAL_STANDARDS_ACTIVE = "internalStandardsActive.gif";
	String IMAGE_EXTERNAL_STANDARDS_DEFAULT = "externalStandardsDefault.gif";
	String IMAGE_EXTERNAL_STANDARDS_ACTIVE = "externalStandardsActive.gif";
	String IMAGE_EDIT_ENTRY_DEFAULT = "editEntryDefault.gif";
	String IMAGE_EDIT_ENTRY_ACTIVE = "editEntryActive.gif";
	//
	String IMAGE_PROCESS_CONTROL = "processControl.gif";
	String IMAGE_START_PROCESSING = "startProcessing.gif";
	String IMAGE_END_PROCESSING = "endProcessing.gif";
	//
	String IMAGE_MEASUREMENT_RESULTS_DEFAULT = "measurementResultsDefault.gif";
	String IMAGE_MEASUREMENT_RESULTS_ACTIVE = "measurementResultsActive.gif";
	//
	String IMAGE_PLATE_PCR = "plate-pcr.gif";
	String IMAGE_METHOD = "method.gif";
	String IMAGE_METHOD_ADD = "methodAdd.gif";
	String IMAGE_METHOD_EDIT = "methodEdit.gif";
	String IMAGE_METHOD_DELETE = "methodDelete.gif";
	//
	String IMAGE_EXECUTE_EXTENSION = "execute-extension.png";
	String IMAGE_POPUP_MENU = "popup-menu.png";
	//
	String IMAGE_AUTH = "auth.png";
	String IMAGE_AUTH_LOCKED = "auth_locked.gif";
	String IMAGE_AUTH_UNLOCKED = "auth_unlocked.gif";
	//
	String IMAGE_HEADER_DATA = "headerdata.gif";
	String IMAGE_CHART_DATA_SHOW = "chartDataShow.png";
	String IMAGE_CHART_DATA_HIDE = "chartDataHide.png";
	//
	String IMAGE_PERSON_DEFAULT = "personDefault.gif";
	String IMAGE_PERSON_ACTIVE = "personActive.gif";
	//
	String IMAGE_FILE_ADD = "fileAdd.gif";
	String IMAGE_FOLDER_ADD = "folderAdd.gif";
}