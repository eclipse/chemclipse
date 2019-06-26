/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilter;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;

@SuppressWarnings("rawtypes")
public abstract class AbstractTransferFilter extends AbstractChromatogramFilter implements IChromatogramFilter {

	@SuppressWarnings("unchecked")
	protected List<IScan> extractIdentifiedScans(IChromatogramSelection chromatogramSelection) {

		IChromatogram<? extends IPeak> chromatogram = chromatogramSelection.getChromatogram();
		//
		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		int startScan = chromatogram.getScanNumber(startRetentionTime);
		int stopScan = chromatogram.getScanNumber(stopRetentionTime);
		//
		List<IScan> identifiedScans = new ArrayList<>();
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			if(scan.getTargets().size() > 0) {
				identifiedScans.add(scan);
			}
		}
		//
		return identifiedScans;
	}

	protected List<? extends IPeak> extractPeaks(IChromatogram chromatogram) {

		if(chromatogram instanceof IChromatogramCSD) {
			return extractPeaks(new ChromatogramSelectionCSD((IChromatogramCSD)chromatogram));
		} else if(chromatogram instanceof IChromatogramMSD) {
			return extractPeaks(new ChromatogramSelectionMSD((IChromatogramMSD)chromatogram));
		} else if(chromatogram instanceof IChromatogramWSD) {
			return extractPeaks(new ChromatogramSelectionWSD((IChromatogramWSD)chromatogram));
		} else {
			return new ArrayList<>();
		}
	}

	@SuppressWarnings("unchecked")
	protected List<? extends IPeak> extractPeaks(IChromatogramSelection chromatogramSelection) {

		return chromatogramSelection.getChromatogram().getPeaks(chromatogramSelection);
	}
}
