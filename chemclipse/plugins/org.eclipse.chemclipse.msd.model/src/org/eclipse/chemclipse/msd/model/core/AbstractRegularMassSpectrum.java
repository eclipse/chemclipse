/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

/**
 * An {@code AbstractRegularMassSpectrum} represents a single mass spectrum
 * consisting of a retention time, a scan number and optionally a retention
 * index.<br/>
 * Opposite to this a combined mass spectrum {@link
 * AbstractCombinedMassSpectrum} represents a mass spectrum range.
 */
public abstract class AbstractRegularMassSpectrum extends AbstractScanMSD implements IRegularMassSpectrum {

	private static final long serialVersionUID = 6051414280468244074L;
	//
	private short massSpectrometer; // TODO: enum?
	private MassSpectrumType massSpectrumType;
	private double precursorIon;
	private double precursorBasePeak;

	/**
	 * By default:
	 * SupplierMassSpectrometer.MS1
	 * SupplierMassSpectrumType.CENTROID
	 */
	protected AbstractRegularMassSpectrum() {

		super();
		massSpectrometer = 1; // MS1
		massSpectrumType = MassSpectrumType.CENTROID;
	}

	protected AbstractRegularMassSpectrum(Collection<? extends IIon> ions) {

		super(ions);
	}

	/**
	 * Creates a new instance of {@code AbstractRegularMassSpectrum} by creating a
	 * shallow copy of provided {@code templateScan}.
	 * 
	 * @param templateScan
	 *            {@link IScan scan} that is used as a template
	 */
	protected AbstractRegularMassSpectrum(IScanMSD templateScan) {

		super(templateScan);
		if(templateScan instanceof IRegularMassSpectrum regularMassSpectrum) {
			this.massSpectrometer = regularMassSpectrum.getMassSpectrometer();
			this.massSpectrumType = regularMassSpectrum.getMassSpectrumType();
			this.precursorIon = regularMassSpectrum.getPrecursorIon();
		}
	}

	protected AbstractRegularMassSpectrum(short massSpectrometer, MassSpectrumType massSpectrumType) {

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
	public MassSpectrumType getMassSpectrumType() {

		return massSpectrumType;
	}

	@Override
	public void setMassSpectrumType(MassSpectrumType massSpectrumType) {

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

	@Override
	public double getPrecursorBasePeak() {

		return precursorBasePeak;
	}

	@Override
	public void setPrecursorBasePeak(double precursorBasePeak) {

		this.precursorBasePeak = precursorBasePeak;
	}
}