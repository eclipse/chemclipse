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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.IQuantDatabase;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntriesMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationCompoundMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationPeakMSD;
import org.eclipse.chemclipse.msd.model.core.quantitation.IQuantitationSignalsMSD;

public class QuantDatabaseWriter {

	public static void write(IQuantDatabase quantDatabase, File file) throws Exception {

		DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
		//
		List<IQuantitationCompoundMSD> quantitationCompounds = quantDatabase.getQuantitationCompounds();
		dataOutputStream.writeInt(quantitationCompounds.size());
		for(IQuantitationCompoundMSD quantitationCompound : quantitationCompounds) {
			/*
			 * Write the data.
			 */
			writeString(dataOutputStream, quantitationCompound.getName());
			writeString(dataOutputStream, quantitationCompound.getConcentrationUnit());
			writeString(dataOutputStream, quantitationCompound.getCalibrationMethod().toString());
			writeString(dataOutputStream, quantitationCompound.getChemicalClass());
			dataOutputStream.writeBoolean(quantitationCompound.isCrossZero());
			dataOutputStream.writeBoolean(quantitationCompound.isUseTIC());
			//
			List<IQuantitationPeakMSD> quantitationPeaks = quantDatabase.getQuantitationPeaks(quantitationCompound);
			writeQuantitationPeaks(dataOutputStream, quantitationPeaks);
			//
			IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD = quantitationCompound.getConcentrationResponseEntriesMSD();
			writeConcentrationResponseEntries(dataOutputStream, concentrationResponseEntriesMSD);
			//
			IQuantitationSignalsMSD quantitationSignalsMSD = quantitationCompound.getQuantitationSignalsMSD();
			writeQuantitationSignals(dataOutputStream, quantitationSignalsMSD);
			//
			IRetentionIndexWindow retentionIndexWindow = quantitationCompound.getRetentionIndexWindow();
			writeRetentionIndexWindow(dataOutputStream, retentionIndexWindow);
			//
			IRetentionTimeWindow retentionTimeWindow = quantitationCompound.getRetentionTimeWindow();
			writeRetentionTimeWindow(dataOutputStream, retentionTimeWindow);
		}
	}

	private static void writeQuantitationPeaks(DataOutputStream dataOutputStream, List<IQuantitationPeakMSD> quantitationPeaks) throws Exception {

		dataOutputStream.writeInt(quantitationPeaks.size());
		for(IQuantitationPeakMSD quantitationPeak : quantitationPeaks) {
			dataOutputStream.writeDouble(quantitationPeak.getConcentration());
			writeString(dataOutputStream, quantitationPeak.getConcentrationUnit());
			IPeakMSD peakMSD = quantitationPeak.getReferencePeakMSD();
			writePeak(dataOutputStream, peakMSD);
		}
	}

	private static void writeConcentrationResponseEntries(DataOutputStream dataOutputStream, IConcentrationResponseEntriesMSD concentrationResponseEntriesMSD) throws Exception {

		dataOutputStream.writeInt(concentrationResponseEntriesMSD.size());
		for(IConcentrationResponseEntry concentrationResponseEntryMSD : concentrationResponseEntriesMSD.getList()) {
			dataOutputStream.writeDouble(concentrationResponseEntryMSD.getConcentration());
			dataOutputStream.writeDouble(concentrationResponseEntryMSD.getSignal());
			dataOutputStream.writeDouble(concentrationResponseEntryMSD.getResponse());
		}
	}

	private static void writeQuantitationSignals(DataOutputStream dataOutputStream, IQuantitationSignalsMSD quantitationSignalsMSD) throws Exception {

		dataOutputStream.writeInt(quantitationSignalsMSD.size());
		for(IQuantitationSignal quantitationSignalMSD : quantitationSignalsMSD.getList()) {
			dataOutputStream.writeDouble(quantitationSignalMSD.getSignal());
			dataOutputStream.writeFloat(quantitationSignalMSD.getRelativeResponse());
			dataOutputStream.writeDouble(quantitationSignalMSD.getUncertainty());
			dataOutputStream.writeBoolean(quantitationSignalMSD.isUse());
		}
	}

