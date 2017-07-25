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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;

public class PcaFiltrationData {

	public void process(IPcaResults pcaResults, boolean reset, List<Boolean>... filters) {

		List<Boolean> selectedRetentionTimes = pcaResults.isSelectedRetentionTimes();
		if(reset) {
			selectedRetentionTimes.forEach(e -> e = false);
		}
		for(List<Boolean> filter : filters) {
			for(int i = 0; i < selectedRetentionTimes.size(); i++) {
				Boolean selectedRetentionTime = selectedRetentionTimes.get(i);
				selectedRetentionTimes.set(i, selectedRetentionTime & filter.get(i));
			}
		}
	}
}
