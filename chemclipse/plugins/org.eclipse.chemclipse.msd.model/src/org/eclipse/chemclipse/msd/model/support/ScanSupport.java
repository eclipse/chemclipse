/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class ScanSupport {

	public static String getSortedTraces(IScanMSD scanMSD) {

		StringBuilder builder = new StringBuilder();
		/*
		 * Get the ions.
		 */
		List<Integer> ions = new ArrayList<Integer>();
		for(IIon ion : scanMSD.getIons()) {
			ions.add(AbstractIon.getIon(ion.getIon()));
		}
		/*
		 * Get the list.
		 */
		Collections.sort(ions);
		Iterator<Integer> iterator = ions.iterator();
		while(iterator.hasNext()) {
			builder.append(Integer.toString(iterator.next()));
			if(iterator.hasNext()) {
				builder.append(" ");
			}
		}
		//
		return builder.toString();
	}

	public static String extractTracesText(IScanMSD scanMSD, int maxCopyTraces) {

		List<Integer> traces = extractTracesList(scanMSD, maxCopyTraces);
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

	public static List<Integer> extractTracesList(IScanMSD scanMSD, int maxCopyTraces) {

		List<Integer> traces = new ArrayList<>();
		if(scanMSD != null) {
			IScanMSD massSpectrum = scanMSD.getOptimizedMassSpectrum() != null ? scanMSD.getOptimizedMassSpectrum() : scanMSD;
			List<IIon> ions = new ArrayList<>(massSpectrum.getIons());
			Collections.sort(ions, (i1, i2) -> Float.compare(i2.getAbundance(), i1.getAbundance()));
			//
			exitloop:
			for(IIon ion : ions) {
				/*
				 * Add the trace.
				 */
				int trace = AbstractIon.getIon(ion.getIon());
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
		 * Sort the traces ascending.
		 */
		Collections.sort(traces);
		return traces;
	}
}