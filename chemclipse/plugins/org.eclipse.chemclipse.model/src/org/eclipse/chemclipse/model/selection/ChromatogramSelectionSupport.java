/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.selection;

import org.eclipse.chemclipse.model.core.IChromatogram;

public class ChromatogramSelectionSupport {

	private ChromatogramSelectionSupport() {
	}

	@SuppressWarnings("rawtypes")
	public static void moveRetentionTimeWindow(IChromatogramSelection chromatogramSelection, MoveDirection moveDirection, int retentionTimeDivider) {

		int startRetentionTime = chromatogramSelection.getStartRetentionTime();
		int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
		int retentionTimeMoveWindow = (stopRetentionTime - startRetentionTime) / retentionTimeDivider;
		//
		int startRetentionTimeNew = (MoveDirection.LEFT.equals(moveDirection)) ? startRetentionTime - retentionTimeMoveWindow : startRetentionTime + retentionTimeMoveWindow;
		int stopRetentionTimeNew = (MoveDirection.LEFT.equals(moveDirection)) ? stopRetentionTime - retentionTimeMoveWindow : stopRetentionTime + retentionTimeMoveWindow;
		//
		startRetentionTimeNew = getValidatedStartRetentionTime(chromatogramSelection, startRetentionTimeNew);
		stopRetentionTimeNew = getValidatedStopRetentionTime(chromatogramSelection, stopRetentionTimeNew);
		chromatogramSelection.setRanges(startRetentionTimeNew, stopRetentionTimeNew, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
	}

	@SuppressWarnings("rawtypes")
	public static int getValidatedStartRetentionTime(IChromatogramSelection chromatogramSelection, int startRetentionTimeNew) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int minRetentionTime = chromatogram.getStartRetentionTime();
		if(startRetentionTimeNew < minRetentionTime) {
			return minRetentionTime;
		} else {
			return startRetentionTimeNew;
		}
	}

	@SuppressWarnings("rawtypes")
	public static int getValidatedStopRetentionTime(IChromatogramSelection chromatogramSelection, int stopRetentionTimeNew) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int maxRetentionTime = chromatogram.getStopRetentionTime();
		if(stopRetentionTimeNew > maxRetentionTime) {
			return maxRetentionTime;
		} else {
			return stopRetentionTimeNew;
		}
	}
}
