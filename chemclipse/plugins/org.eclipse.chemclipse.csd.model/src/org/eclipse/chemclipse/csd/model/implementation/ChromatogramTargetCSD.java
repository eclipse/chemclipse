/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.implementation;

import org.eclipse.chemclipse.csd.model.core.identifier.chromatogram.AbstractChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.core.identifier.chromatogram.IChromatogramTargetCSD;
import org.eclipse.chemclipse.model.exceptions.ReferenceMustNotBeNullException;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;

public class ChromatogramTargetCSD extends AbstractChromatogramTargetCSD implements IChromatogramTargetCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1889531531534498366L;

	public ChromatogramTargetCSD(ILibraryInformation libraryInformation, IComparisonResult comparisonResult) throws ReferenceMustNotBeNullException {
		super(libraryInformation, comparisonResult);
	}
}
