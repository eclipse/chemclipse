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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaFiltrationData {

	private List<IFilter> filters;

	public PcaFiltrationData() {
		filters = new ArrayList<>();
	}

	public List<IFilter> getFilters() {

		return filters;
	}

	public void process(IPcaResults pcaResults, boolean resetSelectedRetentionTimes, IProgressMonitor monitor) {

		List<Boolean> selectedRetentionTimes = pcaResults.isSelectedRetentionTimes();
		if(resetSelectedRetentionTimes) {
			for(int i = 0; i < selectedRetentionTimes.size(); i++) {
				selectedRetentionTimes.set(i, true);
			}
		}
		if(filters != null && !filters.isEmpty()) {
			for(int i = 0; i < filters.size(); i++) {
				List<Boolean> result = filters.get(i).filter(pcaResults);
				for(int j = 0; j < result.size(); j++) {
					selectedRetentionTimes.set(j, selectedRetentionTimes.get(j) && result.get(j));
				}
			}
		}
	}

	public void selectAll(IPcaResults pcaResults) {

		List<Boolean> selectedRetentionTimes = pcaResults.isSelectedRetentionTimes();
		for(int i = 0; i < selectedRetentionTimes.size(); i++) {
			selectedRetentionTimes.set(i, true);
		}
	}
}
