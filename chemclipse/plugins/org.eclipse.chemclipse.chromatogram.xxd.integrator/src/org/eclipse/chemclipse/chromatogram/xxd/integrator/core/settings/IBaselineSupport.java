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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;

public interface IBaselineSupport {

	/**
	 * Holds the baseline from the start to the stop retention time at the level
	 * of the start retention time.<br/>
	 * Other values of baseline support will may be overridden.
	 * 
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 */
	void setBaselineHoldOn(int startRetentionTime, int stopRetentionTime);

	/**
	 * Holds the baseline over the whole chromatogram a the level of the given
	 * retention time.<br/>
	 * Other values of baseline support will may be overridden.
	 * 
	 * @param retentionTime
	 */
	void setBaselineNow(int retentionTime);

	/**
	 * Uses the baseline at the given retention time only from the retention
	 * time backwards.<br/>
	 * Other values of baseline support will may be overridden.
	 * 
	 * @param retentionTime
	 */
	void setBaselineBack(int retentionTime);

	/**
	 * If a new baseline model will be stored, all previous changes will be
	 * discarded.
	 * 
	 * @param baselineModel
	 */
	void setBaselineModel(IBaselineModel baselineModel);

	/**
	 * Returns the baseline model.
	 * 
	 * @return IBaselineModel
	 */
	IBaselineModel getBaselineModel();

	/**
	 * Sets the baseline to the default value.
	 */
	void reset();

	/**
	 * Returns the baseline at the given retention time corrected by the given
	 * baseline support settings.
	 * 
	 * @param retentionTime
	 * @return float
	 */
	float getBackgroundAbundance(int retentionTime);
}
