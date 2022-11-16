/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class EmptyDataFilter extends AbstractFilter implements IFilter {

	private String selectionResult = "";

	public EmptyDataFilter() {

		super(DataTypeProcessing.MODIFIED_DATA);
	}

	@Override
	public <V extends IVariable, S extends ISample> List<Boolean> filter(ISamples<V, S> samples) {

		List<S> selectedSamples = selectSamples(samples);
		List<Boolean> selection = new ArrayList<>();
		for(int i = 0; i < samples.getVariables().size(); i++) {
			final int index = i;
			boolean b = selectedSamples.stream().map(s -> s.getSampleData().get(index)).allMatch(d -> !d.isEmpty());
			selection.add(b);
		}
		selectionResult = IFilter.getNumberSelectedRow(selection);
		return selection;
	}

	@Override
	public String getDescription() {

		return "";
	}

	@Override
	public String getName() {

		return "Empty Data";
	}

	@Override
	public String getSelectionResult() {

		return selectionResult;
	}
}
