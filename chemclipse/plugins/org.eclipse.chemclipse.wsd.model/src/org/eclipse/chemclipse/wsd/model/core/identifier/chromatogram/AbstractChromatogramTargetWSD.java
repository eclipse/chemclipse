/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.identifier.chromatogram;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;

public abstract class AbstractChromatogramTargetWSD extends AbstractIdentificationTarget implements IChromatogramTargetWSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 2752908164516274699L;
	//
	private IChromatogramWSD parentChromatogram = null;

	/**
	 * Constructs a new entry.
	 * 
	 * @param libraryInformation
	 * @param comparisonResult
	 * @throws ReferenceMustNotBeNullException
	 */
	public AbstractChromatogramTargetWSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}

	@Override
	public IChromatogramWSD getParentChromatogram() {

		return parentChromatogram;
	}

	@Override
	public void setParentChromatogram(IChromatogramWSD parentChromatogram) {

		if(parentChromatogram != null) {
			this.parentChromatogram = parentChromatogram;
		}
	}
}
