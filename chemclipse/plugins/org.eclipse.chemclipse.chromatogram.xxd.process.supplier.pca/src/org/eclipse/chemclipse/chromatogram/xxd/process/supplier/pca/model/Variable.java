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

public class Variable implements IVaribleExtracted {

	private String description;
	private boolean isSelected = true;
	private String type;
	private String value;
	private IVariable variable;

	public Variable(IVariable variable) {
		this.variable = variable;
	}

	@Override
	public int compareTo(IVariable o) {

		return 0;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getType() {

		return type;
	}

	@Override
	public String getValue() {

		return value;
	}

	@Override
	public IVariable getVariableOrigin() {

		return variable;
	}

	@Override
	public boolean isSelected() {

		return isSelected;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public void setSelected(boolean selected) {

		isSelected = selected;
	}
}
