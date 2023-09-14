/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class MSLWriter extends AbstractMassSpectraWriter implements IMassSpectraWriter {

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		/*
		 * Retrieve the correct target.
		 */
		IScanMSD optimizedMassSpectrum = getOptimizedMassSpectrum(massSpectrum);
		IIdentificationTarget identificationTarget = getIdentificationTarget(optimizedMassSpectrum);
		if(identificationTarget == null || identificationTarget.getLibraryInformation().getName().isEmpty()) {
			identificationTarget = getIdentificationTarget(massSpectrum);
		}
		/*
		 * Write the fields ... retention time, retention index
		 */
		writeField(fileWriter, getNameField(massSpectrum, identificationTarget));
		writeSynonymsFields(fileWriter, identificationTarget);
		writeCasNumberFields(fileWriter, identificationTarget);
		writeField(fileWriter, getCommentsField(optimizedMassSpectrum));
		writeField(fileWriter, getReferenceIdentifierField(identificationTarget));
		writeField(fileWriter, getFormulaField(identificationTarget));
		writeField(fileWriter, getInChIField(identificationTarget));
		writeField(fileWriter, getInChIKeyField(identificationTarget));
		writeField(fileWriter, getSmilesField(identificationTarget));
		writeField(fileWriter, getMolWeightField(identificationTarget));
		writeField(fileWriter, getExactMassField(identificationTarget));
		writeField(fileWriter, getDatabaseField(identificationTarget));
		writeField(fileWriter, getContributorField(identificationTarget));
		writeField(fileWriter, getRetentionTimeField(optimizedMassSpectrum));
		writeField(fileWriter, getRelativeRetentionTimeField(optimizedMassSpectrum));
		writeField(fileWriter, getRetentionIndexField(optimizedMassSpectrum));
		writeColumnIndicesFields(fileWriter, identificationTarget);
		writeField(fileWriter, getSourceField(massSpectrum, identificationTarget));
		/*
		 * Mass spectrum
		 */
		writeField(fileWriter, getNumberOfPeaks(optimizedMassSpectrum));
		writeField(fileWriter, getIonsFormatMSL(optimizedMassSpectrum));
		/*
		 * To separate the mass spectra correctly.
		 */
		fileWriter.write(CRLF);
		fileWriter.flush();
	}
}