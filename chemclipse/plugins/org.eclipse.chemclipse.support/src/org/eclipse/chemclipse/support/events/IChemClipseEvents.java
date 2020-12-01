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

	// ID copied from IEventBroker to prevent dependency on E4
	String EVENT_BROKER_DATA = "org.eclipse.e4.data";
	//
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE = "chromatogram/msd/update/rawfile";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE = "chromatogram/csd/update/rawfile";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE = "chromatogram/wsd/update/rawfile";
	String TOPIC_SCAN_XIR_UPDATE_RAWFILE = "scan/xir/update/rawfile";
	String TOPIC_SCAN_NMR_UPDATE_RAWFILE = "scan/nmr/update/rawfile";
	String TOPIC_PLATE_PCR_UPDATE_RAWFILE = "plate/pcr/update/rawfile";
	String TOPIC_SEQUENCE_UPDATE_RAWFILE = "sequence/update/rawfile";
	String TOPIC_METHOD_UPDATE_RAWFILE = "method/update/rawfile";
	String TOPIC_QUANTIATION_DATABASE_UPDATE_RAWFILE = "quantitation/database/update/rawfile";
	String TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE = "chromatogram/xxd/update/none";
	//
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW = "chromatogram/msd/update/overview";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW = "chromatogram/csd/update/overview";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW = "chromatogram/wsd/update/overview";
	String TOPIC_SCAN_NMR_UPDATE_OVERVIEW = "scan/nmr/update/overview";
	String TOPIC_SCAN_XIR_UPDATE_OVERVIEW = "scan/xir/update/overview";
	String TOPIC_SEQUENCE_UPDATE_OVERVIEW = "sequence/update/overview";
	String TOPIC_METHOD_UPDATE_OVERVIEW = "method/update/overview";
	String TOPIC_QUANTIATION_DATABASE_UPDATE_OVERVIEW = "quantitation/database/update/overview";
	//
	String TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION = "chromatogram/xxd/load/chromatogramselection";
	String TOPIC_SCAN_XXD_UPDATE_SELECTION = "scan/xxd/update/selection";
	String TOPIC_PEAK_XXD_UPDATE_SELECTION = "peak/xxd/update/selection";
	String TOPIC_SCAN_XIR_UPDATE_SELECTION = "scan/xir/update/selection";
	String TOPIC_SCAN_NMR_UPDATE_SELECTION = "scan/nmr/update/selection";
	//
	String TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION = "chromatogram/xxd/unload/chromatogramselection";
	String TOPIC_SCAN_XXD_UNLOAD_SELECTION = "scan/xxd/unload/selection";
	String TOPIC_PEAK_XXD_UNLOAD_SELECTION = "peak/xxd/unload/selection";
	String TOPIC_SCAN_XIR_UNLOAD_SELECTION = "scan/xir/unload/selection";
	String TOPIC_SCAN_NMR_UNLOAD_SELECTION = "scan/nmr/unload/selection";
	//
	String TOPIC_APPLICATION_SELECT_PERSPECTIVE = "application/select/perspective";
	String TOPIC_APPLICATION_SELECT_VIEW = "application/select/view";
	String TOPIC_PART_CLOSED = "part/closed";
	//
	String TOPIC_PLATE_PCR_UPDATE_OVERVIEW = "plate/pcr/update/overview";
	String TOPIC_WELL_PCR_UPDATE_SELECTION = "well/pcr/update/selection";
	String TOPIC_PLATE_PCR_UPDATE_SELECTION = "plate/pcr/update/selection";
	String TOPIC_WELL_PCR_UNLOAD_SELECTION = "well/pcr/unload/selection";
	String TOPIC_PLATE_PCR_UNLOAD_SELECTION = "plate/pcr/unload/selection";
	//
	String TOPIC_LIBRARY_MSD_ADD_TO_DB_SEARCH = "library/msd/add/dbsearch";
	String TOPIC_LIBRARY_MSD_REMOVE_FROM_DB_SEARCH = "library/msd/remove/dbsearch";
	//
	String TOPIC_RI_LIBRARY_ADD_ADD_TO_PROCESS = "ri/library/add/process";
	String TOPIC_RI_LIBRARY_REMOVE_FROM_PROCESS = "ri/library/remove/process";
	//
	String TOPIC_METHOD_SELECTED = "methods/select";
	String TOPIC_METHOD_CREATED = "methods/create";
	String TOPIC_METHOD_UPDATE = "methods/update";
	String PROPERTY_METHOD_OLD_OBJECT = EVENT_BROKER_DATA + ".olditem";
	//
	String TOPIC_IDENTIFICATION_TARGET_UPDATE = "identification/target/update";
	String TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION = "identification/targets/update/selection";
	String TOPIC_IDENTIFICATION_TARGETS_UNLOAD_SELECTION = "identification/targets/unload/selection";
	//
	String TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM = "filter/supplier/subtract/update/session/subtractmassspectrum";
	//
	String TOPIC_QUANT_DB_COMPOUND_UPDATE = "quantitation/database/compound/update";
	String TOPIC_QUANT_DB_COMPOUND_UNLOAD = "quantitation/database/compound/unload";
	//
	String TOPIC_CHROMATOGRAM_CONVERTER = "chromatogram/update/converter/status";
	//
	String TOPIC_SCAN_TARGET_UPDATE_COMPARISON = "target/update/comparison"; // Object[]{scan, identificationTarget}
	String TOPIC_SCAN_REFERENCE_UPDATE_COMPARISON = "scan/update/comparison"; // Object[]{scan1, scan2}
	//
	String TOPIC_LIBRARY_MSD_UPDATE_SELECTION = "library/msd/update/selection";
	String TOPIC_LIBRARY_MSD_UNLOAD_SELECTION = "library/msd/unload/selection";
	//
	String TOPIC_PROCESSING_INFO_UPDATE = "processinginfo/update";
	String TOPIC_EDIT_HISTORY_UPDATE = "edithistory/update"; // $NON-NLS-1$
	//
	String TOPIC_EDITOR_CHROMATOGRAM_UPDATE = "editor/chromatogram/update";
}
