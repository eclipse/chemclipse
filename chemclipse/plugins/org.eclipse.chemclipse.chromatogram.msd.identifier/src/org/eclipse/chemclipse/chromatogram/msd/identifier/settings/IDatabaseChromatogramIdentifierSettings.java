/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

public interface IDatabaseChromatogramIdentifierSettings extends IDatabaseIdentifierSettings, IChromatogramIdentifierSettings {

	/*
	 * Identification
	 */
	float getMinSignalToNoiseRatioForIdentification();

	void setMinSignalToNoiseRatioForIdentification(float minSignalToNoiseRatioForIdentification);

	float getMinTailingForIdentification();

	void setMinTailingForIdentification(float minTailingForIdentification);

	float getMaxTailingForIdentification();

	void setMaxTailingForIdentification(float maxTailingForIdentification);

	float getMinMatchFactorForIdentification();

	void setMinMatchFactorForIdentification(float minMatchFactorForIdentification);

	float getMinReverseMatchFactorForIdentification();

	void setMinReverseMatchFactorForIdentification(float minReverseMatchFactorForIdentification);

	boolean isStoreTargetsInChromatogram();

	void setStoreTargetsInChromatogram(boolean storeTargetsInChromatogram);

	float getMatchFactorThresholdForIdentification();

	void setMatchFactorThresholdForIdentification(float matchFactorThresholdForIdentification);

	/*
	 * Database
	 */
	float getMinSignalToNoiseRatioForDatabase();

	void setMinSignalToNoiseRatioForDatabase(float minSignalToNoiseRatioForDatabase);

	float getMinTailingForDatabase();

	void setMinTailingForDatabase(float minTailingForDatabase);

	float getMaxTailingForDatabase();

	void setMaxTailingForDatabase(float maxTailingForDatabase);

	float getMinMatchFactorForDatabase();

	void setMinMatchFactorForDatabase(float minMatchFactorForDatabase);

	float getMinReverseMatchFactorForDatabase();

	void setMinReverseMatchFactorForDatabase(float minReverseMatchFactorForDatabase);
}
