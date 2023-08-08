/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.xxd.filter.peaks.settings.PeakFilterOption;
import org.eclipse.core.runtime.SubMonitor;

public class XPassPeaksFilter {

	public static List<IPeak> filterPeaks(Collection<IPeak> filterItems, ProcessExecutionContext context, PeakFilterOption peakFilterOption, int keepPeaks, boolean highPass) {

		String description = (highPass ? "High " : "Low ") + " Pass Peaks";
		/*
		 * Extract the peaks and check the area
		 * as well as the peak filter option.
		 */
		boolean usePeakArea = true;
		List<IPeak> peaks = new ArrayList<>(filterItems);
		//
		switch(peakFilterOption) {
			case HEIGHT:
				usePeakArea = false;
				break;
			default:
				usePeakArea = isUsePeakArea(peaks);
				break;
		}
		/*
		 * Sort the peaks
		 */
		if(usePeakArea) {
			context.addInfoMessage(description, "The peak area is used to filter the peaks.");
			if(highPass) {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p2.getIntegratedArea(), p1.getIntegratedArea()));
			} else {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p1.getIntegratedArea(), p2.getIntegratedArea()));
			}
		} else {
			context.addInfoMessage(description, "The peak height is used to filter the peaks.");
			if(highPass) {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p2.getPeakModel().getPeakMaximum().getTotalSignal(), p1.getPeakModel().getPeakMaximum().getTotalSignal()));
			} else {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p1.getPeakModel().getPeakMaximum().getTotalSignal(), p2.getPeakModel().getPeakMaximum().getTotalSignal()));
			}
		}
		/*
		 * Peaks for deletion.
		 */
		List<IPeak> peaksToDelete = new ArrayList<>();
		SubMonitor subMonitor = SubMonitor.convert(context.getProgressMonitor(), peaks.size());
		for(int i = 0; i < peaks.size(); i++) {
			if(i >= keepPeaks) {
				IPeak peak = peaks.get(i);
				peaksToDelete.add(peak);
			}
			subMonitor.worked(1);
		}
		//
		return peaksToDelete;
	}

	private static boolean isUsePeakArea(List<IPeak> peaks) {

		for(IPeak peak : peaks) {
			if(peak.getIntegratedArea() == 0.0d) {
				return false;
			}
		}
		//
		return true;
	}
}