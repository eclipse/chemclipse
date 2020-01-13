/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove hashcode and equals
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.exceptions.PeakException;

/**
 * This is an abstract peak implementation. It defines and implements the
 * default behavior of a peak.<br/>
 * The peak itself does not store information about its chemical identity. As
 * for that you can't find a CAS number or something else for a peak.<br/>
 * Identification of peaks will be done separately. Instead of identification
 * the peak gives information about peak tailing, purity, background and so on.
 * 
 * @author eselmeister
 */
public abstract class AbstractChromatogramPeakMSD extends AbstractPeakMSD implements IChromatogramPeakMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 7031247154782115765L;
	//
	private transient IChromatogramMSD chromatogram;
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
	public AbstractChromatogramPeakMSD(IPeakModelMSD peakModel, IChromatogramMSD chromatogram) throws IllegalArgumentException, PeakException {
		super(peakModel);
		validateChromatogram(chromatogram);
		validateRetentionTimes(chromatogram, peakModel);
		/*
		 * Assign the references, because all tests has been passed
		 * successfully.
		 */
		this.chromatogram = chromatogram;
	}

	public AbstractChromatogramPeakMSD(IPeakModelMSD peakModel, IChromatogramMSD chromatogram, String modelDescription) throws IllegalArgumentException, PeakException {
		this(peakModel, chromatogram);
		setModelDescription(modelDescription);
	}

	// ------------------------------------------IPeak
	@Override
	public IScanMSD getChromatogramMassSpectrum() {

		return chromatogram.getSupplierScan(getScanMax());
	}

	@Override
	public float getBackgroundAbundanceAtScan(int scan) {

		if(scan >= 1 && scan <= chromatogram.getNumberOfScans()) {
			int retentionTime = chromatogram.getScan(scan).getRetentionTime();
			return getPeakModel().getBackgroundAbundance(retentionTime);
		} else {
			return 0.0f;
		}
	}

	@Override
	public IChromatogramMSD getChromatogram() {

		return chromatogram;
	}

	@Override
	public float getPeakAbundanceAtScan(int scan) {

		int retentionTime = chromatogram.getScan(scan).getRetentionTime();
		return getPeakModel().getPeakAbundance(retentionTime);
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
	public float getPurity() {

		float purity = 0.0f;
		/*
		 * Extracted is the unknown and genuine the reference mass spectrum.
		 */
		IPeakMassSpectrum extractedMassSpectrum = getExtractedMassSpectrum();
		IScanMSD genuineMassSpectrum = getChromatogramMassSpectrum();
		if(extractedMassSpectrum != null && genuineMassSpectrum != null) {
			int numberOfIons = genuineMassSpectrum.getNumberOfIons();
			if(numberOfIons != 0) {
				purity = extractedMassSpectrum.getNumberOfIons() / (float)numberOfIons;
			}
		}
		return purity;
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
