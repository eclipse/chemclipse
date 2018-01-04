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
package org.eclipse.chemclipse.chromatogram.peak.detector.support;

import org.eclipse.chemclipse.numeric.geometry.ISlope;

/**
 * @author eselmeister
 */
public interface IDetectorSlope extends ISlope {

	/**
	 * Returns the retention time in milliseconds.
	 * 
	 * @return retention time
	 */
	int getRetentionTime();

	/**
	 * Returns a "+" if the slope value is > 0. Returns a "-" if the slope value
	 * is < 0. Returns a "0" if the slope value is 0.
	 * 
	 * @return String
	 */
	String getDrift();
}
