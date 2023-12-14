/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Lorenz Gerber - initial API and implementation
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - add method to check if target is valid
 *******************************************************************************/
package org.eclipse.chemclipse.msd.classifier.supplier.molpeak.identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IIdentifierSettingsMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.support.TargetBuilderMSD;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core.MassSpectrumIdentifierFile;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.core.PeakIdentifierFile;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.IFileIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.chromatogram.xxd.identifier.supplier.file.settings.PeakIdentifierSettings;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.DeltaCalculation;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;
import org.eclipse.chemclipse.model.support.LimitSupport;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.PathResolver;
import org.eclipse.chemclipse.msd.classifier.supplier.molpeak.settings.IBasePeakSettings;
import org.eclipse.chemclipse.msd.converter.database.DatabaseConverter;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.MassSpectra;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Journal of Analytical and Applied Pyrolysis
 * Volume 95, May 2012, Pages 95-100
 * Multivariate curve resolution provides a high-throughput data processing pipeline for pyrolysis-gas chromatography/mass spectrometry
 * Lorenz Gerber, Mattias Eliasson, Johan Trygg, Thomas Moritz, Björn Sundberg
 * dx.doi.org/10.1016/j.jaap.2012.01.011
 */
public class BasePeakIdentifier {

	private static final Logger logger = Logger.getLogger(BasePeakIdentifier.class);
	//
	public static final String IDENTIFIER = "SGH + C Identifier";
	public static final String NOT_FOUND = "Not Found (" + IDENTIFIER + ")";
	public static final String SYRINGYL = "Syringyl";
	public static final String GUAIACYL = "Guaiacyl";
	public static final String PHYDROXYPHENYL = "p-Hydroxyphenyl";
	public static final String CARBOHYDRATE = "Carbohydrate";
	//
	private static final ArrayList<Integer> SYRINGYL_BASE = new ArrayList<>();
	private static final ArrayList<Integer> GUAIACYL_BASE = new ArrayList<>();
	private static final ArrayList<Integer> PHYDROXYPHENYL_BASE = new ArrayList<>();
	private static final ArrayList<Integer> CARBOHYDRATE_BASE = new ArrayList<>();
	//
	private final TargetBuilderMSD targetBuilder;
	// These one's are run when initializing the class
	private static final IMassSpectra references = getStandardsMassSpectra();
	private static final IScanMSD syringyl = getSyringyl();
	//
	private final String massSpectraFiles;
	static {
		Integer[] syringyl = {149, 154, 167, 181, 182, 192, 194, 208, 210};
		Integer[] guaiacyl = {109, 123, 136, 137, 138, 140, 150, 151, 152, 162, 164, 168, 178};
		Integer[] pHydroxyPhenyl = {94, 107, 108, 120, 121, 124, 134};
		Integer[] carbohydrate = {29, 31, 39, 41, 42, 43, 44, 45, 46, 55, 56, 57, 58, 59, 60, 68, 69, 73, 81, 82, 84, 85, 87, 95, 96, 98, 114, 126, 142};
		//
		for(Integer mz : syringyl) {
			SYRINGYL_BASE.add(mz);
		}
		//
		for(Integer mz : guaiacyl) {
			GUAIACYL_BASE.add(mz);
		}
		//
		for(Integer mz : pHydroxyPhenyl) {
			PHYDROXYPHENYL_BASE.add(mz);
		}
		//
		for(Integer mz : carbohydrate) {
			CARBOHYDRATE_BASE.add(mz);
		}
	}

	public BasePeakIdentifier() {

		targetBuilder = new TargetBuilderMSD();
		massSpectraFiles = PathResolver.getAbsolutePath(PathResolver.GERBER_ET_AL_2012);
	}

