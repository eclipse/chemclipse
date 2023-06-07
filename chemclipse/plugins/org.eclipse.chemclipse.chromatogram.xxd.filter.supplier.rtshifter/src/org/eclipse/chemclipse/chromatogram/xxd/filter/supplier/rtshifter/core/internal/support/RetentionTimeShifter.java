/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.FilterSettingsShift;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class RetentionTimeShifter extends AbstractRetentionTimeModifier {

	/*
	 * Use only static methods.
	 */
	private RetentionTimeShifter() {

	}

	public static void shiftRetentionTimes(IChromatogramSelection<?, ?>chromatogramSelection, FilterSettingsShift filterSettings) throws FilterException {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			throw new FilterException("The chromatogram must not be null.");
		}
		//
		List<Integer> scansToRemove = adjustRetentionTimesAndReturnScansToRemove(chromatogramSelection, filterSettings);
		removeMarkedScans(chromatogramSelection, scansToRemove);
		adjustScanDelayAndRetentionTimeRange(chromatogramSelection);
	}

	private static List<Integer> adjustRetentionTimesAndReturnScansToRemove(IChromatogramSelection<?, ?> chromatogramSelection, FilterSettingsShift filterSettings) {

		boolean isShiftAllScans = filterSettings.isShiftAllScans();
		int millisecondToShift = filterSettings.getMillisecondsShift();
		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		//
		int startScan;
		int stopScan;
		if(isShiftAllScans) {
			startScan = 1;
			stopScan = chromatogram.getNumberOfScans();
		} else {
			startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
			stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		}
		//
		int retentionTimeLeftBorder = getRetentionTimeLeftBorder(chromatogram, startScan);
		int retentionTimeRightBorder = getRetentionTimeRightBorder(chromatogram, stopScan);
		//
		List<Integer> scansToRemove = new ArrayList<Integer>();
		/*
		 * Adjust the retention times.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IScan scanRecord = chromatogram.getScan(scan);
			int retentionTimeNew = calculateNewRetentionTime(scanRecord, millisecondToShift);
			/*
			 * Remove the scan if less or equal than zero.
			 * But don't remove it now, cause it would change the scan order.
			 */
			if(retentionTimeNew <= retentionTimeLeftBorder || retentionTimeNew >= retentionTimeRightBorder) {
				scansToRemove.add(scan);
			} else {
				scanRecord.setRetentionTime(retentionTimeNew);
			}
		}
		/*
		 * Remove the marked scans.
		 */
		return scansToRemove;
	}

	private static int getRetentionTimeLeftBorder(IChromatogram<?> chromatogram, int startScan) {

		int retentionTime = 0;
		int scanLeftBorder = startScan - 1;
		IScan scan = chromatogram.getScan(scanLeftBorder);
		if(scan != null) {
			retentionTime = scan.getRetentionTime();
		}
		return retentionTime;
	}

	private static int getRetentionTimeRightBorder(IChromatogram<?> chromatogram, int stopScan) {

		int retentionTime = Integer.MAX_VALUE;
		int scanRightBorder = stopScan + 1;
		IScan scan = chromatogram.getScan(scanRightBorder);
		if(scan != null) {
			retentionTime = scan.getRetentionTime();
		}
		return retentionTime;
	}

	private static IChromatogram<?> removeMarkedScans(IChromatogramSelection<?, ?> chromatogramSelection, List<Integer> scansToRemove) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		int counter = 0;
		for(int scan : scansToRemove) {
			chromatogram.removeScan(scan - counter);
			counter++;
		}
		return chromatogram;
	}

	private static int calculateNewRetentionTime(IScan scanRecord, int millisecondToShift) {

		int actualRetentionTime = scanRecord.getRetentionTime();
		return actualRetentionTime + millisecondToShift;
	}
}
