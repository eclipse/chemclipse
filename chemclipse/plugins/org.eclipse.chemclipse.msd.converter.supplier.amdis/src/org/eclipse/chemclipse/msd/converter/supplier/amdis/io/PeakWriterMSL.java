/*******************************************************************************
 * Copyright (c) 2016, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class PeakWriterMSL extends AbstractWriter {

	public void write(File file, IPeaks<?> peaks, boolean append) throws IOException {

		try (FileWriter fileWriter = new FileWriter(file, append)) {
			for(IPeak peak : peaks.getPeaks()) {
				if(peak instanceof IPeakMSD peakMSD) {
					writePeak(fileWriter, peakMSD);
				}
			}
		}
	}

	public void write(File file, IPeakMSD peak, boolean append) throws IOException {

		FileWriter fileWriter = new FileWriter(file, append);
		writePeak(fileWriter, peak);
	}

	private void writePeak(FileWriter fileWriter, IPeakMSD peak) throws IOException {

		IScanMSD massSpectrum = peak.getExtractedMassSpectrum();
		IScanMSD optimizedMassSpectrum = getOptimizedMassSpectrum(massSpectrum);
		/*
		 * Try to get the target.
		 */
		IIdentificationTarget identificationTarget = getIdentificationTarget(optimizedMassSpectrum);
		if(identificationTarget == null) {
			identificationTarget = getPeakTarget(peak);
		} else if("".equals(identificationTarget.getLibraryInformation().getName())) {
			identificationTarget = getIdentificationTarget(massSpectrum);
		}
		/*
		 * Write the fields
		 */
		fileWriter.write(getNameField(massSpectrum, identificationTarget) + CRLF);
		fileWriter.write(getCasNumberField(identificationTarget) + CRLF);
		fileWriter.write(getSmilesField(identificationTarget) + CRLF);
		fileWriter.write(getArea(peak) + CRLF);
		fileWriter.write(getInternalStandards(peak));
		fileWriter.write(getQuantitations(peak));
		/*
		 * Retention time, retention index
		 */
		fileWriter.write(getRetentionTimeField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRelativeRetentionTimeField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionIndexField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getDBField(identificationTarget) + CRLF);
		fileWriter.write(getReferenceIdentifierField(identificationTarget) + CRLF);
		fileWriter.write(getCommentsField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getSourceField(optimizedMassSpectrum, identificationTarget) + CRLF);
		/*
		 * Mass spectrum
		 */
		fileWriter.write(getNumberOfPeaks(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getIonsFormatMSL(optimizedMassSpectrum) + CRLF);
		/*
		 * To separate the mass spectra correctly.
		 */
		fileWriter.write(CRLF);
		fileWriter.flush();
	}
}
