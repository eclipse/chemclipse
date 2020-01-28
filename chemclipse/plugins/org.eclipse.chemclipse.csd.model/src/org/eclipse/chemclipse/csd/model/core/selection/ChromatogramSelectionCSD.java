/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
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
package org.eclipse.chemclipse.csd.model.core.selection;

import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.notifier.ChromatogramSelectionCSDUpdateNotifier;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;

public class ChromatogramSelectionCSD extends AbstractChromatogramSelection<IChromatogramPeakCSD, IChromatogramCSD> implements IChromatogramSelectionCSD {

	private IScanCSD selectedScan;
	private IChromatogramPeakCSD selectedPeak;
	private IScan identifiedScan;

	public ChromatogramSelectionCSD(IChromatogramCSD chromatogram) throws ChromatogramIsNullException {
		this(chromatogram, true);
	}

	public ChromatogramSelectionCSD(IChromatogramCSD chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {
		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		super(chromatogram, fireUpdate);
		reset(fireUpdate);
	}

	@Override
	public void dispose() {

		super.dispose();
		selectedScan = null;
		selectedPeak = null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IChromatogramCSD getChromatogramCSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramCSD) {
			return (IChromatogramCSD)chromatogram;
		}
		return null;
	}

	@Override
	public IScanCSD getSelectedScan() {

		return selectedScan;
	}

	@Override
	public IChromatogramPeakCSD getSelectedPeak() {

		return selectedPeak;
	}

	@Override
	public void reset() {

		reset(true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void reset(boolean fireUpdate) {

		super.reset(fireUpdate);
		IChromatogram chromatogram = getChromatogram();
		/*
		 * Scan
		 */
		if(chromatogram.getNumberOfScans() >= 1) {
			/*
			 * Chromatogram CSD
			 */
			if(chromatogram instanceof IChromatogramCSD) {
				selectedScan = ((IChromatogramCSD)chromatogram).getSupplierScan(1);
			}
		} else {
			selectedScan = null;
		}
		/*
		 * Peak
		 */
		if(chromatogram instanceof IChromatogramCSD) {
			List<IChromatogramPeakCSD> peaks = ((IChromatogramCSD)chromatogram).getPeaks();
			if(peaks != null && peaks.size() >= 1) {
				selectedPeak = peaks.get(0);
			} else {
				selectedPeak = null;
			}
		}
		/*
		 * Fire an update.
		 */
		if(fireUpdate) {
			ChromatogramSelectionCSDUpdateNotifier.fireUpdateChange(this, false);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan) {

		if(selectedScan instanceof IScanCSD) {
			setSelectedScan((IScanCSD)selectedScan);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IScanCSD) {
			setSelectedScan((IScanCSD)selectedScan, update);
		}
	}

	@Override
	public void setSelectedScan(IScanCSD selectedScan) {

		/*
		 * FireUpdateChange will be called in the validate method.
		 */
		setSelectedScan(selectedScan, true);
	}

	@Override
	public void setSelectedScan(IScanCSD selectedScan, boolean update) {

		if(selectedScan != null) {
			this.selectedScan = selectedScan;
			/*
			 * Fire update change if necessary.
			 */
			if(update) {
				ChromatogramSelectionCSDUpdateNotifier.fireUpdateChange(this, false);
			}
		}
	}

	@Override
	public void setSelectedPeak(IChromatogramPeakCSD selectedPeak) {

		setSelectedPeak(selectedPeak, true);
	}

	@Override
	public void setSelectedPeak(IChromatogramPeakCSD selectedPeak, boolean update) {

		/*
		 * It shall be possible to set the peak to null,
		 * e.g. if all peaks are removed.
		 */
		this.selectedPeak = selectedPeak;
		if(selectedPeak != null) {
			/*
			 * Fire update change if neccessary.
			 */
			if(update) {
				ChromatogramSelectionCSDUpdateNotifier.fireUpdateChange(this, false);
			}
		}
	}

	@Override
	public void fireUpdateChange(boolean forceReload) {

		ChromatogramSelectionCSDUpdateNotifier.fireUpdateChange(this, forceReload);
	}

	@Override
	public void update(boolean forceReload) {

		super.update(forceReload);
		setSelectedScan(selectedScan, false);
		setSelectedPeak(selectedPeak, false);
		fireUpdateChange(forceReload);
	}

	@Override
	public IScan getSelectedIdentifiedScan() {

		return identifiedScan;
	}

	@Override
	public void setSelectedIdentifiedScan(IScan identifiedScan) {

		this.identifiedScan = identifiedScan;
	}

	@Override
	public void removeSelectedIdentifiedScan() {

		identifiedScan = null;
	}
}
