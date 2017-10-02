/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IMassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class AlkaneIdentifier {

	public static final String IDENTIFIER = "Alkane Identifier";
	//
	private static final String MASS_SPECTRUM_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos";
	private String massSpectraFiles;
	private DatabasesCache databasesCache;

	public AlkaneIdentifier() {
		//
		FileListUtil fileListUtil = new FileListUtil();
		massSpectraFiles = getDatabase();
		databasesCache = new DatabasesCache(fileListUtil.getFiles(massSpectraFiles));
	}

	public static String getDatabase() {

		return PathResolver.getAbsolutePath(PathResolver.ALKANES);
	}

	/**
	 * Run the peak identification.
	 * 
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param processingInfo
	 * @param monitor
	 * @return {@link IPeakIdentificationResults}
	 * @throws FileNotFoundException
	 */
	public IPeakIdentifierProcessingInfo runPeakIdentification(List<IPeakMSD> peaks, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		IVendorPeakIdentifierSettings peakIdentifierSettings = new VendorPeakIdentifierSettings();
		setIdentifierSettings(peakIdentifierSettings);
		setFileIdentifierSettings(peakIdentifierSettings);
		/*
		 * Run the file identifier.
		 */
		PeakIdentifier peakIdentifier = new PeakIdentifier();
		return peakIdentifier.identify(peaks, peakIdentifierSettings, monitor);
	}

	public IMassSpectraIdentifierProcessingInfo runIdentification(List<IScanMSD> massSpectraList, IVendorMassSpectrumIdentifierSettings fileIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		IVendorMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = new VendorMassSpectrumIdentifierSettings();
		setIdentifierSettings(massSpectrumIdentifierSettings);
		setFileIdentifierSettings(massSpectrumIdentifierSettings);
		/*
		 * Run the file identifier.
		 */
		MassSpectrumIdentifier peakIdentifier = new MassSpectrumIdentifier();
		return peakIdentifier.identify(massSpectraList, massSpectrumIdentifierSettings, monitor);
	}

	/**
	 * Returns identified mass spectra from the database.
	 * 
	 * @param identificationTarget
	 * @param monitor
	 * @return {@link IMassSpectra}
	 */
	public IMassSpectra getMassSpectra(IIdentificationTarget identificationTarget, IProgressMonitor monitor) {

		IMassSpectra massSpectra = new MassSpectra();
		if(identificationTarget != null) {
			/*
			 * Extract the target library information.
			 */
			if(identificationTarget.getIdentifier().equals(IDENTIFIER)) {
				massSpectra.addMassSpectra(databasesCache.getDatabaseMassSpectra(identificationTarget, monitor));
			}
		}
		//
		return massSpectra;
	}

	private void setIdentifierSettings(IIdentifierSettings identifierSettings) {

		identifierSettings.setMassSpectrumComparatorId(MASS_SPECTRUM_COMPARATOR_ID);
		identifierSettings.setPenaltyCalculation(IIdentifierSettings.PENALTY_CALCULATION_NONE);
		identifierSettings.setPenaltyCalculationLevelFactor(0.0f);
		identifierSettings.setMaxPenalty(0.0f);
		identifierSettings.setRetentionTimeWindow(0);
		identifierSettings.setRetentionIndexWindow(0.0f);
	}

	private void setFileIdentifierSettings(IFileIdentifierSettings fileIdentifierSettings) {

		fileIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		fileIdentifierSettings.setUsePreOptimization(false);
		fileIdentifierSettings.setThresholdPreOptimization(0.1d);
		fileIdentifierSettings.setNumberOfTargets(5);
		fileIdentifierSettings.setMinMatchFactor(70.0f);
		fileIdentifierSettings.setMinReverseMatchFactor(70.0f);
		fileIdentifierSettings.setAddUnknownMzListTarget(false);
		fileIdentifierSettings.setAlternateIdentifierId(IDENTIFIER);
	}
}
