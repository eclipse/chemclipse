/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add method to check if target is valid, ad generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.MassSpectrumIdentifierFile;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.core.PeakIdentifierFile;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class AlkaneIdentifier {

	public static final String IDENTIFIER = "Alkane Identifier";
	//
	private static final String MASS_SPECTRUM_COMPARATOR_ID = "org.eclipse.chemclipse.chromatogram.msd.comparison.supplier.incos";
	private final String massSpectraFiles;
	private final DatabasesCache databasesCache;

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
	public IProcessingInfo<IPeakIdentificationResults> runPeakIdentification(List<? extends IPeakMSD> peaks, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		PeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		setIdentifierSettings(peakIdentifierSettings);
		setFileIdentifierSettings(peakIdentifierSettings);
		/*
		 * Run the file identifier.
		 */
		PeakIdentifierFile peakIdentifier = new PeakIdentifierFile();
		return peakIdentifier.identify(peaks, peakIdentifierSettings, monitor);
	}

	public IProcessingInfo<IMassSpectra> runIdentification(List<IScanMSD> massSpectraList, IMassSpectrumIdentifierSettings fileIdentifierSettings, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = new MassSpectrumIdentifierSettings();
		setIdentifierSettings(massSpectrumIdentifierSettings);
		setFileIdentifierSettings(massSpectrumIdentifierSettings);
		/*
		 * Run the file identifier.
		 */
		MassSpectrumIdentifierFile peakIdentifier = new MassSpectrumIdentifierFile();
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
		if(isValid(identificationTarget)) {
			massSpectra.addMassSpectra(databasesCache.getDatabaseMassSpectra(identificationTarget, monitor));
		}
		//
		return massSpectra;
	}

	public DatabasesCache getDatabasesCache() {

		return databasesCache;
	}

	public boolean isValid(IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			return identificationTarget.getIdentifier().equals(IDENTIFIER);
		}
		return false;
	}

	private void setIdentifierSettings(IIdentifierSettingsMSD identifierSettings) {

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
		fileIdentifierSettings.setNumberOfTargets(PreferenceSupplier.getNumberOfTargets());
		fileIdentifierSettings.setMinMatchFactor(PreferenceSupplier.getMinMatchFactor());
		fileIdentifierSettings.setMinReverseMatchFactor(PreferenceSupplier.getMinReverseMatchFactor());
		fileIdentifierSettings.setAlternateIdentifierId(IDENTIFIER);
	}
}
