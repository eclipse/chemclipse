/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.QuantDatabase;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.ConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.RetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.RetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.IonTransitionIsNullException;
import org.eclipse.chemclipse.msd.model.implementation.IntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.IonTransitionSettings;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;

public class QuantDatabaseReader {

	private static final Logger logger = Logger.getLogger(QuantDatabaseReader.class);

	public static IQuantDatabase read(File file) throws Exception {

		IQuantDatabase quantDatabase = new QuantDatabase();
		DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
		//
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			/*
			 * Read the data.
			 */
			String name = readString(dataInputStream);
			String concentrationUnit = readString(dataInputStream);
			CalibrationMethod calibrationMethod = CalibrationMethod.valueOf(readString(dataInputStream));
			String chemicalClass = readString(dataInputStream);
			boolean crossZero = dataInputStream.readBoolean();
			boolean useTIC = dataInputStream.readBoolean();
			//
			List<IQuantitationPeakMSD> quantitationPeaks = readQuantitationPeaks(dataInputStream);
			List<IConcentrationResponseEntry> concentrationResponseEntriesMSD = readConcentrationResponseEntries(dataInputStream);
			List<IQuantitationSignal> quantitationSignalsMSD = readQuantitationSignals(dataInputStream);
			IRetentionIndexWindow retentionIndexWindow = readRetentionIndexWindow(dataInputStream);
			IRetentionTimeWindow retentionTimeWindow = readRetentionTimeWindow(dataInputStream);
			//
			IQuantitationCompoundMSD quantitationCompound = new QuantitationCompoundMSD(name, concentrationUnit, retentionTimeWindow.getRetentionTime());
			quantitationCompound.setCalibrationMethod(calibrationMethod);
			quantitationCompound.setChemicalClass(chemicalClass);
			quantitationCompound.setUseCrossZero(crossZero);
			quantitationCompound.setUseTIC(useTIC);
			//
			quantitationCompound.getRetentionIndexWindow().setAllowedNegativeDeviation(retentionIndexWindow.getAllowedNegativeDeviation());
			quantitationCompound.getRetentionIndexWindow().setAllowedPositiveDeviation(retentionIndexWindow.getAllowedPositiveDeviation());
			quantitationCompound.getRetentionIndexWindow().setRetentionIndex(retentionIndexWindow.getRetentionIndex());
			//
			quantitationCompound.getRetentionTimeWindow().setAllowedNegativeDeviation(retentionTimeWindow.getAllowedNegativeDeviation());
			quantitationCompound.getRetentionTimeWindow().setAllowedPositiveDeviation(retentionTimeWindow.getAllowedPositiveDeviation());
			//
			quantitationCompound.getConcentrationResponseEntriesMSD().addAll(concentrationResponseEntriesMSD);
			quantitationCompound.getQuantitationSignalsMSD().addAll(quantitationSignalsMSD);
			//
			quantDatabase.addQuantitationCompound(quantitationCompound);
			quantDatabase.getQuantitationPeaks(quantitationCompound).addAll(quantitationPeaks);
		}
		//
		return quantDatabase;
	}

	private static List<IQuantitationPeakMSD> readQuantitationPeaks(DataInputStream dataInputStream) throws Exception {

		List<IQuantitationPeakMSD> quantitationPeaks = new ArrayList<IQuantitationPeakMSD>();
		IonTransitionSettings ionTransitionSettings = new IonTransitionSettings();
		//
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			double concentration = dataInputStream.readDouble();
			String concentrationUnit = readString(dataInputStream);
			IPeakMSD referencePeakMSD = readPeak(dataInputStream, ionTransitionSettings);
			//
			IQuantitationPeakMSD quantitationPeak = new QuantitationPeakMSD(referencePeakMSD, concentration, concentrationUnit);
			quantitationPeaks.add(quantitationPeak);
		}
		//
		return quantitationPeaks;
	}

	private static List<IConcentrationResponseEntry> readConcentrationResponseEntries(DataInputStream dataInputStream) throws Exception {

		List<IConcentrationResponseEntry> concentrationResponseEntriesMSD = new ArrayList<IConcentrationResponseEntry>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			double ion = dataInputStream.readDouble();
			double concentration = dataInputStream.readDouble();
			double response = dataInputStream.readDouble();
			IConcentrationResponseEntry concentrationResponseEntryMSD = new ConcentrationResponseEntry(ion, concentration, response);
			concentrationResponseEntriesMSD.add(concentrationResponseEntryMSD);
		}
		return concentrationResponseEntriesMSD;
	}

	private static List<IQuantitationSignal> readQuantitationSignals(DataInputStream dataInputStream) throws Exception {

		List<IQuantitationSignal> quantitationSignalsMSD = new ArrayList<IQuantitationSignal>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			double ion = dataInputStream.readDouble();
			float relativeResponse = dataInputStream.readFloat();
			double uncertainty = dataInputStream.readDouble();
			boolean use = dataInputStream.readBoolean();
			IQuantitationSignal quantitationSignalMSD = new QuantitationSignal(ion, relativeResponse, uncertainty, use);
			quantitationSignalsMSD.add(quantitationSignalMSD);
		}
		return quantitationSignalsMSD;
	}

	private static IRetentionIndexWindow readRetentionIndexWindow(DataInputStream dataInputStream) throws Exception {

		IRetentionIndexWindow retentionIndexWindow = new RetentionIndexWindow();
		retentionIndexWindow.setAllowedNegativeDeviation(dataInputStream.readFloat());
		retentionIndexWindow.setAllowedPositiveDeviation(dataInputStream.readFloat());
		retentionIndexWindow.setRetentionIndex(dataInputStream.readFloat());
		return retentionIndexWindow;
	}

	private static IRetentionTimeWindow readRetentionTimeWindow(DataInputStream dataInputStream) throws Exception {

		IRetentionTimeWindow retentionTimeWindow = new RetentionTimeWindow();
		retentionTimeWindow.setAllowedNegativeDeviation(dataInputStream.readFloat());
		retentionTimeWindow.setAllowedPositiveDeviation(dataInputStream.readFloat());
		retentionTimeWindow.setRetentionTime(dataInputStream.readInt());
		return retentionTimeWindow;
	}

	private static IPeakMSD readPeak(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException, IllegalArgumentException, PeakException {

		String detectorDescription = readString(dataInputStream); // Detector Description
		String quantifierDescription = readString(dataInputStream);
		boolean activeForAnalysis = dataInputStream.readBoolean();
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		int suggestedNumberOfComponents = dataInputStream.readInt(); // Suggest Number Of Components
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		IPeakMassSpectrum peakMaximum = readPeakMassSpectrum(dataInputStream, ionTransitionSettings);
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
		peak.setQuantifierDescription(quantifierDescription);
		peak.setActiveForAnalysis(activeForAnalysis);
		peak.setIntegratorDescription(integratorDescription);
		peak.setModelDescription(modelDescription);
		peak.setPeakType(peakType);
		peak.setSuggestedNumberOfComponents(suggestedNumberOfComponents);
		//
		List<IIntegrationEntry> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
		/*
		 * Identification Results
		 */
		readPeakIdentificationTargets(dataInputStream, peak);
		/*
		 * Quantitation Results
		 */
		readPeakQuantitationEntries(dataInputStream, peak);
		/*
		 * Optimized Mass Spectrum
		 */
		boolean readOptimizedMassSpectrum = dataInputStream.readBoolean();
		if(readOptimizedMassSpectrum) {
			IScanMSD optimizedMassSpectrum = new ScanMSD();
			readNormalMassSpectrum(optimizedMassSpectrum, dataInputStream, ionTransitionSettings);
			peakMaximum.setOptimizedMassSpectrum(optimizedMassSpectrum);
		}
		//
		return peak;
	}

	private static IPeakMassSpectrum readPeakMassSpectrum(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		short massSpectrometer = dataInputStream.readShort(); // Mass Spectrometer
		short massSpectrumType = dataInputStream.readShort(); // Mass Spectrum Type
		double precursorIon = dataInputStream.readDouble(); // Precursor Ion (0 if MS1 or none has been selected)
		//
		IPeakMassSpectrum massSpectrum = new PeakMassSpectrum();
		massSpectrum.setMassSpectrometer(massSpectrometer);
		massSpectrum.setMassSpectrumType(massSpectrumType);
		massSpectrum.setPrecursorIon(precursorIon);
		//
		readNormalMassSpectrum(massSpectrum, dataInputStream, ionTransitionSettings);
		//
		return massSpectrum;
	}

	private static void readNormalMassSpectrum(IScanMSD massSpectrum, DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		int retentionTime = dataInputStream.readInt(); // Retention Time
		int retentionTimeColumn1 = dataInputStream.readInt();
		int retentionTimeColumn2 = dataInputStream.readInt();
		float retentionIndex = dataInputStream.readFloat(); // Retention Index
		if(dataInputStream.readBoolean()) {
			int size = dataInputStream.readInt();
			for(int i = 0; i < size; i++) {
				RetentionIndexType retentionIndexType = RetentionIndexType.valueOf(readString(dataInputStream));
				float retentionIndexAdditional = dataInputStream.readFloat();
				massSpectrum.setRetentionIndex(retentionIndexType, retentionIndexAdditional);
			}
		}
		int timeSegmentId = dataInputStream.readInt(); // Time Segment Id
		int cycleNumber = dataInputStream.readInt(); // Cycle Number
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRetentionTimeColumn1(retentionTimeColumn1);
		massSpectrum.setRetentionTimeColumn2(retentionTimeColumn2);
		massSpectrum.setRetentionIndex(retentionIndex);
		massSpectrum.setTimeSegmentId(timeSegmentId);
		massSpectrum.setCycleNumber(cycleNumber);
		//
		int numberOfIons = dataInputStream.readInt(); // Number of ions
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			try {
				IIon ion = readIon(dataInputStream, ionTransitionSettings);
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
		readMassSpectrumIdentificationTargets(dataInputStream, massSpectrum);
	}

	private static IIon readIon(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException, AbundanceLimitExceededException, IonLimitExceededException, IonTransitionIsNullException {

		IIon ion;
		//
		double mz = dataInputStream.readDouble(); // m/z
		float abundance = dataInputStream.readFloat(); // Abundance
		/*
		 * Ion Transition
		 */
		int transition = dataInputStream.readInt();
		if(transition == 0) {
			ion = new Ion(mz, abundance);
		} else {
			/*
			 * parent m/z start, ...
			 */
			String compoundName = readString(dataInputStream); // compound name
			double filter1FirstIon = dataInputStream.readDouble(); // parent m/z start
			double filter1LastIon = dataInputStream.readDouble(); // parent m/z stop
			double filter3FirstIon = dataInputStream.readDouble(); // daughter m/z start
			double filter3LastIon = dataInputStream.readDouble(); // daughter m/z stop
			double collisionEnergy = dataInputStream.readDouble(); // collision energy
			double filter1Resolution = dataInputStream.readDouble(); // q1 resolution
			double filter3Resolution = dataInputStream.readDouble(); // q3 resolution
			int transitionGroup = dataInputStream.readInt(); // transition group
			int dwell = dataInputStream.readInt(); // dwell
			//
			IIonTransition ionTransition = ionTransitionSettings.getIonTransition(compoundName, filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
			ionTransition.setDwell(dwell);
			ion = new Ion(mz, abundance, ionTransition);
		}
		return ion;
	}

	private static void readMassSpectrumIdentificationTargets(DataInputStream dataInputStream, IScanMSD massSpectrum) throws IOException {

		int numberOfMassSpectrumTargets = dataInputStream.readInt(); // Number Mass Spectrum Targets
		for(int i = 1; i <= numberOfMassSpectrumTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			String smiles = readString(dataInputStream); // SMILES
			String inChI = readString(dataInputStream); // InChI
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float matchFactorDirect = dataInputStream.readFloat(); // Match Factor Direct
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float reverseMatchFactorDirect = dataInputStream.readFloat(); // Reverse Match Factor Direct
			float probability = dataInputStream.readFloat(); // Probability
			boolean isMatch = dataInputStream.readBoolean();
			//
			ILibraryInformation libraryInformation = new LibraryInformation();
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setReferenceIdentifier(referenceIdentifier);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setDatabase(database);
			libraryInformation.setContributor(contributor);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setSmiles(smiles);
			libraryInformation.setInChI(inChI);
			libraryInformation.setMolWeight(molWeight);
			//
			IComparisonResult comparisonResult = new ComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
			comparisonResult.setMatch(isMatch);
			//
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				identificationEntry.setManuallyVerified(manuallyVerified);
				massSpectrum.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private static List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double ion = dataInputStream.readDouble(); // m/z
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntryMSD integrationEntry = new IntegrationEntryMSD(ion, integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	private static void readPeakIdentificationTargets(DataInputStream dataInputStream, IPeakMSD peak) throws IOException {

		int numberOfPeakTargets = dataInputStream.readInt(); // Number Peak Targets
		for(int i = 1; i <= numberOfPeakTargets; i++) {
			//
			String identifier = readString(dataInputStream); // Identifier
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			String casNumber = readString(dataInputStream); // CAS-Number
			String comments = readString(dataInputStream); // Comments
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream); // Miscellaneous
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream); // Name
			Set<String> synonyms = new HashSet<String>(); // Synonyms
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream); // Formula
			String smiles = readString(dataInputStream); // SMILES
			String inChI = readString(dataInputStream); // InChI
			double molWeight = dataInputStream.readDouble(); // Mol Weight
			float matchFactor = dataInputStream.readFloat(); // Match Factor
			float matchFactorDirect = dataInputStream.readFloat(); // Match Factor Direct
			float reverseMatchFactor = dataInputStream.readFloat(); // Reverse Match Factor
			float reverseMatchFactorDirect = dataInputStream.readFloat(); // Reverse Match Factor Direct
			float probability = dataInputStream.readFloat(); // Probability
			boolean isMatch = dataInputStream.readBoolean();
			//
			IPeakLibraryInformation libraryInformation = new PeakLibraryInformation();
			libraryInformation.setCasNumber(casNumber);
			libraryInformation.setComments(comments);
			libraryInformation.setReferenceIdentifier(referenceIdentifier);
			libraryInformation.setMiscellaneous(miscellaneous);
			libraryInformation.setDatabase(database);
			libraryInformation.setContributor(contributor);
			libraryInformation.setName(name);
			libraryInformation.setSynonyms(synonyms);
			libraryInformation.setFormula(formula);
			libraryInformation.setSmiles(smiles);
			libraryInformation.setInChI(inChI);
			libraryInformation.setMolWeight(molWeight);
			//
			IComparisonResult comparisonResult = new PeakComparisonResult(matchFactor, reverseMatchFactor, matchFactorDirect, reverseMatchFactorDirect, probability);
			comparisonResult.setMatch(isMatch);
			//
			try {
				IIdentificationTarget identificationEntry = new IdentificationTarget(libraryInformation, comparisonResult);
				identificationEntry.setIdentifier(identifier);
				identificationEntry.setManuallyVerified(manuallyVerified);
				peak.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private static void readPeakQuantitationEntries(DataInputStream dataInputStream, IPeakMSD peak) throws IOException {

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

	private static String readString(DataInputStream dataInputStream) throws IOException {

		int length = dataInputStream.readInt();
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= length; i++) {
			builder.append(String.valueOf(dataInputStream.readChar()));
		}
		return builder.toString();
	}
}
