/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.chemclipse.model.core.AbstractScan;
import org.eclipse.chemclipse.wsd.model.comparator.WavelengthCombinedComparator;
import org.eclipse.chemclipse.wsd.model.comparator.WavelengthComparatorMode;
import org.eclipse.chemclipse.wsd.model.core.implementation.ScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedSingleWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.ExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedSingleWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;

public abstract class AbstractScanWSD extends AbstractScan implements IScanWSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -8298107894544692691L;
	private List<IScanSignalWSD> scanSignals = new ArrayList<>();

	public AbstractScanWSD() {

	}

	public AbstractScanWSD(IScanWSD scanWSD, float actualPercentageIntensity) throws IllegalArgumentException {

		if(scanWSD == null) {
			throw new IllegalArgumentException("The scanWSD must not be null");
		}
		if(actualPercentageIntensity <= 0.0f) {
			throw new IllegalArgumentException("The percentageIntensity must not be > 0.");
		}
		/*
		 * 
		 */
		for(IScanSignalWSD scanSignal : scanWSD.getScanSignals()) {
			float abundance = (scanSignal.getAbundance() / actualPercentageIntensity) * 100.0f;
			scanSignals.add(new ScanSignalWSD(scanSignal.getWavelength(), abundance));
		}
	}

	@Override
	public IScanSignalWSD getScanSignal(int scan) {

		return scanSignals.get(scan);
	}

	@Override
	public Optional<IScanSignalWSD> getScanSignal(float wavelength) {

		for(IScanSignalWSD scanSignal : scanSignals) {
			if(scanSignal.getWavelength() == wavelength) {
				return Optional.of(scanSignal);
			}
		}
		return Optional.empty();
	}

	@Override
	public void deleteScanSignals() {

		scanSignals.clear();
	}

	@Override
	public void addScanSignal(IScanSignalWSD scanSignalWSD) {

		scanSignals.add(scanSignalWSD);
	}

	@Override
	public void removeScanSignal(IScanSignalWSD scanSignalWSD) {

		scanSignals.remove(scanSignalWSD);
	}

	@Override
	public void removeScanSignal(int scan) {

		scanSignals.remove(scan);
	}

	@Override
	public void removeScanSignals(Set<Integer> wavelengths) {

		List<IScanSignalWSD> scanSignalsToRemove = new ArrayList<>();
		for(int wavelength : wavelengths) {
			for(IScanSignalWSD scanSignal : scanSignals) {
				if(scanSignal.getWavelength() == wavelength) {
					scanSignalsToRemove.add(scanSignal);
				}
			}
		}
		//
		scanSignals.removeAll(scanSignalsToRemove);
	}

	@Override
	public int getNumberOfScanSignals() {

		return scanSignals.size();
	}

	@Override
	public List<IScanSignalWSD> getScanSignals() {

		return Collections.unmodifiableList(scanSignals);
	}

	@Override
	public float getTotalSignal() {

		float totalSignal = 0.0f;
		for(IScanSignalWSD scan : scanSignals) {
			totalSignal += scan.getAbundance();
		}
		return totalSignal;
	}

	@Override
	public float getTotalSignal(IMarkedWavelengths markedWavelengths) {

		float totalSignal = 0;
		/*
		 * If the excluded ions are null, return the total signal.
		 */
		if(markedWavelengths == null || markedWavelengths.isEmpty()) {
			totalSignal = getTotalSignal();
		} else {
			Iterator<IScanSignalWSD> iterator = scanSignals.iterator();
			while(iterator.hasNext()) {
				IScanSignalWSD scan = iterator.next();
				if(useWavelength(scan, markedWavelengths)) {
					totalSignal += scan.getAbundance();
				}
			}
		}
		return totalSignal;
	}

	private static boolean useWavelength(IScanSignalWSD scan, IMarkedWavelengths filterWavelengths) {

		Set<Float> wavelengths = filterWavelengths.getWavelengths();
		switch(filterWavelengths.getMarkedTraceModus()) {
			case EXCLUDE:
				return wavelengths.contains(scan.getWavelength());
			case INCLUDE:
				return !wavelengths.contains(scan.getWavelength());
			default:
				return true;
		}
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		/*
		 * If the total signal is 0 there would be no wavelength stored in
		 * the list.<br/> That's not what we want.
		 */
		if(totalSignal < 0.0f || Float.isNaN(totalSignal) || Float.isInfinite(totalSignal)) {
			return;
		}
		/*
		 * Do not cause a division by zero exception :-).
		 */
		if(getTotalSignal() == 0.0f) {
			return;
		}
		float base = 100.0f;
		float correctionFactor = ((base / getTotalSignal()) * totalSignal) / base;
		float abundance;
		for(IScanSignalWSD scanSignal : scanSignals) {
			abundance = scanSignal.getAbundance();
			abundance *= correctionFactor;
			scanSignal.setAbundance(abundance);
		}
	}

	@Override
	@Deprecated
	public IExtractedWavelengthSignal getExtractedWavelengthSignal() {

		if(hasScanSignals()) {
			IWavelengthBounds bounds = getWavelengthBounds();
			float startWavelength = bounds.getLowestWavelength().getWavelength();
			float stopWavelength = bounds.getHighestWavelength().getWavelength();
			return getExtractedWavelengthSignal(startWavelength, stopWavelength);
		} else {
			return new ExtractedWavelengthSignal(0, 0);
		}
	}

	@Override
	@Deprecated
	public IExtractedWavelengthSignal getExtractedWavelengthSignal(float startWavelength, float stopWavelength) {

		ExtractedWavelengthSignal extractedWavelengthSignal;
		if(hasScanSignals()) {
			extractedWavelengthSignal = new ExtractedWavelengthSignal(startWavelength, stopWavelength);
			extractedWavelengthSignal.setRetentionTime(getRetentionTime());
			for(IScanSignalWSD scanSignal : getScanSignals()) {
				extractedWavelengthSignal.setAbundance(scanSignal);
			}
			return extractedWavelengthSignal;
		} else {
			extractedWavelengthSignal = new ExtractedWavelengthSignal(0, 0);
			extractedWavelengthSignal.setRetentionTime(getRetentionTime());
			return extractedWavelengthSignal;
		}
	}

	@Override
	public Optional<IExtractedSingleWavelengthSignal> getExtractedSingleWavelengthSignal(float wavelength) {

		Optional<IScanSignalWSD> signal = getScanSignal(wavelength);
		if(signal.isPresent()) {
			IExtractedSingleWavelengthSignal extractedSingleWavelengthSignal = new ExtractedSingleWavelengthSignal(signal.get(), getRetentionTime(), getRetentionIndex());
			return Optional.of(extractedSingleWavelengthSignal);
		}
		return Optional.empty();
	}

	@Override
	public boolean hasScanSignals() {

		if(scanSignals.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public IWavelengthBounds getWavelengthBounds() {

		IScanSignalWSD lowest = null;
		IScanSignalWSD highest = null;
		if(hasScanSignals()) {
			Comparator<IScanSignalWSD> comparator = new WavelengthCombinedComparator(WavelengthComparatorMode.WAVELENGTH_FIRST);
			lowest = Collections.min(scanSignals, comparator);
			highest = Collections.max(scanSignals, comparator);
			return new WavelengthBounds(lowest, highest);
		} else {
			return null;
		}
	}
}
