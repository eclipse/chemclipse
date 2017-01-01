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
package org.eclipse.chemclipse.msd.model.core.identifier.chromatogram;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

public abstract class AbstractChromatogramTargetMSD extends AbstractIdentificationTarget implements IChromatogramTargetMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 7432640756057462278L;
	//
	private transient IChromatogramMSD parentChromatogram = null;

	/**
	 * Constructs a new entry.
	 * 
	 * @param libraryInformation
	 * @param comparisonResult
	 * @throws ReferenceMustNotBeNullException
	 */
	public AbstractChromatogramTargetMSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}

	@Override
	public IChromatogramMSD getParentChromatogram() {

		return parentChromatogram;
	}

	@Override
	public void setParentChromatogram(IChromatogramMSD parentChromatogram) {

		if(parentChromatogram != null) {
			this.parentChromatogram = parentChromatogram;
		}
	}
}
