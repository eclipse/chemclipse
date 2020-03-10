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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IAnalysisSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;

public class TableData {

	private List<ISampleVisualization> samples = new ArrayList<>();
	private List<IVariableVisualization> variables = new ArrayList<>();
	private Set<Integer> colors = new HashSet<>();
	private boolean[] modifiableRowList;

	public TableData() {
	}

	public void clear() {

		samples.clear();
		variables.clear();
	}

	/**
	 * @return sorted samples by groups, this List contains instances of class Group
	 */
	public List<ISampleVisualization> getSamples() {

		return samples;
	}

	public String getVariableName() {

		Optional<IVariableVisualization> variable = variables.stream().findAny();
		if(variable.isPresent()) {
			return variable.get().getType();
		} else {
			return "";
		}
	}

	/**
	 *
	 * @return retention times
	 */
	public List<IVariableVisualization> getVariables() {

		return variables;
	}

	public void update(ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> isamples, IAnalysisSettings analysisSettings) {

		/*
		 * remove old data
		 */
		variables.clear();
		samples.clear();
		colors.clear();
		/*
		 * copy data and insert object ISample and IGroup and sort this object by group name
		 */
		samples.addAll(isamples.getSampleList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		// samples.addAll(isamples.getGroupList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		PcaUtils.sortSampleListByName(samples);
		PcaUtils.sortSampleListByGroup(samples);
		/*
		 * set retention time
		 */
		variables.addAll(isamples.getVariables());
		variables.forEach(v -> colors.add(v.getColor()));
		modifiableRowList = new boolean[variables.size()];
		for(int i = 0; i < modifiableRowList.length; i++) {
			if(analysisSettings.isRemoveUselessVariables()) {
				modifiableRowList[i] = isamples.selectVariable(i);
			} else {
				modifiableRowList[i] = true;
			}
		}
	}

	public Set<Integer> getColores() {

		return colors;
	}

	public boolean[] getModifiableRowList() {

		return modifiableRowList;
	}
}
