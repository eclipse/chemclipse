/*******************************************************************************
 * Copyright (c) 2008, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 * Christoph Läubrich - adjust to new {@link INoiseCalculator} API
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IMarkedTraces;
import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.model.core.INoiseCalculator;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.results.ChromatogramSegmentation;
import org.eclipse.chemclipse.model.results.NoiseSegmentMeasurementResult;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.updates.IChromatogramUpdateListener;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.ImmutableZeroIon;
import org.eclipse.chemclipse.msd.model.implementation.IonTransitionSettings;
import org.eclipse.chemclipse.support.traces.ITrace;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * The abstract chromatogram is responsible to handle as much jobs concerning a
 * chromatogram independent of the specific supplier.<br/>
 * AbstractChromatogram extends ({@link IChromatogramMSD}) which implements (
 * {@link IChromatogramOverview}). ({@link IChromatogramOverview}) should enable
 * accessing some values of a chromatogram or a short overview. Some values like
 * amount of scans, min/max signal, min/max retention time and total ion
 * chromatogram signals, without accessing all scans. This should be more faster
 * than parsing all scans if they are not needed. On the other hand,
 * AbstractChromatogram implements ({@link IChromatogramMSD}) which itself
 * extends ({@link IChromatogramOverview}). Why? When working with an
 * IChromatogram instance all the values like min/max signal, min/max retention
 * time should be accessible with out implementing them twice.<br/>
 * But now IChromatogramOverview can be used. It is less confusing to use only
 * those method which are needed for an overview than to select from all the
 * IChromatogram methods.<br/>
 * For instance, a value could be stored for minSignal in an instance of the
 * extended AbstractChromatogram. If no scans are added to the chromatogram,
 * minSignal as stored will be returned, otherwise minSignal will be calculated.
 * <br/>
 * <br/>
 * IUpdater is implemented which takes care that all registered listeners (
 * {@link IChromatogramUpdateListener}) will be informed if values of the
 * chromatogram has been changed.
 */
public abstract class AbstractChromatogramMSD extends AbstractChromatogram implements IChromatogramMSD {

	private static final long serialVersionUID = 6481555040060687481L;

	public static final int DEFAULT_SEGMENT_WIDTH = 10;

	private IIonTransitionSettings ionTransitionSettings = new IonTransitionSettings();
	private IScanMSD combinedMassSpectrum;

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
			resetNoiseFactor();
		}
	}

	@Override
	public int getNumberOfScanIons() {

		int amount = 0;
		for(IScan scan : getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				amount += scanMSD.getNumberOfIons();
			}
		}
		return amount;
	}

	@Override
	public void enforceLoadScanProxies(IProgressMonitor monitor) {

		monitor.beginTask("Load Scans", getScans().size());
		for(IScan scan : getScans()) {
			if(scan instanceof IScanMSD scanMSD && !isUnloaded()) {
				scanMSD.enforceLoadScanProxy();
			}
			monitor.worked(1);
		}
	}

	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionMSD chromatogramSelectionMSD) {
			chromatogramSelectionMSD.update(true);
		}
	}

	@Override
	public IScanMSD getScan(int scan, IMarkedTraces<ITrace> excludedIons) {

		IScanMSD scanMSD = getScan(scan);
		if(scanMSD == null) {
			return null;
		}
		return scanMSD.getMassSpectrum(excludedIons);
	}

	@Override
	public IScanMSD getScan(int scan) {

		int position = scan;
		if(position > 0 && position <= getScans().size()) {
			IScan storedScan = getScans().get(--position);
			if(storedScan instanceof IScanMSD scanMSD) {
				return scanMSD;
			}
		}
		return null;
	}

	@Override
	public float getMinIonAbundance() {

		IIon ion;
		float minAbundance = Float.MAX_VALUE;
		for(IScan scan : getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				ion = scanMSD.getLowestAbundance();
				if(!isZeroImmutableIon(ion)) {
					if(ion.getAbundance() < minAbundance) {
						minAbundance = ion.getAbundance();
					}
				}
			}
		}
		return minAbundance;
	}

	@Override
	public float getMaxIonAbundance() {

		IIon ion;
		float maxAbundance = Float.MIN_VALUE;
		for(IScan scan : getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				ion = scanMSD.getHighestAbundance();
				if(!isZeroImmutableIon(ion)) {
					if(ion.getAbundance() > maxAbundance) {
						maxAbundance = ion.getAbundance();
					}
				}
			}
		}
		return maxAbundance;
	}

	@Override
	public double getStartIon() {

		/*
		 * Return 0 if no scan is stored.
		 */
		if(getScans().isEmpty()) {
			return 0;
		}
		double lowestIon = Double.MAX_VALUE;
		double actualIon;
		/*
		 * Check all scans.
		 */
		for(IScan scan : getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				IIon ion = scanMSD.getLowestIon();
				if(!isZeroImmutableIon(ion)) {
					actualIon = ion.getIon();
					if(actualIon < lowestIon) {
						lowestIon = actualIon;
					}
				}
			}
		}
		return lowestIon;
	}

	@Override
	public double getStopIon() {

		/*
		 * Return 0 if no scan is stored.
		 */
		if(getScans().isEmpty()) {
			return 0;
		}
		double highestIon = Double.MIN_VALUE;
		double actualIon;
		/*
		 * Check all scans.
		 */
		for(IScan scan : getScans()) {
			if(scan instanceof IScanMSD scanMSD) {
				IIon ion = scanMSD.getHighestIon();
				if(!isZeroImmutableIon(ion)) {
					actualIon = ion.getIon();
					if(actualIon > highestIon) {
						highestIon = actualIon;
					}
				}
			}
		}
		return highestIon;
	}

	@Override
	public IIonTransitionSettings getIonTransitionSettings() {

		return ionTransitionSettings;
	}

	@Override
	public IScanMSD getCombinedMassSpectrum() {

		return combinedMassSpectrum;
	}

	@Override
	public void setCombinedMassSpectrum(IScanMSD combinedMassSpectrum) {

		this.combinedMassSpectrum = combinedMassSpectrum;
	}

	private boolean isZeroImmutableIon(IIon ion) {

		if(ImmutableZeroIon.INSTANCE.equals(ion)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String getNoiseCalculatorId() {

		NoiseSegmentMeasurementResult noiseSegmentMeasurementResult = getMeasurementResult(NoiseSegmentMeasurementResult.class);
		if(noiseSegmentMeasurementResult != null) {
			return noiseSegmentMeasurementResult.getNoiseCalculatorId();
		} else {
			return PreferenceSupplier.getSelectedNoiseCalculatorId();
		}
	}

	@Override
	protected INoiseCalculator createNoiseCalculator(String id) {

		return NoiseCalculator.getNoiseCalculator(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IChromatogramPeakMSD> getPeaks() {

		return (List<IChromatogramPeakMSD>)super.getPeaks();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IChromatogramPeakMSD> getPeaks(int startRetentionTime, int stopRetentionTime) {

		return (List<IChromatogramPeakMSD>)super.getPeaks(startRetentionTime, stopRetentionTime);
	}
}