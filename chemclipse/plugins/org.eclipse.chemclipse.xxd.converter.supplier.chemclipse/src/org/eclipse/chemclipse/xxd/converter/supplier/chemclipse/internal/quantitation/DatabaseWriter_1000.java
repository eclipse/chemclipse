/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.quantitation;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationDatabase;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignals;
import org.eclipse.chemclipse.model.quantitation.IRetentionIndexWindow;
import org.eclipse.chemclipse.model.quantitation.IRetentionTimeWindow;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class DatabaseWriter_1000 implements IDatabaseWriter {

	@Override
	public void convert(File file, IQuantitationDatabase quantitationDatabase, IProgressMonitor monitor) throws IOException {

		/*
		 * ZIP
		 */
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
		zipOutputStream.setLevel(PreferenceSupplier.getChromatogramCompressionLevel());
		zipOutputStream.setMethod(IFormat.QUANTDB_COMPRESSION_TYPE);
		/*
		 * Write the data.
		 */
		writeDatabase(zipOutputStream, "", quantitationDatabase, monitor);
		/*
		 * Flush and close the output stream.
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	private void writeDatabase(ZipOutputStream zipOutputStream, String directoryPrefix, IQuantitationDatabase quantitationDatabase, IProgressMonitor monitor) throws IOException {

		writeVersion(zipOutputStream, directoryPrefix, monitor);
		writeDatabaseFolder(zipOutputStream, directoryPrefix, quantitationDatabase, monitor);
	}

	private void writeVersion(ZipOutputStream zipOutputStream, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Version
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_VERSION);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		String version = IFormat.QUANTDB_VERSION_0001;
		dataOutputStream.writeInt(version.length()); // Length Version
		dataOutputStream.writeChars(version); // Version
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeDatabaseFolder(ZipOutputStream zipOutputStream, String directoryPrefix, IQuantitationDatabase quantitationDatabase, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry = new ZipEntry(directoryPrefix + IFormat.DIR_QUANTDB);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.closeEntry();
		//
		writeContent(zipOutputStream, directoryPrefix, quantitationDatabase, monitor);
	}

	private void writeContent(ZipOutputStream zipOutputStream, String directoryPrefix, IQuantitationDatabase quantitationDatabase, IProgressMonitor monitor) throws IOException {

		int size = quantitationDatabase.size();
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Write Database", size);
		//
		try {
			ZipEntry zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_QUANTDB);
			zipOutputStream.putNextEntry(zipEntry);
			DataOutputStream dataOutputStream = new DataOutputStream(zipOutputStream);
			//
			writeString(dataOutputStream, quantitationDatabase.getOperator());
			writeString(dataOutputStream, quantitationDatabase.getDescription());
			dataOutputStream.writeInt(size);
			//
			for(IQuantitationCompound quantitationCompound : quantitationDatabase) {
				//
				writeString(dataOutputStream, quantitationCompound.getName());
				writeString(dataOutputStream, quantitationCompound.getConcentrationUnit());
				writeString(dataOutputStream, quantitationCompound.getCalibrationMethod().toString());
				writeString(dataOutputStream, quantitationCompound.getChemicalClass());
				dataOutputStream.writeBoolean(quantitationCompound.isCrossZero());
				dataOutputStream.writeBoolean(quantitationCompound.isUseTIC());
				//
				List<IQuantitationPeak> quantitationPeaks = quantitationCompound.getQuantitationPeaks();
				writeQuantitationPeaks(dataOutputStream, quantitationPeaks);
				//
				IResponseSignals concentrationResponseEntriesMSD = quantitationCompound.getResponseSignals();
				writeResponseSignals(dataOutputStream, concentrationResponseEntriesMSD);
				//
				IQuantitationSignals quantitationSignalsMSD = quantitationCompound.getQuantitationSignals();
				writeQuantitationSignals(dataOutputStream, quantitationSignalsMSD);
				//
				IRetentionIndexWindow retentionIndexWindow = quantitationCompound.getRetentionIndexWindow();
				writeRetentionIndexWindow(dataOutputStream, retentionIndexWindow);
				//
				IRetentionTimeWindow retentionTimeWindow = quantitationCompound.getRetentionTimeWindow();
				writeRetentionTimeWindow(dataOutputStream, retentionTimeWindow);
				//
				subMonitor.worked(1);
			}
			//
			dataOutputStream.flush();
			zipOutputStream.closeEntry();
		} finally {
			SubMonitor.done(subMonitor);
		}
	}

	private static void writeQuantitationPeaks(DataOutputStream dataOutputStream, List<IQuantitationPeak> quantitationPeaks) throws IOException {

		dataOutputStream.writeInt(quantitationPeaks.size());
		for(IQuantitationPeak quantitationPeak : quantitationPeaks) {
			dataOutputStream.writeDouble(quantitationPeak.getConcentration());
			writeString(dataOutputStream, quantitationPeak.getConcentrationUnit());
			IPeak peak = quantitationPeak.getReferencePeak();
			writePeak(dataOutputStream, peak);
		}
	}

	private static void writeResponseSignals(DataOutputStream dataOutputStream, IResponseSignals responseSignals) throws IOException {

		dataOutputStream.writeInt(responseSignals.size());
		for(IResponseSignal responseSignal : responseSignals) {
			dataOutputStream.writeDouble(responseSignal.getSignal());
			dataOutputStream.writeDouble(responseSignal.getConcentration());
			dataOutputStream.writeDouble(responseSignal.getResponse());
		}
	}

	private static void writeQuantitationSignals(DataOutputStream dataOutputStream, IQuantitationSignals quantitationSignals) throws IOException {

		dataOutputStream.writeInt(quantitationSignals.size());
		for(IQuantitationSignal quantitationSignal : quantitationSignals) {
			dataOutputStream.writeDouble(quantitationSignal.getSignal());
			dataOutputStream.writeFloat((float)quantitationSignal.getRelativeResponse());
			dataOutputStream.writeDouble(quantitationSignal.getUncertainty());
			dataOutputStream.writeBoolean(quantitationSignal.isUse());
		}
	}

	private static void writeRetentionIndexWindow(DataOutputStream dataOutputStream, IRetentionIndexWindow retentionIndexWindow) throws IOException {

		dataOutputStream.writeFloat(retentionIndexWindow.getAllowedNegativeDeviation());
		dataOutputStream.writeFloat(retentionIndexWindow.getAllowedPositiveDeviation());
		dataOutputStream.writeFloat(retentionIndexWindow.getRetentionIndex());
	}

	private static void writeRetentionTimeWindow(DataOutputStream dataOutputStream, IRetentionTimeWindow retentionTimeWindow) throws IOException {

		dataOutputStream.writeFloat(retentionTimeWindow.getAllowedNegativeDeviation());
		dataOutputStream.writeFloat(retentionTimeWindow.getAllowedPositiveDeviation());
		dataOutputStream.writeInt(retentionTimeWindow.getRetentionTime());
	}

	private static void writePeak(DataOutputStream dataOutputStream, IPeak peak) throws IOException {

		if(peak instanceof IPeakMSD) {
			IPeakMSD peakMSD = (IPeakMSD)peak;
			IPeakModelMSD peakModel = peakMSD.getPeakModel();
			//
			writeString(dataOutputStream, peak.getDetectorDescription());
			writeString(dataOutputStream, peak.getQuantifierDescription());
			dataOutputStream.writeBoolean(peak.isActiveForAnalysis());
			writeString(dataOutputStream, peak.getIntegratorDescription());
			writeString(dataOutputStream, peak.getModelDescription());
			writeString(dataOutputStream, peak.getPeakType().toString());
			dataOutputStream.writeInt(peak.getSuggestedNumberOfComponents());
			//
			dataOutputStream.writeFloat(peakModel.getBackgroundAbundance(peakModel.getStartRetentionTime()));
			dataOutputStream.writeFloat(peakModel.getBackgroundAbundance(peakModel.getStopRetentionTime()));
			//
			IPeakMassSpectrum massSpectrum = peakModel.getPeakMassSpectrum();
			writeMassSpectrum(dataOutputStream, massSpectrum);
			//
			List<Integer> retentionTimes = peakModel.getRetentionTimes();
			dataOutputStream.writeInt(retentionTimes.size());
			for(int retentionTime : retentionTimes) {
				dataOutputStream.writeInt(retentionTime);
				dataOutputStream.writeFloat(peakModel.getPeakAbundance(retentionTime));
			}
			//
			List<IIntegrationEntry> integrationEntries = peak.getIntegrationEntries();
			writeIntegrationEntries(dataOutputStream, integrationEntries);
			/*
			 * Identification Results
			 */
			Set<IIdentificationTarget> peakTargets = peak.getTargets();
			dataOutputStream.writeInt(peakTargets.size());
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
			dataOutputStream.writeInt(quantitationEntries.size());
			for(IQuantitationEntry quantitationEntry : quantitationEntries) {
				dataOutputStream.writeDouble(quantitationEntry.getSignal());
				writeString(dataOutputStream, quantitationEntry.getName());
				writeString(dataOutputStream, quantitationEntry.getChemicalClass());
				dataOutputStream.writeDouble(quantitationEntry.getConcentration());
				writeString(dataOutputStream, quantitationEntry.getConcentrationUnit());
				dataOutputStream.writeDouble(quantitationEntry.getArea());
				writeString(dataOutputStream, quantitationEntry.getCalibrationMethod());
				dataOutputStream.writeBoolean(quantitationEntry.getUsedCrossZero());
				writeString(dataOutputStream, quantitationEntry.getDescription());
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
	}

	private static void writeMassSpectrum(DataOutputStream dataOutputStream, IRegularMassSpectrum massSpectrum) throws IOException {

		dataOutputStream.writeShort(massSpectrum.getMassSpectrometer());
		dataOutputStream.writeShort(massSpectrum.getMassSpectrumType());
		dataOutputStream.writeDouble(massSpectrum.getPrecursorIon());
		writeNormalMassSpectrum(dataOutputStream, massSpectrum);
	}

	private static void writeNormalMassSpectrum(DataOutputStream dataOutputStream, IScanMSD massSpectrum) throws IOException {

		dataOutputStream.writeInt(massSpectrum.getRetentionTime());
		dataOutputStream.writeInt(massSpectrum.getRetentionTimeColumn1());
		dataOutputStream.writeInt(massSpectrum.getRetentionTimeColumn2());
		dataOutputStream.writeFloat(massSpectrum.getRetentionIndex());
		dataOutputStream.writeBoolean(massSpectrum.hasAdditionalRetentionIndices());
		if(massSpectrum.hasAdditionalRetentionIndices()) {
			Map<RetentionIndexType, Float> retentionIndicesTyped = massSpectrum.getRetentionIndicesTyped();
			dataOutputStream.writeInt(retentionIndicesTyped.size());
			for(Map.Entry<RetentionIndexType, Float> retentionIndexTyped : retentionIndicesTyped.entrySet()) {
				writeString(dataOutputStream, retentionIndexTyped.getKey().toString());
				dataOutputStream.writeFloat(retentionIndexTyped.getValue());
			}
		}
		dataOutputStream.writeInt(massSpectrum.getTimeSegmentId());
		dataOutputStream.writeInt(massSpectrum.getCycleNumber());
		//
		List<IIon> ions = massSpectrum.getIons();
		writeMassSpectrumIons(dataOutputStream, ions);
		/*
		 * Identification Results
		 */
		dataOutputStream.writeInt(massSpectrum.getTargets().size());
		for(IIdentificationTarget identificationTarget : massSpectrum.getTargets()) {
			writeIdentificationEntry(dataOutputStream, identificationTarget);
		}
	}

	private static void writeMassSpectrumIons(DataOutputStream dataOutputStream, List<IIon> ions) throws IOException {

		dataOutputStream.writeInt(ions.size());
		for(IIon ion : ions) {
			dataOutputStream.writeDouble(ion.getIon());
			dataOutputStream.writeFloat(ion.getAbundance());
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
				dataOutputStream.writeInt(1);
				writeString(dataOutputStream, ionTransition.getCompoundName());
				dataOutputStream.writeDouble(ionTransition.getQ1StartIon());
				dataOutputStream.writeDouble(ionTransition.getQ1StopIon());
				dataOutputStream.writeDouble(ionTransition.getQ3StartIon());
				dataOutputStream.writeDouble(ionTransition.getQ3StopIon());
				dataOutputStream.writeDouble(ionTransition.getCollisionEnergy());
				dataOutputStream.writeDouble(ionTransition.getQ1Resolution());
				dataOutputStream.writeDouble(ionTransition.getQ3Resolution());
				dataOutputStream.writeInt(ionTransition.getTransitionGroup());
				dataOutputStream.writeInt(ionTransition.getDwell());
			}
		}
	}

	private static void writeIntegrationEntries(DataOutputStream dataOutputStream, List<? extends IIntegrationEntry> integrationEntries) throws IOException {

		dataOutputStream.writeInt(integrationEntries.size());
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			dataOutputStream.writeDouble(integrationEntry.getSignal());
			dataOutputStream.writeDouble(integrationEntry.getIntegratedArea());
		}
	}

	private static void writeIdentificationEntry(DataOutputStream dataOutputStream, IIdentificationTarget identificationEntry) throws IOException {

		ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
		IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
		//
		writeString(dataOutputStream, identificationEntry.getIdentifier());
		dataOutputStream.writeBoolean(identificationEntry.isManuallyVerified());
		//
		writeString(dataOutputStream, libraryInformation.getCasNumber());
		writeString(dataOutputStream, libraryInformation.getComments());
		writeString(dataOutputStream, libraryInformation.getReferenceIdentifier());
		writeString(dataOutputStream, libraryInformation.getMiscellaneous());
		writeString(dataOutputStream, libraryInformation.getDatabase());
		writeString(dataOutputStream, libraryInformation.getContributor());
		writeString(dataOutputStream, libraryInformation.getName());
		Set<String> synonyms = libraryInformation.getSynonyms();
		int numberOfSynonyms = synonyms.size();
		dataOutputStream.writeInt(numberOfSynonyms);
		for(String synonym : synonyms) {
			writeString(dataOutputStream, synonym);
		}
		writeString(dataOutputStream, libraryInformation.getFormula());
		writeString(dataOutputStream, libraryInformation.getSmiles());
		writeString(dataOutputStream, libraryInformation.getInChI());
		dataOutputStream.writeDouble(libraryInformation.getMolWeight());
		dataOutputStream.writeFloat(comparisonResult.getMatchFactor());
		dataOutputStream.writeFloat(comparisonResult.getMatchFactorDirect());
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactor());
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactorDirect());
		dataOutputStream.writeFloat(comparisonResult.getProbability());
		dataOutputStream.writeBoolean(comparisonResult.isMatch());
	}

	private static void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length());
		dataOutputStream.writeChars(value);
	}
}
