/*******************************************************************************
 * Copyright (c) 2015, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.identifier.scan;

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.AbstractIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public abstract class AbstractScanTargetCSD extends AbstractIdentificationTarget implements IScanTargetCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -5380450282576684200L;
	//
	private IScanCSD parentScan = null;

	/**
	 * Constructs a new entry.
	 * 
	 * @param libraryInformation
	 * @param comparisonResult
	 * @throws ReferenceMustNotBeNullException
	 */
	public AbstractScanTargetCSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}

	@Override
	public IScanCSD getParentScan() {

		return parentScan;
	}

	@Override
	public void setParentScan(IScanCSD parentScan) {

		if(parentScan != null) {
			this.parentScan = parentScan;
		}
	}
}
