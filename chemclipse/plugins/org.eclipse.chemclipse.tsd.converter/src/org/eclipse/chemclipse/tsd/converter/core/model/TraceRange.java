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
import java.util.Set;

import org.eclipse.chemclipse.support.traces.TraceGenericDelta;

public class TraceRange {

	private int retentionTimeStart = 0;
	private int retentionTimeStop = Integer.MAX_VALUE;
	private List<TraceGenericDelta> traces = new ArrayList<>();
	private Set<Integer> traceIndices = new HashSet<>();

	public int getRetentionTimeStart() {

		return retentionTimeStart;
	}

	public void setRetentionTimeStart(int retentionTimeStart) {

		this.retentionTimeStart = retentionTimeStart;
	}

	public int getRetentionTimeStop() {

		return retentionTimeStop;
	}

	public void setRetentionTimeStop(int retentionTimeStop) {

		this.retentionTimeStop = retentionTimeStop;
	}

	public List<TraceGenericDelta> getTraces() {

		return traces;
	}

	public Set<Integer> getTraceIndices() {

		return traceIndices;
	}
}