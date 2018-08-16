/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.AbstractPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;

public class RetentionTimeFilter extends AbstractPreprocessing implements IFilter {

	final public static int DESELECT_INTERVAL = 1;
	final public static int SELECT_INTERVAL = 0;
	private int filtrationType;
	private List<int[]> intervals;
	private String selectionResult = "";

	public RetentionTimeFilter() {
		super();
		filtrationType = SELECT_INTERVAL;
		intervals = new ArrayList<>();
		setDataTypeProcessing(DATA_TYPE_PROCESSING.VARIABLES);
	}

	public List<int[]> copyInterval() {

		List<int[]> newIntervals = new ArrayList<>();
		for(int i = 0; i < intervals.size(); i++) {
			newIntervals.add(new int[]{intervals.get(i)[0], intervals.get(i)[1]});
		}
		return newIntervals;
	}

	@Override
	public <V extends IVariable, S extends ISample<? extends ISampleData>> List<Boolean> filter(ISamples<V, S> samples) {

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