	private static void writeRetentionIndexWindow(DataOutputStream dataOutputStream, IRetentionIndexWindow retentionIndexWindow) throws Exception {

		dataOutputStream.writeFloat(retentionIndexWindow.getAllowedNegativeDeviation());
		dataOutputStream.writeFloat(retentionIndexWindow.getAllowedPositiveDeviation());
		dataOutputStream.writeFloat(retentionIndexWindow.getRetentionIndex());
	}

	private static void writeRetentionTimeWindow(DataOutputStream dataOutputStream, IRetentionTimeWindow retentionTimeWindow) throws Exception {

		dataOutputStream.writeFloat(retentionTimeWindow.getAllowedNegativeDeviation());
		dataOutputStream.writeFloat(retentionTimeWindow.getAllowedPositiveDeviation());
		dataOutputStream.writeInt(retentionTimeWindow.getRetentionTime());
	}

	private static void writePeak(DataOutputStream dataOutputStream, IPeakMSD peak) throws IOException {

		IPeakModelMSD peakModel = peak.getPeakModel();
		//
		writeString(dataOutputStream, peak.getDetectorDescription()); // Detector Description
		writeString(dataOutputStream, peak.getQuantifierDescription());
		dataOutputStream.writeBoolean(peak.isActiveForAnalysis());
		writeString(dataOutputStream, peak.getIntegratorDescription()); // Integrator Description
		writeString(dataOutputStream, peak.getModelDescription()); // Model Description
		writeString(dataOutputStream, peak.getPeakType().toString()); // Peak Type
		dataOutputStream.writeInt(peak.getSuggestedNumberOfComponents()); // Suggest Number Of Components
		//
		dataOutputStream.writeFloat(peakModel.getBackgroundAbundance(peakModel.getStartRetentionTime())); // Start Background Abundance
		dataOutputStream.writeFloat(peakModel.getBackgroundAbundance(peakModel.getStopRetentionTime())); // Stop Background Abundance
		//
		IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
		writeMassSpectrum(dataOutputStream, massSpectrum); // Mass Spectrum
		//
		List<Integer> retentionTimes = peakModel.getRetentionTimes();
		dataOutputStream.writeInt(retentionTimes.size()); // Number Retention Times
		for(int retentionTime : retentionTimes) {
			dataOutputStream.writeInt(retentionTime); // Retention Time
			dataOutputStream.writeFloat(peakModel.getPeakAbundance(retentionTime)); // Intensity
		}
		//
		List<IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
		writeIntegrationEntries(dataOutputStream, integrationEntries);
		/*
		 * Identification Results
		 */
		Set<IIdentificationTarget> peakTargets = peak.getTargets();
		dataOutputStream.writeInt(peakTargets.size()); // Number Peak Targets
		for(IIdentificationTarget peakTarget : peakTargets) {
			if(peakTarget instanceof IIdentificationTarget) {
				IIdentificationTarget identificationEntry = peakTarget;
				writeIdentificationEntry(dataOutputStream, identificationEntry);
			}
		}
		/*
		 * Quantitation Results
		 */
		List<IQuantitationEntry> quantitationEntries = peak.getQuantitationEntries();
		dataOutputStream.writeInt(quantitationEntries.size()); // Number Quantitation Entries
		for(IQuantitationEntry quantitationEntry : quantitationEntries) {
			writeString(dataOutputStream, quantitationEntry.getName()); // Name
			writeString(dataOutputStream, quantitationEntry.getChemicalClass()); // Chemical Class
			dataOutputStream.writeDouble(quantitationEntry.getConcentration()); // Concentration
			writeString(dataOutputStream, quantitationEntry.getConcentrationUnit()); // Concentration Unit
			dataOutputStream.writeDouble(quantitationEntry.getArea()); // Area
			writeString(dataOutputStream, quantitationEntry.getCalibrationMethod()); // Calibration Method
			dataOutputStream.writeBoolean(quantitationEntry.getUsedCrossZero()); // Used Cross Zero
			writeString(dataOutputStream, quantitationEntry.getDescription()); // Description
			/*
			 * Only MSD stores an ion.
			 */
			if(quantitationEntry instanceof IQuantitationEntryMSD) {
				dataOutputStream.writeBoolean(true); // Ion value is stored.
				IQuantitationEntryMSD quantitationEntryMSD = (IQuantitationEntryMSD)quantitationEntry;
				dataOutputStream.writeDouble(quantitationEntryMSD.getIon()); // Ion
			} else {
				dataOutputStream.writeBoolean(false); // No ion values is stored.
			}
		}
		/*
		 * Optimized Mass Spectrum
		 */
		IScanMSD optimizedMassSpectrum = massSpectrum.getOptimizedMassSpectrum();
		if(optimizedMassSpectrum == null) {
			dataOutputStream.writeBoolean(false);
		} else {
			dataOutputStream.writeBoolean(true);
			writeNormalMassSpectrum(dataOutputStream, optimizedMassSpectrum);
		}
	}

