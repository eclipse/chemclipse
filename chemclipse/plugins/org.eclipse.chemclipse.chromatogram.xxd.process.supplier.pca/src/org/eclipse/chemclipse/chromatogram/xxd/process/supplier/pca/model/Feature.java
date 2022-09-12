/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class Feature {

	private IVariable variable = null;
	private List<ISampleData<?>> sampleData = new ArrayList<>();

	public Feature(IVariable variable) {

		this.variable = variable;
	}

	public IVariable getVariable() {

		return variable;
	}

	public List<ISampleData<?>> getSampleData() {

		return sampleData;
	}
}