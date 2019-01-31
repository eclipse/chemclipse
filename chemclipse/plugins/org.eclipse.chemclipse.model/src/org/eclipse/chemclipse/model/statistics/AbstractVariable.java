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
package org.eclipse.chemclipse.model.statistics;

public abstract class AbstractVariable implements IVariable {

	private String description;
	private boolean selected;
	private String type;
	private String value;
	private String classification;

	public AbstractVariable() {

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
	public boolean isSelected() {

		return selected;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public void setSelected(boolean selected) {

		this.selected = selected;
	}

	@Override
	public void setType(String type) {

		this.type = type;
	}

	@Override
	public void setValue(String value) {

		this.value = value;
	}

	@Override
	public String getClassification() {

		return classification;
	}

	@Override
	public void setClassification(String classification) {

		this.classification = classification;
	}
}
