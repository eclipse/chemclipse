/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.util.Map;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.baseline.IChromatogramBaseline;

public interface IChromatogramWSDBaseline extends IChromatogramBaseline {

	/**
	 * Returns the baseline model of the current chromatogram for specific wavelength.<br/>
	 * You can modify the baseline of the current chromatogram through this
	 * baseline model.
	 * 
	 * @param wavelength
	 * 
	 * @return IBaselineModel
	 */
	IBaselineModel getBaselineModel(double wavelength);

	/**
	 * remove baseline on specific wavelength if exists
	 * 
	 * @param wavelength
	 */
	void removeBaselineModel(double wavelength);

	/**
	 * Returns the map of baselines model of the current chromatogram.<br/>
	 * You can modify the baseline of the current chromatogram through this
	 * baseline model.
	 * 
	 * @return unmodifiable map of baseline
	 */
	Map<Double, IBaselineModel> getBaselineModels();

	/**
	 * Method return true if user call method {@link #getBaselineModel(double)} with same wavelength, <br/>
	 * and does not remove this baseline {@link #removeBaselineModel(double)}
	 * 
	 * @param wavelength
	 * 
	 * @return
	 */
	boolean containsBaseline(double wavelength);
}
