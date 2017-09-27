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
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;

public class RetentionTime2Filter implements IFilter {

	private boolean inverse;
	private boolean onlySelected = true;
	private List<IRetentionTime> retentionTimes;
	private String selectionResult = "";

	public RetentionTime2Filter(List<IRetentionTime> retentionTimes, boolean inverse) {
		this.retentionTimes = new ArrayList<>(retentionTimes);
		this.inverse = inverse;
	}

	@Override
	public List<Boolean> filter(ISamples samples) {

		int size = samples.getExtractedRetentionTimes().size();
		List<Boolean> seletions = new ArrayList<>(size);
		boolean selected = !inverse;
		for(int i = 0; i < size; i++) {
			seletions.add(selected);
		}
		Set<Integer> set = retentionTimes.stream().map(r -> r.getRetentionTime()).collect(Collectors.toSet());
		for(int i = 0; i < size; i++) {
			int time = samples.getExtractedRetentionTimes().get(i).getRetentionTime();
			if(inverse) {
				seletions.set(i, set.contains(time));
			} else {
				seletions.set(i, !set.contains(time));
			}
		}
		selectionResult = IFilter.getNumberSelectedRow(seletions);
		return seletions;
	}

	@Override
	public String getDescription() {

		if(inverse) {
			return "Select " + retentionTimes.size() + " retention times.";
		} else {
			return "Deselect " + retentionTimes.size() + " retention times.";
		}
	}

	@Override
	public String getName() {

		return "Retention time filter";
	}

	public List<IRetentionTime> getRetentionTimes() {

		return retentionTimes;
	}

	@Override
	public String getSelectionResult() {

		return selectionResult;
	}

	@Override
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}
}
