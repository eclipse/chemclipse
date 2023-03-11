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
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.core.selection;

import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramPeakCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;

public class ChromatogramSelectionCSD extends AbstractChromatogramSelection<IChromatogramPeakCSD, IChromatogramCSD> implements IChromatogramSelectionCSD {

	private IScanCSD selectedScan;

	public ChromatogramSelectionCSD(IChromatogramCSD chromatogram) throws ChromatogramIsNullException {

		this(chromatogram, true);
	}

	public ChromatogramSelectionCSD(IChromatogramCSD chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {

		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		super(chromatogram);
		reset(fireUpdate);
	}

	@Override
	public void dispose() {

		super.dispose();
		selectedScan = null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IChromatogramCSD getChromatogramCSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
			return chromatogramCSD;
		}
		return null;
	}

	@Override
	public IScanCSD getSelectedScan() {

		return selectedScan;
	}

	@Override
	public void reset() {

		reset(true);
	}

	@Override
	public void reset(boolean fireUpdate) {

		super.reset(fireUpdate);
		IChromatogram<?> chromatogram = getChromatogram();
		/*
		 * Scan
		 */
		if(chromatogram.getNumberOfScans() >= 1) {
			/*
			 * Chromatogram CSD
			 */
			if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
				selectedScan = chromatogramCSD.getSupplierScan(1);
			}
		} else {
			selectedScan = null;
		}
		/*
		 * Peak
		 */
		if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
			List<IChromatogramPeakCSD> peaks = chromatogramCSD.getPeaks();
			if(peaks != null && !peaks.isEmpty()) {
				setSelectedPeak(peaks.get(0));
			} else {
				setSelectedPeak(null);
			}
		}
		/*
		 * Fire an update.
		 */
		if(fireUpdate) {
			UpdateNotifier.update(this);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan) {

		if(selectedScan instanceof IScanCSD scanCSD) {
			setSelectedScan(scanCSD);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IScanCSD scanCSD) {
			setSelectedScan(scanCSD, update);
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
				UpdateNotifier.update(this);
			}
		}
	}

	@Override
	public void fireUpdateChange(boolean forceReload) {

		UpdateNotifier.update(this);
	}

	@Override
	public void update(boolean forceReload) {

		super.update(forceReload);
		//
		setSelectedScan(selectedScan, false);
		//
		fireUpdateChange(forceReload);
	}
}
