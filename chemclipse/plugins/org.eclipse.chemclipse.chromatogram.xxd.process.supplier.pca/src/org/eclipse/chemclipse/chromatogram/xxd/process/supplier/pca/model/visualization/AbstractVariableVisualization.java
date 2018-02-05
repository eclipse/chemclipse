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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public class AbstractVariableVisualization implements IVariableVisualization {

	private IntegerProperty color;
	private IVariable delegator;

	public AbstractVariableVisualization(IVariable variableModel) {
		super();
		color = new SimpleIntegerProperty();
		this.delegator = variableModel;
	}

	@Override
	public IntegerProperty colorProperty() {

		return this.color;
	}

	@Override
	public int compareTo(IVariable o) {

		return delegator.compareTo(o);
	}

	@Override
	public StringProperty descriptionProperty() {

		return delegator.descriptionProperty();
	}

	@Override
	public int getColor() {

		return this.colorProperty().get();
	}

	@Override
	public String getDescription() {

		return delegator.getDescription();
	}

	@Override
	public String getType() {

		return delegator.getType();
	}

	@Override
	public String getValue() {

		return delegator.getValue();
	}

	@Override
	public boolean isSelected() {

		return delegator.isSelected();
	}

	@Override
	public BooleanProperty selectedProperty() {

		return delegator.selectedProperty();
	}

	@Override
	public void setColor(final int color) {

		this.colorProperty().set(color);
	}

	@Override
	public void setDescription(String description) {

		delegator.setDescription(description);
	}

	@Override
	public void setSelected(boolean selected) {

		delegator.setSelected(selected);
	}

	@Override
	public void setType(String type) {

		delegator.setType(type);
	}

	@Override
	public void setValue(String value) {

		delegator.setValue(value);
	}

	@Override
	public StringProperty typeProperty() {

		return delegator.typeProperty();
	}

	@Override
	public StringProperty valueProperty() {

		return delegator.valueProperty();
	}
}
