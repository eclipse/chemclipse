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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

public class CVFilter implements IFilter {

	private double alpha;
	final private String name = "CV filter";
	private boolean onlySelected;
	private String selectionResult = "";

	public CVFilter() {
		this.onlySelected = true;
		this.alpha = 0.7;
	}

	@Override
	public List<Boolean> filter(IPcaResults pcaResults) {

		List<ISample> samples = pcaResults.getSampleList();
		List<Boolean> selection = new ArrayList<>();
		List<Boolean> selectedRetentionTimes = pcaResults.isSelectedRetentionTimes();
		for(int i = 0; i < selectedRetentionTimes.size(); i++) {
			selection.add(false);
		}
		Map<String, Set<ISample>> samplesByGroupNameMap = PcaUtils.getSamplesByGroupName(samples, false, onlySelected);
		Collection<Set<ISample>> samplesByGroupName = samplesByGroupNameMap.values();
		if(!samplesByGroupName.isEmpty()) {
			for(int i = 0; i < selectedRetentionTimes.size(); i++) {
				Collection<SummaryStatistics> categoryData = new ArrayList<>();
				for(Set<ISample> set : samplesByGroupName) {
					SummaryStatistics summaryStatistics = new SummaryStatistics();
					for(ISample sample : set) {
						double d = sample.getSampleData().get(i).getNormalizedData();
						summaryStatistics.addValue(d);
					}
					categoryData.add(summaryStatistics);
				}
				boolean result = categoryData.stream().parallel().allMatch(s -> {
					double m = Math.abs(s.getMean());
					double d = s.getStandardDeviation();
					if(m != 0.0) {
						return (d / m) < alpha;
					} else {
						return (d == 0.0) ? true : false;
					}
				});
				selection.set(i, result);
			}
		}
		selectionResult = getNumberSelectedRow(selection);
		return selection;
	}

	public double getAlpha() {

		return alpha;
	}

	@Override
	public String getDescription() {

		return "Noise for each group is less than " + alpha * 100 + "%";
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getSelectionResult() {

		return selectionResult;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	public void setAlpha(double alpha) {

		this.alpha = alpha;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}
}
