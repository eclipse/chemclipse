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
import org.eclipse.chemclipse.model.identifier.LibraryInformation;

public abstract class AbstractCombinedLibraryMassSpectrum extends AbstractCombinedMassSpectrum implements ICombinedLibraryMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -3114256142707811855L;
	private ILibraryInformation libraryInformation;

	public AbstractCombinedLibraryMassSpectrum() {
		libraryInformation = new LibraryInformation();
	}

	// -----------------------------------------------ILibraryMassSpectrum
	/*
	 * Why is get/set LibraryInformation implemented twice? Here and in {@link
	 * AbstractRegularLibraryMassSpectrum}?<br/> Java does not support multiple
	 * inheritance like in c++.
	 */
	@Override
	public ILibraryInformation getLibraryInformation() {

		return libraryInformation;
	}

	@Override
	public void setLibraryInformation(ILibraryInformation libraryInformation) {

		if(libraryInformation != null) {
			this.libraryInformation = libraryInformation;
		}
	}
	// -----------------------------------------------ILibraryMassSpectrum
}
