/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.xir.model.core.IScanISD;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;

public class WavenumberSupport {

	public static String extractTracesText(IScanISD scanISD, int maxCopyTraces) {

		return extractTracesText(scanISD, maxCopyTraces, true);
	}

	public static String extractTracesText(IScanISD scanISD, int maxCopyTraces, boolean sortTraces) {

		List<Integer> traces = extractTracesList(scanISD, maxCopyTraces, sortTraces);
		Iterator<Integer> iterator = traces.iterator();
		StringBuilder builder = new StringBuilder();
		//
		while(iterator.hasNext()) {
			builder.append(iterator.next());
			if(iterator.hasNext()) {
				builder.append(" ");
			}
		}
		//
		return builder.toString();
	}

	public static List<Integer> extractTracesList(IScanISD scanISD, int maxCopyTraces, boolean sortTraces) {

		List<Integer> traces = new ArrayList<>();
		if(scanISD != null) {
			List<ISignalXIR> scanSignals = new ArrayList<>(scanISD.getProcessedSignals());
			Collections.sort(scanSignals, (i1, i2) -> Double.compare(i2.getIntensity(), i1.getIntensity()));
			//
			exitloop:
			for(ISignalXIR scanSignal : scanSignals) {
				/*
				 * Add the trace.
				 */
				int trace = CombinedScanCalculator.getWavenumber(scanSignal.getWavenumber());
				if(!traces.contains(trace)) {
					traces.add(trace);
				}
				//
				if(traces.size() >= maxCopyTraces) {
					break exitloop;
				}
			}
		}
		/*
		 * Sort the traces ascending on demand.
		 */
		if(sortTraces) {
			Collections.sort(traces);
		}
		//
		return traces;
	}
}