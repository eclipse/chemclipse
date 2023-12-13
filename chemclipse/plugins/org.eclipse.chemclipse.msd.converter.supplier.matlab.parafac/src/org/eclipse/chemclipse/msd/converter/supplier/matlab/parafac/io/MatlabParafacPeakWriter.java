/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.io.IPeakWriter;
import org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.internal.converter.IConstants;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.IProcessingMessage;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingMessage;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class MatlabParafacPeakWriter implements IPeakWriter {

	private String lineSeparator;
	private int peakCounter = 1;
	private DecimalFormat decimalFormat;

	public MatlabParafacPeakWriter() {

		setLineSeparator();
		decimalFormat = ValueFormat.getDecimalFormatEnglish();
	}

	@Override
	public IProcessingInfo<File> write(File file, IPeakMSD peak, boolean append) throws FileIsNotWriteableException, IOException {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		FileWriter fileWriter = new FileWriter(file, append);
		writePeak(fileWriter, peak, processingInfo);
		fileWriter.close();
		/*
		 * Add the file as the result object.
		 */
		processingInfo.setProcessingResult(file);
		return processingInfo;
	}

	@Override
	public IProcessingInfo<File> write(File file, IPeaks<? extends IPeakMSD> peaks, boolean append) throws FileIsNotWriteableException, IOException {

		IProcessingInfo<File> processingInfo = new ProcessingInfo<>();
		FileWriter fileWriter = new FileWriter(file, append);
		writePeaks(fileWriter, peaks, processingInfo);
		fileWriter.close();
		/*
		 * Add the file as the result object.
		 */
		processingInfo.setProcessingResult(file);
		return processingInfo;
	}

	private void writePeaks(FileWriter fileWriter, IPeaks<? extends IPeakMSD> peaks, IProcessingInfo<File> processingInfo) throws IOException {

		int size = peaks.getPeaks().size();
		for(int i = 0; i < size; i++) {
			IPeakMSD peak = peaks.getPeaks().get(i);
			writePeak(fileWriter, peak, processingInfo);
		}
	}

	private void writePeak(FileWriter fileWriter, IPeakMSD peak, IProcessingInfo<?> processingInfo) throws IOException {

		fileWriter.write(getPeakHeader());
		fileWriter.write(getDescription(peak));
		fileWriter.write(getMassSpectrum(peak));
		fileWriter.write(getElutionProfile(peak));
		fileWriter.flush();
		IProcessingMessage processingMessage = new ProcessingMessage(MessageType.INFO, "Export Peak", "The given peak was exported successfully.");
		processingInfo.addMessage(processingMessage);
	}

	private String getPeakHeader() {

		/*
		 * #------------------------------------------
		 * #
		 * # peak 1
		 * #
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(IConstants.PEAK_IDENTIFIER);
		builder.append(lineSeparator);
		//
		builder.append(IConstants.COMMENT);
		builder.append(lineSeparator);
		//
		builder.append(IConstants.COMMENT);
		builder.append(" ");
		builder.append("Peak");
		builder.append(" ");
		builder.append(peakCounter++);
		builder.append(lineSeparator);
		//
		builder.append(IConstants.COMMENT);
		builder.append(lineSeparator);
		return builder.toString();
	}

	private String getDescription(IPeakMSD peak) {

		/*
		 * description PARAFAC model, 257820 to 276016 milliseconds, Peak 1
		 */
		StringBuilder builder = new StringBuilder();
		builder.append(IConstants.DESCRIPTION);
		builder.append(IConstants.VALUE_DELIMITER);
		String modelDescription = peak.getModelDescription();
		if(modelDescription == null || modelDescription.equals("")) {
			builder.append("Detector [");
			builder.append(peak.getDetectorDescription());
			builder.append("], RT (Minutes) [");
			double retentionTimeInMinutes = peak.getPeakModel().getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
			builder.append(decimalFormat.format(retentionTimeInMinutes));
			builder.append("]");
		} else {
			builder.append(modelDescription);
		}
		builder.append(lineSeparator);
		return builder.toString();
	}

	private String getMassSpectrum(IPeakMSD peak) {

		/*
		 * # mass spectrum (m/z - intensity)
		 * 50 0.00222065
		 * 51 0.0570025
		 * ...
		 */
		IPeakMassSpectrum peakMassSpectrum = peak.getPeakModel().getPeakMassSpectrum();
		IExtractedIonSignal extractedIonSignal = peakMassSpectrum.getExtractedIonSignal();
		int startIon = extractedIonSignal.getStartIon();
		int stopIon = extractedIonSignal.getStopIon();
		StringBuilder builder = new StringBuilder();
		/*
		 * Header
		 */
		builder.append(IConstants.MASS_SPECTRUM + " " + IConstants.MASS_SPECTRUM_INFO);
		builder.append(lineSeparator);
		/*
		 * Values
		 */
		for(int ion = startIon; ion <= stopIon; ion++) {
			/*
			 * Do not store ions with zero abundance.
			 */
			float abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0.0f) {
				builder.append(ion);
				builder.append(IConstants.VALUE_DELIMITER);
				builder.append(abundance);
				builder.append(lineSeparator);
			}
		}
		return builder.toString();
	}

	private String getElutionProfile(IPeakMSD peak) {

		/*
		 * # elution profile (minutes - intensity)
		 * 257820 0.000745753
		 * 258164 0.00113633
		 * ...
		 */
		//
		IPeakModelMSD peakModel = peak.getPeakModel();
		StringBuilder builder = new StringBuilder();
		/*
		 * Header
		 */
		builder.append(IConstants.ELUTION_PROFILE + " " + IConstants.ELUTION_PROFILE_INFO);
		builder.append(lineSeparator);
		/*
		 * Values
		 */
		for(Integer retentionTime : peakModel.getRetentionTimes()) {
			builder.append(retentionTime);
			builder.append(IConstants.VALUE_DELIMITER);
			builder.append(peakModel.getPeakAbundance(retentionTime));
			builder.append(lineSeparator);
		}
		return builder.toString();
	}

	private void setLineSeparator() {

		if(OperatingSystemUtils.isWindows()) {
			lineSeparator = IConstants.CRLF;
		} else if(OperatingSystemUtils.isMac()) {
			lineSeparator = IConstants.CR;
		} else {
			lineSeparator = IConstants.LF;
		}
	}
}
