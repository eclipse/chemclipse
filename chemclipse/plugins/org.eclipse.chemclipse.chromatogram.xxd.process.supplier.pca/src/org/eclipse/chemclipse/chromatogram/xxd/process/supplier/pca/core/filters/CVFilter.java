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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

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
	public <V extends IVariable, S extends ISample<? extends ISampleData>> List<Boolean> filter(ISamples<V, S> samples) {

		List<S> samplesList = samples.getSampleList().stream().filter(s -> s.isSelected() || !onlySelected).collect(Collectors.toList());
		List<Boolean> selection = new ArrayList<>();
		List<V> variables = samples.getVariables();
		for(int i = 0; i < variables.size(); i++) {
			selection.add(false);
		}
		Map<String, Set<S>> samplesByGroupNameMap = PcaUtils.getSamplesByGroupName(samplesList, false, onlySelected);
		Collection<Set<S>> samplesByGroupName = samplesByGroupNameMap.values();
		if(!samplesByGroupName.isEmpty()) {
			for(int i = 0; i < variables.size(); i++) {
				Collection<SummaryStatistics> categoryData = new ArrayList<>();
				for(Set<S> set : samplesByGroupName) {
					SummaryStatistics summaryStatistics = new SummaryStatistics();
					for(ISample<?> sample : set) {
						double d = sample.getSampleData().get(i).getModifiedData();
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
		selectionResult = IFilter.getNumberSelectedRow(selection);
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
