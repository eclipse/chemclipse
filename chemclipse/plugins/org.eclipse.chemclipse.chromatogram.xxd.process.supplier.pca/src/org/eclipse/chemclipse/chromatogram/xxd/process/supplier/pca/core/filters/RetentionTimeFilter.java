/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;

public class RetentionTimeFilter implements IFilter {

	final public static int DESELECT_INTERVAL = 1;
	final public static int SELECT_INTERVAL = 0;
	private int filtrationType;
	private List<int[]> intervals;
	private boolean onlySelected;

	public RetentionTimeFilter() {
		onlySelected = true;
		filtrationType = SELECT_INTERVAL;
		intervals = new ArrayList<>();
	}

	@Override
	public List<Boolean> filter(IPcaResults pcaResults) {

		List<Integer> retentionTime = pcaResults.getExtractedRetentionTimes();
		List<Boolean> filter = new ArrayList<>(retentionTime.size());
		boolean set;
		if(SELECT_INTERVAL == filtrationType) {
			set = true;
		} else {
			set = false;
		}
		for(int i = 0; i < retentionTime.size(); i++) {
			filter.add(!set);
		}
		for(int[] interval : intervals) {
			int begin = interval[0];
			int finish = interval[1];
			for(int i = 0; i < retentionTime.size(); i++) {
				int ret = retentionTime.get(i);
				if(ret >= begin && ret <= finish) {
					filter.set(i, set);
				}
			}
		}
		return filter;
	}

	@Override
	public String getDescription() {

		return "";
	}

	public int getFiltrationType() {

		return filtrationType;
	}

	public List<int[]> getIntervals() {

		return intervals;
	}

	@Override
	public String getName() {

		return "Retention time filter";
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	public void setFiltrationType(int filtrationType) {

		if(filtrationType >= 0 && filtrationType <= 1) {
			this.filtrationType = filtrationType;
		}
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}
}
