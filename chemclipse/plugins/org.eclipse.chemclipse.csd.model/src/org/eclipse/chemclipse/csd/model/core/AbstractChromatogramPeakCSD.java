/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import org.eclipse.chemclipse.model.exceptions.PeakException;

public abstract class AbstractChromatogramPeakCSD extends AbstractPeakCSD implements IChromatogramPeakCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -3212539952833570462L;
	//
	private IChromatogramCSD chromatogram;
	private static final float INITIAL_SN_VALUE = -1.0f;
	private float signalToNoiseRatio = INITIAL_SN_VALUE;

	/**
	 * Construct a peak.
	 * 
	 * @param peakModel
	 * @param chromatogram
	 * @throws IllegalArgumentException
	 * @throws PeakException
	 */
	public AbstractChromatogramPeakCSD(IPeakModelCSD peakModel, IChromatogramCSD chromatogram) throws IllegalArgumentException, PeakException {
		super(peakModel);
		validateChromatogram(chromatogram);
		validateRetentionTimes(chromatogram, peakModel);
		/*
		 * Assign the references, because all tests has been passed
		 * successfully.
		 */
		this.chromatogram = chromatogram;
	}

	public AbstractChromatogramPeakCSD(IPeakModelCSD peakModel, IChromatogramCSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {
		this(peakModel, chromatogram);
		setModelDescription(modelDescription);
	}

	@Override
	public int getScanMax() {

		int retentionTime = getPeakModel().getRetentionTimeAtPeakMaximum();
		return chromatogram.getScanNumber(retentionTime);
	}

	// TODO JUnit
	@Override
	public float getSignalToNoiseRatio() {

		/*
		 * The value INITIAL_SN_VALUE (-1.0f) means, that the signal to noise value has been not set yet.
		 */
		if(signalToNoiseRatio == INITIAL_SN_VALUE) {
			float totalSignal = getPeakModel().getPeakAbundance();
			signalToNoiseRatio = chromatogram.getSignalToNoiseRatio(totalSignal);
		}
		return signalToNoiseRatio;
	}

	@Override
	public int getWidthBaselineTotalInScans() {

		int start = chromatogram.getScanNumber(getPeakModel().getStartRetentionTime());
		if(start == 0) {
			return 0;
		}
		int stop = chromatogram.getScanNumber(getPeakModel().getStopRetentionTime());
		if(stop == 0) {
			return 0;
		}
		return stop - start + 1;
	}

	@Override
	public IChromatogramCSD getChromatogram() {

		return chromatogram;
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		AbstractChromatogramPeakCSD other = (AbstractChromatogramPeakCSD)otherObject;
		return getPeakModel().equals(other.getPeakModel()) && chromatogram == other.getChromatogram();
	}

	@Override
	public int hashCode() {

		return 7 * getPeakModel().hashCode() + 11 * chromatogram.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("peakModel=" + getPeakModel());
		builder.append(",");
		builder.append("chromatogram=" + chromatogram);
		builder.append("]");
		return builder.toString();
	}
}
