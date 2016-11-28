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

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class AmdisMSPWriter extends AbstractAmdisWriter implements IMassSpectraWriter {

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		IScanMSD optimizedMassSpectrum = getOptimizedMassSpectrum(massSpectrum);
		IIdentificationTarget identificationTarget = getIdentificationTarget(optimizedMassSpectrum);
		/*
		 * Write the fields
		 */
		fileWriter.write(getNameField(massSpectrum, identificationTarget) + CRLF);
		String synonyms = getSynonyms(optimizedMassSpectrum);
		if(synonyms != null && !synonyms.equals("")) {
			fileWriter.write(synonyms);
		}
		fileWriter.write(getCommentsField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionTimeField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRelativeRetentionTimeField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionIndexField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getFormulaField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getMWField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getCasNumberField(identificationTarget) + CRLF);
		fileWriter.write(getSmilesField(identificationTarget) + CRLF);
		fileWriter.write(getNumberOfPeaks(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getDBField(identificationTarget) + CRLF);
		fileWriter.write(getReferenceIdentifierField(identificationTarget) + CRLF);
		fileWriter.write(getIons(optimizedMassSpectrum));
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
	 * Returns the mass spectra in the convenient AMDIS format.
	 * 
	 * @param massSpectrum
	 * @return String
	 */
	private String getIons(IScanMSD massSpectrum) {

		boolean exportIntensityAsInteger = PreferenceSupplier.isExportIntensitiesAsInteger();
		StringBuilder builder = new StringBuilder();
		List<IIon> ions = massSpectrum.getIons();
		for(IIon ion : ions) {
			/*
			 * Add each ion.
			 */
			builder.append(ion.getIon());
			builder.append(" ");
			if(exportIntensityAsInteger) {
				builder.append(AbstractIon.getAbundance(ion.getAbundance()));
			} else {
				builder.append(ion.getAbundance());
			}
			builder.append(";");
			builder.append(CRLF);
		}
		return builder.toString();
	}
}