	private static void writeMassSpectrum(DataOutputStream dataOutputStream, IRegularMassSpectrum massSpectrum) throws IOException {

		dataOutputStream.writeShort(massSpectrum.getMassSpectrometer()); // Mass Spectrometer
		dataOutputStream.writeShort(massSpectrum.getMassSpectrumType()); // Mass Spectrum Type
		dataOutputStream.writeDouble(massSpectrum.getPrecursorIon()); // Precursor Ion (0 if MS1 or none has been selected)
		writeNormalMassSpectrum(dataOutputStream, massSpectrum);
	}

	private static void writeNormalMassSpectrum(DataOutputStream dataOutputStream, IScanMSD massSpectrum) throws IOException {

		dataOutputStream.writeInt(massSpectrum.getRetentionTime()); // Retention Time
		dataOutputStream.writeInt(massSpectrum.getRetentionTimeColumn1());
		dataOutputStream.writeInt(massSpectrum.getRetentionTimeColumn2());
		dataOutputStream.writeFloat(massSpectrum.getRetentionIndex()); // Retention Index
		dataOutputStream.writeBoolean(massSpectrum.hasAdditionalRetentionIndices());
		if(massSpectrum.hasAdditionalRetentionIndices()) {
			Map<RetentionIndexType, Float> retentionIndicesTyped = massSpectrum.getRetentionIndicesTyped();
			dataOutputStream.writeInt(retentionIndicesTyped.size());
			for(Map.Entry<RetentionIndexType, Float> retentionIndexTyped : retentionIndicesTyped.entrySet()) {
				writeString(dataOutputStream, retentionIndexTyped.getKey().toString());
				dataOutputStream.writeFloat(retentionIndexTyped.getValue());
			}
		}
		dataOutputStream.writeInt(massSpectrum.getTimeSegmentId()); // Time Segment Id
		dataOutputStream.writeInt(massSpectrum.getCycleNumber()); // Cycle Number
		//
		List<IIon> ions = massSpectrum.getIons();
		writeMassSpectrumIons(dataOutputStream, ions); // Ions
		/*
		 * Identification Results
		 */
		dataOutputStream.writeInt(massSpectrum.getTargets().size()); // Number Mass Spectrum Targets
		for(IIdentificationTarget identificationTarget : massSpectrum.getTargets()) {
			writeIdentificationEntry(dataOutputStream, identificationTarget);
		}
	}

