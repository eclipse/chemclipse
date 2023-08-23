/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.core.selection;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;
import org.eclipse.chemclipse.xir.model.core.IChromatogramISD;
import org.eclipse.chemclipse.xir.model.core.IChromatogramPeakISD;
import org.eclipse.chemclipse.xir.model.core.IScanISD;

public class ChromatogramSelectionISD extends AbstractChromatogramSelection<IChromatogramPeakISD, IChromatogramISD> implements IChromatogramSelectionISD {

	private IScanISD selectedScan;

	public ChromatogramSelectionISD(IChromatogramISD chromatogram) throws ChromatogramIsNullException {

		this(chromatogram, true);
	}

	public ChromatogramSelectionISD(IChromatogramISD chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {

		super(chromatogram);
		reset(fireUpdate);
	}

	@Override
	public void dispose() {

		selectedScan = null;
		super.dispose();
	}

	@Override
	public IScanISD getSelectedScan() {

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
			if(chromatogram instanceof IChromatogramISD chromatogramISD) {
				selectedScan = (IScanISD)chromatogramISD.getScan(1);
			}
		} else {
			selectedScan = null;
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

		if(selectedScan instanceof IScanISD scanISD) {
			setSelectedScan(scanISD, true);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IScanISD scanISD) {
			/*
			 * Fire update change if neccessary.
			 */
			this.selectedScan = scanISD;
			if(update) {
				fireUpdateChange(false);
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
		setSelectedScan(selectedScan, false);
		fireUpdateChange(forceReload);
	}
}