/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.statistics.IRetentionTime;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class RetentionTimeFilter extends AbstractFilter implements IFilter {

	public static final int DESELECT_INTERVAL = 1;
	public static final int SELECT_INTERVAL = 0;
	//
	private int filtrationType;
	private List<int[]> intervals;
	private String selectionResult = "";

	public RetentionTimeFilter() {

		super(DataTypeProcessing.VARIABLES);
		filtrationType = SELECT_INTERVAL;
		intervals = new ArrayList<>();
	}

	public List<int[]> copyInterval() {

		List<int[]> newIntervals = new ArrayList<>();
		for(int i = 0; i < intervals.size(); i++) {
			newIntervals.add(new int[]{intervals.get(i)[0], intervals.get(i)[1]});
		}
		return newIntervals;
	}

	@Override
	public <V extends IVariable, S extends ISample> List<Boolean> filter(ISamples<V, S> samples) {

		List<V> variables = samples.getVariables();
		List<Boolean> selection = new ArrayList<>(variables.size());
		boolean set;
		if(SELECT_INTERVAL == filtrationType) {
			set = true;
		} else {
			set = false;
		}
		// check if list contains retention times
		for(int i = 0; i < variables.size(); i++) {
			selection.add(!set);
		}
		if(!IFilter.isRetentionTimes(variables)) {
			return selection;
		}
		for(int[] interval : intervals) {
			int begin = interval[0];
			int finish = interval[1];
			for(int i = 0; i < variables.size(); i++) {
				int ret = ((IRetentionTime)variables.get(i)).getRetentionTime();
				if(ret >= begin && ret <= finish) {
					selection.set(i, set);
				}
			}
		}
		selectionResult = IFilter.getNumberSelectedRow(selection);
		return selection;
	}

	@Override
	public String getDescription() {

		StringBuilder sb = new StringBuilder();
		if(filtrationType == SELECT_INTERVAL) {
			sb.append("Selected intervals: ");
		} else {
			sb.append("Deselected intevals: ");
		}
		for(int i = 0; i < intervals.size(); i++) {
			sb.append("[");
			sb.append(Double.toString(intervals.get(i)[0] / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			sb.append(", ");
			sb.append(Double.toString(intervals.get(i)[1] / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
			sb.append("] ");
		}
		return sb.toString();
	}

	public int getFiltrationType() {

		return filtrationType;
	}

	public List<int[]> getIntervals() {

		return intervals;
	}

	@Override
	public String getName() {

		return "Retention time intervals filter";
	}

	@Override
	public String getSelectionResult() {

		return selectionResult;
	}

	public void setFiltrationType(int filtrationType) {

		if(filtrationType >= 0 && filtrationType <= 1) {
			this.filtrationType = filtrationType;
		}
	}
}
