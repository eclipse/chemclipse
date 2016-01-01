/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
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
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class AmdisMSPWriter extends AbstractAmdisWriter implements IMassSpectraWriter {

	private static final Logger logger = Logger.getLogger(AmdisMSPWriter.class);

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
		String synonyms = getSynonyms(normalizedMassSpectrum);
		if(synonyms != null && !synonyms.equals("")) {
			fileWriter.write(synonyms);
		}
		fileWriter.write(getCommentsField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionTimeField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionIndexField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getFormulaField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getMWField(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getCasNumberField(identificationTarget) + CRLF);
		fileWriter.write(getNumberOfPeaks(normalizedMassSpectrum) + CRLF);
		fileWriter.write(getMassSpectra(normalizedMassSpectrum));
		/*
		 * To separate the mass spectra correctly.
		 */
		fileWriter.write(CRLF);
		fileWriter.flush();
	}

	private String getSynonyms(IScanMSD massSpectrum) {

		StringBuilder builder = new StringBuilder();
		if(massSpectrum instanceof ILibraryMassSpectrum) {
			ILibraryMassSpectrum libraryMassSpectrum = (ILibraryMassSpectrum)massSpectrum;
			Set<String> synonyms = libraryMassSpectrum.getLibraryInformation().getSynonyms();
			if(synonyms.size() > 0) {
				for(String synonym : synonyms) {
					/*
					 * Set the synonym.
					 */
					builder.append("Synon: ");
					builder.append(synonym);
					builder.append(CRLF);
				}
			}
		}
		//
		return builder.toString();
	}

	/**
	 * Returns the mass spectra in the convenient amdis format.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	private String getMassSpectra(IScanMSD massSpectrum) {

		StringBuilder builder = new StringBuilder();
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			/*
			 * Add each ion.
			 */
			builder.append(AbstractIon.getIon(ion.getIon()));
			builder.append(" ");
			builder.append(AbstractIon.getAbundance(ion.getAbundance()));
			builder.append(";");
			builder.append(CRLF);
		}
		return builder.toString();
	}
}
