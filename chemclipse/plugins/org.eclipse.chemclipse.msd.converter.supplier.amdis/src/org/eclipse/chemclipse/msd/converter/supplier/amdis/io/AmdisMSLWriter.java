/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class AmdisMSLWriter extends AbstractAmdisWriter implements IMassSpectraWriter {

	private static final Logger logger = Logger.getLogger(AmdisMSLWriter.class);

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum) throws IOException {

		IScanMSD normalizedMassSpectrum;
		try {
			normalizedMassSpectrum = makeDeepCopyAndNormalize(massSpectrum);
		} catch(CloneNotSupportedException e) {
			logger.warn(e);
			return;
		}
		/*
		 * Get the best identification target if available.
		 */
		IIdentificationTarget identificationTarget = getIdentificationTarget(normalizedMassSpectrum);
		/*
		 * Write the fields
		 */
		fileWriter.write(getNameField(massSpectrum, identificationTarget) + CRLF);
		fileWriter.write(getCasNumberField(identificationTarget) + CRLF);
		/*
		 * Retention time, retention index
		 */
		fileWriter.write(getRetentionTimeField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionIndexField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getCommentsField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getSourceField(normalizedMassSpectrum, identificationTarget) + CRLF);
		/*
		 * Mass spectra
		 */
		fileWriter.write(getNumberOfPeaks(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getMassSpectra(normalizedMassSpectrum) + CRLF);
		/*
		 * To separate the mass spectra correctly.
		 */
		fileWriter.write(CRLF);
		fileWriter.flush();
	}

	/**
	 * Returns the mass spectra in the convenient amdis format.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	private String getMassSpectra(IScanMSD massSpectrum) {

		int blockSize = 5;
		int actualPosition = 1;
		StringBuilder builder = new StringBuilder();
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			/*
			 * Insert a carriage return / line feed after each block size.
			 */
			if(actualPosition > blockSize) {
				builder.append(CRLF);
				actualPosition = 1;
			}
			/*
			 * Add each ion.
			 */
			builder.append("(");
			builder.append(AbstractIon.getIon(ion.getIon()));
			builder.append(" ");
			builder.append(AbstractIon.getAbundance(ion.getAbundance()));
			builder.append(")");
			/*
			 * The last element in the row do not need to have a whitespace at
			 * its end.
			 */
			if(actualPosition < blockSize) {
				builder.append(" ");
			}
			/*
			 * Increase the actual position.
			 */
			actualPosition++;
		}
		return builder.toString();
	}
}
