/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.massspectrum;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public abstract class AbstractMassSpectrumTarget extends AbstractIdentificationTarget implements IScanTargetMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 3774815554992298233L;
	//
	private IScanMSD parentMassSpectrum;

	/**
	 * Constructs the entry.
	 * 
	 * @param libraryInformation
	 * @param comparisonResult
	 * @throws ReferenceMustNotBeNullException
	 */
	public AbstractMassSpectrumTarget(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}

	@Override
	public IScanMSD getParentMassSpectrum() {

		return parentMassSpectrum;
	}

	@Override
	public void setParentMassSpectrum(IScanMSD parentMassSpectrum) {

		if(parentMassSpectrum != null) {
			this.parentMassSpectrum = parentMassSpectrum;
		}
	}
}
