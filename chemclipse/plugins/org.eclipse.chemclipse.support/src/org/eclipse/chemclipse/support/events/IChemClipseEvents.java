/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * chemclipse - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.events;

import org.eclipse.e4.core.services.events.IEventBroker;

public interface IChemClipseEvents {

	/*
	 * Properties
	 */
	String PROPERTY_PERSPECTIVE_NAME = IEventBroker.DATA; // String
	String PROPERTY_VIEW_NAME = IEventBroker.DATA; // String
	//
	String PROPERTY_CHROMATOGRAM_MSD_RAWFILE = IEventBroker.DATA; // File
	String PROPERTY_CHROMATOGRAM_CSD_RAWFILE = IEventBroker.DATA; // File
	String PROPERTY_CHROMATOGRAM_WSD_RAWFILE = IEventBroker.DATA; // File
	//
	String PROPERTY_CHROMATOGRAM_MSD_OVERVIEW = IEventBroker.DATA; // IChromatogramOverview - no map
	String PROPERTY_CHROMATOGRAM_CSD_OVERVIEW = IEventBroker.DATA; // IChromatogramOverview - no map
	String PROPERTY_CHROMATOGRAM_WSD_OVERVIEW = IEventBroker.DATA; // IChromatogramOverview - no map
	//
	String PROPERTY_CHROMATOGRAM_SELECTION = "ChromatogramSelection"; // IChromatogramSelection (MSD, CSD, WSD)
	String PROPERTY_LIBRARY_SELECTION = IEventBroker.DATA; // IMassSpectra
	String PROPERTY_SCAN_SELECTION = IEventBroker.DATA; // IScanMSD, ...
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
	String PROPERTY_SELECTED_ION = IEventBroker.DATA; // double ion
	//
	String PROPERTY_FORCE_RELOAD = "ForceReload";
	String PROPERTY_PROCESSING_INFO = IEventBroker.DATA;
	//
	String PROPERTY_IDENTIFICATION_TARGET = IEventBroker.DATA;
	String PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN = "MassSpectrumUnknown";
	String PROPERTY_IDENTIFICATION_TARGET_MASS_SPECTRUM_LIBRARY = "MassSpectrumLibrary";
	String PROPERTY_IDENTIFICATION_TARGET_ENTRY = "IdentificationTarget";
	/*
	 * Topics
	 */
	String TOPIC_APPLICATION_SELECT_PERSPECTIVE = "application/select/perspective";
	String TOPIC_APPLICATION_SELECT_VIEW = "application/select/view";
	//
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE = "chromatogram/msd/update/rawfile";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE = "chromatogram/csd/update/rawfile";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE = "chromatogram/wsd/update/rawfile";
	/*
	 * UNLOAD
	 * Close the chromatogram editor.
	 */
	String TOPIC_CHROMATOGRAM_XXD_UNLOAD_CHROMATOGRAM_SELECTION = "chromatogram/xxd/unload/chromatogramselection";
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
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_ION_SELECTION = "chromatogram/msd/update/ionselection";
	//
	String TOPIC_LIBRARY_MSD_UPDATE_SELECTION = "library/msd/update/selection";
	String TOPIC_LIBRARY_MSD_UNLOAD_SELECTION = "library/msd/unload/selection";
	//
	String TOPIC_SCAN_MSD_UPDATE_SELECTION = "scan/msd/update/selection";
	String TOPIC_SCAN_MSD_UNLOAD_SELECTION = "scan/msd/unload/selection";
	/*
	 * Detector: CSD (conductivity selective)
	 */
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW = "chromatogram/csd/update/overview";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION = "chromatogram/csd/update/chromatogramselection";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_AND_PEAK_SELECTION = "chromatogram/csd/update/chromatogramandpeakselection";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM = "chromatogram/csd/update/chromatogram";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK = "chromatogram/csd/update/peak";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAKS = "chromatogram/csd/update/peaks";
	/*
	 * Detector: WSD (wavelength selective)
	 */
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW = "chromatogram/wsd/update/overview";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION = "chromatogram/wsd/update/chromatogramselection";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM = "chromatogram/wsd/update/chromatogram";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAK = "chromatogram/wsd/update/peak";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAKS = "chromatogram/wsd/update/peaks";
	//
	String TOPIC_PROCESSING_INFO_UPDATE = "processinginfo/update";
	String TOPIC_IDENTIFICATION_TARGET_UPDATE = "identification/target/update";
	String TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_UNKNOWN_UPDATE = "identification/target/update/massspectrum/unknown";
	String TOPIC_IDENTIFICATION_TARGET_MASS_SPECTRUM_LIBRARY_UPDATE = "identification/target/update/massspectrum/library";
	/*
	 * Converter
	 */
	String TOPIC_CHROMATOGRAM_CONVERTER = "chromatogram/update/converter/status";
	String PROPERTY_CHROMATOGRAM_CONVERTER_INFO = IEventBroker.DATA; // String
	/*
	 * Event Broker: Edit History
	 */
	String PROPERTY_EDIT_HISTORY = IEventBroker.DATA; // IEditHistory
	String TOPIC_EDIT_HISTORY_UPDATE = "edithistory/update"; // $NON-NLS-1$
}
