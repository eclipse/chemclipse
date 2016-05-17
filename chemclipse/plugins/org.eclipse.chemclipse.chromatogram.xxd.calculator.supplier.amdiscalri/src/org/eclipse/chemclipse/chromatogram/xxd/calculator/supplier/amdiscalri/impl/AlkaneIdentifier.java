/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IMassSpectraIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.processing.IPeakIdentifierProcessingInfo;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.MassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.PeakIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IVendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.VendorPeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.io.StandardsReader;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.targets.IPeakTarget;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.identifier.massspectrum.IMassSpectrumTarget;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.core.runtime.IProgressMonitor;

public class AlkaneIdentifier {

	public static final String IDENTIFIER = "Alkane Identifier";
	//
	private static final String MASS_SPECTRUM_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos";
	private List<String> massSpectraFiles;
	private DatabasesCache databasesCache;

	public AlkaneIdentifier() {
		//
		massSpectraFiles = new ArrayList<String>();
		massSpectraFiles.add(getDatabase());
		databasesCache = new DatabasesCache(massSpectraFiles);
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
		IVendorPeakIdentifierSettings fileIdentifierSettings = new VendorPeakIdentifierSettings();
		fileIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		fileIdentifierSettings.setUsePreOptimization(false);
		fileIdentifierSettings.setThresholdPreOptimization(0.1d);
		fileIdentifierSettings.setMassSpectrumComparatorId(MASS_SPECTRUM_COMPARATOR_ID);
		fileIdentifierSettings.setNumberOfTargets(5);
		fileIdentifierSettings.setMinMatchFactor(70.0f);
		fileIdentifierSettings.setMinReverseMatchFactor(70.0f);
		fileIdentifierSettings.setAddUnknownMzListTarget(false);
		fileIdentifierSettings.setPenaltyCalculation(IIdentifierSettings.PENALTY_CALCULATION_NONE);
		fileIdentifierSettings.setPenaltyCalculationLevelFactor(0.0f);
		fileIdentifierSettings.setMaxPenalty(0.0f);
		fileIdentifierSettings.setRetentionTimeWindow(0);
		fileIdentifierSettings.setRetentionIndexWindow(0.0f);
		/*
		 * Run the file identifier.
		 */
		PeakIdentifier peakIdentifier = new PeakIdentifier();
		IPeakIdentifierProcessingInfo processingInfo = peakIdentifier.identify(peaks, fileIdentifierSettings, monitor);
		/*
		 * Overwrite the identifier ID.
		 * This is needed by the library service to get the appropriate DB mass spectrum.
		 */
		StandardsReader standardsReader = new StandardsReader();
		Set<String> standardNames = standardsReader.getStandardNames();
		//
		for(IPeakMSD peak : peaks) {
			List<IPeakTarget> peakTargets = peak.getTargets();
			for(IPeakTarget peakTarget : peakTargets) {
				String name = peakTarget.getLibraryInformation().getName();
				if(standardNames.contains(name)) {
					peakTarget.setIdentifier(IDENTIFIER);
				}
			}
		}
		//
		return processingInfo;
	}

	public IMassSpectraIdentifierProcessingInfo runIdentification(List<IScanMSD> massSpectraList, IVendorMassSpectrumIdentifierSettings fileIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		IVendorMassSpectrumIdentifierSettings massSpectrumIdentifierSettings = new VendorMassSpectrumIdentifierSettings();
		massSpectrumIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		massSpectrumIdentifierSettings.setUsePreOptimization(false);
		massSpectrumIdentifierSettings.setThresholdPreOptimization(0.1d);
		massSpectrumIdentifierSettings.setMassSpectrumComparatorId(MASS_SPECTRUM_COMPARATOR_ID);
		massSpectrumIdentifierSettings.setNumberOfTargets(5);
		massSpectrumIdentifierSettings.setMinMatchFactor(70.0f);
		massSpectrumIdentifierSettings.setMinReverseMatchFactor(70.0f);
		massSpectrumIdentifierSettings.setAddUnknownMzListTarget(false);
		massSpectrumIdentifierSettings.setPenaltyCalculation(IIdentifierSettings.PENALTY_CALCULATION_NONE);
		massSpectrumIdentifierSettings.setPenaltyCalculationLevelFactor(0.0f);
		massSpectrumIdentifierSettings.setMaxPenalty(0.0f);
		massSpectrumIdentifierSettings.setRetentionTimeWindow(0);
		massSpectrumIdentifierSettings.setRetentionIndexWindow(0.0f);
		/*
		 * Run the file identifier.
		 */
		MassSpectrumIdentifier peakIdentifier = new MassSpectrumIdentifier();
		IMassSpectraIdentifierProcessingInfo processingInfo = peakIdentifier.identify(massSpectraList, massSpectrumIdentifierSettings, monitor);
		IMassSpectra massSpectra = processingInfo.getMassSpectra();
		/*
		 * Overwrite the identifier ID.
		 * This is needed by the library service to get the appropriate DB mass spectrum.
		 */
		StandardsReader standardsReader = new StandardsReader();
		Set<String> standardNames = standardsReader.getStandardNames();
		//
		for(IScanMSD scan : massSpectra.getList()) {
			List<IMassSpectrumTarget> massSpectrumTargets = scan.getTargets();
			for(IMassSpectrumTarget massSpectrumTarget : massSpectrumTargets) {
				String name = massSpectrumTarget.getLibraryInformation().getName();
				if(standardNames.contains(name)) {
					massSpectrumTarget.setIdentifier(IDENTIFIER);
				}
			}
		}
		//
		return processingInfo;
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
}
