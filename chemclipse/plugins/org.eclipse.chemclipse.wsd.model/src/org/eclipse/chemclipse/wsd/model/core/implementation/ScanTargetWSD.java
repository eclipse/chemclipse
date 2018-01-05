/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.wsd.model.core.identifier.scan.AbstractScanTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.identifier.scan.IScanTargetWSD;

public class ScanTargetWSD extends AbstractScanTargetWSD implements IScanTargetWSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 5580885758368467171L;

	public ScanTargetWSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}
}
