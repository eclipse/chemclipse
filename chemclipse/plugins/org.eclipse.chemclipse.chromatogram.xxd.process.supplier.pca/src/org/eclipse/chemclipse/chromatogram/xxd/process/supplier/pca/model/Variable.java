/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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

public class Variable implements IVariable {

	private boolean isSelected = true;
	private IVariable variable;

	public Variable(IVariable variable) {
		this.variable = variable;
	}

	@Override
	public int compareTo(IVariable o) {

		return variable.compareTo(o);
	}

	@Override
	public String getDescription() {

		return variable.getDescription();
	}

	@Override
	public Object getObject() {

		return variable.getObject();
	}

	@Override
	public String getValue() {

		return variable.getValue();
	}

	@Override
	public boolean isSelected() {

		return isSelected;
	}

	@Override
	public void setDescription(String description) {

		variable.getDescription();
	}

	@Override
	public void setSelected(boolean selected) {

		isSelected = selected;
	}
}
