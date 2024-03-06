/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.core;

import org.eclipse.chemclipse.model.exceptions.PeakException;

public class AbstractChromatogramPeakVSD extends AbstractPeakVSD implements IChromatogramPeakVSD {

	private IChromatogramVSD chromatogram;
	private static final float INITIAL_SN_VALUE = -1.0f;
	private float signalToNoiseRatio = INITIAL_SN_VALUE;

	public AbstractChromatogramPeakVSD(IPeakModelVSD peakModel, IChromatogramVSD chromatogram) throws IllegalArgumentException, PeakException {

		super(peakModel);
		validateChromatogram(chromatogram);
		validateRetentionTimes(chromatogram, peakModel);
		/*
		 * Assign the references, because all tests has been passed
		 * successfully.
		 */
		this.chromatogram = chromatogram;
	}

	public AbstractChromatogramPeakVSD(IPeakModelVSD peakModel, IChromatogramVSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {

		this(peakModel, chromatogram);
		setModelDescription(modelDescription);
	}

	@Override
	public IChromatogramVSD getChromatogram() {

		return chromatogram;
	}

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
	public int getScanMax() {

		int retentionTime = getPeakModel().getRetentionTimeAtPeakMaximum();
		return chromatogram.getScanNumber(retentionTime);
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