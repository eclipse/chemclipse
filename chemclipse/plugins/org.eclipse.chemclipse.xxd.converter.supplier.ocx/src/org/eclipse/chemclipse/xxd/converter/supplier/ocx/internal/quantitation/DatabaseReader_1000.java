/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.quantitation;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.PeakType;
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
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.model.implementation.QuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.CalibrationMethod;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.model.quantitation.QuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.QuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.ResponseSignal;
import org.eclipse.chemclipse.model.quantitation.RetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.RetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.IonTransitionSettings;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.ScanMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.model.quantitation.QuantitationCompound;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class DatabaseReader_1000 extends AbstractDatabaseReader implements IDatabaseReader {

	private static final Logger logger = Logger.getLogger(DatabaseReader_1000.class);

	@Override
	public IQuantitationDatabase convert(File file, IProgressMonitor monitor) throws IOException {

		IQuantitationDatabase quantitationDatabase = null;
		ZipFile zipFile = new ZipFile(file);
		try {
			if(isValidFileFormat(zipFile)) {
				quantitationDatabase = readFromZipFile(zipFile, "", file, monitor);
			}
		} finally {
			zipFile.close();
		}
		//
		return quantitationDatabase;
	}

	private IQuantitationDatabase readFromZipFile(ZipFile zipFile, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		return readZipData(zipFile, directoryPrefix, file, monitor);
	}

	private IQuantitationDatabase readZipData(Object object, String directoryPrefix, File file, IProgressMonitor monitor) throws IOException {

		IQuantitationDatabase quantitationDatabase = new QuantitationDatabase();
		quantitationDatabase.setFile(file);
		quantitationDatabase.setConverterId(CONVERTER_ID);
		//
		boolean closeStream;
		if(object instanceof ZipFile) {
			/*
			 * ZipFile
			 */
			closeStream = true;
		} else if(object instanceof ZipInputStream) {
			/*
			 * ZipInputStream
			 */
			closeStream = false;
		} else {
			return null;
		}
		//
		readContent(getDataInputStream(object, directoryPrefix + IFormat.FILE_QUANTDB), closeStream, quantitationDatabase, monitor);
		//
		return quantitationDatabase;
	}

	private void readContent(DataInputStream dataInputStream, boolean closeStream, IQuantitationDatabase quantitationDatabase, IProgressMonitor monitor) throws IOException {

		quantitationDatabase.setOperator(readString(dataInputStream));
		quantitationDatabase.setDescription(readString(dataInputStream));
		int size = dataInputStream.readInt();
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Read Database", size);
		//
		try {
			for(int i = 0; i < size; i++) {
				//
				String name = readString(dataInputStream);
				String concentrationUnit = readString(dataInputStream);
				CalibrationMethod calibrationMethod = CalibrationMethod.valueOf(readString(dataInputStream));
				String chemicalClass = readString(dataInputStream);
				boolean crossZero = dataInputStream.readBoolean();
				boolean useTIC = dataInputStream.readBoolean();
				//
				List<IQuantitationPeak> quantitationPeaks = readQuantitationPeaks(dataInputStream);
				List<IResponseSignal> concentrationResponseEntriesMSD = readConcentrationResponseEntries(dataInputStream);
				List<IQuantitationSignal> quantitationSignalsMSD = readQuantitationSignals(dataInputStream);
				IRetentionIndexWindow retentionIndexWindow = readRetentionIndexWindow(dataInputStream);
				IRetentionTimeWindow retentionTimeWindow = readRetentionTimeWindow(dataInputStream);
				//
				IQuantitationCompound quantitationCompound = new QuantitationCompound(name, concentrationUnit, retentionTimeWindow.getRetentionTime());
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
				quantitationCompound.getResponseSignals().addAll(concentrationResponseEntriesMSD);
				quantitationCompound.getQuantitationSignals().addAll(quantitationSignalsMSD);
				//
				quantitationCompound.getQuantitationPeaks().addAll(quantitationPeaks);
				/*
				 * DB
				 */
				quantitationDatabase.add(quantitationCompound);
				subMonitor.worked(1);
			}
		} finally {
			SubMonitor.done(subMonitor);
		}
		//
		//
		if(closeStream) {
			dataInputStream.close();
		}
	}

	private List<IQuantitationPeak> readQuantitationPeaks(DataInputStream dataInputStream) throws IOException {

		List<IQuantitationPeak> quantitationPeaks = new ArrayList<IQuantitationPeak>();
		IonTransitionSettings ionTransitionSettings = new IonTransitionSettings();
		//
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			double concentration = dataInputStream.readDouble();
			String concentrationUnit = readString(dataInputStream);
			IPeakMSD referencePeakMSD = readPeak(dataInputStream, ionTransitionSettings);
			//
			IQuantitationPeak quantitationPeak = new QuantitationPeakMSD(referencePeakMSD, concentration, concentrationUnit);
			quantitationPeaks.add(quantitationPeak);
		}
		//
		return quantitationPeaks;
	}

	private List<IResponseSignal> readConcentrationResponseEntries(DataInputStream dataInputStream) throws IOException {

		List<IResponseSignal> responseSignals = new ArrayList<IResponseSignal>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			double signal = dataInputStream.readDouble();
			double concentration = dataInputStream.readDouble();
			double response = dataInputStream.readDouble();
			IResponseSignal responseSignal = new ResponseSignal(signal, concentration, response);
			responseSignals.add(responseSignal);
		}
		return responseSignals;
	}

	private List<IQuantitationSignal> readQuantitationSignals(DataInputStream dataInputStream) throws IOException {

		List<IQuantitationSignal> quantitationSignals = new ArrayList<IQuantitationSignal>();
		int size = dataInputStream.readInt();
		for(int i = 0; i < size; i++) {
			double signal = dataInputStream.readDouble();
			float relativeResponse = dataInputStream.readFloat();
			double uncertainty = dataInputStream.readDouble();
			boolean use = dataInputStream.readBoolean();
			IQuantitationSignal quantitationSignal = new QuantitationSignal(signal, relativeResponse, uncertainty, use);
			quantitationSignals.add(quantitationSignal);
		}
		return quantitationSignals;
	}

	private IRetentionIndexWindow readRetentionIndexWindow(DataInputStream dataInputStream) throws IOException {

		IRetentionIndexWindow retentionIndexWindow = new RetentionIndexWindow();
		retentionIndexWindow.setAllowedNegativeDeviation(dataInputStream.readFloat());
		retentionIndexWindow.setAllowedPositiveDeviation(dataInputStream.readFloat());
		retentionIndexWindow.setRetentionIndex(dataInputStream.readFloat());
		return retentionIndexWindow;
	}

	private IRetentionTimeWindow readRetentionTimeWindow(DataInputStream dataInputStream) throws IOException {

		IRetentionTimeWindow retentionTimeWindow = new RetentionTimeWindow();
		retentionTimeWindow.setAllowedNegativeDeviation(dataInputStream.readFloat());
		retentionTimeWindow.setAllowedPositiveDeviation(dataInputStream.readFloat());
		retentionTimeWindow.setRetentionTime(dataInputStream.readInt());
		return retentionTimeWindow;
	}

	private IPeakMSD readPeak(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException, IllegalArgumentException, PeakException {

		String detectorDescription = readString(dataInputStream);
		String quantifierDescription = readString(dataInputStream);
		boolean activeForAnalysis = dataInputStream.readBoolean();
		String integratorDescription = readString(dataInputStream);
		String modelDescription = readString(dataInputStream);
		PeakType peakType = PeakType.valueOf(readString(dataInputStream));
		int suggestedNumberOfComponents = dataInputStream.readInt();
		//
		float startBackgroundAbundance = dataInputStream.readFloat();
		float stopBackgroundAbundance = dataInputStream.readFloat();
		//
		IPeakMassSpectrum peakMaximum = readPeakMassSpectrum(dataInputStream, ionTransitionSettings);
		//
		int numberOfRetentionTimes = dataInputStream.readInt();
		IPeakIntensityValues intensityValues = new PeakIntensityValues(Float.MAX_VALUE);
		for(int i = 1; i <= numberOfRetentionTimes; i++) {
			int retentionTime = dataInputStream.readInt();
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

	private IPeakMassSpectrum readPeakMassSpectrum(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		short massSpectrometer = dataInputStream.readShort();
		short massSpectrumType = dataInputStream.readShort();
		double precursorIon = dataInputStream.readDouble();
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

	private void readNormalMassSpectrum(IScanMSD massSpectrum, DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		int retentionTime = dataInputStream.readInt();
		int retentionTimeColumn1 = dataInputStream.readInt();
		int retentionTimeColumn2 = dataInputStream.readInt();
		float retentionIndex = dataInputStream.readFloat();
		if(dataInputStream.readBoolean()) {
			int size = dataInputStream.readInt();
			for(int i = 0; i < size; i++) {
				SeparationColumnType separationColumnType = SeparationColumnFactory.getSeparationColumnType(readString(dataInputStream));
				float retentionIndexAdditional = dataInputStream.readFloat();
				massSpectrum.setRetentionIndex(separationColumnType, retentionIndexAdditional);
			}
		}
		int timeSegmentId = dataInputStream.readInt();
		int cycleNumber = dataInputStream.readInt();
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRetentionTimeColumn1(retentionTimeColumn1);
		massSpectrum.setRetentionTimeColumn2(retentionTimeColumn2);
		massSpectrum.setRetentionIndex(retentionIndex);
		massSpectrum.setTimeSegmentId(timeSegmentId);
		massSpectrum.setCycleNumber(cycleNumber);
		//
		int numberOfIons = dataInputStream.readInt();
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			IIon ion = readIon(dataInputStream, ionTransitionSettings);
			massSpectrum.addIon(ion);
		}
		/*
		 * Identification Results
		 */
		readMassSpectrumIdentificationTargets(dataInputStream, massSpectrum);
	}

	private IIon readIon(DataInputStream dataInputStream, IIonTransitionSettings ionTransitionSettings) throws IOException {

		IIon ion;
		//
		double mz = dataInputStream.readDouble();
		float abundance = dataInputStream.readFloat();
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
			String compoundName = readString(dataInputStream);
			double filter1FirstIon = dataInputStream.readDouble();
			double filter1LastIon = dataInputStream.readDouble();
			double filter3FirstIon = dataInputStream.readDouble();
			double filter3LastIon = dataInputStream.readDouble();
			double collisionEnergy = dataInputStream.readDouble();
			double filter1Resolution = dataInputStream.readDouble();
			double filter3Resolution = dataInputStream.readDouble();
			int transitionGroup = dataInputStream.readInt();
			int dwell = dataInputStream.readInt();
			//
			IIonTransition ionTransition = ionTransitionSettings.getIonTransition(compoundName, filter1FirstIon, filter1LastIon, filter3FirstIon, filter3LastIon, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
			ionTransition.setDwell(dwell);
			ion = new Ion(mz, abundance, ionTransition);
		}
		return ion;
	}

	private void readMassSpectrumIdentificationTargets(DataInputStream dataInputStream, IScanMSD massSpectrum) throws IOException {

		int numberOfMassSpectrumTargets = dataInputStream.readInt();
		for(int i = 1; i <= numberOfMassSpectrumTargets; i++) {
			//
			String identifier = readString(dataInputStream);
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			String casNumber = readString(dataInputStream);
			String comments = readString(dataInputStream);
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream);
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream);
			Set<String> synonyms = new HashSet<String>();
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream);
			String smiles = readString(dataInputStream);
			String inChI = readString(dataInputStream);
			double molWeight = dataInputStream.readDouble();
			float matchFactor = dataInputStream.readFloat();
			float matchFactorDirect = dataInputStream.readFloat();
			float reverseMatchFactor = dataInputStream.readFloat();
			float reverseMatchFactorDirect = dataInputStream.readFloat();
			float probability = dataInputStream.readFloat();
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
				identificationEntry.setVerified(manuallyVerified);
				massSpectrum.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<IIntegrationEntry>();
		int numberOfIntegrationEntries = dataInputStream.readInt();
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double signal = dataInputStream.readDouble();
			double integratedArea = dataInputStream.readDouble();
			IIntegrationEntry integrationEntry = new IntegrationEntry(signal, integratedArea);
			integrationEntries.add(integrationEntry);
		}
		return integrationEntries;
	}

	private void readPeakIdentificationTargets(DataInputStream dataInputStream, IPeakMSD peak) throws IOException {

		int numberOfPeakTargets = dataInputStream.readInt();
		for(int i = 1; i <= numberOfPeakTargets; i++) {
			//
			String identifier = readString(dataInputStream);
			boolean manuallyVerified = dataInputStream.readBoolean();
			//
			String casNumber = readString(dataInputStream);
			String comments = readString(dataInputStream);
			String referenceIdentifier = readString(dataInputStream);
			String miscellaneous = readString(dataInputStream);
			String database = readString(dataInputStream);
			String contributor = readString(dataInputStream);
			String name = readString(dataInputStream);
			Set<String> synonyms = new HashSet<String>();
			int numberOfSynonyms = dataInputStream.readInt();
			for(int j = 0; j < numberOfSynonyms; j++) {
				synonyms.add(readString(dataInputStream));
			}
			String formula = readString(dataInputStream);
			String smiles = readString(dataInputStream);
			String inChI = readString(dataInputStream);
			double molWeight = dataInputStream.readDouble();
			float matchFactor = dataInputStream.readFloat();
			float matchFactorDirect = dataInputStream.readFloat();
			float reverseMatchFactor = dataInputStream.readFloat();
			float reverseMatchFactorDirect = dataInputStream.readFloat();
			float probability = dataInputStream.readFloat();
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
				identificationEntry.setVerified(manuallyVerified);
				peak.getTargets().add(identificationEntry);
			} catch(ReferenceMustNotBeNullException e) {
				logger.warn(e);
			}
		}
	}

	private void readPeakQuantitationEntries(DataInputStream dataInputStream, IPeakMSD peak) throws IOException {

		int numberOfQuantitationEntries = dataInputStream.readInt();
		for(int i = 1; i <= numberOfQuantitationEntries; i++) {
			//
			double signal = dataInputStream.readDouble();
			String name = readString(dataInputStream);
			String chemicalClass = readString(dataInputStream);
			double concentration = dataInputStream.readDouble();
			String concentrationUnit = readString(dataInputStream);
			double area = dataInputStream.readDouble();
			String calibrationMethod = readString(dataInputStream);
			boolean usedCrossZero = dataInputStream.readBoolean();
			String description = readString(dataInputStream);
			//
			IQuantitationEntry quantitationEntry = new QuantitationEntry(name, concentration, concentrationUnit, area);
			quantitationEntry.setSignal(signal);
			quantitationEntry.setChemicalClass(chemicalClass);
			quantitationEntry.setCalibrationMethod(calibrationMethod);
			quantitationEntry.setUsedCrossZero(usedCrossZero);
			quantitationEntry.setDescription(description);
			//
			peak.addQuantitationEntry(quantitationEntry);
		}
	}

	private boolean isValidFileFormat(ZipFile zipFile) throws IOException {

		boolean isValid = false;
		DataInputStream dataInputStream;
		//
		dataInputStream = getDataInputStream(zipFile, IFormat.FILE_VERSION);
		String version = readString(dataInputStream);
		if(version.equals(IFormat.QUANTDB_VERSION_0001)) {
			isValid = true;
		}
		dataInputStream.close();
		//
		return isValid;
	}
}
