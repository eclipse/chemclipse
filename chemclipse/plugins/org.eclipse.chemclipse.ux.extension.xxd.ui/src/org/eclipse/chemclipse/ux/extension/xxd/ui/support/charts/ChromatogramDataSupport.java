/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - make methods static, fix warnings
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.selection.ChromatogramSelectionSupport;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.selection.MoveDirection;
import org.eclipse.chemclipse.model.support.HeaderUtil;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.vsd.model.core.IChromatogramVSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.jface.preference.IPreferenceStore;

public class ChromatogramDataSupport {

	private static PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);

	public static String getChromatogramEditorLabel(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			return chromatogram.getName() + " " + getChromatogramType(chromatogramSelection);
		} else {
			return "";
		}
	}

	public static String getChromatogramType(IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection != null) {
			return getChromatogramType(chromatogramSelection.getChromatogram());
		} else {
			return "";
		}
	}

	public static String getChromatogramType(IChromatogram<?> chromatogram) {

		if(chromatogram instanceof IChromatogramMSD) {
			return "[MSD]";
		} else if(chromatogram instanceof IChromatogramCSD) {
			return "[CSD]";
		} else if(chromatogram instanceof IChromatogramWSD) {
			return "[WSD]";
		} else if(chromatogram instanceof IChromatogramVSD) {
			return "[VSD]";
		}
		return "";
	}

	public static List<? extends IPeak> extractPeaks(IChromatogramSelection<?, ?> chromatogramSelection) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		List<? extends IPeak> peaks = null;
		if(chromatogram instanceof IChromatogramMSD chromatogramMSD) {
			peaks = chromatogramMSD.getPeaks(chromatogramSelection);
		} else if(chromatogram instanceof IChromatogramCSD chromatogramCSD) {
			peaks = chromatogramCSD.getPeaks(chromatogramSelection);
		} else if(chromatogram instanceof IChromatogramWSD chromatogramWSD) {
			peaks = chromatogramWSD.getPeaks(chromatogramSelection);
		}
		//
		return peaks;
	}

	/**
	 * Object could be IChromatogramSelection... or IChromatogram... instance.
	 * In case of no match, null will be returned.
	 *
	 * @param object
	 * @return IChromatogramSelection
	 */
	public IChromatogramSelection<?, ?> getChromatogramSelection(Object object) {

		if(object instanceof IChromatogramSelection) {
			return (IChromatogramSelection<?, ?>)object;
		} else if(object instanceof IChromatogramCSD chromatogramCSD) {
			return new ChromatogramSelectionCSD(chromatogramCSD);
		} else if(object instanceof IChromatogramMSD chromatogramMSD) {
			return new ChromatogramSelectionMSD(chromatogramMSD);
		} else if(object instanceof IChromatogramWSD chromatogramWSD) {
			return new ChromatogramSelectionWSD(chromatogramWSD);
		} else {
			return null;
		}
	}

	public static String getChromatogramLabel(IChromatogram<?> chromatogram) {

		return getChromatogramLabel((IChromatogramOverview)chromatogram);
	}

	public static String getChromatogramLabel(IChromatogramOverview chromatogramOverview) {

		StringBuilder builder = new StringBuilder();
		if(chromatogramOverview != null) {
			builder.append("Chromatogram: ");
			builder.append(chromatogramOverview.getName());
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}

	public static String getChromatogramLabelExtended(IChromatogram<?> chromatogram) {

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

	public static String getChromatogramSelectionLabel(IChromatogramSelection<?, ?> chromatogramSelection) {

		StringBuilder builder = new StringBuilder();
		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
				builder.append("Chromatogram: ");
				builder.append(chromatogram.getName());
				builder.append(" | ");
				builder.append("RT: ");
				builder.append(decimalFormat.format(chromatogramSelection.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				builder.append(" - ");
				builder.append(decimalFormat.format(chromatogramSelection.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			}
		} else {
			builder.append("No chromatogram has been selected yet.");
		}
		return builder.toString();
	}

	public static <T extends IPeak> List<T> getPeaks(IChromatogram<T> chromatogram) {

		return getPeaks(chromatogram, null);
	}

	public static <T extends IPeak> List<T> getPeaks(IChromatogram<T> chromatogram, IChromatogramSelection<T, ?> selectedRange) {

		if(selectedRange != null) {
			return chromatogram.getPeaks(selectedRange);
		} else {
			return chromatogram.getPeaks();
		}
	}

	public static List<IScan> getIdentifiedScans(IChromatogram<?> chromatogram) {

		return getIdentifiedScans(chromatogram, null);
	}

	public static List<IScan> getIdentifiedScans(IChromatogram<? extends IPeak> chromatogram, IChromatogramSelection<?, ?> selectedRange) {

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

	public static <T extends IPeak> List<T> getPeaks(IChromatogramSelection<T, ?> chromatogramSelection, boolean extractPeaksInSelectedRange) {

		if(chromatogramSelection != null) {
			IChromatogram<T> chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				if(extractPeaksInSelectedRange) {
					return chromatogram.getPeaks(chromatogramSelection);
				} else {
					return chromatogram.getPeaks();
				}
			}
		}
		return Collections.emptyList();
	}

	public static List<? extends IScan> getIdentifiedScans(IChromatogramSelection<?, ?> chromatogramSelection, boolean showScansInSelectedRange) {

		List<? extends IScan> scans = new ArrayList<>();
		//
		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			if(showScansInSelectedRange) {
				scans = getIdentifiedScans(chromatogram, chromatogramSelection);
			} else {
				scans = getIdentifiedScans(chromatogram);
			}
		}
		//
		return scans;
	}

	public static void adjustChromatogramSelection(IPeak peak, IChromatogramSelection<?, ?> chromatogramSelection) {

		if(chromatogramSelection != null) {
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			List<? extends IPeak> peaks = getPeaks(chromatogram);
			List<? extends IPeak> peaksSelection = new ArrayList<>(chromatogram.getPeaks(chromatogramSelection));
			Collections.sort(peaks, peakRetentionTimeComparator);
			Collections.sort(peaksSelection, peakRetentionTimeComparator);
			//
			if(peaks.get(0).equals(peak) || peaks.get(peaks.size() - 1).equals(peak)) {
				/*
				 * Don't move if it is the first or last peak of the chromatogram.
				 */
			} else {
				/*
				 * First peak of the selection: move left
				 * Last peak of the selection: move right
				 */
				if(peaksSelection.get(0).equals(peak)) {
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.LEFT, 5);
				} else if(peaksSelection.get(peaksSelection.size() - 1).equals(peak)) {
					ChromatogramSelectionSupport.moveRetentionTimeWindow(chromatogramSelection, MoveDirection.RIGHT, 5);
				}
			}
		}
	}

	/**
	 * Index == 0 is the master, > 0 is the reference. -1 is the default.
	 * Use addTypeInfo to append MSD, CSD, WSD.
	 * 
	 * @param chromatogram
	 * @param index
	 * @param addTypeInfo
	 * @return String
	 */
	public static String getReferenceLabel(IChromatogram<?> chromatogram, int index, boolean addTypeInfo) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		HeaderField referencesLabel = HeaderUtil.getHeaderField(preferenceStore.getString(PreferenceSupplier.P_CHROMATOGRAM_REFERENCE_LABEL));
		//
		return getReferenceLabel(chromatogram, referencesLabel, index, addTypeInfo);
	}

	/**
	 * Index == 0 is the master, > 0 is the reference. -1 is the default.
	 * Use addTypeInfo to append MSD, CSD, WSD.
	 * 
	 * @param chromatogram
	 * @param headerField
	 * @param index
	 * @param addTypeInfo
	 * @return String
	 */
	public static String getReferenceLabel(IChromatogram<?> chromatogram, HeaderField headerField, int index, boolean addTypeInfo) {

		/*
		 * Get the information to display.
		 */
		String type = ChromatogramDataSupport.getChromatogramType(chromatogram);
		String description = null;
		//
		switch(headerField) {
			case NAME:
				description = chromatogram.getName();
				break;
			case SAMPLE_NAME:
				description = chromatogram.getSampleName();
				break;
			case DATA_NAME:
				description = chromatogram.getDataName();
				break;
			case SHORT_INFO:
				description = chromatogram.getShortInfo();
				break;
			case SAMPLE_GROUP:
				description = chromatogram.getSampleGroup();
				break;
			case MISC_INFO:
				description = chromatogram.getMiscInfo();
				break;
			default:
				// Do nothing, see check default.
				break;
		}
		/*
		 * Check default
		 */
		if(description != null && description.isBlank()) {
			description = null;
		}
		//
		if(description == null) {
			if(index == -1) {
				description = "Chromatogram";
			} else if(index == 0) {
				description = "Master Chromatogram";
			} else {
				description = "Referenced Chromatogram (" + index + ")";
			}
		}
		/*
		 * Add Type Info
		 */
		if(addTypeInfo) {
			return description + " " + type;
		} else {
			return description;
		}
	}

	private static boolean scanIsInSelectedRange(IScan scan, int startRetentionTime, int stopRetentionTime) {

		int retentionTime = scan.getRetentionTime();
		if(retentionTime >= startRetentionTime && retentionTime <= stopRetentionTime) {
			return true;
		}
		return false;
	}

	private static boolean scanContainsTargets(IScan scan) {

		if(scan != null) {
			return !scan.getTargets().isEmpty();
		}
		return false;
	}
}