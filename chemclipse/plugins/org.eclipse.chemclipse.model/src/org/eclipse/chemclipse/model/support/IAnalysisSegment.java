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
package org.eclipse.chemclipse.model.support;

public interface IAnalysisSegment {

	/**
	 * Returns the width of the analysis segment in scan units.
	 * 
	 * @return int
	 */
	int getSegmentWidth();

	/**
	 * Returns the first scan number of the segment.<br/>
	 * The scan number is the position of the scan in its parent chromatogram.
	 * 
	 * @return int
	 */
	int getStartScan();

	/**
	 * Returns the last scan number of the segment. The scan number is the
	 * position of the scan in its parent chromatogram.
	 * 
	 * @return int
	 */
	int getStopScan();
}
