/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.msd.model.core.IIon;

public interface IExtractedIonSignal {

	int ION_NOT_SET = 0;

	/**
	 * Sets the abundance for the given ion.<br/>
	 * If still a ion exists, the stored abundance will be removed and
	 * the actual will be stored if removePreviousAbundance is true.<br/>
	 * Otherwise you could also call setAbundance(IIon ion).<br/>
	 * This could occur for example, if high resolution mass spectra are used.
	 * 
	 * @param ion
	 * @param removePreviousAbundance
	 */
	void setAbundance(IIon ion, boolean removePreviousAbundance);

	/**
	 * Sets the abundance for the given ion.<br/>
	 * If still a ion exists, the abundance will be added.<br/>
	 * This could occur for example, if high resolution mass spectra are used.
	 * 
	 * @param ion
	 */
	void setAbundance(IIon ion);

	/**
	 * Sets the abundance for the given ion.<br/>
	 * If still a ion exists, the abundance will be added.<br/>
	 * This could occur for example, if high resolution mass spectra are used.<br/>
	 * You could also use the method setAbundance(IIon ion).
	 * 
	 * @param ion
	 * @param abundance
	 */
	void setAbundance(int ion, float abundance);

	/**
	 * Sets the abundance for the given ion.<br/>
	 * If still a ion exists, the stored abundance will be removed and
	 * the actual will be stored if removePreviousAbundance is true.<br/>
	 * Otherwise you could also call setAbundance(IIon ion).<br/>
	 * This could occur for example, if high resolution mass spectra are used.
	 * 
	 * @param ion
	 * @param abundance
	 * @param removePreviousAbundance
	 */
	void setAbundance(int ion, float abundance, boolean removePreviousAbundance);

	/**
	 * Returns the abundance for the given ion.
	 * 
	 * @param ion
	 * @return float
	 */
	float getAbundance(int ion);

	/**
	 * Returns the number of abundance values.
	 * 
	 * @return int
	 */
	int getNumberOfIonValues();

	/**
	 * Returns the total signal of all stored ion abundances.
	 * 
	 * @return float
	 */
	float getTotalSignal();

	/**
	 * Returns the ion with the highest intensity.
	 * May return 0 if no ion was found.
	 * 
	 * @return int
	 */
	int getIonMaxIntensity();

	/**
	 * Returns the highest intensity.
	 * 
	 * @return float
	 */
	float getMaxIntensity();

	/**
	 * Returns the lowest intensity.
	 * 
	 * @return float
	 */
	float getMinIntensity();

	/**
	 * Returns nth highest intensity.
	 * 
	 * @return float
	 */
	float getNthHighestIntensity(int n);

	/**
	 * Returns the mean intensity.
	 * 
	 * @return float
	 */
	float getMeanIntensity();

	/**
	 * Returns the median intensity.
	 * 
	 * @return float
	 */
	float getMedianIntensity();

	/**
	 * Returns the retention time.<br/>
	 * The retention time is stored in milliseconds.
	 * 
	 * @return int
	 */
	int getRetentionTime();

	/**
	 * Sets the retention time in milliseconds.<br/>
	 * The retention time must be greater than 0 otherwise it will not be
	 * stored.
	 * 
	 * @param retentionTime
	 */
	void setRetentionTime(int retentionTime);

	/**
	 * Returns the retention index.
	 * 
	 * @return float
	 */
	float getRetentionIndex();

	/**
	 * Sets the retention index.<br/>
	 * The retention index must be greater than 0 otherwise it will not be
	 * stored.
	 * 
	 * @param retentionIndex
	 */
	void setRetentionIndex(float retentionIndex);

	/**
	 * Returns the lowest stored ion value (ion).
	 * 
	 * @return int
	 */
	int getStartIon();

	/**
	 * Returns the highest stored ion value (ion).
	 * 
	 * @return int
	 */
	int getStopIon();

	/**
	 * Returns the ion range.
	 * 
	 * @return
	 */
	IIonRange getIonRange();

	/**
	 * Normalize all stored signals.
	 *
	 */
	void normalize();

	/**
	 * Normalize all intensity signals.
	 * The base must be > 0.
	 * 
	 * @return float
	 */
	void normalize(float normalizationBase);
}
