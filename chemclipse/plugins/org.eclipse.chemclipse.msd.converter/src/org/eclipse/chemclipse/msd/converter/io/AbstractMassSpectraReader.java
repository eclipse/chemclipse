/*******************************************************************************
 * Copyright (c) 2015, 2016 Dr. Philip Wenig.
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
import org.eclipse.chemclipse.model.core.RetentionIndexType;
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

public abstract class AbstractMassSpectraReader extends AbstractFileHelper implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(AbstractMassSpectraReader.class);

	@Override
	public void extractNameAndReferenceIdentifier(IRegularLibraryMassSpectrum massSpectrum, String value, String referenceIdentifierMarker, String referenceIdentifierPrefix) {

		if(value != null) {
			boolean setNameTraditionally = true;
			if(referenceIdentifierMarker != null && !referenceIdentifierMarker.equals("")) {
				if(value.contains(referenceIdentifierMarker)) {
					String[] values = value.split(referenceIdentifierMarker);
					if(values.length >= 2) {
						/*
						 * Extract the reference identifier.
						 */
						setNameTraditionally = false;
						massSpectrum.getLibraryInformation().setName(values[0].trim());
						//
						StringBuilder builder = new StringBuilder();
						if(referenceIdentifierPrefix != null) {
							builder.append(referenceIdentifierPrefix);
						}
						int size = values.length;
						for(int i = 1; i < size; i++) {
							builder.append(values[i]);
							if(i < size - 1) {
								builder.append(" ");
							}
						}
						massSpectrum.getLibraryInformation().setReferenceIdentifier(builder.toString().trim());
					}
				}
			}
			//
			if(setNameTraditionally) {
				massSpectrum.getLibraryInformation().setName(value);
			}
		}
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
					massSpectrum.setRetentionIndex(RetentionIndexType.APOLAR, retentionIndex);
					massSpectrum.setRetentionIndex(RetentionIndexType.POLAR, parseFloat(values[1]));
				}
			}
			//
			if(setRetentionIndexTraditionally) {
				massSpectrum.setRetentionIndex(parseFloat(value));
			}
		}
	}

	private float parseFloat(String value) {

		float result = 0.0f;
		try {
			result = Float.parseFloat(value.trim());
		} catch(Exception e) {
			logger.warn(e);
		}
		return result;
	}
}
