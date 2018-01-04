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
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public interface ILibraryMassSpectrum {

	/**
	 * Returns the library information.<br/>
	 * This contains e.g. CAS number, name, etc.
	 * 
	 * @return {@link ILibraryInformation}
	 */
	ILibraryInformation getLibraryInformation();

	/**
	 * Sets a valid library information instance to the current library mass
	 * spectrum.<br/>
	 * LibraryInformation must not be null.
	 * 
	 * @param libraryInformation
	 */
	void setLibraryInformation(ILibraryInformation libraryInformation);
}
