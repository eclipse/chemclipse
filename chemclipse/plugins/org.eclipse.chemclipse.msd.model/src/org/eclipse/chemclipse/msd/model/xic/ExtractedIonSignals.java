/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignal;
import org.eclipse.chemclipse.model.signals.TotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.support.ScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;
import org.eclipse.chemclipse.msd.model.implementation.Ion;
import org.eclipse.chemclipse.msd.model.implementation.RegularMassSpectrum;

/**
 * This class stores {@link ExtractedIonSignal} objects from each scan of a
 * chromatogram.
 * 
 */
public class ExtractedIonSignals implements IExtractedIonSignals {

	private static final Logger logger = Logger.getLogger(ExtractedIonSignals.class);
	//
	private List<IExtractedIonSignal> signals;
	private int startIon = IExtractedIonSignal.ION_NOT_SET;
	private int stopIon = IExtractedIonSignal.ION_NOT_SET;
	private int startScan = 0;
	private int stopScan = 0;
	private IChromatogramMSD chromatogram = null;

	/**
	 * Creates a ExtractedIonSignals instance with the given scan length.
	 * 
	 * @param numberOfScans
	 */
	public ExtractedIonSignals(int numberOfScans) {
		if(numberOfScans <= 0) {
			numberOfScans = 0;
			startScan = 0;
			stopScan = 0;
		} else {
			startScan = 1;
			stopScan = numberOfScans;
		}
		signals = new ArrayList<IExtractedIonSignal>(numberOfScans);
	}

	/**
	 * Sets additionally the parent chromatogram to the signals instance.
	 * 
	 * @param numberOfScans
	 * @param chromatogram
	 */
	public ExtractedIonSignals(int numberOfScans, IChromatogramMSD chromatogram) {
		this(numberOfScans);
		this.chromatogram = chromatogram;
	}

	/**
	 * Creates a {@link IExtractedIonSignals} instance.<br/>
	 * The start and stop scan needs also to be specified, if e.g. only a
	 * selection from scan 40 - 60 is chosen.
	 * 
	 * @param startScan
	 * @param stopScan
	 */
	public ExtractedIonSignals(int startScan, int stopScan) {
		startScan = (startScan <= 0) ? 0 : startScan;
		stopScan = (stopScan <= 0) ? 0 : stopScan;
		int start = Math.min(startScan, stopScan);
		int stop = Math.max(startScan, stopScan);
		int numberOfScans;
		if(start == 0 || stop == 0) {
			numberOfScans = 0;
			start = 0;
			stop = 0;
		} else {
			numberOfScans = stop - start + 1;
		}
		signals = new ArrayList<IExtractedIonSignal>(numberOfScans);
		this.startScan = start;
		this.stopScan = stop;
	}

	/**
	 * Sets additionally the parent chromatogram to the signals instance.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @param chromatogram
	 */
	public ExtractedIonSignals(int startScan, int stopScan, IChromatogramMSD chromatogram) {
		this(startScan, stopScan);
		this.chromatogram = chromatogram;
	}

	@Override
	public IChromatogramMSD getChromatogram() {

		return chromatogram;
	}

	@Override
	public void add(IExtractedIonSignal extractedIonSignal) {

		signals.add(extractedIonSignal);
		/*
		 * Sets the start and stop ion values.<br/> This feature is necessary if
		 * the start and stop ion values are calculated by the mass
		 * spectrum.<br/> See mass spectrum "getExtractedIonSignal()" in
		 * opposite to "getExtractedIonSignal(float startIon, float stopIon)".
		 */
		setStartIon(extractedIonSignal.getStartIon());
		setStopIon(extractedIonSignal.getStopIon());
	}

	@Override
	public void add(int ion, float abundance, int retentionTime, boolean removePreviousAbundance) {

		IExtractedIonSignal extractedIonSignal;
		try {
			int scan = findClosestScan(retentionTime);
			if(scan > 0) {
				extractedIonSignal = getExtractedIonSignal(scan);
				extractedIonSignal.setAbundance(ion, abundance, removePreviousAbundance);
			}
		} catch(ChromatogramIsNullException e) {
			logger.warn(e);
		} catch(NoExtractedIonSignalStoredException e) {
			logger.warn(e);
		}
	}

	@Override
	public IExtractedIonSignal getExtractedIonSignal(int scan) throws NoExtractedIonSignalStoredException {

		/*
		 * Scan is 1 based.
		 */
		if(scan <= 0) {
			throw new NoExtractedIonSignalStoredException("The requested scan: " + scan + " is not available.");
		}
		/*
		 * The requested scan is outside of this range.
		 */
		if(scan < startScan || scan > stopScan) {
			throw new NoExtractedIonSignalStoredException("The requested scan: " + scan + " is not available.");
		}
		/*
		 * Correct the offset.
		 */
		int correction = startScan - 1;
		scan -= correction;
		int index = scan - 1;
		//
		if(index < 0 || index >= signals.size()) {
			throw new NoExtractedIonSignalStoredException("The requested scan: " + scan + " is not available.");
		}
		//
		return signals.get(index);
	}

	// TODO JUnit
	@Override
	public List<IExtractedIonSignal> getExtractedIonSignals() {

		return signals;
	}

	@Override
	public IScanMSD getScan(int scan) {

		IMarkedIons excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		return getScan(scan, excludedIons);
	}

