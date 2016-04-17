/*******************************************************************************
 * Copyright (c) 2011, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.core.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.exceptions.FilterException;
import org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.rtshifter.settings.ISupplierFilterSettings;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;

public class RTShifter {

	/*
	 * Use only static methods.
	 */
	private RTShifter() {
	}

	public static void shiftRetentionTimes(IChromatogramSelection chromatogramSelection, ISupplierFilterSettings supplierFilterSettings) throws FilterException {

		if(chromatogramSelection == null || chromatogramSelection.getChromatogram() == null) {
			throw new FilterException("The chromatogram must not be null.");
		}
		//
		List<Integer> scansToRemove = adjustRetentionTimesAndReturnScansToRemove(chromatogramSelection, supplierFilterSettings);
		removeMarkedScans(chromatogramSelection, scansToRemove);
		setScanDelay(chromatogramSelection);
	}

	private static List<Integer> adjustRetentionTimesAndReturnScansToRemove(IChromatogramSelection chromatogramSelection, ISupplierFilterSettings supplierFilterSettings) {

		boolean isShiftAllScans = supplierFilterSettings.isShiftAllScans();
		int millisecondToShift = supplierFilterSettings.getMillisecondsToShift();
		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
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

	private static int getRetentionTimeLeftBorder(IChromatogram chromatogram, int startScan) {

		int retentionTime = 0;
		int scanLeftBorder = startScan - 1;
		IScan scan = chromatogram.getScan(scanLeftBorder);
		if(scan != null) {
			retentionTime = scan.getRetentionTime();
		}
		return retentionTime;
	}

	private static int getRetentionTimeRightBorder(IChromatogram chromatogram, int stopScan) {

		int retentionTime = Integer.MAX_VALUE;
		int scanRightBorder = stopScan + 1;
		IScan scan = chromatogram.getScan(scanRightBorder);
		if(scan != null) {
			retentionTime = scan.getRetentionTime();
		}
		return retentionTime;
	}

	private static IChromatogram removeMarkedScans(IChromatogramSelection chromatogramSelection, List<Integer> scansToRemove) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int counter = 0;
		for(int scan : scansToRemove) {
			chromatogram.removeScan(scan - counter);
			counter++;
		}
		return chromatogram;
	}

	private static void setScanDelay(IChromatogramSelection chromatogramSelection) throws FilterException {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		if(chromatogram.getNumberOfScans() <= 0) {
			throw new FilterException("There is no scan available.");
		}
		IScan scanRecord = chromatogram.getScan(1);
		int scanDelay = scanRecord.getRetentionTime();
		chromatogram.setScanDelay(scanDelay);
	}

	private static int calculateNewRetentionTime(IScan scanRecord, int millisecondToShift) {

		int actualRetentionTime = scanRecord.getRetentionTime();
		return actualRetentionTime + millisecondToShift;
	}
}
