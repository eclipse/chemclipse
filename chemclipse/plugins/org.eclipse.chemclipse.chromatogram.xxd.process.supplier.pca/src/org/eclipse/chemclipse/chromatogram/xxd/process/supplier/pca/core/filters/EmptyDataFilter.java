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
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

public class EmptyDataFilter implements IFilter {

	private boolean onlySelected;

	public EmptyDataFilter() {
		onlySelected = true;
	}

	@Override
	public List<Boolean> filter(List<ISample> samples) {

		List<ISample> selectedSamples = samples.stream().filter(s -> s.isSelected() || !onlySelected).collect(Collectors.toList());
		List<Boolean> selection = new ArrayList<>();
		for(int i = 0; i < selectedSamples.get(0).getSampleData().size(); i++) {
			final int index = i;
			boolean b = selectedSamples.stream().map(s -> s.getSampleData().get(index)).allMatch(d -> !d.isEmpty());
			selection.add(b);
		}
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
	public boolean isOnlySelected() {

		return onlySelected;
	}

	@Override
	public void setOnlySelected(boolean onlySelected) {

		this.onlySelected = onlySelected;
	}
}
