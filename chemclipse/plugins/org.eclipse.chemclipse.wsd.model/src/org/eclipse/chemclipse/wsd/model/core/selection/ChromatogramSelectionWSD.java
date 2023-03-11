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
 * Matthias Mailänder - refined the wavelength selection
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.selection;

import java.util.Optional;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.notifier.UpdateNotifier;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;

public class ChromatogramSelectionWSD extends AbstractChromatogramSelection<IChromatogramPeakWSD, IChromatogramWSD> implements IChromatogramSelectionWSD {

	private IScanWSD selectedScan;
	private IMarkedWavelengths selectedWavelengths;

	public ChromatogramSelectionWSD(IChromatogramWSD chromatogram) throws ChromatogramIsNullException {

		this(chromatogram, true);
	}

	public ChromatogramSelectionWSD(IChromatogramWSD chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {

		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		super(chromatogram, fireUpdate);
		//
		populateWavelengths(chromatogram);
		reset(fireUpdate);
	}

	@Override
	public void populateWavelengths(IChromatogramWSD chromatogram) {

		Optional<IScan> scan = chromatogram.getScans().stream().findFirst();
		if(!scan.isEmpty() && scan.get() instanceof IScanWSD scanWSD) {
			selectedWavelengths = new MarkedWavelengths();
			for(IScanSignalWSD signal : scanWSD.getScanSignals()) {
				selectedWavelengths.add(signal.getWavelength());
			}
		}
	}

	@Override
	public void dispose() {

		selectedScan = null;
		super.dispose();
	}

	@Override
	@Deprecated
	public IChromatogramWSD getChromatogramWSD() {

		IChromatogram<?> chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
			return chromatogramWSD;
		}
		return null;
	}

	@Override
	public IScanWSD getSelectedScan() {

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
			 * Chromatogram WSD
			 */
			if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
				selectedScan = chromatogramWSD.getSupplierScan(1);
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

		if(selectedScan instanceof IScanWSD scanWSD) {
			setSelectedScan(scanWSD);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IScanWSD scanWSD) {
			setSelectedScan(scanWSD, update);
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

	@Override
	public IMarkedWavelengths getSelectedWavelengths() {

		return selectedWavelengths;
	}

	@Override
	public void setSelectedWavelengths(IMarkedWavelengths selectedWavelengths) {

		this.selectedWavelengths = selectedWavelengths;
	}
}
