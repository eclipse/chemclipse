/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

/**
 * An abstract regular mass spectrum represents a single mass spectrum
 * consisting of a retention time, a scan number and optionally a retention
 * index.<br/>
 * Opposite to this a combined mass spectrum (@link
 * AbstractCombinedMassSpectrum) represents a mass spectrum range.
 * 
 * @author eselmeister
 */
public abstract class AbstractRegularMassSpectrum extends AbstractScanMSD implements IRegularMassSpectrum {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 491290989250840877L;
	private short massSpectrometer;
	private short massSpectrumType;
	private double precursorIon;

	/**
	 * By default:
	 * SupplierMassSpectrometer.MS1
	 * SupplierMassSpectrumType.CENTROID
	 */
	public AbstractRegularMassSpectrum() {
		super();
		massSpectrometer = 1; // MS1
		massSpectrumType = 0; // 0 = Centroid, 1 = Profile
	}

	public AbstractRegularMassSpectrum(short massSpectrometer, short massSpectrumType) {
		this();
		setMassSpectrometer(massSpectrometer);
		setMassSpectrumType(massSpectrumType);
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal() {

		IExtractedIonSignal extractedIonSignal = super.getExtractedIonSignal();
		extractedIonSignal.setRetentionTime(getRetentionTime());
		extractedIonSignal.setRetentionIndex(getRetentionIndex());
		return extractedIonSignal;
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal(double startIon, double stopIon) {

		IExtractedIonSignal extractedIonSignal = super.getExtractedIonSignal(startIon, stopIon);
		extractedIonSignal.setRetentionTime(getRetentionTime());
		extractedIonSignal.setRetentionIndex(getRetentionIndex());
		return extractedIonSignal;
	}

	@Override
	public short getMassSpectrometer() {

		return massSpectrometer;
	}

	@Override
	public void setMassSpectrometer(short massSpectrometer) {

		this.massSpectrometer = massSpectrometer;
	}

	@Override
	public short getMassSpectrumType() {

		return massSpectrumType;
	}

	@Override
	public String getMassSpectrumTypeDescription() {

		String massSpectrumTypeDescription;
		switch(massSpectrumType) {
			case 0:
				massSpectrumTypeDescription = "Centroid";
				break;
			case 1:
				massSpectrumTypeDescription = "Profile";
				break;
			default:
				massSpectrumTypeDescription = "Unknown";
				break;
		}
		return massSpectrumTypeDescription;
	}

	@Override
	public void setMassSpectrumType(short massSpectrumType) {

		this.massSpectrumType = massSpectrumType;
	}

	@Override
	public double getPrecursorIon() {

		return precursorIon;
	}

	@Override
	public void setPrecursorIon(double precursorIon) {

		this.precursorIon = precursorIon;
	}
}
