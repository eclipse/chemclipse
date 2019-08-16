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

import java.util.List;

/**
 * @author eselmeister
 */
public interface IAnalysisSupport {

	/**
	 * Returns the number of segments to be analyzed.<br/>
	 * If you have for example a chromatogram with 5726 scans and your analysis
	 * segment width is 13 scans, the analysis support would return a value of
	 * 441.<br/>
	 * Why 441? 440x a segment width of 13 scans and 1x a segment width of 6
	 * scans.
	 * 
	 * @return int
	 */
	int getNumberOfAnalysisSegments();

	/**
	 * Returns a list of the available analysis segments.
	 * 
	 * @return List<IAnalysisSegment>
	 */
	List<IAnalysisSegment> getAnalysisSegments();
}
