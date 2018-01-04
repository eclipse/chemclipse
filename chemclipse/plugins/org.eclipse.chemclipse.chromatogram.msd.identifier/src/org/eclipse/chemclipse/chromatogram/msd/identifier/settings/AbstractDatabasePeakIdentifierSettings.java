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

public abstract class AbstractDatabasePeakIdentifierSettings extends AbstractDatabaseIdentifierSettings implements IDatabasePeakIdentifierSettings {

	/*
	 * Identification
	 */
	private float minSignalToNoiseRatioForIdentification;
	private float minTailingForIdentification;
	private float maxTailingForIdentification;
	private float minMatchFactorForIdentification;
	private float minReverseMatchFactorForIdentification;
	private boolean useHitEntryOnlyOnce;
	private boolean storeTargetsInEachPeak;
	private boolean useOnlyBestHit;
	/*
	 * Database
	 */
	private float minSignalToNoiseRatioForDatabase;
	private float minMatchFactorForDatabase;
	private float minReverseMatchFactorForDatabase;
	private boolean mergePeak;
	private boolean mergePeakOnlyWithBestHit;

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
	public boolean isUseHitEntryOnlyOnce() {

		return this.useHitEntryOnlyOnce;
	}

	@Override
	public void setUseHitEntryOnlyOnce(boolean useHitEntryOnlyOnce) {

		this.useHitEntryOnlyOnce = useHitEntryOnlyOnce;
	}

	@Override
	public boolean isStoreTargetsInEachPeak() {

		return storeTargetsInEachPeak;
	}

	@Override
	public void setStoreTargetsInEachPeak(boolean storeTargetsInEachPeak) {

		this.storeTargetsInEachPeak = storeTargetsInEachPeak;
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

	@Override
	public boolean isMergePeak() {

		return mergePeak;
	}

	@Override
	public void setMergePeak(boolean mergePeak) {

		this.mergePeak = mergePeak;
	}

	@Override
	public boolean isMergePeakOnlyWithBestHit() {

		return mergePeakOnlyWithBestHit;
	}

	@Override
	public void setMergePeakOnlyWithBestHit(boolean mergePeakOnlyWithBestHit) {

		this.mergePeakOnlyWithBestHit = mergePeakOnlyWithBestHit;
	}

	@Override
	public boolean isUseOnlyBestHit() {

		return useOnlyBestHit;
	}

	@Override
	public void setUseOnlyBestHit(boolean useOnlyBestHit) {

		this.useOnlyBestHit = useOnlyBestHit;
	}
}