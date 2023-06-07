/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core.internal.support;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsStretch;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class RetentionTimeStretcher extends AbstractRetentionTimeModifier {

	/*
	 * Use only static methods.
	 */
	private RetentionTimeStretcher() {

	}

	public static void stretchChromatogram(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsStretch filterSettings) throws FilterException {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			throw new FilterException("The chromatogram must not be null.");
		}
		//
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int scanRange = chromatogram.getNumberOfScans() - 1;
		if(scanRange > 0) {
			float retentionTimeRange = filterSettings.getChromatogramLength() - filterSettings.getScanDelay();
			int scanInterval = Math.round(retentionTimeRange / scanRange);
			//
			chromatogram.setScanDelay(filterSettings.getScanDelay());
			chromatogram.setScanInterval(scanInterval);
			chromatogram.recalculateRetentionTimes();
		}
		//
		adjustScanDelayAndRetentionTimeRange(chromatogramSelection);
	}
}
