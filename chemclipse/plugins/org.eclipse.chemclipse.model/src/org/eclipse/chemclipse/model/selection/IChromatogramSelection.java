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
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.model.selection;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.support.IRetentionTimeRange;
import org.eclipse.chemclipse.model.support.IScanRange;
import org.eclipse.chemclipse.model.updates.IChromatogramUpdateListener;
import org.eclipse.chemclipse.numeric.core.Point;

public interface IChromatogramSelection<P extends IPeak, C extends IChromatogram<P>> extends IChromatogramUpdateListener, IRetentionTimeRange, IScanRange {

	/**
	 * Returns the stored chromatogram.
	 * May return null.
	 *
	 * @return {@link IChromatogram}
	 */
	C getChromatogram();

	/**
	 * Reset all values to the given chromatogram bounds.<br/>
	 * E.g. if the chromatogram stores 5726 scans, reset would set the startScan
	 * to 1 and the stopScan to 5726. The start and stop abundance will be set
	 * to 0 and max abundance and the selected scan and peak will be each time
	 * the first one.
	 */
	void reset();

	/**
	 * Resets all values as reset and fires optionally an update.
	 *
	 * @param fireUpdate
	 */
	void reset(boolean fireUpdate);

	/**
	 * Returns the start retention time.<br/>
	 * The retention time is stored in milliseconds.
	 *
	 * @return int
	 */
	@Override
	int getStartRetentionTime();

	/**
	 * Sets the start retention time.<br/>
	 * The retention time is stored in milliseconds.<br/>
	 * The start retention time must be >= 0.
	 */
	void setStartRetentionTime(int startRetentionTime);

	/**
	 * Returns the stop retention time.<br/>
	 * The retention time is stored in milliseconds.
	 *
	 * @return int
	 */
	@Override
	int getStopRetentionTime();

	/**
	 * Sets the stop retention time.<br/>
	 * The retention time is stored in milliseconds.<br/>
	 * The stop retention time must be >= 0.
	 */
	void setStopRetentionTime(int stopRetentionTime);

	/**
	 * Returns the start abundance.
	 *
	 * @return float
	 */
	float getStartAbundance();

	/**
	 * Sets the start abundance.<br/>
	 * The startAbundance must be >= 0.
	 */
	void setStartAbundance(float startAbundance);

	/**
	 * Returns the stop abundance.
	 *
	 * @return float
	 */
	float getStopAbundance();

	/**
	 * Sets the stop abundance.<br/>
	 * The stopAbundance must be >= 0.
	 */
	void setStopAbundance(float startAbundance);

	void setRanges(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance);

	/**
	 * Sets the stop retention time.<br/>
	 * The stopRetentionTime must be >= 0.
	 */
	void setRangeRetentionTime(int startRetentionTime, int stopRetentionTime, boolean validate);

	/**
	 * Sets the stop retention time.<br/>
	 * The stopRetentionTime must be >= 0.
	 */
	void setRangeRetentionTime(int startRetentionTime, int stopRetentionTime);

	/**
	 * Sets the values and fires an update after validating and setting all
	 * values.
	 *
	 * @param startRetentionTime
	 * @param stopRetentionTime
	 * @param startAbundance
	 * @param stopAbundance
	 */
	void setRanges(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance, boolean validate);

	/**
	 * All listeners will be informed about an update.
	 * This method needs to be implemented by each ChromatogramSelection implementation, e.g. for ChromatogramSelectionMSD:
	 * org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionUpdateNotifier.fireUpdateChange(this, false);
	 *
	 * @param forceReload
	 */
	void fireUpdateChange(boolean forceReload);

	/**
	 * Returns the selected scan of the current chromatogram or null, if none is
	 * stored.
	 *
	 * @return {@link IScan}
	 */
	IScan getSelectedScan();

	/**
	 * Sets the selected scan of the current chromatogram.<br/>
	 * The scan must not be null.
	 */
	void setSelectedScan(IScan selectedScan);

	/**
	 * Use this convenient method, if you don't want to fire and update.
	 *
	 * @param selectedScan
	 * @param update
	 */
	void setSelectedScan(IScan selectedScan, boolean update);

	/**
	 * Returns the selected peak.
	 *
	 * @return IPeak
	 */
	P getSelectedPeak();

	List<P> getSelectedPeaks();

	void setSelectedPeak(P selectedPeak);

	void setSelectedPeaks(List<P> selectedPeaks);

	IScan getSelectedIdentifiedScan();

	List<IScan> getSelectedIdentifiedScans();

	void setSelectedIdentifiedScan(IScan selectedIdentifiedScan);

	void setSelectedIdentifiedScans(List<IScan> selectedIdentifiedScans);

	boolean isOverlaySelected();

	void setOverlaySelected(boolean overlaySelected);

	/**
	 * Lock the offset in the UI display.
	 *
	 * @return boolean
	 */
	boolean isLockOffset();

	/**
	 * Set this value to lock the offset in the UI.
	 *
	 * @param lockOffset
	 */
	void setLockOffset(boolean lockOffset);

	Point getOffset();

	void resetOffset();

	@Override
	default int getStartScan() {

		int scanNumber = getChromatogram().getScanNumber(getStartRetentionTime());
		if(scanNumber > 0) {
			return scanNumber;
		} else {
			return -1;
		}
	}

	@Override
	default int getStopScan() {

		int scanNumber = getChromatogram().getScanNumber(getStopRetentionTime());
		if(scanNumber > 0) {
			return scanNumber;
		} else {
			return -1;
		}
	}

	/**
	 * Validates the peak. If the peak has been marked as deleted,
	 * null is returned.
	 * 
	 * @param peak
	 * @return P
	 */
	default P validatePeak(P peak) {

		if(peak != null) {
			if(peak.isMarkedAsDeleted()) {
				return null;
			} else if(getChromatogram().getNumberOfPeaks() == 0) {
				return null;
			}
		}
		//
		return peak;
	}
}
