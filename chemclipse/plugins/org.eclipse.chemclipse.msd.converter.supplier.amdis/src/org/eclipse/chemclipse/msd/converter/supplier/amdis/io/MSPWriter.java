/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class MSPWriter extends AbstractMassSpectraWriter implements IMassSpectraWriter {

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		IScanMSD optimizedMassSpectrum = getOptimizedMassSpectrum(massSpectrum);
		IIdentificationTarget identificationTarget = getIdentificationTarget(optimizedMassSpectrum);
		if(identificationTarget == null) {
			identificationTarget = getIdentificationTarget(massSpectrum);
		} else if("".equals(identificationTarget.getLibraryInformation().getName())) {
			identificationTarget = getIdentificationTarget(massSpectrum);
		}
		/*
		 * Write the fields
		 */
		fileWriter.write(getNameField(massSpectrum, identificationTarget) + CRLF);
		fileWriter.write(getSynonyms(massSpectrum));
		fileWriter.write(getCommentsField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionTimeField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRelativeRetentionTimeField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getRetentionIndexField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getFormulaField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getMWField(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getCasNumberField(identificationTarget) + CRLF);
		fileWriter.write(getSmilesField(identificationTarget) + CRLF);
		fileWriter.write(getDBField(identificationTarget) + CRLF);
		fileWriter.write(getReferenceIdentifierField(identificationTarget) + CRLF);
		/*
		 * Mass spectrum
		 */
		fileWriter.write(getNumberOfPeaks(optimizedMassSpectrum) + CRLF);
		fileWriter.write(getIonsFormatMSP(optimizedMassSpectrum));
		/*
		 * To separate the mass spectra correctly.
		 */
		fileWriter.write(CRLF);
		fileWriter.flush();
	}
}
