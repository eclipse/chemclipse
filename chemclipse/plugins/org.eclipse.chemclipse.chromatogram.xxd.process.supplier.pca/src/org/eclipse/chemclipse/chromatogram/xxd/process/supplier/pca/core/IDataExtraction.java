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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IDataExtraction {

	int CLOSEST_SCAN = 2;
	int DERIVED_SCAN = 4;
	int EXTRACT_PEAK = 0;
	int EXTRACT_PEAK_CUMULATION = 1;
	int LINEAR_INTERPOLATION_SCAN = 3;

	static void createGroup(IPcaResults pcaResults) {

		/*
		 * create Groups
		 */
		PcaUtils.setGroups(pcaResults, true);
	}

	static List<IDataInputEntry> removeFileSameName(List<IDataInputEntry> entries) {

		Map<String, IDataInputEntry> uniqueNames = new HashMap<>();
		entries.forEach((input -> {
			uniqueNames.put(input.getName(), input);
		}));
		return new ArrayList<>(uniqueNames.values());
	}

	static void setSelectedRetentionTime(IPcaResults pcaResults) {

		/*
		 * Set selected retention Time
		 */
		List<Boolean> selectedRetentionTime = new ArrayList<>();
		for(int i = 0; i < pcaResults.getExtractedRetentionTimes().size(); i++) {
			selectedRetentionTime.add(new Boolean(true));
		}
		pcaResults.setSelectedRetentionTimes(selectedRetentionTime);
	}

	IPcaResults process(IProgressMonitor monitor);
}
