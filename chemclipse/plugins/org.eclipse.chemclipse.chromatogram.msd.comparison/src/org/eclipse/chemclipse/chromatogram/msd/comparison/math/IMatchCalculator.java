/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.comparison.math;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IIonRange;

/**
 * Returns the geometric distance match quality.<br/>
 * 0 : no match
 * 1 : best match <br/>
 * A normalization of the unknown and reference mass spectrum is not
 * required.<br/>
 * See
 * "Alfassi, Z. B., Vector analysis of multi-measurements identification", 2004
 * Equation (3).
 */
public interface IMatchCalculator {

	/**
	 * The given ion range will be used to compare the unknown against the reference.
	 * 
	 * @param unknown
	 * @param reference
	 * @param ionRange
	 * @return float
	 */
	float calculate(IScanMSD unknown, IScanMSD reference, IIonRange ionRange);

	/**
	 * This could be called a direct comparison.
	 * Only the ions available in the unknown will be used to make a comparison
	 * against the reference mass spectrum.
	 * 
	 * @param unknown
	 * @param reference
	 * @return float
	 */
	float calculate(IScanMSD unknown, IScanMSD reference);
}
