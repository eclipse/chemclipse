/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.core.AbstractScan;

public abstract class AbstractScanVSD extends AbstractScan implements IScanVSD {

	private static final long serialVersionUID = 4376839034057299437L;
	/*
	 * Needed by FT-IR, NIR
	 */
	private double rotationAngle = 0.0d;
	private double[] rawSignals = new double[0];
	private double[] backgroundSignals = new double[0];

	@Override
	public void adjustTotalSignal(float totalSignal) {

		if(Float.isNaN(totalSignal) || Float.isInfinite(totalSignal)) {
			return;
		}
		/*
		 * Avoid a division by zero exception :-).
		 */
		if(getTotalSignal() == 0.0f) {
			return;
		}
		//
		double base = 100.0d;
		double correctionFactor = ((base / getTotalSignal()) * totalSignal) / base;
		/*
		 * Adjust signal
		 */
		for(ISignalVSD scanSignal : getProcessedSignals()) {
			double intensity = scanSignal.getIntensity();
			intensity *= correctionFactor;
			scanSignal.setIntensity(intensity);
		}
	}

	@Override
	public double getRotationAngle() {

		return rotationAngle;
	}

	@Override
	public void setRotationAngle(double rotationAngle) {

		this.rotationAngle = rotationAngle;
	}

	@Override
	public double[] getRawSignals() {

		return rawSignals;
	}

	@Override
	public void setRawSignals(double[] rawSignals) {

		this.rawSignals = rawSignals;
	}

	@Override
	public double[] getBackgroundSignals() {

		return backgroundSignals;
	}

	@Override
	public void setBackgroundSignals(double[] backgroundSignals) {

		this.backgroundSignals = backgroundSignals;
	}

	@Override
	public void removeWavenumbers(Set<Integer> wavenumbers) {

		List<ISignalVSD> scanSignalsRemove = new ArrayList<>();
		for(ISignalVSD scanSignal : getProcessedSignals()) {
			int wavenumber = (int)Math.round(scanSignal.getWavenumber());
			if(wavenumbers.contains(wavenumber)) {
				scanSignalsRemove.add(scanSignal);
			}
		}
		//
		getProcessedSignals().removeAll(scanSignalsRemove);
	}

	@Override
	public void keepWavenumbers(Set<Integer> wavenumbers) {

		List<ISignalVSD> scanSignalsKeep = new ArrayList<>();
		for(ISignalVSD scanSignal : getProcessedSignals()) {
			int wavenumber = (int)Math.round(scanSignal.getWavenumber());
			if(wavenumbers.contains(wavenumber)) {
				scanSignalsKeep.add(scanSignal);
			}
		}
		//
		getProcessedSignals().clear();
		getProcessedSignals().addAll(scanSignalsKeep);
	}
}