	private static void writeMassSpectrumIons(DataOutputStream dataOutputStream, List<IIon> ions) throws IOException {

		dataOutputStream.writeInt(ions.size()); // Number of ions
		for(IIon ion : ions) {
			dataOutputStream.writeDouble(ion.getIon()); // m/z
			dataOutputStream.writeFloat(ion.getAbundance()); // Abundance
			/*
			 * Ion Transition
			 */
			IIonTransition ionTransition = ion.getIonTransition();
			if(ionTransition == null) {
				dataOutputStream.writeInt(0); // No ion transition available
			} else {
				/*
				 * parent m/z start, ...
				 */
				dataOutputStream.writeInt(1); // Ion transition available
				writeString(dataOutputStream, ionTransition.getCompoundName()); // compound name
				dataOutputStream.writeDouble(ionTransition.getQ1StartIon()); // parent m/z start
				dataOutputStream.writeDouble(ionTransition.getQ1StopIon()); // parent m/z stop
				dataOutputStream.writeDouble(ionTransition.getQ3StartIon()); // daughter m/z start
				dataOutputStream.writeDouble(ionTransition.getQ3StopIon()); // daughter m/z stop
				dataOutputStream.writeDouble(ionTransition.getCollisionEnergy()); // collision energy
				dataOutputStream.writeDouble(ionTransition.getQ1Resolution()); // q1 resolution
				dataOutputStream.writeDouble(ionTransition.getQ3Resolution()); // q3 resolution
				dataOutputStream.writeInt(ionTransition.getTransitionGroup()); // transition group
				dataOutputStream.writeInt(ionTransition.getDwell()); // dwell
			}
		}
	}

	private static void writeIntegrationEntries(DataOutputStream dataOutputStream, List<? extends IIntegrationEntry> integrationEntries) throws IOException {

		dataOutputStream.writeInt(integrationEntries.size()); // Number Integration Entries
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			if(integrationEntry instanceof IIntegrationEntryMSD) {
				/*
				 * It must be a MSD integration entry.
				 */
				IIntegrationEntryMSD integrationEntryMSD = (IIntegrationEntryMSD)integrationEntry;
				dataOutputStream.writeDouble(integrationEntryMSD.getIon()); // m/z
				dataOutputStream.writeDouble(integrationEntryMSD.getIntegratedArea()); // Integrated Area
			}
		}
	}

	private static void writeIdentificationEntry(DataOutputStream dataOutputStream, IIdentificationTarget identificationEntry) throws IOException {

		ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
		IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
		//
		writeString(dataOutputStream, identificationEntry.getIdentifier()); // Identifier
		dataOutputStream.writeBoolean(identificationEntry.isManuallyVerified());
		//
		writeString(dataOutputStream, libraryInformation.getCasNumber()); // CAS-Number
		writeString(dataOutputStream, libraryInformation.getComments()); // Comments
		writeString(dataOutputStream, libraryInformation.getReferenceIdentifier());
		writeString(dataOutputStream, libraryInformation.getMiscellaneous()); // Miscellaneous
		writeString(dataOutputStream, libraryInformation.getDatabase());
		writeString(dataOutputStream, libraryInformation.getContributor());
		writeString(dataOutputStream, libraryInformation.getName()); // Name
		Set<String> synonyms = libraryInformation.getSynonyms(); // Synonyms
		int numberOfSynonyms = synonyms.size();
		dataOutputStream.writeInt(numberOfSynonyms);
		for(String synonym : synonyms) {
			writeString(dataOutputStream, synonym);
		}
		writeString(dataOutputStream, libraryInformation.getFormula()); // Formula
		writeString(dataOutputStream, libraryInformation.getSmiles()); // SMILES
		writeString(dataOutputStream, libraryInformation.getInChI()); // InChI
		dataOutputStream.writeDouble(libraryInformation.getMolWeight()); // Mol Weight
		dataOutputStream.writeFloat(comparisonResult.getMatchFactor()); // Match Factor
		dataOutputStream.writeFloat(comparisonResult.getMatchFactorDirect()); // Match Factor Direct
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactor()); // Reverse Match Factor
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactorDirect()); // Reverse Match Factor Direct
		dataOutputStream.writeFloat(comparisonResult.getProbability()); // Probability
		dataOutputStream.writeBoolean(comparisonResult.isMatch()); // Is Match
	}

	private static void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}
}
