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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import java.util.List;

import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.exceptions.NoExtractedIonSignalStoredException;

/**
 * @author eselmeister
 */
public interface IExtractedIonSignals {

	/**
	 * Returns the chromatogram where these signals derive from.
	 * 
	 * @return {@link IChromatogramMSD}
	 */
	IChromatogramMSD getChromatogram();

	/**
	 * Adds a new ExtractedIonSignal at the end of the stored signals.
	 * 
	 * @param extractedIonSignal
	 */
	void add(IExtractedIonSignal extractedIonSignal);

	/**
	 * Adds the given ion with its abundance to the scan, next to the retention
	 * time.<br/>
	 * Use removePreviousAbundance to clear the abundance value except of the
	 * given one.
	 * 
	 * @param ion
	 * @param abundance
	 * @param retentionTime
	 * @param removePreviousAbundance
	 */
	void add(int ion, float abundance, int retentionTime, boolean removePreviousAbundance);

	/**
	 * Returns an IExtractedIonSignal instance from the given scan position.
	 * 
	 * @param scan
	 * @throws NoExtractedIonSignalStoredException
	 * @return {@link IExtractedIonSignal}
	 */
	// TODO eventuell doch null zurückgeben, wenn kein Signal gespeichert ist
	IExtractedIonSignal getExtractedIonSignal(int scan) throws NoExtractedIonSignalStoredException;

	// TODO JUnit
	/**
	 * Returns a list of extracted ion signals.
	 * 
	 * @return List<IExtractedIonSignal>
	 */
	List<IExtractedIonSignal> getExtractedIonSignals();

	/**
	 * Returns a {@link ITotalScanSignals} instance of the given ion
	 * (ion) value.
	 * 
	 * @param ion
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(int ion);

	/**
	 * Returns all stored signals as a {@link ITotalScanSignals} instance.
	 * 
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals();

	/**
	 * Returns a {@link ITotalScanSignals} instance of the given ion
	 * (ion) value and the given scan range.
	 * 
	 * @param ion
	 * @param scanRange
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(int ion, IScanRange scanRange);

	/**
	 * Returns a {@link ITotalScanSignals} instance of the total ion signals and
	 * the given scan range.
	 * 
	 * @param scanRange
	 * @return {@link ITotalScanSignals}
	 */
	ITotalScanSignals getTotalIonSignals(IScanRange scanRange);

	/**
	 * Returns an {@link IScanMSD} instance for the given scan.<br/>
	 * If no mass spectrum is available, null will be returned.
	 * 
	 * @param scan
	 * @return {@link IScanMSD}
	 */
	IScanMSD getScan(int scan);

	/**
	 * Returns an {@link IScanMSD} instance for the given scan without the
	 * excluded ions.<br/>
	 * If no mass spectrum is available, null will be returned.
	 * 
	 * @param scan
	 * @param excludedIons
	 * @return IMassSpectrum
	 */
	IScanMSD getScan(int scan, IMarkedIons excludedIons);

	/**
	 * Returns the lowest start ion (ion).
	 * 
	 * @return int
	 */
	int getStartIon();

	/**
	 * Returns the highest stop ion (ion).
	 * 
	 * @return int
	 */
	int getStopIon();

	/**
	 * Returns the size.
	 * 
	 * @return int
	 */
	int size();

	/**
	 * Returns the start scan.
	 * 
	 * @return int
	 */
	int getStartScan();

	/**
	 * Returns the stop scan.
	 * 
	 * @return int
	 */
	int getStopScan();

	/**
	 * Returns a deep copy of the actual extracted ion signals.<br/>
	 * The copy will contain scans as {@link IExtractedIonSignal} but without
	 * ion values.<br/>
	 * The returned instance can be used to calculate another ion
	 * distribution but with the same scan - retention time pattern.
	 * 
	 * @return {@link IExtractedIonSignals}
	 */
	IExtractedIonSignals makeDeepCopyWithoutSignals();
}