	public void identifyPeaks(List<? extends IPeakMSD> peaks, IBasePeakSettings settings, IProgressMonitor monitor) {

		List<IPeakMSD> peaksNotFound = new ArrayList<>();
		float limitMatchFactor = settings.getLimitMatchFactor();
		//
		for(int i = 0; i < peaks.size(); i++) {
			IPeakMSD peak = peaks.get(i);
			if(LimitSupport.doIdentify(peak.getTargets(), limitMatchFactor)) {
				/*
				 * Identification
				 */
				IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
				String name = getIdentification(massSpectrum);
				/*
				 * Add the peak target
				 */
				IComparisonResult comparisonResult = getComparisonResult(settings);
				IIdentificationTarget peakTarget = targetBuilder.getPeakTarget(massSpectrum, comparisonResult, IDENTIFIER);
				setLibraryInformationFields(peakTarget.getLibraryInformation(), name);
				peak.getTargets().add(peakTarget);
				/*
				 * Add the classifier field.
				 */
				peak.addClassifier(name);
				/*
				 * Grep all not identified peaks.
				 */
				if(peakTarget.getLibraryInformation().getName().equals(NOT_FOUND)) {
					peaksNotFound.add(peak);
				}
			}
		}
		/*
		 * Post identify NOTFOUND peaks.
		 */
		PeakIdentifierSettings peakIdentifierSettings = new PeakIdentifierSettings();
		setIdentifierSettings(peakIdentifierSettings);
		setFileIdentifierSettings(peakIdentifierSettings);
		PeakIdentifierFile peakIdentifier = new PeakIdentifierFile();
		peakIdentifier.identify(peaksNotFound, peakIdentifierSettings, monitor);
		//
		for(IPeakMSD peak : peaksNotFound) {
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			if(containsMoreThanOneBasePeakIdentification(peakTargets)) {
				IIdentificationTarget peakTargetToRemove = null;
				exitloop:
				for(IIdentificationTarget peakTarget : peakTargets) {
					ILibraryInformation libraryInformation = peakTarget.getLibraryInformation();
					if(libraryInformation.getName().equals(NOT_FOUND)) {
						peakTargetToRemove = peakTarget;
						break exitloop;
					}
				}
				//
				if(peakTargetToRemove != null) {
					peak.getTargets().remove(peakTargetToRemove);
				}
			}
		}
	}

	public void identifyMassSpectra(List<IScanMSD> massSpectrumList, IBasePeakSettings settings, IProgressMonitor monitor) {

		List<IScanMSD> scansNotFound = new ArrayList<>();
		float limitMatchFactor = settings.getLimitMatchFactor();
		//
		for(int i = 0; i < massSpectrumList.size(); i++) {
			IScanMSD massSpectrum = massSpectrumList.get(i);
			if(LimitSupport.doIdentify(massSpectrum.getTargets(), limitMatchFactor)) {
				/*
				 * Identification
				 */
				String name = getIdentification(massSpectrum);
				IComparisonResult comparisonResult = getComparisonResult(settings);
				IIdentificationTarget massSpectrumTarget = targetBuilder.getMassSpectrumTarget(massSpectrum, comparisonResult, IDENTIFIER);
				setLibraryInformationFields(massSpectrumTarget.getLibraryInformation(), name);
				massSpectrum.getTargets().add(massSpectrumTarget);
				/*
				 * Grep all not identified scans.
				 */
				if(massSpectrumTarget.getLibraryInformation().getName().equals(NOT_FOUND)) {
					scansNotFound.add(massSpectrum);
				}
			}
		}
		/*
		 * Post identify NOTFOUND peaks.
		 */
		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings = new MassSpectrumIdentifierSettings();
		setIdentifierSettings(massSpectrumIdentifierSettings);
		setFileIdentifierSettings(massSpectrumIdentifierSettings);
		MassSpectrumIdentifierFile massSpectrumIdentifier = new MassSpectrumIdentifierFile();
		massSpectrumIdentifier.identify(scansNotFound, massSpectrumIdentifierSettings, monitor);
		//
		for(IScanMSD scan : scansNotFound) {
			if(containsMoreThanOneBasePeakIdentificationMassSpectrum(scan.getTargets())) {
				IIdentificationTarget targetToRemove = null;
				exitloop:
				for(IIdentificationTarget target : scan.getTargets()) {
					ILibraryInformation libraryInformation = target.getLibraryInformation();
					if(libraryInformation.getName().equals(NOT_FOUND)) {
						targetToRemove = target;
						break exitloop;
					}
				}
				//
				if(targetToRemove != null) {
					scan.getTargets().remove(targetToRemove);
				}
			}
		}
	}

	/**
	 * Returns identified mass spectra from the database.
	 *
	 * @param identificationTarget
	 * @param monitor
	 * @return {@link IMassSpectra}
	 */
	public IMassSpectra getMassSpectra(IIdentificationTarget identificationTarget) {

		IMassSpectra massSpectra = new MassSpectra();
		if(identificationTarget != null) {
			String identifier = identificationTarget.getIdentifier();
			if(identifier.equals(IDENTIFIER)) {
				String nameTarget = identificationTarget.getLibraryInformation().getName();
				if(nameTarget.equals(SYRINGYL)) {
					/*
					 * Single ion
					 */
					if(syringyl != null) {
						massSpectra.addMassSpectrum(syringyl);
					}
				} else if(nameTarget.equals(GUAIACYL) || nameTarget.equals(PHYDROXYPHENYL)) {
					/*
					 * List mass spectra
					 */
					List<IScanMSD> identifiedMassSpectra = new ArrayList<>();
					if(references != null) {
						for(IScanMSD reference : references.getList()) {
							/*
							 * Search for Guaiacyl
							 */
							if(reference instanceof ILibraryMassSpectrum libraryMassSpectrum) {
								String nameReference = libraryMassSpectrum.getLibraryInformation().getName();
								if(nameReference.equals(nameTarget)) {
									identifiedMassSpectra.add(reference);
								}
							}
						}
					}
					/*
					 * Only add the list if targets have been found.
					 */
					if(!identifiedMassSpectra.isEmpty()) {
						massSpectra.addMassSpectra(identifiedMassSpectra);
					}
				}
			}
		}
		//
		return massSpectra;
	}

