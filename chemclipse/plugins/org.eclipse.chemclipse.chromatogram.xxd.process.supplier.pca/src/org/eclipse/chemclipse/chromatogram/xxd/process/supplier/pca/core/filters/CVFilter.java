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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

public class CVFilter implements IFilter {

	private double alpha;
	final private String name = "CV filter";
	private boolean onlySelected;

	public CVFilter() {
		this.onlySelected = true;
		this.alpha = 0.7;
	}

	@Override
	public List<Boolean> filter(List<ISample> samples) {

		int len = samples.get(0).getSampleData().size();
		List<Boolean> selection = new ArrayList<>();
		Map<String, Set<ISample>> samplesByGroupName = PcaUtils.getSamplesByGroupName(samples, false, onlySelected);
		Collection<Set<ISample>> collection = samplesByGroupName.values();
		for(int i = 0; i < len; i++) {
			Collection<SummaryStatistics> categoryData = new ArrayList<>();
			for(Set<ISample> set : collection) {
				SummaryStatistics summaryStatistics = new SummaryStatistics();
				for(ISample sample : set) {
					double d = sample.getSampleData().get(i).getNormalizedData();
					summaryStatistics.addValue(d);
				}
				categoryData.add(summaryStatistics);
			}
			boolean b = categoryData.stream().parallel().allMatch(s -> {
				double m = Math.abs(s.getMean());
				double v = s.getVariance();
				if(m == 0.0) {
					return (v / m) < alpha;
				} else {
					return (v == 0.0) ? true : false;
				}
			});
			selection.add(b);
		}
		return selection;
	}

	public double getAlpha() {

		return alpha;
	}

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return name;
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
