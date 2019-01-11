/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVaribleExtracted;

public class VariableExtractedVisualization extends AbstractVariableVisualization implements IVariableExtractedVisalization {

	private IVariableVisualization variable;

	public VariableExtractedVisualization(IVaribleExtracted variableModel) {

		super(variableModel);
		this.variable = new VariableVisualization(variableModel);
	}

	@Override
	public IVariableVisualization getVariableOrigin() {

		return variable;
	}
}
