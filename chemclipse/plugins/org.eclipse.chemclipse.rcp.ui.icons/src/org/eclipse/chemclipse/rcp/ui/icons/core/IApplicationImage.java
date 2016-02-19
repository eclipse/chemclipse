/*******************************************************************************
 * Copyright (c) 2013, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

public interface IApplicationImage extends IApplicationImageProvider {

	/*
	 * Icons and Images
	 */
	String IMAGE_EMPTY = "empty.png";
	String IMAGE_LOGO = "logo.png";
	String IMAGE_ABOUT = "about.png";
	//
	String IMAGE_DESELECTED = "deselected.gif";
	String IMAGE_SELECTED = "selected.gif";
	String IMAGE_CHECK_ALL = "checkAll.gif";
	String IMAGE_UNCHECK_ALL = "uncheckAll.gif";
	//
	String IMAGE_PREFERENCES = "preferences.gif";
	String IMAGE_DRIVE = "drive.gif";
	String IMAGE_CHROMATOGRAM = "chromatogram.gif";
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
	String IMAGE_BATCHPROCESS = "batchprocess.gif";
	String IMAGE_BATCHPROCESS_PEAKIDENT = "batchprocessPeakIdent.gif";
	String IMAGE_CONFIGURE = "configure.gif";
	String IMAGE_EXECUTE = "execute.gif";
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
	String IMAGE_OFFSET_RIGHT = "offsetRight.gif";
	String IMAGE_OFFSET_UP = "offsetUp.gif";
	String IMAGE_OFFSET_DOWN = "offsetDown.gif";
	//
	String IMAGE_NEXT = "next.gif";
	String IMAGE_PREVIOUS = "previous.gif";
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
	String IMAGE_SUBSTANCE_HIT_RESULT = "substanceHitResult.gif";
	String IMAGE_PLUS = "plus.gif";
	String IMAGE_MINUS = "minus.gif";
	//
	String IMAGE_ARROW_DOWN = "arrow_down.gif";
	String IMAGE_ARROW_UP = "arrow_up.gif";
	String IMAGE_ARROW_EQUAL = "arrow_equal.gif";
}
