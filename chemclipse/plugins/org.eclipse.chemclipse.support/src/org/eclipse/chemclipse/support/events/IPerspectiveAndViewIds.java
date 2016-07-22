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
package org.eclipse.chemclipse.support.events;

/**
 * This interface contains the perspective and view
 * ids stored in the fragment.e4xmi file.
 * It neccessary to keep both ids in sync.
 */
public interface IPerspectiveAndViewIds {

	/*
	 * Is used to show 3.x editors too:
	 * "org.eclipse.e4.primaryDataStack"
	 * Is an own area to show only 4.x parts (editors)
	 * "org.eclipse.chemclipse.rcp.app.ui.partstack.editor"
	 * Definition in Application.e4xmi
	 * org.eclipse.chemclipse.rcp.app.ui
	 */
	String EDITOR_PART_STACK_ID = "org.eclipse.e4.primaryDataStack";
	String STACK_PERSPECTIVES = "org.eclipse.chemclipse.rcp.app.ui.perspectivestack.main";
	/*
	 * Perspectives
	 */
	String PERSPECTIVE_WELCOME = "org.eclipse.chemclipse.ux.extension.ui.perspective.welcome";
	String PERSPECTIVE_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.perspective.main";
	String PERSPECTIVE_LOGGING = "org.eclipse.chemclipse.logging.ui.perspective.main";
	String PERSPECTIVE_MSD = "org.eclipse.chemclipse.chromatogram.msd.perspective.ui.perspective.main";
	String PERSPECTIVE_PEAKS_MSD = "org.eclipse.chemclipse.chromatogram.msd.perspective.ui.perspective.chromatogramPeaks";
	String PERSPECTIVE_OVERLAY = "org.eclipse.chemclipse.ux.extension.ui.perspective.chromatogramOverlay";
	String PERSPECTIVE_MS_TRANSITION = "org.eclipse.chemclipse.chromatogram.msd.perspective.ui.perspective.transitionMassSpectrum";
	String PERSPECTIVE_MS_EXACT = "org.eclipse.chemclipse.chromatogram.msd.perspective.ui.perspective.exactMassSpectrum";
	String PERSPECTIVE_SUBTRACT = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.perspective.main";
	String PERSPECTIVE_WSD = "org.eclipse.chemclipse.ux.extension.wsd.ui.perspective.main";
	String PERSPECTIVE_MS_LIBRARY = "org.eclipse.chemclipse.ux.extension.msd.ui.perspective.massSpectrumLibrary";
	String PERSPECTIVE_CLASSIFIER = "org.eclipse.chemclipse.ux.extension.msd.ui.perspective.classifier";
	/*
	 * Views
	 */
	String VIEW_CHROMATOGRAM_OVERLAY = "org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.ui.part.chromatogramOverlay";
	String VIEW_CHROMATOGRAM_OVERLAY_SELECTED_IONS = "org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramOverlaySelectedIons";
	String VIEW_SCAN_NOMINAL = "org.eclipse.chemclipse.ux.extension.msd.ui.part.scanNominal";
	String VIEW_PEAK_TARGETS_MSD = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakTargetsView";
	String VIEW_PEAK_TARGETS_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.part.peakTargetsView";
	String VIEW_BASELINE = "org.eclipse.chemclipse.ux.extension.ui.part.baselineAndChromatogramView";
	String VIEW_SELECTED_ION_CHROMATOGRAM_NOMINAL = "org.eclipse.chemclipse.ux.extension.msd.ui.part.selectedNominalIonChromtogramView";
	String VIEW_CHROMATOGRAM_HEATMAP = "org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramHeatmapView";
	String VIEW_PEAK_LIST_MSD = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakListView";
	String VIEW_PEAK_LIST_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.part.peakListView";
	String VIEW_PROCESSING_INFO = "org.eclipse.chemclipse.processing.ui.parts.ProcessingInfoPart";
	String VIEW_CHROMATOGRAM_TARGETS = "org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramTargetsView";
	String VIEW_EXCLUDED_ION_CHROMATOGRAM_NOMINAL = "org.eclipse.chemclipse.ux.extension.msd.ui.part.excludedIonChromatogramView";
	String VIEW_SCAN_EXACT = "org.eclipse.chemclipse.ux.extension.msd.ui.part.scanExact";
	String VIEW_SCAN_ACCURATE = "org.eclipse.chemclipse.ux.extension.msd.ui.part.scanAccurate";
	String VIEW_MASS_SPECTRUM_IONS_LIST = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumIonsListView";
	String VIEW_MASS_SPECTRUM_IONS_LIST_NOMINAL = "org.eclipse.chemclipse.ux.extension.msd.ui.part.simpleNominalMassSpectrumIonListView";
	String VIEW_PEAK_MASS_SPECTRUM_IONS_LIST = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakMassSpectrumIonsListView";
	String VIEW_MASS_SPECTRUM_EDIT_LIST = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumEditListView";
	String VIEW_PEAK_MASS_SPECTRUM = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakMassSpectrumView";
	String VIEW_PEAK_VALUES_LIST = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakValuesListView";
	String VIEW_PEAK_MSD = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakMSDView";
	String VIEW_PEAK_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.part.peakCSDView";
	String VIEW_PEAK_WITH_BACKGROUND = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakWithBackgroundView";
	String VIEW_SELECTED_ION_CHROMATOGRAM_ACCURATE = "org.eclipse.chemclipse.ux.extension.msd.ui.part.selectedAccurateIonChromtogramView";
	String VIEW_SELECTED_ION_CHROMATOGRAM_EXACT = "org.eclipse.chemclipse.ux.extension.msd.ui.part.selectedExactIonChromtogramView";
	String VIEW_SELECTED_ION_CHROMATOGRAM_NOMINAL_COMBINED = "org.eclipse.chemclipse.ux.extension.msd.ui.part.selectedIonCombinedChromtogramView";
	String VIEW_SELECTED_PEAK_AND_CHROMATOGRAM = "org.eclipse.chemclipse.ux.extension.msd.ui.part.selectedPeakChromtogramView";
	String VIEW_MASS_SPECTRUM = "org.eclipse.chemclipse.ux.extension.msd.ui.part.simpleNominalMassSpectrumView";
	String VIEW_PROFILE_MASS_SPECTRUM = "org.eclipse.chemclipse.ux.extension.msd.ui.part.profileMassSpectrumView";
	String VIEW_PEAK_QUANTITATION_ENRTIES = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakQuantitationEntries";
	String VIEW_PEAK_INTEGRATION_ENRTIES = "org.eclipse.chemclipse.ux.extension.msd.ui.part.peakIntegrationEntries";
	String VIEW_COMBINED_MASS_SPECTRUM = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.part.combinedMassSpectrumChromatogramSelection";
	String VIEW_SUBTRACT_MASS_SPECTRUM = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.subtract.ui.part.sessionSubtractMassSpectrum";
	String VIEW_INTEGRATION_RESULTS = "org.eclipse.chemclipse.chromatogram.xxd.integrator.ui.views.integrationResultView";
	String VIEW_MASS_SPECTRUM_IDENTIFICATION_RESULTS = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumTargetsView";
	String VIEW_CHROMATOGRAM_FILE_EXPLORER_MSD = "org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramFileExplorerView";
	String VIEW_CHROMATOGRAM_OVERVIEW_MSD = "org.eclipse.chemclipse.ux.extension.msd.ui.part.chromatogramOverviewView";
	String VIEW_CHROMATOGRAM_FILE_EXPLORER_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.part.chromatogramFileExplorerView";
	String VIEW_CHROMATOGRAM_OVERVIEW_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.part.chromatogramOverviewView";
	String VIEW_CHROMATOGRAM_FILE_EXPLORER_WSD = "org.eclipse.chemclipse.ux.extension.wsd.ui.part.chromatogramFileExplorerView";
	String VIEW_CHROMATOGRAM_OVERVIEW_WSD = "org.eclipse.chemclipse.ux.extension.wsd.ui.part.chromatogramOverviewView";
	String VIEW_SCAN_CSD = "org.eclipse.chemclipse.ux.extension.csd.ui.part.selectedScan";
	String VIEW_MASS_SPECTRA_IDENTIFIED_LIST = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectraIdentifiedList";
	String VIEW_MASS_SPECTRUM_TARGETS = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massSpectrumTargetsView";
	String VIEW_OPTIMIZED_MASS_SPECTRUM = "org.eclipse.chemclipse.ux.extension.msd.ui.part.simpleOptimizedNominalMassSpectrumView";
	String VIEW_MASS_SPECTRUM_LIBRARY_COMPARISON = "org.eclipse.chemclipse.ux.extension.msd.ui.part.differencelibrarycomparison";
	String VIEW_MASS_SPECTRUM_STACK = "org.eclipse.chemclipse.ux.extension.msd.ui.part.editorMassSpectrumStackView";
	String VIEW_MASS_SPECTRUM_LIBRARY_STACK = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massspectrumlibrarystack";
	/*
	 * Legacy
	 */
	String VIEW_CONSOLE = "org.eclipse.ui.console.ConsoleView";
	/*
	 * Browser
	 */
	String VIEW_BROWSER_MASSBANK = "org.eclipse.chemclipse.ux.extension.msd.ui.part.massBankBrowserView";
	String VIEW_BROWSER_SCIFINDER = "org.eclipse.chemclipse.ux.extension.msd.ui.part.sciFinderBrowserView";
	String VIEW_BROWSER_MASC = "org.eclipse.chemclipse.ux.extension.msd.ui.part.mascBrowserView";
}
