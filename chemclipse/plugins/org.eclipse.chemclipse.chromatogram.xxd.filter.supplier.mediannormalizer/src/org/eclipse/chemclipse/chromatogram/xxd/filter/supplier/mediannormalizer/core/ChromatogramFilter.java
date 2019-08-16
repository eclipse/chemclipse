/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Janos Binder - implementation of median filter
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.mediannormalizer.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramSignalFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.ChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.mediannormalizer.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.mediannormalizer.settings.FilterSettings;
import org.eclipse.chemclipse.model.exceptions.CalculationException;
import org.eclipse.chemclipse.model.exceptions.NoTotalSignalStoredException;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramSignalFilter {

	@Override
	protected IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		try {
			/*
			 * Try to median normalize the total ion signals. The scans of the
			 * chromatogram are still untouched, the total ion signals instance
			 * is just a copy of the scan values. Good luck, i still implemented
			 * a median normalization method in the helper class
			 * "TotalIonSignalsModifier.java".
			 */
			TotalScanSignalsModifier.medianNormalize(totalSignals);
		} catch(NoTotalSignalStoredException e) {
			return new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		} catch(CalculationException e) {
			return new ChromatogramFilterResult(ResultStatus.EXCEPTION, e.getMessage());
		}
		return new ChromatogramFilterResult(ResultStatus.OK, "The chromatogram selection was successfully normalized.");
	}

	@Override
	protected IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IProgressMonitor monitor) {

		FilterSettings filterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(totalSignals, filterSettings, monitor);
	}
}
