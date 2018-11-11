/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.ChromatogramSelectionException;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.FilterSettingsException;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoFilterAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.exceptions.NoChromatogramConverterAvailableException;
import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.util.IonSettingUtil;
import org.eclipse.core.runtime.NullProgressMonitor;

public class Show_Filter extends ChromatogramImporterTestCase {

	// private static final float NORM_BASE = 1000000.0f;
	private final static String EXTENSION_POINT_ID = "org.eclipse.chemclipse.msd.converter.supplier.agilent";
	private final static String FILTER_BACKGROUND = "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.ionremover";
	// private final static String FILTER_NORMALIZE =
	// "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.normalizer";
	// private final static String FILTER_BACKFOLDING =
	// "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding";
	protected IChromatogramMSD chromatogram;
	protected IChromatogramSelectionMSD chromatogramSelection;
	private IMarkedIons excludedIons;
	private File fileImport;
	private File fileExport;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		/*
		 * Import
		 */
		fileImport = new File("E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705.D\\DATA.MS");
		IProcessingInfo processingInfo = ChromatogramConverterMSD.convert(fileImport, EXTENSION_POINT_ID, new NullProgressMonitor());
		chromatogram = processingInfo.getProcessingResult(IChromatogramMSD.class);
		chromatogramSelection = new ChromatogramSelectionMSD(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testApplyFilter_1() throws NoExtractedIonSignalStoredException, ChromatogramSelectionException, FilterSettingsException, FileNotFoundException, FileIsNotWriteableException, IOException, NoChromatogramConverterAvailableException, NoFilterAvailableException {

		/**
		 * Normalize
		 */
		// INormalizerFilterSettings nFilterSettings = new
		// NormalizerFilterSettings();
		// nFilterSettings.setNormalizationBase(NORM_BASE);
		// ChromatogramFilter.applyFilter(chromatogramSelection,
		// nFilterSettings, FILTER_NORMALIZE);
		/*
		 * fileExport = new File(
		 * "E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-N.D\\DATA.MS"
		 * ); ChromatogramConverter.convert(fileExport, chromatogram,
		 * EXTENSION_POINT_ID);
		 */
		/**
		 * Filter Background
		 */
		ChromatogramFilterSettings rFilterSettings = new ChromatogramFilterSettings();
		IonSettingUtil settingIon = new IonSettingUtil();
		excludedIons = new MarkedIons(settingIon.extractIons(settingIon.deserialize(rFilterSettings.getIonsToRemove())));
		excludedIons.add(new MarkedIon(28));
		excludedIons.add(new MarkedIon(32));
		excludedIons.add(new MarkedIon(207));
		excludedIons.add(new MarkedIon(281));
		ChromatogramFilterMSD.applyFilter(chromatogramSelection, rFilterSettings, FILTER_BACKGROUND, new NullProgressMonitor());
		/*
		 * fileExport = new File(
		 * "E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-NB.D\\DATA.MS"
		 * ); ChromatogramConverter.convert(fileExport, chromatogram,
		 * EXTENSION_POINT_ID);
		 */
		/**
		 * Normalize
		 */
		// ChromatogramFilter.applyFilter(chromatogramSelection,
		// nFilterSettings, FILTER_NORMALIZE);
		// fileExport = new
		// File("E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-NBN.D\\DATA.MS");
		// ChromatogramConverter.convert(fileExport, chromatogram,
		// EXTENSION_POINT_ID);
		/**
		 * Mean Normalize
		 */
		/*
		 * IMeanNormalizerFilterSettings mnFilterSettings = new
		 * MeanNormalizerFilterSettings();
		 * ChromatogramFilter.applyFilter(chromatogramSelection,
		 * mnFilterSettings, FILTER_MEAN_NORMALIZE);
		 */
		/*
		 * fileExport = new File(
		 * "E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-NM.D\\DATA.MS"
		 * ); ChromatogramConverter.convert(fileExport, chromatogram,
		 * EXTENSION_POINT_ID);
		 */
	}

	public void testApplyFilter_2() throws NoExtractedIonSignalStoredException, ChromatogramSelectionException, FilterSettingsException, FileNotFoundException, FileIsNotWriteableException, IOException, NoChromatogramConverterAvailableException, NoFilterAvailableException {

		/**
		 * Normalize
		 */
		// INormalizerFilterSettings nFilterSettings = new
		// NormalizerFilterSettings();
		// nFilterSettings.setNormalizationBase(NORM_BASE);
		// ChromatogramFilter.applyFilter(chromatogramSelection,
		// nFilterSettings, FILTER_NORMALIZE);
		fileExport = new File("E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-N.D\\DATA.MS");
		ChromatogramConverterMSD.convert(fileExport, chromatogram, EXTENSION_POINT_ID, new NullProgressMonitor());
		/**
		 * Filter Background
		 */
		ChromatogramFilterSettings rFilterSettings = new ChromatogramFilterSettings();
		IonSettingUtil settingIon = new IonSettingUtil();
		excludedIons = new MarkedIons(settingIon.extractIons(settingIon.deserialize(rFilterSettings.getIonsToRemove())));
		excludedIons.add(new MarkedIon(28));
		excludedIons.add(new MarkedIon(32));
		excludedIons.add(new MarkedIon(207));
		excludedIons.add(new MarkedIon(281));
		ChromatogramFilterMSD.applyFilter(chromatogramSelection, rFilterSettings, FILTER_BACKGROUND, new NullProgressMonitor());
		fileExport = new File("E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-NB.D\\DATA.MS");
		ChromatogramConverterMSD.convert(fileExport, chromatogram, EXTENSION_POINT_ID, new NullProgressMonitor());
		/**
		 * Normalize
		 */
		// ChromatogramFilter.applyFilter(chromatogramSelection,
		// nFilterSettings, FILTER_NORMALIZE);
		fileExport = new File("E:\\Dissertation\\Pyrolyseläufe\\OP\\OP21680-707\\OP21705-NBMN.D\\DATA.MS");
		ChromatogramConverterMSD.convert(fileExport, chromatogram, EXTENSION_POINT_ID, new NullProgressMonitor());
	}
}
