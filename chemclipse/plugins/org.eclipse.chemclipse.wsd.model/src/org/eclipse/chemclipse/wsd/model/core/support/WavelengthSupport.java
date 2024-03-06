/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class WavelengthSupport {

	public static String extractTracesText(IScanWSD scanWSD, int maxCopyTraces) {

		return extractTracesText(scanWSD, maxCopyTraces, true);
	}

	public static String extractTracesText(IScanWSD scanWSD, int maxCopyTraces, boolean sortTraces) {

		List<Integer> traces = extractTracesList(scanWSD, maxCopyTraces, sortTraces);
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

	public static List<Integer> extractTracesList(IScanWSD scanWSD, int maxCopyTraces, boolean sortTraces) {

		List<Integer> traces = new ArrayList<>();
		if(scanWSD != null) {
			List<IScanSignalWSD> scanSignals = new ArrayList<>(scanWSD.getScanSignals());
			Collections.sort(scanSignals, (i1, i2) -> Float.compare(i2.getAbsorbance(), i1.getAbsorbance()));
			//
			exitloop:
			for(IScanSignalWSD scanSignal : scanSignals) {
				/*
				 * Add the trace.
				 */
				int trace = (int)Math.round(scanSignal.getWavelength());
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
