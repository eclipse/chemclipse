/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
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
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public abstract class AbstractRetentionTimeModifier {

	@SuppressWarnings("rawtypes")
	protected static void adjustScanDelayAndRetentionTimeRange(IChromatogramSelection chromatogramSelection) throws FilterException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		if(chromatogram.getNumberOfScans() <= 0) {
			throw new FilterException("There is no scan available.");
		}
		IScan firstScan = chromatogram.getScan(1);
		int scanDelay = firstScan.getRetentionTime();
		chromatogram.setScanDelay(scanDelay);
		//
		int startRetentionTime = firstScan.getRetentionTime();
		if(chromatogramSelection.getStartRetentionTime() < startRetentionTime) {
			chromatogramSelection.setStartRetentionTime(startRetentionTime);
		}
		//
		int stopRetentionTime = chromatogram.getStopRetentionTime();
		if(chromatogramSelection.getStopRetentionTime() > stopRetentionTime) {
			chromatogramSelection.setStopRetentionTime(stopRetentionTime);
		}
	}
}
