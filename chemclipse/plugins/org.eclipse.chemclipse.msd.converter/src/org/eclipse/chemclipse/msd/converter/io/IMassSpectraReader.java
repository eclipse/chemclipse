/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.io.IFileHelper;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IMassSpectraReader extends IFileHelper {

	/**
	 * Reads the mass spectra from the file.
	 * 
	 * @param file
	 * @param monitor
	 * @return {@link IMassSpectra}
	 * @throws FileNotFoundException
	 * @throws FileIsNotReadableException
	 * @throws FileIsEmptyException
	 * @throws IOException
	 */
	IMassSpectra read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException;

	/**
	 * This method tries to extract the reference identifier from the given name.
	 * Separated name and identifier will be set. If no identifier is available, the name will be set.
	 * 
	 * @param massSpectrum
	 * @param name
	 * @param referenceIdentifierMarker
	 * @param referenceIdentifierPrefix
	 */
	void extractNameAndReferenceIdentifier(IRegularLibraryMassSpectrum massSpectrum, String name, String referenceIdentifierMarker, String referenceIdentifierPrefix);

	/**
	 * Extracts the retention indices.
	 * 
	 * @param massSpectrum
	 * @param value
	 * @param delimiter
	 */
	void extractRetentionIndices(IRegularLibraryMassSpectrum massSpectrum, String value, String delimiter);
}
