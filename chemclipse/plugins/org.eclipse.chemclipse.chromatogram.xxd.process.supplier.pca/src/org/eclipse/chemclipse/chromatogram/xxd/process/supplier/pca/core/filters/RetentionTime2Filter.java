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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class RetentionTime2Filter extends AbstractFilter implements IFilter {

	private boolean inverse;
	private String selectionResult = "";
	private List<IVariable> variablesSelected;

	public RetentionTime2Filter(List<IVariable> variables, boolean inverse) {

		super(DataTypeProcessing.VARIABLES);
		this.variablesSelected = new ArrayList<>(variables);
		this.inverse = inverse;
	}

	@Override
	public <V extends IVariable, S extends ISample> List<Boolean> filter(ISamples<V, S> samples) {

		int size = samples.getVariables().size();
		List<Boolean> seletions = new ArrayList<>(size);
		boolean selected = !inverse;
		for(int i = 0; i < size; i++) {
			seletions.add(selected);
		}
		List<V> variables = samples.getVariables();
		Set<IVariable> set = variables.stream().collect(Collectors.toSet());
		for(int i = 0; i < size; i++) {
			IVariable variable = variables.get(i);
			if(inverse) {
				seletions.set(i, set.contains(variable));
			} else {
				seletions.set(i, !set.contains(variable));
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
}
