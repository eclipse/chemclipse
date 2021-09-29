/*******************************************************************************
 * Copyright (c) 2012, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - update to reflect changes in {@link INoiseCalculator} API
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;
import org.eclipse.chemclipse.model.results.NoiseSegmentMeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public abstract class AbstractChromatogramCSD extends AbstractChromatogram<IChromatogramPeakCSD> implements IChromatogramCSD {

	private static final long serialVersionUID = -1514838958855146167L;
	//
	private INoiseCalculator noiseCalculator;

	public AbstractChromatogramCSD() {

		updateNoiseCalculator();
	}

	private void updateNoiseCalculator() {

		String noiseCalculatorId;
		NoiseSegmentMeasurementResult noiseResult = getMeasurementResult(NoiseSegmentMeasurementResult.class);
		if(noiseResult != null) {
			noiseCalculatorId = noiseResult.getNoiseCalculatorId();
		} else {
			noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
		}
		noiseCalculator = NoiseCalculator.getNoiseCalculator(noiseCalculatorId);
	}

	@Override
	public void recalculateTheNoiseFactor() {

		updateNoiseCalculator();
	}

	@Override
	public float getSignalToNoiseRatio(float abundance) {

		if(noiseCalculator != null) {
			return noiseCalculator.getSignalToNoiseRatio(this, abundance);
		}
		return 0;
	}

	@Override
	public IScanCSD getSupplierScan(int scan) {

		int position = scan;
		if(position > 0 && position <= getScans().size()) {
			IScan storedScan = getScans().get(--position);
			if(storedScan instanceof IScanCSD) {
				return (IScanCSD)storedScan;
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
			((ChromatogramSelectionCSD)chromatogramSelection).update(true);
		}
	}

	@Override
	public double getPeakIntegratedArea() {

		double integratedArea = 0.0d;
		for(IChromatogramPeakCSD peak : getPeaks()) {
			integratedArea += peak.getIntegratedArea();
		}
		return integratedArea;
	}

	@Override
	public <ResultType extends IMeasurementResult<?>> ResultType getMeasurementResult(Class<ResultType> type) {

		ResultType result = super.getMeasurementResult(type);
		if(result == null && type == ChromatogramSegmentation.class) {
			return type.cast(new ChromatogramSegmentation(this, PreferenceSupplier.getSelectedSegmentWidth()));
		}
		return result;
	}

	@Override
	public void addMeasurementResult(IMeasurementResult<?> chromatogramResult) {

		super.addMeasurementResult(chromatogramResult);
		if(chromatogramResult instanceof NoiseSegmentMeasurementResult) {
			recalculateTheNoiseFactor();
		}
	}
}
