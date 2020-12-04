/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.core;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.multiplier.exceptions.FilterException;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.TotalScanSignalsModifier;

@SuppressWarnings("rawtypes")
public abstract class ModifierChromatogramFilter extends AbstractChromatogramFilter {

	protected void applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, float factor, boolean multiply) throws FilterException {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		ITotalScanSignals totalScanSignals;
		//
		try {
			ITotalScanSignalExtractor totalScanSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			totalScanSignals = totalScanSignalExtractor.getTotalScanSignals(startScan, stopScan);
			if(multiply) {
				TotalScanSignalsModifier.multiply(totalScanSignals, factor);
			} else {
				TotalScanSignalsModifier.divide(totalScanSignals, factor);
			}
		} catch(Exception e) {
			throw new FilterException("There were no total ion signals stored.");
		}
		/*
		 * Iterate through all selected scans and set the calculated abundance
		 * values.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan scanRecord = chromatogram.getScan(scan);
			float normalizedAbundance = totalScanSignals.getTotalScanSignal(scan).getTotalSignal();
			if(normalizedAbundance <= 0.0f) {
				normalizedAbundance = 0.1f;
			}
			scanRecord.adjustTotalSignal(normalizedAbundance);
		}
	}
}