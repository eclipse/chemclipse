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
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

public class AnovaFilter implements IFilter {

	private double alpha;
	final private String name = "Anova filter";
	private boolean onlySelected;

	public AnovaFilter() {
		alpha = 0.05;
		onlySelected = true;
	}

	@Override
	public List<Boolean> filter(IPcaResults pcaResults) {

		List<ISample> samples = pcaResults.getSampleList();
		List<Boolean> selection = new ArrayList<>();
		Map<String, Set<ISample>> samplesByGroupName = PcaUtils.getSamplesByGroupName(samples, false, onlySelected);
		Collection<Set<ISample>> collection = samplesByGroupName.values();
		for(int i = 0; i < samples.get(0).getSampleData().size(); i++) {
			OneWayAnova oneWayAnova = new OneWayAnova();
			Collection<SummaryStatistics> categoryData = new ArrayList<>();
			for(Set<ISample> set : collection) {
				SummaryStatistics summaryStatistics = new SummaryStatistics();
				for(ISample sample : set) {
					double d = sample.getSampleData().get(i).getNormalizedData();
					summaryStatistics.addValue(d);
				}
				categoryData.add(summaryStatistics);
			}
			double pValue = oneWayAnova.anovaPValue(categoryData, true);
			selection.add((pValue < alpha));
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
