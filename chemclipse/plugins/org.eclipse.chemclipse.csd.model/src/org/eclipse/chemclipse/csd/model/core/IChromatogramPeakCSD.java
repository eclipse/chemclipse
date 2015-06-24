/*******************************************************************************
 * Copyright (c) 2014, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

public interface IChromatogramPeakCSD extends IPeakCSD {

	/**
	 * Returns the scan number of the peak maximum.
	 * 
	 * @return int
	 */
	int getScanMax();

	/**
	 * Returns the signal to noise ratio of the peak.
	 * 
	 * @return float
	 */
	float getSignalToNoiseRatio();

	/**
	 * Returns the chromatogram to which this peak belongs to.
	 * 
	 * @return {@link IChromatogramCSD}
	 */
	IChromatogramCSD getChromatogram();

	/**
	 * Returns the width of the actual peak at its absolute baseline.<br/>
	 * The width is given in scan units.<br/>
	 * The width is not measured at the points of inflection.<br/>
	 * If the peak is out of limits or something has gone wrong, 0 will be
	 * returned.
	 * 
	 * @return int
	 */
	int getWidthBaselineTotalInScans();
}
