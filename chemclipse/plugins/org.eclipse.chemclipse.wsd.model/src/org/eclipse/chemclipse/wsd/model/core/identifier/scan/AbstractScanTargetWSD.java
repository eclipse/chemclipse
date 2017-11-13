/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.identifier.scan;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public abstract class AbstractScanTargetWSD extends AbstractIdentificationTarget implements IScanTargetWSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 8673133109026849771L;
	//
	private IScanWSD parentScan = null;

	/**
	 * Constructs a new entry.
	 * 
	 * @param libraryInformation
	 * @param comparisonResult
	 * @throws ReferenceMustNotBeNullException
	 */
	public AbstractScanTargetWSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}

	@Override
	public IScanWSD getParentScan() {

		return parentScan;
	}

	@Override
	public void setParentScan(IScanWSD parentScan) {

		if(parentScan != null) {
			this.parentScan = parentScan;
		}
	}
}
