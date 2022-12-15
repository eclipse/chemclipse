/*******************************************************************************
 * Copyright (c) 2015, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.io.AbstractFileHelper;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.support.LibraryInformationSupport;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

public abstract class AbstractMassSpectraReader extends AbstractFileHelper implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(AbstractMassSpectraReader.class);
	private LibraryInformationSupport libraryInformationSupport = new LibraryInformationSupport();

	@Override
	public void extractNameAndReferenceIdentifier(IRegularLibraryMassSpectrum massSpectrum, String value, String referenceIdentifierMarker, String referenceIdentifierPrefix) {

		ILibraryInformation libraryInformation = massSpectrum.getLibraryInformation();
		libraryInformationSupport.extractNameAndReferenceIdentifier(value, libraryInformation, referenceIdentifierMarker, referenceIdentifierPrefix);
	}

	@Override
	public void extractRetentionIndices(IRegularLibraryMassSpectrum massSpectrum, String value, String delimiter) {

		if(value != null && delimiter != null) {
			boolean setRetentionIndexTraditionally = true;
			if(value.contains(delimiter)) {
				String[] values = value.split(", ");
				if(values.length >= 2) {
					/*
					 * Default
					 */
					setRetentionIndexTraditionally = false;
					float retentionIndex = parseFloat(values[0]);
					massSpectrum.setRetentionIndex(retentionIndex);
					/*
					 * Extra values.
					 */
					massSpectrum.setRetentionIndex(SeparationColumnType.NON_POLAR, retentionIndex);
					massSpectrum.setRetentionIndex(SeparationColumnType.POLAR, parseFloat(values[1]));
				}
			}
			//
			if(setRetentionIndexTraditionally) {
				massSpectrum.setRetentionIndex(parseFloat(value));
			}
		}
	}

	// Allow missing retention indices if the DB doesn't provide them.
	private float parseFloat(String value) {

		float result = 0.0f;
		if(value == null) {
			return result;
		}
		value = value.trim();
		if(value.isEmpty()) {
			return result;
		}
		try {
			result = Float.parseFloat(value);
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return result;
	}
}