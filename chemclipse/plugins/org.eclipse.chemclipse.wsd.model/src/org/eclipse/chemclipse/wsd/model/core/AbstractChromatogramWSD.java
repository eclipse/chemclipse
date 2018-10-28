/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.baseline.BaselineModel;
import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.identifier.chromatogram.IChromatogramTargetWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;

public abstract class AbstractChromatogramWSD extends AbstractChromatogram<IChromatogramPeakWSD> implements IChromatogramWSD {

	private static final long serialVersionUID = -7048942996283330150L;
	//
	private Set<IChromatogramTargetWSD> targets;
	private INoiseCalculator noiseCalculator;
	private Map<Double, IBaselineModel> baselineModels;

	public AbstractChromatogramWSD() {

		targets = new HashSet<IChromatogramTargetWSD>();
		baselineModels = new HashMap<>();
		String noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
		noiseCalculator = NoiseCalculator.getNoiseCalculator(noiseCalculatorId);
		if(noiseCalculator != null) {
			int segmentWidth = PreferenceSupplier.getSelectedSegmentWidth();
			noiseCalculator.setChromatogram(this, segmentWidth);
		}
	}

	@Override
	public Set<Double> getWavelengths() {

		Set<Double> wavelengths = new HashSet<>();
		for(int i = 1; i < getNumberOfScans(); i++) {
			getSupplierScan(i).getScanSignals().forEach(signal -> wavelengths.add(signal.getWavelength()));
		}
		return wavelengths;
	}

	@Override
	public void recalculateTheNoiseFactor() {

		if(noiseCalculator != null) {
			noiseCalculator.recalculate();
		}
	}

	@Override
	public float getSignalToNoiseRatio(float abundance) {

		if(noiseCalculator != null) {
			return noiseCalculator.getSignalToNoiseRatio(abundance);
		}
		return 0;
	}

	@Override
	public IScanWSD getSupplierScan(int scan) {

		int position = scan;
		if(position > 0 && position <= getScans().size()) {
			IScan storedScan = getScans().get(--position);
			if(storedScan instanceof IScanWSD) {
				return (IScanWSD)storedScan;
			}
		}
		return null;
	}

	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionWSD) {
			((ChromatogramSelectionWSD)chromatogramSelection).update(true);
		}
	}

	@Override
	public void addTarget(IChromatogramTargetWSD chromatogramTarget) {

		if(chromatogramTarget != null) {
			targets.add(chromatogramTarget);
		}
	}

	@Override
	public void removeTarget(IChromatogramTargetWSD chromatogramTarget) {

		targets.remove(chromatogramTarget);
	}

	@Override
	public void removeTargets(List<IChromatogramTargetWSD> targetsToRemove) {

		targets.removeAll(targetsToRemove);
	}

	@Override
	public void removeAllTargets() {

		targets.clear();
	}

	@Override
	public List<IChromatogramTargetWSD> getTargets() {

		List<IChromatogramTargetWSD> targetList = new ArrayList<IChromatogramTargetWSD>(targets);
		return targetList;
	}

	@Override
	public double getPeakIntegratedArea() {

		double integratedArea = 0.0d;
		for(IChromatogramPeakWSD peak : getPeaks()) {
			integratedArea += peak.getIntegratedArea();
		}
		return integratedArea;
	}

	@Override
	public IBaselineModel getBaselineModel(double wavelength) {

		baselineModels.putIfAbsent(wavelength, new BaselineModel(this, Float.NaN));
		return baselineModels.get(wavelength);
	}

	@Override
	public void removeBaselineModel(double wavelength) {

		baselineModels.remove(wavelength);
	}

	@Override
	public Map<Double, IBaselineModel> getBaselineModels() {

		return Collections.unmodifiableMap(baselineModels);
	}

	@Override
	public boolean containsBaseline(double wavelength) {

		return baselineModels.containsValue(wavelength);
	}
}
