/*******************************************************************************
 * Copyright (c) 2012, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove dependency on IEventbroker
 * Matthias Mailänder - add MALDI support
 *******************************************************************************/
package org.eclipse.chemclipse.support.events;

public interface IChemClipseEvents {

	// ID copied from IEventBroker to prevent dependency on E4
	String EVENT_BROKER_DATA = "org.eclipse.e4.data";
	//
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE = "chromatogram/msd/update/rawfile";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE = "chromatogram/csd/update/rawfile";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE = "chromatogram/wsd/update/rawfile";
	String TOPIC_CHROMATOGRAM_TSD_UPDATE_RAWFILE = "chromatogram/tsd/update/rawfile";
	String TOPIC_SCAN_XIR_UPDATE_RAWFILE = "scan/xir/update/rawfile";
	String TOPIC_SCAN_NMR_UPDATE_RAWFILE = "scan/nmr/update/rawfile";
	String TOPIC_PLATE_PCR_UPDATE_RAWFILE = "plate/pcr/update/rawfile";
	String TOPIC_SEQUENCE_UPDATE_RAWFILE = "sequence/update/rawfile";
	String TOPIC_MASS_SPECTRUM_UPDATE_RAWFILE = "spectrum/ms/update/rawfile";
	String TOPIC_METHOD_UPDATE_RAWFILE = "method/update/rawfile";
	String TOPIC_QUANTIATION_DATABASE_UPDATE_RAWFILE = "quantitation/database/update/rawfile";
	String TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE = "chromatogram/xxd/update/none";
	//
	String TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW = "chromatogram/msd/update/overview";
	String TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW = "chromatogram/csd/update/overview";
	String TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW = "chromatogram/wsd/update/overview";
	String TOPIC_CHROMATOGRAM_TSD_UPDATE_OVERVIEW = "chromatogram/tsd/update/overview";
	String TOPIC_SCAN_NMR_UPDATE_OVERVIEW = "scan/nmr/update/overview";
	String TOPIC_SCAN_XIR_UPDATE_OVERVIEW = "scan/xir/update/overview";
	String TOPIC_SEQUENCE_UPDATE_OVERVIEW = "sequence/update/overview";
	String TOPIC_MASS_SPECTRUM_UPDATE_OVERVIEW = "spectrum/ms/update/overview";
	String TOPIC_METHOD_UPDATE_OVERVIEW = "method/update/overview";
	String TOPIC_QUANTIATION_DATABASE_UPDATE_OVERVIEW = "quantitation/database/update/overview";
	//
	String TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION = "chromatogram/xxd/load/chromatogramselection";
	String TOPIC_SCAN_XXD_UPDATE_SELECTION = "scan/xxd/update/selection";
	String TOPIC_PEAK_XXD_UPDATE_SELECTION = "peak/xxd/update/selection";
	String TOPIC_SCAN_XIR_UPDATE_SELECTION = "scan/xir/update/selection";
	String TOPIC_SCAN_NMR_UPDATE_SELECTION = "scan/nmr/update/selection";
	//
	String TOPIC_APPLICATION_SELECT_PERSPECTIVE = "application/select/perspective";
	String TOPIC_APPLICATION_SELECT_VIEW = "application/select/view";
	String TOPIC_PART_CLOSED = "part/closed";
	String TOPIC_APPLICATION_RESET_PERSPECTIVE = "application/reset/perspective";
	//
	String TOPIC_PLATE_PCR_UPDATE_OVERVIEW = "plate/pcr/update/overview";
	String TOPIC_WELL_PCR_UPDATE_SELECTION = "well/pcr/update/selection";
	String TOPIC_PLATE_PCR_UPDATE_SELECTION = "plate/pcr/update/selection";
	//
	String TOPIC_LIBRARY_MSD_ADD_TO_DB_SEARCH = "library/msd/add/dbsearch";
	String TOPIC_LIBRARY_MSD_REMOVE_FROM_DB_SEARCH = "library/msd/remove/dbsearch";
	//
	String TOPIC_RI_LIBRARY_ADD_ADD_TO_PROCESS = "ri/library/add/process";
	String TOPIC_RI_LIBRARY_REMOVE_FROM_PROCESS = "ri/library/remove/process";
	String TOPIC_RI_LIBRARY_UPDATE = "ri/library/update";
	//
	String TOPIC_PCA_UPDATE_SELECTION = "pca/update/selection";
	String TOPIC_PCA_UPDATE_FEATURES = "pca/update/features";
	String TOPIC_PCA_UPDATE_COLORSCHEME = "pca/update/colorscheme";
	String TOPIC_PCA_UPDATE_LABELS = "pca/update/labels";
	//
	String TOPIC_METHOD_SELECTED = "methods/select";
	String TOPIC_METHOD_CREATED = "methods/create";
	String TOPIC_METHOD_UPDATE = "methods/update";
	String PROPERTY_METHOD_OLD_OBJECT = EVENT_BROKER_DATA + ".olditem";
	//
	String TOPIC_IDENTIFICATION_TARGET_UPDATE = "identification/target/update";
	String TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION = "identification/targets/update/selection";
	//
	String TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM = "filter/supplier/subtract/update/session/subtractmassspectrum";
	//
	String TOPIC_QUANT_DB_COMPOUND_UPDATE = "quantitation/database/compound/update";
	//
	String TOPIC_CHROMATOGRAM_CONVERTER = "chromatogram/update/converter/status";
	//
	String TOPIC_SCAN_TARGET_UPDATE_COMPARISON = "target/update/comparison"; // Object[]{scan, identificationTarget}
	String TOPIC_SCAN_REFERENCE_UPDATE_COMPARISON = "scan/update/comparison"; // Object[]{scan1, scan2}
	//
	String TOPIC_LIBRARY_MSD_UPDATE = "library/msd/update";
	String TOPIC_LIBRARY_MSD_UPDATE_SELECTION = "library/msd/update/selection";
	//
	String TOPIC_PROCESSING_INFO_UPDATE = "processinginfo/update";
	String TOPIC_EDIT_HISTORY_UPDATE = "edithistory/update"; // $NON-NLS-1$
	//
	String TOPIC_EDITOR_CHROMATOGRAM_UPDATE = "editor/chromatogram/update";
	String TOPIC_EDITOR_CHROMATOGRAM_ADJUST = "editor/chromatogram/adjust";
	//
	String EDITOR_CLOSE_REGEX = "(editor/)(.*)(/close)";
	String TOPIC_EDITOR_CHROMATOGRAM_CLOSE = "editor/chromatogram/close";
	String TOPIC_EDITOR_LIBRARY_CLOSE = "editor/library/close";
	String TOPIC_EDITOR_PCR_CLOSE = "editor/pcr/close";
	String TOPIC_EDITOR_NMR_CLOSE = "editor/nmr/close";
	String TOPIC_EDITOR_XIR_CLOSE = "editor/xir/close";
	String TOPIC_EDITOR_PCA_CLOSE = "editor/pca/close";
	//
	String TOPIC_EDITOR_CHROMATOGRAM_TOOLBAR_UPDATE = "editor/chromatogram/toolbar/update";
}