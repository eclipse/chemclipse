/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.core.internal.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.rtshifter.exceptions.FilterException;

public class RTShifter {

	/*
	 * Use only static methods.
	 */
	private RTShifter() {
	}

	public static void shiftRetentionTimes(IChromatogram chromatogram, int millisecondToShift) throws FilterException {

		if(chromatogram == null) {
			throw new FilterException("The chromatogram must not be null.");
		}
		List<Integer> scansToRemove = adjustRetentionTimesAndReturnScansToRemove(chromatogram, millisecondToShift);
		removeMarkedScans(chromatogram, scansToRemove);
		setScanDelay(chromatogram);
	}

	private static List<Integer> adjustRetentionTimesAndReturnScansToRemove(IChromatogram chromatogram, int millisecondToShift) {

		IScan scanRecord;
		int numberOfScans = chromatogram.getNumberOfScans();
		List<Integer> scansToRemove = new ArrayList<Integer>();
		/*
		 * Adjust the retention times.
		 */
		for(int scan = 1; scan <= numberOfScans; scan++) {
			scanRecord = chromatogram.getScan(scan);
			int retentionTimeNew = calculateNewRetentionTime(scanRecord, millisecondToShift);
			/*
			 * Remove the scan if less or equal than zero.
			 * But don't remove it now, cause it would change the scan order.
			 */
			if(retentionTimeNew <= 0) {
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

	private static IChromatogram removeMarkedScans(IChromatogram chromatogram, List<Integer> scansToRemove) {

		int counter = 0;
		for(int scan : scansToRemove) {
			chromatogram.removeScan(scan - counter);
			counter++;
		}
		return chromatogram;
	}

	private static void setScanDelay(IChromatogram chromatogram) throws FilterException {

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