	@Override
	public IScanMSD getScan(int scan, IMarkedIons excludedIons) {

		IExtractedIonSignal extractedIonSignal;
		/*
		 * Retrieve the extracted ion signal.
		 */
		try {
			extractedIonSignal = getExtractedIonSignal(scan);
		} catch(NoExtractedIonSignalStoredException e) {
			logger.warn(e);
			return null;
		}
		/*
		 * Create a new mass spectrum and add the ions.
		 */
		IRegularMassSpectrum massSpectrum = new RegularMassSpectrum();
		int startIon = extractedIonSignal.getStartIon();
		int stopIon = extractedIonSignal.getStopIon();
		/*
		 * Set additional supplier mass spectrum info.
		 */
		massSpectrum.setParentChromatogram(getChromatogram());
		/*
		 * Add the ions.
		 */
		Set<Integer> excludedIonsNominal = excludedIons.getIonsNominal();
		for(int ion = startIon; ion <= stopIon; ion++) {
			/*
			 * Do nothing if the ion is listed in the excluded mass
			 * fragments list.
			 */
			if(excludedIonsNominal.contains(ion)) {
				continue;
			}
			float abundance = extractedIonSignal.getAbundance(ion);
			if(abundance > 0.0f) {
				try {
					IIon defaultIon = new Ion(ion, abundance);
					massSpectrum.addIon(defaultIon);
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		return massSpectrum;
	}

	@Override
	public int getStartIon() {

		return startIon;
	}

	@Override
	public int getStopIon() {

		return stopIon;
	}

	@Override
	public int size() {

		return signals.size();
	}

	@Override
	public int getStartScan() {

		return startScan;
	}

	@Override
	public int getStopScan() {

		return stopScan;
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(int ion) {

		IScanRange scanRange = new ScanRange(getStartScan(), getStopScan());
		return getTotalIonSignals(ion, scanRange);
	}

	@Override
	public ITotalScanSignals getTotalIonSignals() {

		IScanRange scanRange = new ScanRange(getStartScan(), getStopScan());
		return getTotalIonSignals((int)IIon.TIC_ION, scanRange);
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(IScanRange scanRange) {

		return getTotalIonSignals((int)IIon.TIC_ION, scanRange);
	}

	@Override
	public ITotalScanSignals getTotalIonSignals(int ion, IScanRange scanRange) {

		int startScan;
		int stopScan;
		if(scanRange != null) {
			startScan = scanRange.getStartScan();
			stopScan = scanRange.getStopScan();
		} else {
			startScan = 0;
			stopScan = 0;
		}
		IExtractedIonSignal extractedIonSignal;
		ITotalScanSignal totalIonSignal;
		ITotalScanSignals totalIonSignals = new TotalScanSignals(startScan, stopScan, getChromatogram());
		int retentionTime;
		float retentionIndex;
		float signal;
		for(int scan = startScan; scan <= stopScan; scan++) {
			try {
				extractedIonSignal = getExtractedIonSignal(scan);
				retentionTime = extractedIonSignal.getRetentionTime();
				retentionIndex = extractedIonSignal.getRetentionIndex();
				// TIC
				if(ion == (int)IIon.TIC_ION) {
					signal = extractedIonSignal.getTotalSignal();
				} else { // XIC
					signal = extractedIonSignal.getAbundance(ion);
				}
				/*
				 * Add the total ion signal to the total ion signal list.
				 */
				totalIonSignal = new TotalScanSignal(retentionTime, retentionIndex, signal);
				totalIonSignals.add(totalIonSignal);
			} catch(NoExtractedIonSignalStoredException e) {
				logger.warn(e);
			}
		}
		return totalIonSignals;
	}

	@Override
	public IExtractedIonSignals makeDeepCopyWithoutSignals() {

		IExtractedIonSignals extractedIonSignals = new ExtractedIonSignals(startScan, stopScan, chromatogram);
		IExtractedIonSignal extractedIonSignal;
		/*
		 * Iterates through all extracted ion signals and creates a new
		 * extracted ion signal, but without ions.<br/> The freshly
		 * created extracted ion signal will be stored in a new
		 * IExtractedIonSignals instance.
		 */
		for(IExtractedIonSignal signal : signals) {
			extractedIonSignal = new ExtractedIonSignal(signal.getStartIon(), signal.getStopIon());
			extractedIonSignal.setRetentionTime(signal.getRetentionTime());
			extractedIonSignal.setRetentionIndex(signal.getRetentionIndex());
			extractedIonSignals.add(extractedIonSignal);
		}
		return extractedIonSignals;
	}

	/**
	 * Sets the ion value as the new start ion if its value is lower
	 * than the actual start ion.
	 * 
	 * @param ion
	 */
	private void setStartIon(int ion) {

		if(ion != IExtractedIonSignal.ION_NOT_SET) {
			if(startIon == IExtractedIonSignal.ION_NOT_SET || ion < startIon) {
				startIon = ion;
			}
		}
	}

	/**
	 * Sets the ion value as the new stop ion if its value is higher
	 * than the actual stop ion.
	 * 
	 * @param ion
	 */
	private void setStopIon(int ion) {

		if(ion != IExtractedIonSignal.ION_NOT_SET && ion > stopIon) {
			stopIon = ion;
		}
	}

	/**
	 * Returns the closest scan to the given retention time.
	 * 
	 * @param retentionTime
	 * @return int
	 */
	private int findClosestScan(int retentionTime) throws ChromatogramIsNullException {

		if(chromatogram == null) {
			throw new ChromatogramIsNullException();
		}
		return chromatogram.getScanNumber(retentionTime);
	}
}
