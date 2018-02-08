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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class ChromatogramDataSupport {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public String getChromatogramLabel(IChromatogram chromatogram) {

		StringBuilder builder = new StringBuilder();
		if(chromatogram != null) {
			builder.append("Chromatogram: ");
			builder.append(chromatogram.getName());
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}

	public String getChromatogramSelectionLabel(IChromatogramSelection chromatogramSelection) {

		StringBuilder builder = new StringBuilder();
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				builder.append("Chromatogram: ");
				builder.append(chromatogram.getName());
				builder.append(" | ");
				builder.append("RT: ");
				builder.append(decimalFormat.format(chromatogramSelection.getStartRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
				builder.append(" - ");
				builder.append(decimalFormat.format(chromatogramSelection.getStopRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR));
			}
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}

	public List<? extends IPeak> getPeaks(IChromatogram chromatogram) {

		return getPeaks(chromatogram, null);
	}

	public List<? extends IPeak> getPeaks(IChromatogram chromatogram, IChromatogramSelection selectedRange) {

		List<? extends IPeak> peaks = new ArrayList<IPeak>();
		if(chromatogram != null) {
			if(chromatogram instanceof IChromatogramMSD) {
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				if(selectedRange instanceof IChromatogramSelectionMSD) {
					peaks = chromatogramMSD.getPeaks((IChromatogramSelectionMSD)selectedRange);
				} else {
					peaks = chromatogramMSD.getPeaks();
				}
			} else if(chromatogram instanceof IChromatogramCSD) {
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				if(selectedRange instanceof IChromatogramSelectionCSD) {
					peaks = chromatogramCSD.getPeaks((IChromatogramSelectionCSD)selectedRange);
				} else {
					peaks = chromatogramCSD.getPeaks();
				}
			} else if(chromatogram instanceof IChromatogramWSD) {
				//
			}
		}
		//
		return peaks;
	}

	public List<IScan> getIdentifiedScans(IChromatogram chromatogram) {

		return getIdentifiedScans(chromatogram, null);
	}

	public List<IScan> getIdentifiedScans(IChromatogram chromatogram, IChromatogramSelection selectedRange) {

		int startRetentionTime = 0;
		int stopRetentionTime = 0;
		boolean useSelectedRange = false;
		//
		if(selectedRange != null) {
			useSelectedRange = true;
			startRetentionTime = selectedRange.getStartRetentionTime();
			stopRetentionTime = selectedRange.getStopRetentionTime();
		}
		//
		List<IScan> scans = new ArrayList<>();
		if(chromatogram != null) {
			for(IScan scan : chromatogram.getScans()) {
				if(useSelectedRange) {
					/*
					 * Check the range.
					 */
					if(scanIsInSelectedRange(scan, startRetentionTime, stopRetentionTime)) {
						if(scanContainsTargets(scan)) {
							scans.add(scan);
						}
					}
				} else {
					/*
					 * This is faster than doing the checks.
					 */
					if(scanContainsTargets(scan)) {
						scans.add(scan);
					}
				}
			}
		}
		return scans;
	}

	private boolean scanIsInSelectedRange(IScan scan, int startRetentionTime, int stopRetentionTime) {

		int retentionTime = scan.getRetentionTime();
		if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
			return true;
		}
		return false;
	}

	private boolean scanContainsTargets(IScan scan) {

		boolean scanContainsTargets = false;
		if(scan instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)scan;
			if(scanMSD.getTargets().size() > 0) {
				scanContainsTargets = true;
			}
		} else if(scan instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)scan;
			if(scanCSD.getTargets().size() > 0) {
				scanContainsTargets = true;
			}
		} else if(scan instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)scan;
			if(scanWSD.getTargets().size() > 0) {
				scanContainsTargets = true;
			}
		}
		return scanContainsTargets;
	}
}
