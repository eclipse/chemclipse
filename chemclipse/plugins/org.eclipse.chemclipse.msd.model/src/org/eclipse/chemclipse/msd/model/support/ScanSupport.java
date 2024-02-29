/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
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
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class ScanSupport {

	/*
	 * Returns the TandemMS label, e.g.:
	 * 258 > 159.1 @30
	 * ---
	 * If the ion contains no transition information, the m/z will be returned, e.g.:
	 * 159.1
	 */
	public static String getLabelTandemMS(IIon ion) {

		String label = "";
		if(ion != null) {
			IIonTransition ionTransition = ion.getIonTransition();
			StringBuilder builder = new StringBuilder();
			if(ionTransition != null) {
				builder.append(ionTransition.getQ1Ion());
				builder.append(" > ");
				builder.append(ValueFormat.getDecimalFormatEnglish("0.0").format(ion.getIon()));
				builder.append(" @");
				builder.append((int)ionTransition.getCollisionEnergy());
			} else {
				builder.append(ion.getIon());
			}
			label = builder.toString();
		}
		//
		return label;
	}

	/*
	 * Returns the TandemMS label, e.g.:
	 * 258 > 159 @30
	 */
	public static String getLabelTandemMS(IIonTransition ionTransition) {

		String label = "";
		if(ionTransition != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(ionTransition.getQ1Ion());
			builder.append(" > ");
			builder.append(ValueFormat.getDecimalFormatEnglish("0.0").format(ionTransition.getQ3Ion()));
			builder.append(" @");
			builder.append((int)ionTransition.getCollisionEnergy());
			label = builder.toString();
		}
		//
		return label;
	}

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

		return extractTracesText(scanMSD, maxCopyTraces, true);
	}

	public static String extractTracesText(IScanMSD scanMSD, int maxCopyTraces, boolean sortTraces) {

		List<Integer> traces = extractTracesList(scanMSD, maxCopyTraces, sortTraces);
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

		return extractTracesList(scanMSD, maxCopyTraces, true);
	}

	public static List<Integer> extractTracesList(IScanMSD scanMSD, int maxCopyTraces, boolean sortTraces) {

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
		 * Sort the traces ascending on demand.
		 */
		if(sortTraces) {
			Collections.sort(traces);
		}
		//
		return traces;
	}
}