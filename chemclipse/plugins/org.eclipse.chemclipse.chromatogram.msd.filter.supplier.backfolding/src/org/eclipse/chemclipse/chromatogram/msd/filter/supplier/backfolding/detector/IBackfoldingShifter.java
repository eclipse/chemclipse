/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.detector;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.FilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backfolding.settings.IBackfoldingSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IBackfoldingShifter {

	/**
	 * Runs the backfolding algorithm on the given chromatogram selection and
	 * returns an {@link IExtractedIonSignals} instance containing the shifted
	 * ions.<br/>
	 * The retention times and scan will be preserved but the distribution of
	 * ions could be different.<br/>
	 * All backfolding settings are stored in {@link IBackfoldingSettings}.<br/>
	 * <br/>
	 * See: "Backfolding applied to differential gas chromatography mass
	 * spectrometry as a mathematical enhancement of chromatographic
	 * resolution", Pool, W. G. and deLeeuw, J. W. and vandeGraaf, B., 1996
	 * 
	 * @param chromatogramSelection
	 * @param backfoldingSettings
	 * @param monitor
	 * @return {@link IExtractedIonSignals}
	 */
	IExtractedIonSignals shiftIons(IChromatogramSelectionMSD chromatogramSelection, FilterSettings filterSettings, IProgressMonitor monitor);
}
