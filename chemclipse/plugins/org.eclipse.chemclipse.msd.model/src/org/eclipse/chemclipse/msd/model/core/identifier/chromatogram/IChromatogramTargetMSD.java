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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.chromatogram;

import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;

public interface IChromatogramTargetMSD extends IIdentificationTarget {

	/**
	 * Returns the parent chromatogram.<br/>
	 * The value could be also null.
	 * 
	 * @return {@link IChromatogramMSD}
	 */
	IChromatogramMSD getParentChromatogram();

	/**
	 * Sets the parent chromatogram.
	 * 
	 * @param parentChromatogram
	 */
	void setParentChromatogram(IChromatogramMSD parentChromatogram);
}
