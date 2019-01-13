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

import org.eclipse.chemclipse.model.statistics.AbstractVariable;
import org.eclipse.chemclipse.model.statistics.IVariable;

public class Variable extends AbstractVariable implements IVaribleExtracted {

	private IVariable variable;

	public Variable(IVariable variable) {

		this.variable = variable;
		setValue(variable.getValue());
		setDescription(variable.getDescription());
		setType(variable.getType());
		setDescription(variable.getDescription());
	}

	@Override
	public int compareTo(IVariable o) {

		return variable.compareTo(o);
	}

	@Override
	public IVariable getVariableOrigin() {

		return variable;
	}
}
