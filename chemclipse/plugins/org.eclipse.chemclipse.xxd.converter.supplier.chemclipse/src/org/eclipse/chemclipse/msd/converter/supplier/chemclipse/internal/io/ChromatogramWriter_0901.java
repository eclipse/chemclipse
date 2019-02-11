/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.ISignal;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.quantitation.IQuantitationEntry;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io.IChromatogramMSDZipWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class ChromatogramWriter_0901 extends AbstractChromatogramWriter implements IChromatogramMSDZipWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		// monitor.subTask(IConstants.EXPORT_CHROMATOGRAM);
		/*
		 * ZIP
		 */
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
		zipOutputStream.setLevel(PreferenceSupplier.getChromatogramCompressionLevel());
		zipOutputStream.setMethod(IFormat.CHROMATOGRAM_COMPRESSION_TYPE);
		/*
		 * Write the data
		 */
		writeChromatogram(zipOutputStream, "", chromatogram, monitor);
		/*
		 * Flush and close the output stream.
		 */
		zipOutputStream.flush();
		zipOutputStream.close();
	}

	@Override
	public void writeChromatogram(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		writeVersion(zipOutputStream, directoryPrefix, monitor);
		writeOverviewFolder(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramFolder(zipOutputStream, directoryPrefix, chromatogram, monitor);
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
		String version = IFormat.CHROMATOGRAM_VERSION_0901;
		dataOutputStream.writeInt(version.length()); // Length Version
		dataOutputStream.writeChars(version); // Version
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeOverviewFolder(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Create the overview folder
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.DIR_OVERVIEW);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.closeEntry();
		/*
		 * TIC
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_TIC);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		// Retention Times - Total Signals
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.EXPORT_SCAN + scan);
			dataOutputStream.writeInt(chromatogram.getScan(scan).getRetentionTime()); // Retention Time
			dataOutputStream.writeFloat(chromatogram.getScan(scan).getTotalSignal()); // Total Signal
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramFolder(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		/*
		 * Create the chromatogram folder
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.DIR_CHROMATOGRAM);
		zipOutputStream.putNextEntry(zipEntry);
		zipOutputStream.closeEntry();
		/*
		 * WRITE THE FILES
		 */
		writeChromatogramScans(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramBaseline(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramPeaks(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramArea(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramIdentification(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramHistory(zipOutputStream, directoryPrefix, chromatogram, monitor);
		writeChromatogramMiscellaneous(zipOutputStream, directoryPrefix, chromatogram, monitor);
	}

	private void writeChromatogramScans(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Scans
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_SCANS);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		// Scans
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.EXPORT_SCAN + scan);
			IVendorMassSpectrum massSpectrum = chromatogram.getSupplierScan(scan);
			writeMassSpectrum(dataOutputStream, massSpectrum);
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramBaseline(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Baseline
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_BASELINE);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		int scans = chromatogram.getNumberOfScans();
		dataOutputStream.writeInt(scans); // Number of Scans
		//
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		// Scans
		for(int scan = 1; scan <= scans; scan++) {
			// monitor.subTask(IConstants.EXPORT_BASELINE + scan);
			int retentionTime = chromatogram.getSupplierScan(scan).getRetentionTime();
			float backgroundAbundance = baselineModel.getBackgroundAbundance(retentionTime);
			dataOutputStream.writeInt(retentionTime); // Retention Time
			dataOutputStream.writeFloat(backgroundAbundance); // Background Abundance
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramPeaks(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Peaks
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_PEAKS);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		List<IChromatogramPeakMSD> peaks = chromatogram.getPeaks();
		dataOutputStream.writeInt(peaks.size()); // Number of Peaks
		// Peaks
		// int counter = 1;
		for(IChromatogramPeakMSD peak : peaks) {
			// monitor.subTask(IConstants.EXPORT_PEAK + counter++);
			writePeak(dataOutputStream, peak);
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramArea(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Area
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_AREA);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		List<IIntegrationEntry> chromatogramIntegrationEntries = chromatogram.getChromatogramIntegrationEntries();
		writeString(dataOutputStream, chromatogram.getChromatogramIntegratorDescription()); // Chromatogram Integrator Description
		writeIntegrationEntries(dataOutputStream, chromatogramIntegrationEntries);
		//
		List<IIntegrationEntry> backgroundIntegrationEntries = chromatogram.getBackgroundIntegrationEntries();
		writeString(dataOutputStream, chromatogram.getBackgroundIntegratorDescription()); // Background Integrator Description
		writeIntegrationEntries(dataOutputStream, backgroundIntegrationEntries);
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramIdentification(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Identification
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_IDENTIFICATION);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		Set<IIdentificationTarget> chromatogramTargets = chromatogram.getTargets();
		dataOutputStream.writeInt(chromatogramTargets.size()); // Number of Targets
		for(IIdentificationTarget chromatogramTarget : chromatogramTargets) {
			if(chromatogramTarget instanceof IIdentificationTarget) {
				IIdentificationTarget identificationEntry = chromatogramTarget;
				writeIdentificationEntry(dataOutputStream, identificationEntry);
			}
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramHistory(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Edit-History
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_HISTORY);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		IEditHistory editHistory = chromatogram.getEditHistory();
		dataOutputStream.writeInt(editHistory.size()); // Number of entries
		// Date, Description
		for(IEditInformation editInformation : editHistory) {
			dataOutputStream.writeLong(editInformation.getDate().getTime()); // Date
			writeString(dataOutputStream, editInformation.getDescription()); // Description
		}
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeChromatogramMiscellaneous(ZipOutputStream zipOutputStream, String directoryPrefix, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws IOException {

		ZipEntry zipEntry;
		DataOutputStream dataOutputStream;
		/*
		 * Miscellaneous
		 */
		zipEntry = new ZipEntry(directoryPrefix + IFormat.FILE_MISC);
		zipOutputStream.putNextEntry(zipEntry);
		dataOutputStream = new DataOutputStream(zipOutputStream);
		//
		dataOutputStream.writeLong(chromatogram.getDate().getTime()); // Date
		writeString(dataOutputStream, chromatogram.getMiscInfo()); // Miscellaneous Info
		writeString(dataOutputStream, chromatogram.getOperator()); // Operator
		//
		dataOutputStream.flush();
		zipOutputStream.closeEntry();
	}

	private void writeMassSpectrum(DataOutputStream dataOutputStream, IRegularMassSpectrum massSpectrum) throws IOException {

		dataOutputStream.writeShort(massSpectrum.getMassSpectrometer()); // Mass Spectrometer
		dataOutputStream.writeShort(massSpectrum.getMassSpectrumType()); // Mass Spectrum Type
		dataOutputStream.writeDouble(massSpectrum.getPrecursorIon()); // Precursor Ion (0 if MS1 or none has been selected)
		dataOutputStream.writeInt(massSpectrum.getRetentionTime()); // Retention Time
		dataOutputStream.writeFloat(massSpectrum.getRetentionIndex()); // Retention Index
		List<IIon> ions = massSpectrum.getIons();
		writeMassSpectrumIons(dataOutputStream, ions); // Ions
	}

	private void writeMassSpectrumIons(DataOutputStream dataOutputStream, List<IIon> ions) throws IOException {

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
				dataOutputStream.writeDouble(ionTransition.getQ1StartIon()); // parent m/z start
				dataOutputStream.writeDouble(ionTransition.getQ1StopIon()); // parent m/z stop
				dataOutputStream.writeDouble(ionTransition.getQ3StartIon()); // daughter m/z start
				dataOutputStream.writeDouble(ionTransition.getQ3StopIon()); // daughter m/z stop
				dataOutputStream.writeDouble(ionTransition.getCollisionEnergy()); // collision energy
				dataOutputStream.writeDouble(ionTransition.getQ1Resolution()); // q1 resolution
				dataOutputStream.writeDouble(ionTransition.getQ3Resolution()); // q3 resolution
				dataOutputStream.writeInt(ionTransition.getTransitionGroup()); // transition group
			}
		}
	}

	private void writePeak(DataOutputStream dataOutputStream, IPeakMSD peak) throws IOException {

		IPeakModelMSD peakModel = peak.getPeakModel();
		//
		writeString(dataOutputStream, peak.getDetectorDescription()); // Detector Description
		writeString(dataOutputStream, peak.getIntegratorDescription()); // Integrator Description
		writeString(dataOutputStream, peak.getModelDescription()); // Model Description
		writeString(dataOutputStream, peak.getPeakType().toString()); // Peak Type
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
			 * Legacy support
			 */
			if(quantitationEntry.getSignal() != ISignal.TOTAL_INTENSITY) {
				dataOutputStream.writeBoolean(true);
				dataOutputStream.writeDouble(quantitationEntry.getSignal());
			} else {
				dataOutputStream.writeBoolean(false);
			}
		}
	}

	private void writeIntegrationEntries(DataOutputStream dataOutputStream, List<? extends IIntegrationEntry> integrationEntries) throws IOException {

		dataOutputStream.writeInt(integrationEntries.size()); // Number Integration Entries
		for(IIntegrationEntry integrationEntry : integrationEntries) {
			dataOutputStream.writeDouble(integrationEntry.getSignal()); // m/z
			dataOutputStream.writeDouble(integrationEntry.getIntegratedArea()); // Integrated Area
		}
	}

	private void writeIdentificationEntry(DataOutputStream dataOutputStream, IIdentificationTarget identificationEntry) throws IOException {

		ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
		IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
		//
		writeString(dataOutputStream, identificationEntry.getIdentifier()); // Identifier
		//
		writeString(dataOutputStream, libraryInformation.getCasNumber()); // CAS-Number
		writeString(dataOutputStream, libraryInformation.getComments()); // Comments
		writeString(dataOutputStream, libraryInformation.getMiscellaneous()); // Miscellaneous
		writeString(dataOutputStream, libraryInformation.getName()); // Name
		writeString(dataOutputStream, libraryInformation.getFormula()); // Formula
		dataOutputStream.writeDouble(libraryInformation.getMolWeight()); // Mol Weight
		//
		dataOutputStream.writeFloat(comparisonResult.getMatchFactor()); // Match Factor
		dataOutputStream.writeFloat(comparisonResult.getReverseMatchFactor()); // Reverse Match Factor
		dataOutputStream.writeFloat(comparisonResult.getProbability()); // Probability
	}

	private void writeString(DataOutputStream dataOutputStream, String value) throws IOException {

		dataOutputStream.writeInt(value.length()); // Value Length
		dataOutputStream.writeChars(value); // Value
	}
}
