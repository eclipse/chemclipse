/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.List;

/**
 * This class stores a list of peaks.
 * 
 * @author eselmeister
 * 
 */
public interface IPeaks {

	/**
	 * Adds the peak to the end of the list.
	 * 
	 * @param peak
	 */
	void addPeak(IPeak peak);

	/**
	 * Removes the peak from the list.
	 * 
	 * @param peak
	 */
	void removePeak(IPeak peak);

	/**
	 * Returns the peak with the given number.<br/>
	 * Be aware, the index is 1 based and not 0 based like in a normal list.<br/>
	 * If no peak is available, null will be returned.
	 * 
	 * @param i
	 * @return IPeak
	 */
	IPeak getPeak(int i);

	/**
	 * Returns the list of peaks.
	 * 
	 * @return List<IPeak>
	 */
	List<IPeak> getPeaks();

	/**
	 * Returns the number of stored peaks.
	 * 
	 * @return int
	 */
	int size();
}
