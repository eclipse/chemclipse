/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.xwc;

import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;

/**
 * @deprecated Use {@link IExtractedSingleWavelengthSignal} instead
 *
 * @see {@link IExtractedSingleWavelengthSignalExtractor}
 * @see {@link IExtractedSingleWavelengthSignals}
 * 
 */
@Deprecated
public interface IExtractedWavelengthSignal {

	void setAbundance(IScanSignalWSD scanSignal, boolean removePreviousAbundance);

	void setAbundance(IScanSignalWSD scanSignal);

	void setAbundance(int wavelength, float abundance);

	void setAbundance(int wavelength, float abundance, boolean removePreviousAbundance);

	float getAbundance(int wavelength);

	int getNumberOfWavelengthValues();

	float getTotalSignal();

	int getWavelengthMaxIntensity();

	float getMaxIntensity();

	float getMinIntensity();

	float getNthHighestIntensity(int n);

	float getMeanIntensity();

	float getMedianIntensity();

	int getRetentionTime();

	void setRetentionTime(int retentionTime);

	float getRetentionIndex();

	void setRetentionIndex(float retentionIndex);

	int getStartWavelength();

	int getStopWavelength();

	IWavelengthRange getWavelengthRange();

	void normalize();

	void normalize(float normalizationBase);
}
