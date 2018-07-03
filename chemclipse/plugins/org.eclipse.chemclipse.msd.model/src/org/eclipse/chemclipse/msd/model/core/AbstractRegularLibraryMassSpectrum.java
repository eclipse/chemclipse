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
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;

public abstract class AbstractRegularLibraryMassSpectrum extends AbstractRegularMassSpectrum implements IRegularLibraryMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -3521383640386911035L;
	//
	private ILibraryInformation libraryInformation;

	/**
	 * Creates a new instance of {@code AbstractRegularLibraryMassSpectrum} by creating a
	 * shallow copy of provided {@code templateScan}.
	 * 
	 * @param templateScan
	 *            {@link IScan scan} that is used as a template
	 */
	public AbstractRegularLibraryMassSpectrum(IScanMSD templateScan) {
		super(templateScan);
	}

	public AbstractRegularLibraryMassSpectrum(short massSpectrometer, short massSpectrumType) {
		super(massSpectrometer, massSpectrumType);
	}

	public AbstractRegularLibraryMassSpectrum() {
		libraryInformation = new LibraryInformation();
	}

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
}
