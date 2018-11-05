/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public abstract class AbstractDatabaseChromatogramIdentifierSettings extends AbstractDatabaseIdentifierSettings implements IDatabaseChromatogramIdentifierSettings {

	/*
	 * Identification
	 */
	@JsonProperty(value = "Min S/N ratio (Identification)", defaultValue = "10")
	@JsonPropertyDescription(value = "The minimum signal to noise ratio for identification.")
	private float minSignalToNoiseRatioForIdentification;
	private float minTailingForIdentification;
	private float maxTailingForIdentification;
	private float minMatchFactorForIdentification;
	private float minReverseMatchFactorForIdentification;
	private float matchFactorThresholdForIdentification;
	private boolean storeTargetsInChromatogram;
	/*
	 * Database
	 */
	private float minSignalToNoiseRatioForDatabase;
	private float minTailingForDatabase;
	private float maxTailingForDatabase;
	private float minMatchFactorForDatabase;
	private float minReverseMatchFactorForDatabase;

	/*
	 * Identification
	 */
	@Override
	public float getMinSignalToNoiseRatioForIdentification() {

		return minSignalToNoiseRatioForIdentification;
	}

	@Override
	public void setMinSignalToNoiseRatioForIdentification(float minSignalToNoiseRatioForIdentification) {

		this.minSignalToNoiseRatioForIdentification = minSignalToNoiseRatioForIdentification;
	}

	@Override
	public float getMinTailingForIdentification() {

		return minTailingForIdentification;
	}

	@Override
	public void setMinTailingForIdentification(float minTailingForIdentification) {

		this.minTailingForIdentification = minTailingForIdentification;
	}

	@Override
	public float getMaxTailingForIdentification() {

		return maxTailingForIdentification;
	}

	@Override
	public void setMaxTailingForIdentification(float maxTailingForIdentification) {

		this.maxTailingForIdentification = maxTailingForIdentification;
	}

	@Override
	public float getMatchFactorThresholdForIdentification() {

		return matchFactorThresholdForIdentification;
	}

	@Override
	public void setMatchFactorThresholdForIdentification(float matchFactorThresholdForIdentification) {

		this.matchFactorThresholdForIdentification = matchFactorThresholdForIdentification;
	}

	@Override
	public float getMinMatchFactorForIdentification() {

		return minMatchFactorForIdentification;
	}

	@Override
	public void setMinMatchFactorForIdentification(float minMatchFactorForIdentification) {

		this.minMatchFactorForIdentification = minMatchFactorForIdentification;
	}

	@Override
	public float getMinReverseMatchFactorForIdentification() {

		return minReverseMatchFactorForIdentification;
	}

	@Override
	public void setMinReverseMatchFactorForIdentification(float minReverseMatchFactorForIdentification) {

		this.minReverseMatchFactorForIdentification = minReverseMatchFactorForIdentification;
	}

	@Override
	public boolean isStoreTargetsInChromatogram() {

		return storeTargetsInChromatogram;
	}

	@Override
	public void setStoreTargetsInChromatogram(boolean storeTargetsInChromatogram) {

		this.storeTargetsInChromatogram = storeTargetsInChromatogram;
	}

	/*
	 * Database
	 */
	@Override
	public float getMinSignalToNoiseRatioForDatabase() {

		return minSignalToNoiseRatioForDatabase;
	}

	@Override
	public void setMinSignalToNoiseRatioForDatabase(float minSignalToNoiseRatioForDatabase) {

		this.minSignalToNoiseRatioForDatabase = minSignalToNoiseRatioForDatabase;
	}

	@Override
	public float getMinTailingForDatabase() {

		return minTailingForDatabase;
	}

	@Override
	public void setMinTailingForDatabase(float minTailingForDatabase) {

		this.minTailingForDatabase = minTailingForDatabase;
	}

	@Override
	public float getMaxTailingForDatabase() {

		return maxTailingForDatabase;
	}

	@Override
	public void setMaxTailingForDatabase(float maxTailingForDatabase) {

		this.maxTailingForDatabase = maxTailingForDatabase;
	}

	@Override
	public float getMinMatchFactorForDatabase() {

		return minMatchFactorForDatabase;
	}

	@Override
	public void setMinMatchFactorForDatabase(float minMatchFactorForDatabase) {

		this.minMatchFactorForDatabase = minMatchFactorForDatabase;
	}

	@Override
	public float getMinReverseMatchFactorForDatabase() {

		return minReverseMatchFactorForDatabase;
	}

	@Override
	public void setMinReverseMatchFactorForDatabase(float minReverseMatchFactorForDatabase) {

		this.minReverseMatchFactorForDatabase = minReverseMatchFactorForDatabase;
	}
}
