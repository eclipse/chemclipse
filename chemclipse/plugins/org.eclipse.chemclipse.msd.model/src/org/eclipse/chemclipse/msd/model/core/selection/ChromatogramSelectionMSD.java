/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - reset selected peak in case of deletion
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.selection;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.PeakType;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.model.selection.AbstractChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IChromatogramPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IIonTransitionSettings;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIonTransitions;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIonTransitions;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.msd.model.notifier.ChromatogramSelectionMSDUpdateNotifier;

/**
 * You can define within a {@link ChromatogramSelectionMSD} instance a certain view
 * on a chromatogram.<br/>
 * This is appropriate if you like to apply a filter to a specific chromatogram
 * area.<br/>
 * Start and stop scan are not provided as they can be calculated by the
 * retention time.
 */
public class ChromatogramSelectionMSD extends AbstractChromatogramSelection<IChromatogramPeakMSD, IChromatogramMSD> implements IChromatogramSelectionMSD {

	private IVendorMassSpectrum selectedScan;
	private IVendorMassSpectrum selectedIdentifiedScan;
	private IChromatogramPeakMSD selectedPeak;
	private IMarkedIons selectedIons;
	private IMarkedIons excludedIons;
	private IMarkedIonTransitions markedIonTransitions;

	public ChromatogramSelectionMSD(IChromatogramMSD chromatogram) throws ChromatogramIsNullException {
		this(chromatogram, true);
	}

	public ChromatogramSelectionMSD(IChromatogramMSD chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {
		super(chromatogram, fireUpdate);
		/*
		 * Create instances of selected and excluded ions.
		 */
		selectedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		excludedIons = new MarkedIons(IMarkedIons.IonMarkMode.INCLUDE);
		/*
		 * Marked ion transitions.
		 */
		if(chromatogram instanceof IChromatogramMSD) {
			IIonTransitionSettings ionTransitionSettings = chromatogram.getIonTransitionSettings();
			if(ionTransitionSettings == null) {
				markedIonTransitions = new MarkedIonTransitions();
			} else {
				markedIonTransitions = new MarkedIonTransitions(ionTransitionSettings.getIonTransitions());
			}
		} else {
			markedIonTransitions = new MarkedIonTransitions();
		}
		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		reset(fireUpdate);
	}

	@Override
	public void dispose() {

		super.dispose();
		selectedScan = null;
		selectedIdentifiedScan = null;
		selectedPeak = null;
		selectedIons = null;
		excludedIons = null;
	}

	@Override
	public IChromatogramMSD getChromatogramMSD() {

		@SuppressWarnings("rawtypes")
		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramMSD) {
			return (IChromatogramMSD)chromatogram;
		}
		return null;
	}

	@Override
	public IVendorMassSpectrum getSelectedScan() {

		return selectedScan;
	}

	@Override
	public IVendorMassSpectrum getSelectedIdentifiedScan() {

		return selectedIdentifiedScan;
	}

	@Override
	public IChromatogramPeakMSD getSelectedPeak() {

		return selectedPeak;
	}

	@Override
	public IMarkedIons getExcludedIons() {

		return excludedIons;
	}

	@Override
	public IMarkedIons getSelectedIons() {

		return selectedIons;
	}

	@Override
	public IMarkedIonTransitions getMarkedIonTransitions() {

		return markedIonTransitions;
	}

	@Override
	public void reset() {

		reset(true);
	}

	@Override
	public void reset(boolean fireUpdate) {

		super.reset(fireUpdate);
		@SuppressWarnings("rawtypes")
		IChromatogram chromatogram = getChromatogram();
		/*
		 * Scan
		 */
		if(chromatogram.getNumberOfScans() >= 1) {
			/*
			 * Chromatogram MSD
			 */
			if(chromatogram instanceof IChromatogramMSD) {
				selectedScan = ((IChromatogramMSD)chromatogram).getSupplierScan(1);
			}
		} else {
			selectedScan = null;
		}
		/*
		 * Selected Identified Scan
		 */
		selectedIdentifiedScan = null;
		/*
		 * Peak
		 */
		if(chromatogram instanceof IChromatogramMSD) {
			List<IChromatogramPeakMSD> peaks = ((IChromatogramMSD)chromatogram).getPeaks();
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
			fireUpdateChange(false);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan) {

		if(selectedScan instanceof IVendorMassSpectrum) {
			setSelectedScan((IVendorMassSpectrum)selectedScan);
		}
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		if(selectedScan instanceof IVendorMassSpectrum) {
			setSelectedScan((IVendorMassSpectrum)selectedScan, update);
		}
	}

	@Override
	public void setSelectedScan(IVendorMassSpectrum selectedScan) {

		/*
		 * FireUpdateChange will be called in the validate method.
		 */
		setSelectedScan(selectedScan, true);
	}

	@Override
	public void setSelectedIdentifiedScan(IScan identifiedScan) {

		if(identifiedScan instanceof IVendorMassSpectrum) {
			setSelectedIdentifiedScan((IVendorMassSpectrum)identifiedScan);
		}
	}

	@Override
	public void setSelectedIdentifiedScan(IVendorMassSpectrum selectedIdentifiedScan) {

		/*
		 * FireUpdateChange will be called in the validate method.
		 */
		setSelectedIdentifiedScan(selectedIdentifiedScan, true);
	}

	@Override
	public void setSelectedScan(IVendorMassSpectrum selectedScan, boolean update) {

		if(selectedScan != null) {
			this.selectedScan = selectedScan;
			/*
			 * Fire update change if neccessary.
			 */
			if(update) {
				fireUpdateChange(false);
			}
		}
	}

	@Override
	public void setSelectedIdentifiedScan(IVendorMassSpectrum selectedIdentifiedScan, boolean update) {

		if(selectedIdentifiedScan != null) {
			this.selectedIdentifiedScan = selectedIdentifiedScan;
			/*
			 * Fire update change if neccessary.
			 */
			if(update) {
				fireUpdateChange(false);
			}
		}
	}

	@Override
	public void setSelectedPeak(IChromatogramPeakMSD selectedPeak) {

		setSelectedPeak(selectedPeak, true);
	}

	@Override
	public void setSelectedPeak(IChromatogramPeakMSD selectedPeak, boolean update) {

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
				fireUpdateChange(false);
			}
		}
	}

	@Override
	public void fireUpdateChange(boolean forceReload) {

		try {
			ChromatogramSelectionMSDUpdateNotifier.fireUpdateChange(this, forceReload);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(boolean forceReload) {

		if(selectedPeak != null && selectedPeak.getPeakType() == PeakType.DELETED) {
			selectedPeak = null;
		}
		super.update(forceReload);
		setSelectedScan(selectedScan, false);
		setSelectedIdentifiedScan(selectedIdentifiedScan, false);
		setSelectedPeak(selectedPeak, false);
		fireUpdateChange(forceReload);
	}

	@Override
	public void removeSelectedIdentifiedScan() {

		selectedIdentifiedScan = null;
	}
}
