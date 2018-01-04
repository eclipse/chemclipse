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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;

public interface IBaselineDetectorSupport {

	/**
	 * Returns the id of the selected detector name.<br/>
	 * The id of the selected filter is used to determine which detector should
	 * be used to set the baseline of the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoBaselineDetectorAvailableException
	 */
	String getDetectorId(int index) throws NoBaselineDetectorAvailableException;

	/**
	 * Returns an IBaselineDetectorSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param detectorId
	 * @return {@link IBaselineDetectorSupplier}
	 * @throws NoBaselineDetectorAvailableException
	 */
	IBaselineDetectorSupplier getBaselineDetectorSupplier(String detectorId) throws NoBaselineDetectorAvailableException;

	/**
	 * Returns an ArrayList with all available baseline detector ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoBaselineDetectorAvailableException
	 */
	List<String> getAvailableDetectorIds() throws NoBaselineDetectorAvailableException;

	/**
	 * Returns the list of available baseline detector names.
	 * 
	 * @return String[]
	 * @throws NoBaselineDetectorAvailableException
	 */
	String[] getDetectorNames() throws NoBaselineDetectorAvailableException;
}
