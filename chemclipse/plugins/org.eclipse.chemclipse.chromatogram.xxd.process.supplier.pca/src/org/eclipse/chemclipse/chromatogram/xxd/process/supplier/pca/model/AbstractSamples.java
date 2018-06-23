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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class AbstractSamples<V extends IVariable, S extends ISample<? extends ISampleData>> implements ISamples<V, S> {

	private ObservableList<S> samples;
	private ObservableList<V> variables;

	public AbstractSamples() {
		samples = FXCollections.observableArrayList(ISample.extractor());
		variables = FXCollections.observableArrayList(IVariable.extractor());
	}

	@Override
	public ObservableList<S> getSampleList() {

		return samples;
	}

	@Override
	public ObservableList<V> getVariables() {

		return variables;
	}
}
