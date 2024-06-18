/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core.internal.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsScanFiller;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class ScanFiller {

	public static void autofillScans(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsScanFiller filterSettings) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		//
		if(!chromatogram.getScans().isEmpty()) {
			int scanInterval = chromatogram.getScanInterval();
			if(scanInterval > 0) {
				/*
				 * Settings
				 */
				List<IScan> fillerScans = new ArrayList<>();
				IScan scanReference = chromatogram.getScan(1);
				if(scanReference != null) {
					int retentionTime = scanReference.getRetentionTime();
					int scanDelay = chromatogram.getScanDelay();
					while(retentionTime >= 0) {
						retentionTime -= scanInterval;
						IScan scan = ScanUtil.createEmptyScan(scanReference, retentionTime);
						if(scan != null) {
							fillerScans.add(scan);
							scanDelay = retentionTime;
						}
					}
					/*
					 * Insert new scans and sort them by retention time.
					 */
					if(!fillerScans.isEmpty()) {
						List<IScan> scans = new ArrayList<>(chromatogram.getScans());
						scans.addAll(fillerScans);
						Collections.sort(scans, (s1, s2) -> Integer.compare(s1.getRetentionTime(), s2.getRetentionTime()));
						chromatogram.replaceAllScans(scans);
						chromatogram.setScanDelay(scanDelay);
					}
				}
			}
		}
	}
}