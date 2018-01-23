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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

public class TableData {

	private List<ISample<? extends ISampleData>> samples = new ArrayList<>();
	private List<IVariable> variables = new ArrayList<>();

	public TableData() {
	}

	public void clear() {

		samples.clear();
		variables.clear();
	}

	/**
	 * @return sorted samples by groups, this List contains instances of class Group
	 */
	public List<ISample<? extends ISampleData>> getSamples() {

		return samples;
	}

	/**
	 *
	 * @return retention times
	 */
	public List<IVariable> getVariables() {

		return variables;
	}

	public void update(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> isamples) {

		/*
		 * remove old data
		 */
		variables.clear();
		samples.clear();
		/*
		 * copy data and insert object ISample and IGroup and sort this object by group name
		 */
		samples.addAll(isamples.getSampleList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		samples.addAll(isamples.getGroupList().stream().filter(s -> s.isSelected()).collect(Collectors.toList()));
		PcaUtils.sortSampleListByName(samples);
		PcaUtils.sortSampleListByGroup(samples);
		/*
		 * set retention time
		 */
		variables.addAll(isamples.getVariables());
		/*
		 * Set peaks names
		 */
	}
}
