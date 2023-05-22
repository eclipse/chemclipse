/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - reduce compiler warnings
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.PcaUtils;

public class CVFilter extends AbstractFilter implements IFilter {

	private static final String NAME = "CV filter";
	//
	private double alpha;
	private String selectionResult = "";

	public CVFilter() {

		super(DataTypeProcessing.RAW_DATA);
		this.alpha = 0.7;
	}

	@Override
	public <V extends IVariable, S extends ISample> List<Boolean> filter(ISamples<V, S> samples) {

		List<S> samplesList = selectSamples(samples);
		List<Boolean> selection = new ArrayList<>();
		List<V> variables = samples.getVariables();
		for(int i = 0; i < variables.size(); i++) {
			selection.add(false);
		}
		Map<String, Set<S>> samplesByGroupNameMap = PcaUtils.getSamplesByGroupName(samplesList, false, isOnlySelected());
		Collection<Set<S>> samplesByGroupName = samplesByGroupNameMap.values();
		if(!samplesByGroupName.isEmpty()) {
			for(int i = 0; i < variables.size(); i++) {
				Collection<SummaryStatistics> categoryData = new ArrayList<>();
				for(Set<S> set : samplesByGroupName) {
					SummaryStatistics summaryStatistics = new SummaryStatistics();
					for(ISample sample : set) {
						double d = getData(sample.getSampleData().get(i));
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
						return (d == 0.0);
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

		return NAME;
	}

	@Override
	public String getSelectionResult() {

		return selectionResult;
	}

	public void setAlpha(double alpha) {

		this.alpha = alpha;
	}
}
