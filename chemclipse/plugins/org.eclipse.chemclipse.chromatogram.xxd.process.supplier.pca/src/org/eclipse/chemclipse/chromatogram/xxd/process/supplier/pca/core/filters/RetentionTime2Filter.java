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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class RetentionTime2Filter implements IFilter {

	private boolean inverse;
	private boolean onlySelected = true;
	private String selectionResult = "";
	private List<IVariable> variablesSelected;

	public RetentionTime2Filter(List<IVariable> variables, boolean inverse) {
		this.variablesSelected = new ArrayList<>(variables);
		this.inverse = inverse;
	}

	@Override
	public <V extends IVariable, S extends ISample<? extends ISampleData>> List<Boolean> filter(ISamples<V, S> samples) {

		int size = samples.getVariables().size();
		List<Boolean> seletions = new ArrayList<>(size);
		boolean selected = !inverse;
		for(int i = 0; i < size; i++) {
			seletions.add(selected);
		}
		List<V> variables = samples.getVariables();
		Set<Object> set = variables.stream().map(r -> r.getObject()).collect(Collectors.toSet());
		for(int i = 0; i < size; i++) {
			Object object = variables.get(i).getObject();
			if(inverse) {
				seletions.set(i, set.contains(object));
			} else {
				seletions.set(i, !set.contains(object));
			}
		}
		selectionResult = IFilter.getNumberSelectedRow(seletions);
		return seletions;
	}

	@Override
	public String getDescription() {

		if(inverse) {
			return "Select " + variablesSelected.size() + " retention times.";
		} else {
			return "Deselect " + variablesSelected.size() + " retention times.";
		}
	}

	@Override
	public String getName() {

		return "Retention time filter";
	}

	@Override
	public String getSelectionResult() {

		return selectionResult;
	}

	public List<IVariable> getVariables() {

		return variablesSelected;
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
