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
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;

public interface IPeakDetectorSupport {

	/**
	 * Returns the id of the selected peak detector name.<br/>
	 * The id of the selected filter is used to determine which detector should
	 * be used to calculate the integration results of the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoPeakDetectorAvailableException
	 */
	String getPeakDetectorId(int index) throws NoPeakDetectorAvailableException;

	/**
	 * Returns an IPeakDetectorSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param peakDetectorId
	 * @return {@link IPeakDetectorSupplier}
	 * @throws NoPeakDetectorAvailableException
	 */
	IPeakDetectorSupplier getPeakDetectorSupplier(String peakDetectorId) throws NoPeakDetectorAvailableException;

	/**
	 * Returns an ArrayList with all available peak detector ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoPeakDetectorAvailableException
	 */
	List<String> getAvailablePeakDetectorIds() throws NoPeakDetectorAvailableException;

	/**
	 * Returns the list of available peak detector names.
	 * 
	 * @return String[]
	 * @throws NoPeakDetectorAvailableException
	 */
	String[] getPeakDetectorNames() throws NoPeakDetectorAvailableException;
}
