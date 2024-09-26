/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.tsd.converter.core.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.support.traces.TraceFactory;
import org.eclipse.chemclipse.support.traces.TraceGenericDelta;
import org.eclipse.chemclipse.support.traces.TraceHighResMSD;
import org.eclipse.chemclipse.support.traces.TraceHighResWSD;

/*
 * ------------
 * Traces
 * ------------
 * 94.05±0.05 Qualifier
 * 150.15±0.05 Quantifier
 * ---
 * 118.05±0.05 Qualifier
 * 146.05±0.05 Quantifier
 * ---
 * "94.05±0.05, 118.05±0.05, 146.05±0.05, 150.15±0.05"
 * ---
 */
public class TraceRangeMatcher {

	private int retentionTimeMin = 0;
	private int retentionTimeMax = Integer.MAX_VALUE;
	private List<TraceRange> traceRanges = new ArrayList<>();
	private boolean parseFully = false;

	public boolean isParseFully() {

		return parseFully;
	}

	public void setParseFully(boolean parseFully) {

		this.parseFully = parseFully;
	}

	public void addHighResMSD(String traces, int retentionTimeStart, int retentionTimeStop) {

		add(traces, retentionTimeStart, retentionTimeStop, TraceHighResMSD.class);
	}

	public void addHighResWSD(String traces, int retentionTimeStart, int retentionTimeStop) {

		add(traces, retentionTimeStart, retentionTimeStop, TraceHighResWSD.class);
	}

	/**
	 * The value map is either a index, mz or index, wavelength map.
	 * 
	 * @param valueMap
	 */
	public void calculateTraceIndices(Map<Integer, Double> valueMap) {

		for(Map.Entry<Integer, Double> entry : valueMap.entrySet()) {
			int index = entry.getKey();
			double value = entry.getValue();
			for(TraceRange traceRange : traceRanges) {
				for(TraceGenericDelta trace : traceRange.getTraces()) {
					if(value >= trace.getStartValue() && value <= trace.getStopValue()) {
						traceRange.getTraceIndices().add(index);
					}
				}
			}
		}
	}

	public List<TraceRange> getTraceRanges(int retentionTime) {

		List<TraceRange> selection = new ArrayList<>();
		if(retentionTime >= retentionTimeMin && retentionTime <= retentionTimeMax) {
			for(TraceRange traceRange : traceRanges) {
				if(retentionTime >= traceRange.getRetentionTimeStart() && retentionTime <= traceRange.getRetentionTimeStop()) {
					selection.add(traceRange);
				}
			}
		}
		//
		return selection;
	}

	public Set<Integer> getTraceIndices(int retentionTime, List<TraceRange> traceRanges, int start, int stop) {

		Set<Integer> traceIndices = new HashSet<>();
		for(TraceRange traceRange : traceRanges) {
			if(retentionTime >= traceRange.getRetentionTimeStart() && retentionTime <= traceRange.getRetentionTimeStop()) {
				if(useIndices(traceRange.getTraceIndices(), start, stop)) {
					traceIndices.addAll(traceRange.getTraceIndices());
				}
			}
		}
		//
		return traceIndices;
	}

	private boolean useIndices(Set<Integer> traceIndices, int start, int stop) {

		if(traceIndices.isEmpty()) {
			return true;
		} else {
			for(int i = start; i <= stop; i++) {
				if(traceIndices.contains(i)) {
					return true;
				}
			}
		}
		//
		return false;
	}

	private void add(String traces, int retentionTimeStart, int retentionTimeStop, Class<? extends TraceGenericDelta> clazz) {

		List<? extends TraceGenericDelta> tracesGenericDelta = TraceFactory.parseTraces(traces, clazz);
		if(!tracesGenericDelta.isEmpty()) {
			/*
			 * TraceRange
			 */
			TraceRange traceRange = new TraceRange();
			traceRange.setRetentionTimeStart(retentionTimeStart);
			traceRange.setRetentionTimeStop(retentionTimeStop);
			traceRange.getTraces().addAll(tracesGenericDelta);
			traceRanges.add(traceRange);
			/*
			 * Min/Max
			 */
			retentionTimeMin = Math.min(retentionTimeMin, retentionTimeStart);
			retentionTimeMax = Math.max(retentionTimeMax, retentionTimeStop);
		}
	}
}