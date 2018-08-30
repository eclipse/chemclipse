/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.INoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise.NoiseCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.updates.IChromatogramUpdateListener;
import org.eclipse.chemclipse.msd.model.core.identifier.chromatogram.IChromatogramTargetMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.DefaultNoiseCalculator;
import org.eclipse.chemclipse.msd.model.implementation.ImmutableZeroIon;
import org.eclipse.chemclipse.msd.model.implementation.IonTransitionSettings;
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
 * 
 * @author eselmeister
 */
public abstract class AbstractChromatogramMSD extends AbstractChromatogram<IChromatogramPeakMSD> implements IChromatogramMSD {

	private static final long serialVersionUID = 6481555040060687480L;
	//
	public static int DEFAULT_SEGMENT_WIDTH = 10;
	private static final Logger logger = Logger.getLogger(AbstractChromatogramMSD.class);
	private List<IChromatogramTargetMSD> targets;
	private IIonTransitionSettings ionTransitionSettings;
	private INoiseCalculator noiseCalculator;
	private ImmutableZeroIon immutableZeroIon;

	public AbstractChromatogramMSD() {
		targets = new ArrayList<IChromatogramTargetMSD>();
		ionTransitionSettings = new IonTransitionSettings();
		int segmentWidth = DEFAULT_SEGMENT_WIDTH;
		try {
			immutableZeroIon = new ImmutableZeroIon();
		} catch(AbundanceLimitExceededException | IonLimitExceededException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		if(PreferenceSupplier.isAvailable()) {
			segmentWidth = PreferenceSupplier.getSelectedSegmentWidth();
			String noiseCalculatorId = PreferenceSupplier.getSelectedNoiseCalculatorId();
			noiseCalculator = NoiseCalculator.getNoiseCalculator(noiseCalculatorId);
			if(noiseCalculator == null) {
				noiseCalculator = new DefaultNoiseCalculator();
			}
		} else {
			noiseCalculator = new DefaultNoiseCalculator();
		}
		noiseCalculator.setChromatogram(this, segmentWidth);
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
	public int getNumberOfScanIons() {

		int amount = 0;
		for(IScan scan : getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				amount += ms.getNumberOfIons();
			}
		}
		return amount;
	}

	@Override
	public void enforceLoadScanProxies(IProgressMonitor monitor) {

		for(IScan scan : getScans()) {
			if(scan instanceof IVendorMassSpectrum && isUnloaded() == false) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				ms.enforceLoadScanProxy();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void fireUpdate(IChromatogramSelection chromatogramSelection) {

		/*
		 * Fire an update to inform all listeners.
		 */
		if(chromatogramSelection instanceof ChromatogramSelectionMSD) {
			((ChromatogramSelectionMSD)chromatogramSelection).update(true);
		}
	}

	// -----------------------------------------------------------do, undo, redo
	// -----------------------------------------------------------add / remove
	// scans
	@Override
	public IScanMSD getScan(int scan, IMarkedIons excludedIons) {

		IVendorMassSpectrum supplierMassSpectrum = getSupplierScan(scan);
		if(supplierMassSpectrum == null) {
			return null;
		}
		IScanMSD massSpectrum = supplierMassSpectrum.getMassSpectrum(excludedIons);
		return massSpectrum;
	}

	@Override
	public IVendorMassSpectrum getSupplierScan(int scan) {

		int position = scan;
		if(position > 0 && position <= getScans().size()) {
			IScan storedScan = getScans().get(--position);
			if(storedScan instanceof IVendorMassSpectrum) {
				return (IVendorMassSpectrum)storedScan;
			}
		}
		return null;
	}

	// -----------------------------------------------------------add / remove
	// scans
	@Override
	public float getMinIonAbundance() {

		IIon ion;
		float minAbundance = Float.MAX_VALUE;
		for(IScan scan : getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				ion = ms.getLowestAbundance();
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
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				ion = ms.getHighestAbundance();
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
		if(getScans().size() == 0) {
			return 0;
		}
		double lowestIon = Double.MAX_VALUE;
		double actualIon;
		/*
		 * Check all scans.
		 */
		for(IScan scan : getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				IIon ion = ms.getLowestIon();
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
		if(getScans().size() == 0) {
			return 0;
		}
		double highestIon = Double.MIN_VALUE;
		double actualIon;
		/*
		 * Check all scans.
		 */
		for(IScan scan : getScans()) {
			if(scan instanceof IVendorMassSpectrum) {
				IVendorMassSpectrum ms = (IVendorMassSpectrum)scan;
				IIon ion = ms.getHighestIon();
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
	// -----------------------------------------------IChromatogram

	// -----------------------------------------------IIntegration
	@Override
	public double getPeakIntegratedArea() {

		double integratedArea = 0.0d;
		for(IChromatogramPeakMSD peak : getPeaks()) {
			integratedArea += peak.getIntegratedArea();
		}
		return integratedArea;
	}

	// -----------------------------------------------IChromatogramPeaks
	@Override
	public IIonTransitionSettings getIonTransitionSettings() {

		return ionTransitionSettings;
	}

	// -----------------------------------------------IChromatogramTargetsMSD
	@Override
	public void addTarget(IChromatogramTargetMSD chromatogramTarget) {

		if(chromatogramTarget != null) {
			targets.add(chromatogramTarget);
		}
	}

	@Override
	public void removeTarget(IChromatogramTargetMSD chromatogramTarget) {

		targets.remove(chromatogramTarget);
	}

	@Override
	public void removeTargets(List<IChromatogramTargetMSD> targetsToRemove) {

		targets.removeAll(targetsToRemove);
	}

	@Override
	public void removeAllTargets() {

		targets.clear();
	}

	@Override
	public List<IChromatogramTargetMSD> getTargets() {

		return Collections.unmodifiableList(targets);
	}

	// -----------------------------------------------IChromatogramTargetsMSD
	private boolean isZeroImmutableIon(IIon ion) {

		if(immutableZeroIon.equals(ion)) {
			return true;
		} else {
			return false;
		}
	}
}
