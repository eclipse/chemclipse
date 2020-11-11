/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * chemclipse - initial API and implementation
 * Christoph Läubrich - remove dependency on IEventbroker
 *******************************************************************************/
package org.eclipse.chemclipse.support.events;

public interface IChemClipseEvents {

	// copied from IEventBroker to prevent dependency on E4
	public static String IEVENTBROKER_DATA = "org.eclipse.e4.data";
	/*
	 * Properties
	 */
	String PROPERTY_PERSPECTIVE_NAME = IEVENTBROKER_DATA; // String
	String PROPERTY_VIEW_NAME = IEVENTBROKER_DATA; // String
	//
	String PROPERTY_CHROMATOGRAM_MSD_RAWFILE = IEVENTBROKER_DATA; // File
	String PROPERTY_CHROMATOGRAM_CSD_RAWFILE = IEVENTBROKER_DATA; // File
	String PROPERTY_CHROMATOGRAM_WSD_RAWFILE = IEVENTBROKER_DATA; // File
	String PROPERTY_CHROMATOGRAM_XXD_RAWFILE = IEVENTBROKER_DATA; // File
	String PROPERTY_SCAN_NMR_RAWFILE = IEVENTBROKER_DATA; // File
	String PROPERTY_SCAN_XIR_RAWFILE = IEVENTBROKER_DATA; // File
	//
	String PROPERTY_CHROMATOGRAM_MSD_OVERVIEW = IEVENTBROKER_DATA; // IChromatogramOverview - no map
	String PROPERTY_CHROMATOGRAM_CSD_OVERVIEW = IEVENTBROKER_DATA; // IChromatogramOverview - no map
	String PROPERTY_CHROMATOGRAM_WSD_OVERVIEW = IEVENTBROKER_DATA; // IChromatogramOverview - no map
	String PROPERTY_SCAN_NMR_OVERVIEW = IEVENTBROKER_DATA;
	String PROPERTY_SCAN_XIR_OVERVIEW = IEVENTBROKER_DATA;
	//
	String PROPERTY_CHROMATOGRAM_SELECTION = "ChromatogramSelection"; // IChromatogramSelection (MSD, CSD, WSD)
	String PROPERTY_CHROMATOGRAM_SELECTION_XXD = IEVENTBROKER_DATA; // IChromatogramSelection
	String PROPERTY_LIBRARY_SELECTION = IEVENTBROKER_DATA; // IMassSpectra
	String PROPERTY_SCAN_SELECTION = IEVENTBROKER_DATA; // IScanMSD, ...
	String PROPERTY_PEAK_SELECTION = IEVENTBROKER_DATA; // IPeakMSD, ...
	//
	String PROPERTY_CHROMATOGRAM_OVERVIEW = "ChromatogramOverview"; // IChromatogramOverview
	String PROPERTY_CHROMATOGRAM_PEAK_MSD = "ChromatogramPeakMSD"; // IChromatogramPeakMSD
	String PROPERTY_CHROMATOGRAM_MSD = "ChromatogramMSD"; // IChromatogramMSD
	String PROPERTY_PEAK_MSD = "PeakMSD"; // IPeakMSD
	String PROPERTY_PEAKS_MSD = "PeaksMSD"; // IPeaksMSD
	String PROPERTY_MASSPECTRUM = "MassSpectrum"; // IMassSpectrum
	//
	String PROPERTY_CHROMATOGRAM_CSD = "ChromatogramCSD"; // IChromatogramCSD
	String PROPERTY_CHROMATOGRAM_PEAK_CSD = "ChromatogramPeakCSD"; // IChromatogramPeakCSD
	String PROPERTY_PEAK_CSD = "PeakCSD"; // IPeakCSD
	String PROPERTY_PEAKS_CSD = "PeaksCSD"; // IPeaksCSD
	//
	String PROPERTY_SELECTED_ION = IEVENTBROKER_DATA; // double ion
	String PROPERTY_SELECTED_SCAN = IEVENTBROKER_DATA; // IScan
	String PROPERTY_SELECTED_PEAK = IEVENTBROKER_DATA; // IPeak
	//
	String PROPERTY_FORCE_RELOAD = "ForceReload";
	String PROPERTY_PROCESSING_INFO = IEVENTBROKER_DATA;
	//
	String PROPERTY_IDENTIFICATION_TARGET = IEVENTBROKER_DATA;
	String PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN = "MassSpectrumUnknown";
	String PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_LIBRARY = "MassSpectrumLibrary";
	String PROPERTY_IDENTIFICATION_TARGET_ENTRY = "IdentificationTarget";
	//
	String PROPERTY_QUANT_DB_COMPOUND = IEVENTBROKER_DATA; // IQuantitationCompound
	/*
	 * Topics
	 */
	String TOPIC_APPLICATION_SELECT_PERSPECTIVE = "application/select/perspective";
	String TOPIC_APPLICATION_SELECT_VIEW = "application/select/view";
	//
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE = "chromatogram/msd/update/rawfile";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE = "chromatogram/csd/update/rawfile";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE = "chromatogram/wsd/update/rawfile";
	String TOPIC_SCAN_XIR_UPDATE_RAWFILE = "scan/xir/update/rawfile";
	String TOPIC_SCAN_NMR_UPDATE_RAWFILE = "scan/nmr/update/rawfile";
	String TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE = "chromatogram/xxd/update/none";
	String TOPIC_SCAN_XXD_UPDATE_NONE = "scan/xxd/update/none";
	String TOPIC_PLATE_PCR_UPDATE_RAWFILE = "plate/pcr/update/rawfile";
	String TOPIC_SEQUENCE_UPDATE_RAWFILE = "sequence/update/rawfile";
	String TOPIC_METHOD_UPDATE_RAWFILE = "method/update/rawfile";
	String TOPIC_QUANTIATION_DATABASE_UPDATE_RAWFILE = "quantitation/database/update/rawfile";
	/*
	 * UNLOAD
	 * Close the chromatogram editor.
	 */
	String TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION = "chromatogram/xxd/load/chromatogramselection"; // DATA
	String TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION = "chromatogram/xxd/unload/chromatogramselection";
	String TOPIC_SCAN_XXD_UPDATE_SELECTION = "scan/xxd/update/selection";
	String TOPIC_SCAN_XXD_UNLOAD_SELECTION = "scan/xxd/unload/selection";
	String TOPIC_PEAK_XXD_UPDATE_SELECTION = "peak/xxd/update/selection";
	String TOPIC_PEAK_XXD_UNLOAD_SELECTION = "peak/xxd/unload/selection";
	/*
	 * Detector: MSD
	 */
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW = "chromatogram/msd/update/overview";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION = "chromatogram/msd/update/chromatogramselection";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_AND_PEAK_SELECTION = "chromatogram/msd/update/chromatogramandpeakselection";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM = "chromatogram/msd/update/chromatogram";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK = "chromatogram/msd/update/peak";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAKS = "chromatogram/msd/update/peaks";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRUM = "chromatogram/msd/update/massspectrum";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_MASSSPECTRA = "chromatogram/msd/update/massspectra";
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_ION_SELECTION = "chromatogram/msd/update/ionselection";
	String TOPIC_FILE_MSD_UPDATE_SELECTION = "file/msd/update/selection";
	String TOPIC_FILE_MSD_UNLOAD_SELECTION = "file/msd/unload/selection";
	String TOPIC_LIBRARY_MSD_UPDATE_SELECTION = "library/msd/update/selection";
	String TOPIC_LIBRARY_MSD_UNLOAD_SELECTION = "library/msd/unload/selection";
	String TOPIC_SCAN_MSD_UPDATE_SELECTION = "scan/msd/update/selection";
	String TOPIC_PEAK_MSD_UPDATE_SELECTION = "peak/msd/update/selection";
	//
	String TOPIC_SCAN_MSD_UPDATE_COMPARISON = "scan/msd/update/comparison";
	String PROPERTY_REFERENCE_MS = "referenceMS";
	String PROPERTY_COMPARISON_MS = "comparisonMS";
	/*
	 * Detector: CSD (conductivity selective)
	 */
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW = "chromatogram/csd/update/overview";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION = "chromatogram/csd/update/chromatogramselection";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_AND_PEAK_SELECTION = "chromatogram/csd/update/chromatogramandpeakselection";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM = "chromatogram/csd/update/chromatogram";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK = "chromatogram/csd/update/peak";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAKS = "chromatogram/csd/update/peaks";
	String TOPIC_SCAN_CSD_UPDATE_SELECTION = "scan/csd/update/selection";
	String TOPIC_PEAK_CSD_UPDATE_SELECTION = "peak/csd/update/selection";
	/*
	 * Detector: WSD (wavelength selective)
	 */
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW = "chromatogram/wsd/update/overview";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION = "chromatogram/wsd/update/chromatogramselection";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM = "chromatogram/wsd/update/chromatogram";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAK = "chromatogram/wsd/update/peak";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAKS = "chromatogram/wsd/update/peaks";
	String TOPIC_SCAN_WSD_UPDATE_SELECTION = "scan/wsd/update/selection";
	String TOPIC_PEAK_WSD_UPDATE_SELECTION = "peak/wsd/update/selection";
	//
	String TOPIC_PROCESSING_INFO_UPDATE = "processinginfo/update";
	String TOPIC_IDENTIFICATION_TARGET_UPDATE = "identification/target/update";
	String TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE = "identification/target/update/massspectrum/unknown";
	String TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_LIBRARY_UPDATE = "identification/target/update/massspectrum/library";
	/*
	 * FTIR, NMR, PCR
	 */
	String TOPIC_SCAN_XIR_UPDATE_OVERVIEW = "scan/xir/update/overview";
	String TOPIC_SCAN_NMR_UPDATE_OVERVIEW = "scan/nmr/update/overview";
	String TOPIC_SCAN_XIR_UPDATE_SELECTION = "scan/xir/update/selection";
	String TOPIC_SCAN_NMR_UPDATE_SELECTION = "scan/nmr/update/selection";
	String TOPIC_SCAN_XIR_UNLOAD_SELECTION = "scan/xir/unload/selection";
	String TOPIC_SCAN_NMR_UNLOAD_SELECTION = "scan/nmr/unload/selection";
	String TOPIC_SEQUENCE_UPDATE_OVERVIEW = "sequence/update/overview";
	String TOPIC_METHOD_UPDATE_OVERVIEW = "method/update/overview";
	String TOPIC_QUANTIATION_DATABASE_UPDATE_OVERVIEW = "quantitation/database/update/overview";
	String TOPIC_PLATE_PCR_UPDATE_OVERVIEW = "plate/pcr/update/overview";
	String TOPIC_PLATE_PCR_UPDATE_SELECTION = "plate/pcr/update/selection";
	String TOPIC_PLATE_PCR_UNLOAD_SELECTION = "plate/pcr/unload/selection";
	String TOPIC_WELL_PCR_UPDATE_SELECTION = "well/pcr/update/selection";
	String TOPIC_WELL_PCR_UNLOAD_SELECTION = "well/pcr/unload/selection";
	/*
	 * QUANTITATION DATABASE
	 */
	String TOPIC_QUANT_DB_COMPOUND_UPDATE = "quantitation/database/compound/update";
	String TOPIC_QUANT_DB_COMPOUND_UNLOAD = "quantitation/database/compound/unload";
	/*
	 * Converter
	 */
	String TOPIC_CHROMATOGRAM_CONVERTER = "chromatogram/update/converter/status";
	String PROPERTY_CHROMATOGRAM_CONVERTER_INFO = IEVENTBROKER_DATA; // String
	/*
	 * Event Broker: Edit History
	 */
	String PROPERTY_EDIT_HISTORY = IEVENTBROKER_DATA; // IEditHistory
	String TOPIC_EDIT_HISTORY_UPDATE = "edithistory/update"; // $NON-NLS-1$
	//
	String PROPERTY_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM = IEVENTBROKER_DATA; // Always true
	String TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM = "filter/supplier/subtract/update/session/subtractmassspectrum";
	/*
	 * MSD Library
	 */
	String PROPERTY_DB_SEARCH_LIBRARY = IEVENTBROKER_DATA; // File
	String TOPIC_LIBRARY_MSD_ADD_TO_DB_SEARCH = "library/msd/add/dbsearch";
	String TOPIC_LIBRARY_MSD_REMOVE_FROM_DB_SEARCH = "library/msd/remove/dbsearch";
	/*
	 * RI Library
	 */
	String PROPERTY_RI_CALIBRATION_LIBRARY = IEVENTBROKER_DATA; // File
	String TOPIC_RI_LIBRARY_ADD_ADD_TO_PROCESS = "ri/library/add/process";
	String TOPIC_RI_LIBRARY_REMOVE_FROM_PROCESS = "ri/library/remove/process";
	/*
	 * IIdenticiation Targets
	 */
	String TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION = "identification/targets/update/selection";
	String TOPIC_IDENTIFICATION_TARGETS_UNLOAD_SELECTION = "identification/targets/unload/selection";
	String PROPERTY_SELECTED_IDENTIFICATION_TARGET_SUPPLIER = IEVENTBROKER_DATA; // ITargetSupplier
	String TOPIC_METHOD_SELECTED = "methods/select";
	String TOPIC_METHOD_CREATED = "methods/create";
	String TOPIC_METHOD_UPDATE = "methods/update";
	String PROPERTY_METHOD_OLD_OBJECT = IEVENTBROKER_DATA + ".olditem";
	/*
	 * Close Parts
	 */
	String TOPIC_PART_CLOSED = "part/closed";
}
