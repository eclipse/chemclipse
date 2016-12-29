/*******************************************************************************
 * Copyright (c) 2015, 2016 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;

public interface IChromatogramTargetCSD extends IIdentificationTarget {

	/**
	 * Returns the parent chromatogram.<br/>
	 * The value could be also null.
	 * 
	 * @return {@link IChromatogramCSD}
	 */
	IChromatogramCSD getParentChromatogram();

	/**
	 * Sets the parent chromatogram.
	 * 
	 * @param parentChromatogram
	 */
	void setParentChromatogram(IChromatogramCSD parentChromatogram);
}