	public boolean isValid(IIdentificationTarget identificationTarget) {

		if(identificationTarget != null) {
			return identificationTarget.getIdentifier().equals(IDENTIFIER);
		}
		return false;
	}

	private static IScanMSD getSyringyl() {

		IScanMSD massSpectrum = null;
		try {
			massSpectrum = new ScanMSD();
			massSpectrum.addIon(new Ion(156.0d, 1000.0f));
		} catch(AbundanceLimitExceededException e) {
			logger.warn(e);
		} catch(IonLimitExceededException e) {
			logger.warn(e);
		}
		return massSpectrum;
	}

	private static IMassSpectra getStandardsMassSpectra() {

		File file = new File(PathResolver.getAbsolutePath(PathResolver.REFERENCES));
		IProcessingInfo<IMassSpectra> processingInfo = DatabaseConverter.convert(file, new NullProgressMonitor());
		IMassSpectra massSpectra = processingInfo.getProcessingResult();
		return massSpectra;
	}

	private void setLibraryInformationFields(ILibraryInformation libraryInformation, String name) {

		// TODO implement library info
		libraryInformation.setName(name);
		libraryInformation.setMiscellaneous("");
		libraryInformation.setComments("");
		libraryInformation.setCasNumber("");
	}

	private String getIdentification(IScanMSD massSpectrum) {

		int basePeak = (int)massSpectrum.getBasePeak();
		if(SYRINGYL_BASE.contains(basePeak)) {
			return SYRINGYL;
		} else if(GUAIACYL_BASE.contains(basePeak)) {
			return GUAIACYL;
		} else if(PHYDROXYPHENYL_BASE.contains(basePeak)) {
			return PHYDROXYPHENYL;
		} else if(CARBOHYDRATE_BASE.contains(basePeak)) {
			return CARBOHYDRATE;
		} else {
			return NOT_FOUND;
		}
	}

	private IComparisonResult getComparisonResult(IBasePeakSettings settings) {

		float matchQuality = settings.getMatchQuality();
		return new ComparisonResult(matchQuality, matchQuality, matchQuality, matchQuality);
	}

	private void setIdentifierSettings(IIdentifierSettingsMSD identifierSettings) {

		identifierSettings.setMassSpectrumComparatorId(IIdentifierSettingsMSD.DEFAULT_COMPARATOR_ID);
		identifierSettings.setLimitMatchFactor(100.0f);
		identifierSettings.setDeltaCalculation(DeltaCalculation.NONE);
		identifierSettings.setDeltaWindow(0.0f);
		identifierSettings.setPenaltyCalculation(PenaltyCalculation.NONE);
		identifierSettings.setPenaltyWindow(0.0f);
		identifierSettings.setPenaltyLevelFactor(0.0f);
		identifierSettings.setMaxPenalty(0.0f);
	}

	private void setFileIdentifierSettings(IFileIdentifierSettings fileIdentifierSettings) {

		fileIdentifierSettings.setMassSpectraFiles(massSpectraFiles);
		fileIdentifierSettings.setUsePreOptimization(false);
		fileIdentifierSettings.setThresholdPreOptimization(0.1d);
		fileIdentifierSettings.setNumberOfTargets(10);
		fileIdentifierSettings.setMinMatchFactor(70.0f);
		fileIdentifierSettings.setMinReverseMatchFactor(70.0f);
		fileIdentifierSettings.setAlternateIdentifierId(IDENTIFIER);
	}

	private boolean containsMoreThanOneBasePeakIdentification(Set<IIdentificationTarget> peakTargets) {

		int counter = 0;
		for(IIdentificationTarget peakTarget : peakTargets) {
			if(peakTarget.getIdentifier().equals(IDENTIFIER)) {
				counter++;
				if(counter > 1) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean containsMoreThanOneBasePeakIdentificationMassSpectrum(Set<IIdentificationTarget> targets) {

		int counter = 0;
		for(IIdentificationTarget target : targets) {
			if(target.getIdentifier().equals(IDENTIFIER)) {
				counter++;
				if(counter > 1) {
					return true;
				}
			}
		}
		return false;
	}
}
