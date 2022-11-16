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
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class EvaluationPCA {

	private ISamplesPCA<? extends IVariable, ? extends ISample> samples = null;
	private IResultsPCA<? extends IResultPCA, ? extends IVariable> results = null;

	public EvaluationPCA(ISamplesPCA<? extends IVariable, ? extends ISample> samples, IResultsPCA<? extends IResultPCA, ? extends IVariable> results) {

		this.samples = samples;
		this.results = results;
	}

	public ISamplesPCA<? extends IVariable, ? extends ISample> getSamples() {

		return samples;
	}

	public IResultsPCA<? extends IResultPCA, ? extends IVariable> getResults() {

		return results;
	}
}
