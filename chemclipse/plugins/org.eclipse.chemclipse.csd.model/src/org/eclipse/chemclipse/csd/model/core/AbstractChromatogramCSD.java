/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.identifier.chromatogram.IChromatogramTargetCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public abstract class AbstractChromatogramCSD extends AbstractChromatogram implements IChromatogramCSD {

	private List<IChromatogramPeakCSD> peaks;
	private Set<IChromatogramTargetCSD> targets;
	private INoiseCalculator noiseCalculator;

	public AbstractChromatogramCSD() {
		peaks = new ArrayList<IChromatogramPeakCSD>();
		targets = new HashSet<IChromatogramTargetCSD>();
		String noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
		noiseCalculator = NoiseCalculator.getNoiseCalculator(noiseCalculatorId);
		if(noiseCalculator != null) {
			int segmentWidth = PreferenceSupplier.getSelectedSegmentWidth();
			noiseCalculator.setChromatogram(this, segmentWidth);
		}
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

	// -----------------------------------------------------
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

	// -----------------------------------------------------
	@Override
	public List<IChromatogramPeakCSD> getPeaks() {

		return peaks;
	}

	// TODO JUnit
	@Override
	public List<IChromatogramPeakCSD> getPeaks(IChromatogramSelectionCSD chromatogramSelection) {

		List<IChromatogramPeakCSD> peakList = new ArrayList<IChromatogramPeakCSD>();
		if(chromatogramSelection != null) {
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int peakRetentionTime;
			for(IChromatogramPeakCSD peak : peaks) {
				/*
				 * Include all peaks which retention time at peak maximum is in
				 * between start and stop retention time of the selection.
				 */
				peakRetentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
				if(peakRetentionTime >= startRetentionTime && peakRetentionTime <= stopRetentionTime) {
					peakList.add(peak);
				}
			}
		}
		return peakList;
	}

	// TODO JUnit
	@Override
	public void addPeak(IChromatogramPeakCSD peak) {

		/*
		 * Add the peak only if it not contains a type/instance of the peak
		 * (equals).
		 */
		if(!peaks.contains(peak) && peak.getPeakModel().getWidthByInflectionPoints() > 0) {
			peaks.add(peak);
		}
	}

	@Override
	public void removePeak(IChromatogramPeakCSD peak) {

		peaks.remove(peak);
	}

	@Override
	public void removePeaks(List<IChromatogramPeakCSD> peaksToDelete) {

		peaks.removeAll(peaksToDelete);
	}

	@Override
	public void removeAllPeaks() {

		peaks.clear();
	}

	// TODO JUnit Test
	@Override
	public int getNumberOfPeaks() {

		return peaks.size();
	}

	@Override
	public IChromatogramPeakCSD getPeak(int retentionTime) {

		/*
		 * Try to get a peak in the surrounding of the retention time.
		 */
		IChromatogramPeakCSD selectedPeak = null;
		exitloop:
		for(IChromatogramPeakCSD peak : peaks) {
			int peakStartRetentionTime = peak.getPeakModel().getStartRetentionTime();
			int peakStopRetentionTime = peak.getPeakModel().getStopRetentionTime();
			if(retentionTime >= peakStartRetentionTime && retentionTime <= peakStopRetentionTime) {
				selectedPeak = peak;
				break exitloop;
			}
		}
		//
		return selectedPeak;
	}

	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionCSD) {
			((ChromatogramSelectionCSD)chromatogramSelection).update(true);
		}
	}

	// -----------------------------------------------------------do, undo, redo
	// -----------------------------------------------IIntegration
	@Override
	public double getPeakIntegratedArea() {

		double integratedArea = 0.0d;
		for(IChromatogramPeakCSD peak : peaks) {
			integratedArea += peak.getIntegratedArea();
		}
		return integratedArea;
	}

	// -----------------------------------------------IChromatogramTargetsFID
	@Override
	public void addTarget(IChromatogramTargetCSD chromatogramTarget) {

		if(chromatogramTarget != null) {
			targets.add(chromatogramTarget);
		}
	}

	@Override
	public void removeTarget(IChromatogramTargetCSD chromatogramTarget) {

		targets.remove(chromatogramTarget);
	}

	@Override
	public void removeTargets(List<IChromatogramTargetCSD> targetsToRemove) {

		targets.removeAll(targetsToRemove);
	}

	@Override
	public void removeAllTargets() {

		targets.clear();
	}

	@Override
	public List<IChromatogramTargetCSD> getTargets() {

		List<IChromatogramTargetCSD> targetList = new ArrayList<IChromatogramTargetCSD>(targets);
		return targetList;
	}
	// -----------------------------------------------IChromatogramTargetsFID
}
