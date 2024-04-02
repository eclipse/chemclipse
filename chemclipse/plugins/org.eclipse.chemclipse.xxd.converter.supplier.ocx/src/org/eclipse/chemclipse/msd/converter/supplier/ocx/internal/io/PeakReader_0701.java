/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IIntegrationEntry;
import org.eclipse.chemclipse.model.core.IPeakIntensityValues;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.PeakException;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IPeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.IPeakLibraryInformation;
import org.eclipse.chemclipse.model.identifier.PeakComparisonResult;
import org.eclipse.chemclipse.model.identifier.PeakLibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.model.implementation.IntegrationEntry;
import org.eclipse.chemclipse.model.implementation.PeakIntensityValues;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.VendorIon;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.core.PeaksMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMSD;
import org.eclipse.chemclipse.msd.model.implementation.PeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.implementation.PeakModelMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.MassSpectrometer;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.MassSpectrumType;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Methods are copied to ensure that file formats are kept readable even if they contain errors.
 * This is suitable but I know, it's not the best way to achieve long term support for older formats.
 */
public class PeakReader_0701 extends AbstractZipReader implements IPeakReader {

	private static final Logger logger = Logger.getLogger(PeakReader_0701.class);

	@Override
	public IProcessingInfo<IPeaks<IPeakMSD>> read(File file, IProgressMonitor monitor) throws IOException {

		ZipFile zipFile = new ZipFile(file);
		IProcessingInfo<IPeaks<IPeakMSD>> processingInfo = new ProcessingInfo<>();
		try {
			IPeaks<IPeakMSD> peaks = readPeaksFromZipFile(zipFile, monitor);
			processingInfo.setProcessingResult(peaks);
		} finally {
			zipFile.close();
		}
		return processingInfo;
	}

	private IPeaks<IPeakMSD> readPeaksFromZipFile(ZipFile zipFile, IProgressMonitor monitor) throws IOException {

		IPeaks<IPeakMSD> peaks = new PeaksMSD();
		DataInputStream dataInputStream = getDataInputStream(zipFile, IFormat.FILE_PEAKS);
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

		String detectorDescription = readString(dataInputStream); // Detector Description
		String integratorDescription = readString(dataInputStream); // Integrator Description
		String modelDescription = readString(dataInputStream); // Model Description
		PeakType peakType = PeakType.valueOf(readString(dataInputStream)); // Peak Type
		//
		float startBackgroundAbundance = dataInputStream.readFloat(); // Start Background Abundance
		float stopBackgroundAbundance = dataInputStream.readFloat(); // Stop Background Abundance
		//
		IPeakMassSpectrum peakMaximum = readPeakMassSpectrum(dataInputStream);
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
		//
		List<IIntegrationEntry> integrationEntries = readIntegrationEntries(dataInputStream);
		peak.setIntegratedArea(integrationEntries, integratorDescription);
		//
		readPeakIdentificationTargets(dataInputStream, peak, monitor);
		//
		return peak;
	}

	private IPeakMassSpectrum readPeakMassSpectrum(DataInputStream dataInputStream) throws IOException {

		String massSpectrometer = readString(dataInputStream); // Mass Spectrometer
		String massSpectrumType = readString(dataInputStream); // Mass Spectrum Type
		IPeakMassSpectrum massSpectrum = new PeakMassSpectrum();
		/*
		 * MassSpectrometer and MassSpectrumType
		 * Convert from the 0.7.0 enum format to short value.
		 */
		massSpectrum.setMassSpectrometer(getMassSpectrometerValue(massSpectrometer));
		massSpectrum.setMassSpectrumType(getMassSpectrumTypeValue(massSpectrumType));
		int retentionTime = dataInputStream.readInt(); // Retention Time
		float retentionIndex = dataInputStream.readFloat(); // Retention Index
		massSpectrum.setRetentionTime(retentionTime);
		massSpectrum.setRetentionIndex(retentionIndex);
		int numberOfIons = dataInputStream.readInt(); // Number of ions
		for(int i = 1; i <= numberOfIons; i++) {
			/*
			 * Read Ions
			 */
			double mz = dataInputStream.readDouble(); // m/z
			float abundance = dataInputStream.readFloat(); // Abundance
			IVendorIon ion = new VendorIon(mz, abundance);
			massSpectrum.addIon(ion);
		}
		return massSpectrum;
	}

	private List<IIntegrationEntry> readIntegrationEntries(DataInputStream dataInputStream) throws IOException {

		List<IIntegrationEntry> integrationEntries = new ArrayList<>();
		int numberOfIntegrationEntries = dataInputStream.readInt(); // Number Integration Entries
		for(int i = 1; i <= numberOfIntegrationEntries; i++) {
			double ion = dataInputStream.readDouble(); // m/z
			double integratedArea = dataInputStream.readDouble(); // Integrated Area
			IIntegrationEntry integrationEntry = new IntegrationEntry(ion, integratedArea);
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

	/**
	 * Legacy.
	 * Convert between Enum and short value.
	 * 
	 * @param massSpectrometer
	 * @return
	 */
	private short getMassSpectrometerValue(String massSpectrometer) {

		MassSpectrometer detector = MassSpectrometer.valueOf(massSpectrometer);
		short value = 1; // MS1
		if(detector == MassSpectrometer.MS2) {
			value = 2;
		} else if(detector == MassSpectrometer.MS3) {
			value = 3;
		}
		return value;
	}

	/**
	 * Legacy.
	 * Convert between Enum and short value.
	 * 
	 * @param massSpectrometer
	 * @return
	 */
	private short getMassSpectrumTypeValue(String massSpectrumType) {

		MassSpectrumType msType = MassSpectrumType.valueOf(massSpectrumType);
		short value = 0; // centroid
		if(msType == MassSpectrumType.PROFILE) {
			value = 1;
		}
		return value;
	}
}
