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

import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.preprocessing.IPreprocessing;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;

public interface IFilter extends IPreprocessing {

	List<Boolean> filter(ISamples samples);

	String getDescription();

	default String getErrorMessage(String messagge) {

		return "Error: " + messagge;
	}

	String getName();

	default String getNumberSelectedRow(Collection<Boolean> selection) {

		long countSelectedData = selection.stream().filter(b -> b).count();
		return Long.toString(countSelectedData);
	}

	String getSelectionResult();

	@Override
	boolean isOnlySelected();

	@Override
	default void process(ISamples samples) {

		List<Boolean> result = filter(samples);
		List<IRetentionTime> retentionTimes = samples.getExtractedRetentionTimes();
		for(int j = 0; j < result.size(); j++) {
			retentionTimes.get(j).setSelected(retentionTimes.get(j).isSelected() && result.get(j));
		}
	}

	@Override
	void setOnlySelected(boolean onlySelected);
}
