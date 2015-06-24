/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.selection;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.notifier.ChromatogramSelectionUpdateNotifier;

public class ChromatogramSelectionWSD extends AbstractChromatogramSelection implements IChromatogramSelectionWSD {

	private IScanWSD selectedScan;

	public ChromatogramSelectionWSD(IChromatogram chromatogram) throws ChromatogramIsNullException {

		this(chromatogram, true);
	}

	public ChromatogramSelectionWSD(IChromatogram chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {

		super(chromatogram, fireUpdate);
		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		reset(fireUpdate);
	}

	// ------------------------------------IChromatogramSelection
	public void dispose() {

		super.dispose();
	}

	@Override
	public IChromatogramWSD getChromatogramWSD() {

		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD) {
			return (IChromatogramWSD)chromatogram;
		}
		return null;
	}

	@Override
	public void reset() {

		reset(true);
	}

	@Override
	public void fireUpdateChange(boolean forceReload) {

		ChromatogramSelectionUpdateNotifier.fireUpdateChange(this, forceReload);
	}

	@Override
	public IScanWSD getSelectedScan() {

		return selectedScan;
	}

	@Override
	public void setSelectedScan(IScan selectedScan) {

		if(selectedScan instanceof IScanWSD) {
			setSelectedScan((IScanWSD)selectedScan);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IScanWSD) {
			setSelectedScan((IScanWSD)selectedScan, update);
		}
	}

	@Override
	public void setSelectedScan(IScanWSD selectedScan) {

		/*
		 * FireUpdateChange will be called in the validate method.
		 */
		setSelectedScan(selectedScan, true);
	}

	@Override
	public void setSelectedScan(IScanWSD selectedScan, boolean update) {

		if(selectedScan != null) {
			this.selectedScan = selectedScan;
			/*
			 * Fire update change if neccessary.
			 */
			if(update) {
				ChromatogramSelectionUpdateNotifier.fireUpdateChange(this, false);
			}
		}
	}
}
