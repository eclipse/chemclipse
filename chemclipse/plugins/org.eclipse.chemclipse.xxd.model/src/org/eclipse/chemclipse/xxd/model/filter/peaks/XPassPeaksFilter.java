/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class XPassPeaksFilter {

	public static <X extends IPeak> void filterPeaks(CRUDListener<X, IPeakModel> listener, MessageConsumer messageConsumer, int keepPeaks, boolean highPass, IProgressMonitor monitor) throws IllegalArgumentException {

		String description = (highPass ? "High " : "Low ") + " Pass Peak(s)";
		/*
		 * Extract the peak(s) and check the area.
		 */
		boolean peaksAreIntegrated = true;
		List<X> peaks = new ArrayList<>();
		//
		Collection<X> read = listener.read();
		for(X peak : read) {
			peaks.add(peak);
			if(peak.getIntegratedArea() == 0.0d) {
				peaksAreIntegrated = false;
			}
		}
		/*
		 * Sort the peak(s)
		 */
		if(peaksAreIntegrated) {
			messageConsumer.addInfoMessage(description, "The peak area is used to filter the peak(s).");
			if(highPass) {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p2.getIntegratedArea(), p1.getIntegratedArea()));
			} else {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p1.getIntegratedArea(), p2.getIntegratedArea()));
			}
		} else {
			messageConsumer.addWarnMessage(description, "At least one peak is not integrated. Switch to peak height at maximum.");
			if(highPass) {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p2.getPeakModel().getPeakMaximum().getTotalSignal(), p1.getPeakModel().getPeakMaximum().getTotalSignal()));
			} else {
				Collections.sort(peaks, (p1, p2) -> Double.compare(p1.getPeakModel().getPeakMaximum().getTotalSignal(), p2.getPeakModel().getPeakMaximum().getTotalSignal()));
			}
		}
		/*
		 * Delete the peak(s).
		 */
		SubMonitor subMonitor = SubMonitor.convert(monitor, read.size());
		for(int i = 0; i < peaks.size(); i++) {
			if(i >= keepPeaks) {
				X peak = peaks.get(i);
				listener.delete(peak);
			}
			subMonitor.worked(1);
		}
	}
}
