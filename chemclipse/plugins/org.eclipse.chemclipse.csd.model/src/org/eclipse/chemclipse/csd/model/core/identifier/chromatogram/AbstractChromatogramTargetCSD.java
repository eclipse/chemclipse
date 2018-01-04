/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.identifier.chromatogram;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public abstract class AbstractChromatogramTargetCSD extends AbstractIdentificationTarget implements IChromatogramTargetCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2068089855978708975L;
	//
	private IChromatogramCSD parentChromatogram = null;

	/**
	 * Constructs a new entry.
	 * 
	 * @param libraryInformation
	 * @param comparisonResult
	 * @throws ReferenceMustNotBeNullException
	 */
	public AbstractChromatogramTargetCSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}

	@Override
	public IChromatogramCSD getParentChromatogram() {

		return parentChromatogram;
	}

	@Override
	public void setParentChromatogram(IChromatogramCSD parentChromatogram) {

		if(parentChromatogram != null) {
			this.parentChromatogram = parentChromatogram;
		}
	}
}
