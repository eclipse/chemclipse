/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramSignalFilter;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.processor.SavitzkyGolayProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.savitzkygolay.settings.ChromatogramFilterSettings;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramFilter extends AbstractChromatogramSignalFilter {

	@Override
	protected IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IChromatogramFilterSettings filterSettings, IProgressMonitor monitor) {

		SavitzkyGolayProcessor processor = new SavitzkyGolayProcessor();
		return processor.apply(totalSignals, (ChromatogramFilterSettings)filterSettings, monitor);
	}

	@Override
	protected IChromatogramFilterResult applyFilter(ITotalScanSignals totalSignals, IProgressMonitor monitor) {

		ChromatogramFilterSettings chromatogramFilterSettings = PreferenceSupplier.getFilterSettings();
		return applyFilter(totalSignals, chromatogramFilterSettings, monitor);
	}
}
