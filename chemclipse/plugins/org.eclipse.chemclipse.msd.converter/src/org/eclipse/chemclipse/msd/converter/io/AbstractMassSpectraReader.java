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
import org.eclipse.chemclipse.msd.model.core.IRegularLibraryMassSpectrum;

public abstract class AbstractMassSpectraReader extends AbstractFileHelper implements IMassSpectraReader {

	public void extractNameAndReferenceIdentifier(IRegularLibraryMassSpectrum massSpectrum, String name, String referenceIdentifierMarker, String referenceIdentifierPrefix) {

		boolean setNameTraditionally = true;
		if(referenceIdentifierMarker != null && !referenceIdentifierPrefix.equals("")) {
			if(name.contains(referenceIdentifierMarker)) {
				String[] values = name.split(referenceIdentifierMarker);
				if(values.length >= 2) {
					/*
					 * Extract the reference identifier.
					 */
					setNameTraditionally = false;
					massSpectrum.getLibraryInformation().setName(values[0].trim());
					//
					StringBuilder builder = new StringBuilder();
					builder.append(referenceIdentifierPrefix);
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
			massSpectrum.getLibraryInformation().setName(name);
		}
	}
}
