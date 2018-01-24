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

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class AnovaFilter implements IFilter {

	private double alpha;
	final private String name = "Anova filter";
	private boolean onlySelected;
	private String selectionResult = "";

	public AnovaFilter() {
		alpha = 0.05;
		onlySelected = true;
	}

	@Override
	public <V extends IVariable, S extends ISample<? extends ISampleData>> List<Boolean> filter(ISamples<V, S> samples) {

		List<S> samplesList = samples.getSampleList();
		List<V> variables = samples.getVariables();
		List<Boolean> selection = new ArrayList<>(variables.size());
		for(int i = 0; i < variables.size(); i++) {
			selection.add(false);
		}
		Map<String, Set<S>> samplesByGroupNameMap = PcaUtils.getSamplesByGroupName(samplesList, false, onlySelected);
		Collection<Set<S>> samplesByGroupName = samplesByGroupNameMap.values();
		try {
			for(int i = 0; i < selection.size(); i++) {
				OneWayAnova oneWayAnova = new OneWayAnova();
				Collection<SummaryStatistics> categoryData = new ArrayList<>();
				for(Set<S> group : samplesByGroupName) {
					SummaryStatistics summaryStatistics = new SummaryStatistics();
					for(ISample<?> sample : group) {
						double d = sample.getSampleData().get(i).getModifiedData();
						summaryStatistics.addValue(d);
					}
					categoryData.add(summaryStatistics);
				}
				double pValue = oneWayAnova.anovaPValue(categoryData, true);
				selection.set(i, (pValue < alpha));
			}
			selectionResult = IFilter.getNumberSelectedRow(selection);
		} catch(Exception e) {
			selectionResult = IFilter.getErrorMessage(e.getMessage());
		}
		return selection;
	}

	public double getAlpha() {

		return alpha;
	}

	@Override
	public String getDescription() {

		return "P-Value is less then " + alpha * 100 + "%";
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
