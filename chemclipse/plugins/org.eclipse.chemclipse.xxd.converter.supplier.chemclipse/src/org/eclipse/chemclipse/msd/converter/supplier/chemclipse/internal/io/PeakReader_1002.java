/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.Peaks;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.model.chromatogram.VendorIon;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonTransitionIsNullException;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.implementation.IonTransitionSettings;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationEntryMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class PeakReader_1002 extends AbstractZipReader implements IPeakReader {

	private static final Logger logger = Logger.getLogger(PeakReader_1002.class);

	@Override
	public IProcessingInfo read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		ZipFile zipFile = new ZipFile(file);
		IProcessingInfo processingInfo = new ProcessingInfo();
		try {
			IPeaks peaks = readPeaksFromZipFile(zipFile, monitor);
			processingInfo.setProcessingResult(peaks);
		} finally {
			zipFile.close();
		}
		return processingInfo;
	}

	private IPeaks readPeaksFromZipFile(ZipFile zipFile, IProgressMonitor monitor) throws IOException {

		IPeaks peaks = new Peaks();
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_PEAKS_MSD);
		//
		int numberOfPeaks = dataInputStream.readInt(); // Number of Peaks
		for(int i = 1; i <= numberOfPeaks; i++) {
			// monitor.subTask(IConstants.IMPORT_PEAK + i);
			try {
				IPeakMSD peak = readPeak(dataInputStream, monitor);
				peaks.addPeak(peak);
			} catch(IllegalArgumentException e) {
				logger.warn(e);
			} catch(PeakException e) {
				logger.warn(e);
			}
		}
		dataInputStream.close();
		/*
		 * Return the peaks instance.
		 */
		return peaks;
	}

	private IPeakMSD readPeak(DataInputStream dataInputStream, IProgressMonitor monitor) throws IOException, IllegalArgumentException, PeakException {

		IIonTransitionSettings ionTransitionSettings = new IonTransitionSettings();
		//
		String detectorDescription = readString(dataInputStream); // Detector Description
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		int suggestedNumberOfComponents = dataInputStream.readInt(); // Suggest Number Of Components
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		IPeakMassSpectrum peakMaximum = readPeakMassSpectrum(dataInputStream, ionTransitionSettings, monitor);
		//
		int numberOfRetentionTimes = dataInputStream.readInt(); // Number Retention Times
		IPeakIntensityValues intensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		for(int i = 1; i <= numberOfRetentionTimes; i++) {
			int retentionTime = dataInputStream.readInt(); // Retention Time
			float relativeIntensity = dataInputStream.readFloat(); // Intensity
			intensityValues.addIntensityValue(retentionTime, relativeIntensity);
		}
		intensityValues.normalize();
		//
		IPeakModelMSD peakModel = new PeakModelMSD(peakMaximum, intensityValues, startBackgroundAbundance, stopBackgroundAbundance);
		IPeakMSD peak = new PeakMSD(peakModel);
		peak.setDetectorDescription(detectorDescription);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		peak.setSuggestedNumberOfComponents(suggestedNumberOfComponents);
		//
		List<IIntegrationEntryMSD> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
		/*
		 * Identification Results
		 */
		readPeakIdentificationTargets(dataInputStream, peak, monitor);
		/*
		 * Quantitation Results
		 */
		readPeakQuantitationEntries(dataInputStream, peak, monitor);
		//
		return peak;
	}

	private IPeakMassSpectrum readPeakMassSpectrum(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings, IProgressMonitor monitor) throws IOException {

		short massSpectrometer = dataInputStream.readShort(); // Mass Spectrometer
		short massSpectrumType = dataInputStream.readShort(); // Mass Spectrum Type
		double precursorIon = dataInputStream.readDouble(); // Precursor Ion (0 if MS1 or none has been selected)
		IPeakMassSpectrum massSpectrum = new PeakMassSpectrum();
		massSpectrum.setMassSpectrometer(massSpectrometer);
		massSpectrum.setMassSpectrumType(massSpectrumType);
		massSpectrum.setPrecursorIon(precursorIon);
		int retentionTime = dataInputStream.readInt(); // Retention Time
		float retentionIndex = dataInputStream.readFloat(); // Retention Index
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRetentionIndex(retentionIndex);
		int numberOfIons = dataInputStream.readInt(); // Number of ions
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			try {
				IVendorIon ion = readIon(dataInputStream, ionTransitionSettings);
				massSpectrum.addIon(ion);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			} catch(IonTransitionIsNullException e) {
				logger.warn(e);
			}
		}
		/*
		 * Identification Results
		 */
		readMassSpectrumIdentificationTargets(dataInputStream, massSpectrum, monitor);
		//
		return massSpectrum;
	}

	private void readMassSpectrumIdentificationTargets(DataInputStream dataInputStream, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		int numberOfMassSpectrumTargets = dataInputStream.readInt(); // Number Mass Spectrum Targets
		for(int i = 1; i <= numberOfMassSpectrumTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			//
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			//
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float probability = dataInputStream.readFloat(); // Probability
			//
			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setMolWeight(molWeight);
			IComparisonResult comparisonResult = new ComparisonResult(matchFactor, reverseMatchFactor, 0.0f, 0.0f, probability);
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				massSpectrum.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private IVendorIon readIon(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException, AbundanceLimitExceededException, IonLimitExceededException, IonTransitionIsNullException {

		IVendorIon ion;
		//
		double mz = dataInputStream.readDouble(); // m/z
		float abundance = dataInputStream.readFloat(); // Abundance
		/*
		 * Ion Transition
		 */
		int transition = dataInputStream.readInt();
		if(transition == 0) {
			ion = new VendorIon(mz, abundance);
		} else {
			/*
			 * parent m/z start, ...
			 */
			double filter1FirstIon = dataInputStream.readDouble(); // parent m/z start
			double filter1LastIon = dataInputStream.readDouble(); // parent m/z stop
			double filter3FirstIon = dataInputStream.readDouble(); // daughter m/z start
			double filter3LastIon = dataInputStream.readDouble(); // daughter m/z stop
			double collisionEnergy = dataInputStream.readDouble(); // collision energy
			double filter1Resolution = dataInputStream.readDouble(); // q1 resolution
			double filter3Resolution = dataInputStream.readDouble(); // q3 resolution
			int transitionGroup = dataInputStream.readInt(); // transition group
			//
			IIonTransition ionTransition = ionTransitionSettings.getIonTransition(filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
			ion = new VendorIon(mz, abundance, ionTransition);
		}
		return ion;
	}

	private List<IIntegrationEntryMSD> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntryMSD> integrationEntries = new ArrayList<IIntegrationEntryMSD>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double ion = dataInputStream.readDouble(); // m/z
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntryMSD integrationEntry = new IntegrationEntryMSD(ion, integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	private void readPeakIdentificationTargets(DataInputStream dataInputStream, IPeakMSD peak, IProgressMonitor monitor) throws IOException {

		int numberOfPeakTargets = dataInputStream.readInt(); // Number Peak Targets
		for(int i = 1; i <= numberOfPeakTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			//
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			//
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float probability = dataInputStream.readFloat(); // Probability
			//
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setMolWeight(molWeight);
			IPeakComparisonResult comparisonResult = new PeakComparisonResult(matchFactor, reverseMatchFactor, 0.0f, 0.0f, probability);
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				peak.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private void readPeakQuantitationEntries(DataInputStream dataInputStream, IPeakMSD peak, IProgressMonitor monitor) throws IOException {

		int numberOfQuantitationEntries = dataInputStream.readInt(); // Number Quantitation Entries
		for(int i = 1; i <= numberOfQuantitationEntries; i++) {
			//
			String name = readString(dataInputStream); // Name
			String chemicalClass = readString(dataInputStream); // Chemical Class
			double concentration = dataInputStream.readDouble(); // Concentration
			String concentrationUnit = readString(dataInputStream); // Concentration Unit
			double area = dataInputStream.readDouble(); // Area
			String calibrationMethod = readString(dataInputStream); // Calibration Method
			boolean usedCrossZero = dataInputStream.readBoolean(); // Used Cross Zero
			String description = readString(dataInputStream); // Description
			/*
			 * Only MSD stores an ion.
			 */
			IQuantitationEntry quantitationEntry;
			boolean isMSD = dataInputStream.readBoolean(); // Ion value is stored or not.
			if(isMSD) {
				double ion = dataInputStream.readDouble(); // Ion
				quantitationEntry = new QuantitationEntryMSD(name, concentration, concentrationUnit, area, ion);
			} else {
				quantitationEntry = new QuantitationEntry(name, concentration, concentrationUnit, area);
			}
			//
			quantitationEntry.setChemicalClass(chemicalClass);
			quantitationEntry.setCalibrationMethod(calibrationMethod);
			quantitationEntry.setUsedCrossZero(usedCrossZero);
			quantitationEntry.setDescription(description);
			//
			peak.addQuantitationEntry(quantitationEntry);
		}
	}
}
