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
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;

public abstract class AbstractRegularLibraryMassSpectrum extends AbstractRegularMassSpectrum implements IRegularLibraryMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 3140941167337900216L;
	private ILibraryInformation libraryInformation;

	public AbstractRegularLibraryMassSpectrum() {
		libraryInformation = new LibraryInformation();
	}

	// -----------------------------------------------ILibraryMassSpectrum
	/*
	 * Why is get/set LibraryInformation implemented twice? Here and in {@link
	 * AbstractCombinedLibraryMassSpectrum}?<br/> Java does not support multiple
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
