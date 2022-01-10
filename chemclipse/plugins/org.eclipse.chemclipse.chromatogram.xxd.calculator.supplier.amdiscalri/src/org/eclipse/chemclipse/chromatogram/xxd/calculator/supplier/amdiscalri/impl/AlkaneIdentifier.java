/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.DatabasesCache;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.PathResolver;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.MassSpectrumIdentifierAlkaneSettings;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.settings.PeakIdentifierAlkaneSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core.MassSpectrumIdentifierFile;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core.PeakIdentifierFile;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.model.identifier.DeltaCalculation;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakIdentificationResults;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.support.util.FileListUtil;
import org.eclipse.core.runtime.IProgressMonitor;

public class AlkaneIdentifier {

	public static final String IDENTIFIER = "Alkane(s) Identifier";
	//
	private final String massSpectraFiles; // Initialized in constructor.
	private final DatabasesCache databasesCache; // Initialized in constructor.

	public AlkaneIdentifier() {

		FileListUtil fileListUtil = new FileListUtil();
		massSpectraFiles = getDatabase();
		databasesCache = new DatabasesCache(fileListUtil.getFiles(massSpectraFiles));
	}

	public static String getDatabase() {

		return PathResolver.getAbsolutePath(PathResolver.ALKANES);
	}

	public IProcessingInfo<IPeakIdentificationResults> runPeakIdentification(List<? extends IPeakMSD> peaks, PeakIdentifierAlkaneSettings alkaneSettings, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		PeakIdentifierSettings fileIdentifierSettings = new PeakIdentifierSettings();
		initializeSettings(fileIdentifierSettings);
		transferAlkaneSettings(fileIdentifierSettings, alkaneSettings);
		/*
		 * Run the file identifier.
		 */
		PeakIdentifierFile peakIdentifier = new PeakIdentifierFile();
		return peakIdentifier.identify(peaks, fileIdentifierSettings, monitor);
	}

	public IProcessingInfo<IMassSpectra> runIdentification(List<IScanMSD> massSpectraList, MassSpectrumIdentifierAlkaneSettings alkaneSettings, IProgressMonitor monitor) throws FileNotFoundException {

		/*
		 * Create the file identifier settings.
		 */
		MassSpectrumIdentifierSettings fileIdentifierSettings = new MassSpectrumIdentifierSettings();
		initializeSettings(fileIdentifierSettings);
		transferAlkaneSettings(fileIdentifierSettings, alkaneSettings);
		/*
		 * Run the file identifier.
		 */
		MassSpectrumIdentifierFile peakIdentifier = new MassSpectrumIdentifierFile();
		return peakIdentifier.identify(massSpectraList, fileIdentifierSettings, monitor);
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

	private void initializeSettings(IIdentifierSettingsMSD identifierSettings) {

		identifierSettings.setMassSpectrumComparatorId(IIdentifierSettingsMSD.DEFAULT_COMPARATOR_ID);
		identifierSettings.setLimitMatchFactor(100.0f);
		identifierSettings.setDeltaCalculation(DeltaCalculation.NONE);
		identifierSettings.setDeltaWindow(0.0f);
		identifierSettings.setPenaltyCalculation(PenaltyCalculation.NONE);
		identifierSettings.setPenaltyWindow(0.0f);
		identifierSettings.setPenaltyLevelFactor(0.0f);
		identifierSettings.setMaxPenalty(0.0f);
	}

	private void transferAlkaneSettings(IFileIdentifierSettings fileIdentifierSettings, PeakIdentifierAlkaneSettings alkaneSettings) {

		fileIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		fileIdentifierSettings.setUsePreOptimization(false);
		fileIdentifierSettings.setThresholdPreOptimization(0.1d);
		fileIdentifierSettings.setNumberOfTargets(alkaneSettings.getNumberOfTargets());
		fileIdentifierSettings.setMinMatchFactor(alkaneSettings.getMinMatchFactor());
		fileIdentifierSettings.setMinReverseMatchFactor(alkaneSettings.getMinReverseMatchFactor());
		fileIdentifierSettings.setAlternateIdentifierId(IDENTIFIER);
	}

	private void transferAlkaneSettings(IFileIdentifierSettings fileIdentifierSettings, MassSpectrumIdentifierAlkaneSettings alkaneSettings) {

		fileIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		fileIdentifierSettings.setUsePreOptimization(false);
		fileIdentifierSettings.setThresholdPreOptimization(0.1d);
		fileIdentifierSettings.setNumberOfTargets(alkaneSettings.getNumberOfTargets());
		fileIdentifierSettings.setMinMatchFactor(alkaneSettings.getMinMatchFactor());
		fileIdentifierSettings.setMinReverseMatchFactor(alkaneSettings.getMinReverseMatchFactor());
		fileIdentifierSettings.setAlternateIdentifierId(IDENTIFIER);
	}
}