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
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;

@SuppressWarnings("rawtypes")
public class ChromatogramDataSupport {

	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	public String getChromatogramType(IChromatogramSelection chromatogramSelection) {

		String type = "";
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			type = " [MSD]";
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			type = " [CSD]";
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			type = " [WSD]";
		}
		//
		return type;
	}

	public String getChromatogramLabel(IChromatogram chromatogram) {

		return getChromatogramLabel((IChromatogramOverview)chromatogram);
	}

	public String getChromatogramLabel(IChromatogramOverview chromatogramOverview) {

		StringBuilder builder = new StringBuilder();
		if(chromatogramOverview != null) {
			builder.append("Chromatogram: ");
			builder.append(chromatogramOverview.getName());
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}

	public String getChromatogramLabelExtended(IChromatogram chromatogram) {

		StringBuilder builder = new StringBuilder();
		if(chromatogram != null) {
			builder.append("Chromatogram: ");
			builder.append(chromatogram.getName());
			builder.append(" ");
			if(chromatogram instanceof IChromatogramMSD) {
				builder.append("(MSD)");
			} else if(chromatogram instanceof IChromatogramCSD) {
				builder.append("(CSD)");
			} else if(chromatogram instanceof IChromatogramWSD) {
				builder.append("(WSD)");
			}
			String miscInfoSeparated = chromatogram.getMiscInfoSeparated();
			if("".equals(miscInfoSeparated)) {
				String miscInfo = chromatogram.getMiscInfo();
				if(!"".equals(miscInfo)) {
					builder.append(" | ");
					builder.append(miscInfo);
				}
			} else {
				builder.append(" | ");
				builder.append(miscInfoSeparated);
			}
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
				/*
				 * MSD
				 */
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				if(selectedRange instanceof IChromatogramSelectionMSD) {
					peaks = chromatogramMSD.getPeaks((IChromatogramSelectionMSD)selectedRange);
				} else {
					peaks = chromatogramMSD.getPeaks();
				}
			} else if(chromatogram instanceof IChromatogramCSD) {
				/*
				 * CSD
				 */
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				if(selectedRange instanceof IChromatogramSelectionCSD) {
					peaks = chromatogramCSD.getPeaks((IChromatogramSelectionCSD)selectedRange);
				} else {
					peaks = chromatogramCSD.getPeaks();
				}
			} else if(chromatogram instanceof IChromatogramWSD) {
				/*
				 * WSD
				 */
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				if(selectedRange instanceof IChromatogramSelectionWSD) {
					peaks = chromatogramWSD.getPeaks((IChromatogramSelectionWSD)selectedRange);
				} else {
					peaks = chromatogramWSD.getPeaks();
				}
			}
		}
		//
		return peaks;
	}

	@SuppressWarnings("unchecked")
	public List<IScan> getIdentifiedScans(IChromatogram chromatogram) {

		return getIdentifiedScans(chromatogram, null);
	}

	public List<IScan> getIdentifiedScans(IChromatogram<? extends IPeak> chromatogram, IChromatogramSelection selectedRange) {

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

	public List<? extends IPeak> getPeaks(IChromatogramSelection chromatogramSelection, boolean extractPeaksInSelectedRange) {

		List<? extends IPeak> peaks = new ArrayList<IPeak>();
		//
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram instanceof IChromatogramMSD) {
				/*
				 * MSD
				 */
				IChromatogramMSD chromatogramMSD = (IChromatogramMSD)chromatogram;
				if(extractPeaksInSelectedRange) {
					peaks = chromatogramMSD.getPeaks((IChromatogramSelectionMSD)chromatogramSelection);
				} else {
					peaks = chromatogramMSD.getPeaks();
				}
			} else if(chromatogram instanceof IChromatogramCSD) {
				/*
				 * CSD
				 */
				IChromatogramCSD chromatogramCSD = (IChromatogramCSD)chromatogram;
				if(extractPeaksInSelectedRange) {
					peaks = chromatogramCSD.getPeaks((IChromatogramSelectionCSD)chromatogramSelection);
				} else {
					peaks = chromatogramCSD.getPeaks();
				}
			} else if(chromatogram instanceof IChromatogramWSD) {
				/*
				 * WSD
				 */
				IChromatogramWSD chromatogramWSD = (IChromatogramWSD)chromatogram;
				if(extractPeaksInSelectedRange) {
					peaks = chromatogramWSD.getPeaks((IChromatogramSelectionWSD)chromatogramSelection);
				} else {
					peaks = chromatogramWSD.getPeaks();
				}
			}
		}
		//
		return peaks;
	}

	@SuppressWarnings("unchecked")
	public List<? extends IScan> getIdentifiedScans(IChromatogramSelection chromatogramSelection, boolean showScansInSelectedRange) {

		List<? extends IScan> scans = new ArrayList<IScan>();
		//
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(showScansInSelectedRange) {
				scans = getIdentifiedScans(chromatogram, chromatogramSelection);
			} else {
				scans = getIdentifiedScans(chromatogram);
			}
		}
		//
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
			List<? extends IIdentificationTarget> targets = scanMSD.getTargets();
			if(targets != null && targets.size() > 0) {
				scanContainsTargets = true;
			}
		} else if(scan instanceof IScanCSD) {
			IScanCSD scanCSD = (IScanCSD)scan;
			List<? extends IIdentificationTarget> targets = scanCSD.getTargets();
			if(targets != null && targets.size() > 0) {
				scanContainsTargets = true;
			}
		} else if(scan instanceof IScanWSD) {
			IScanWSD scanWSD = (IScanWSD)scan;
			List<? extends IIdentificationTarget> targets = scanWSD.getTargets();
			if(targets != null && targets.size() > 0) {
				scanContainsTargets = true;
			}
		}
		return scanContainsTargets;
	}
}
