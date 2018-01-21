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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractVariable implements IVariable {

	private StringProperty description;
	private BooleanProperty selected;
	private StringProperty type;
	private StringProperty value;

	public AbstractVariable() {
		value = new SimpleStringProperty();
		type = new SimpleStringProperty();
		selected = new SimpleBooleanProperty();
		description = new SimpleStringProperty();
	}

	@Override
	public StringProperty descriptionProperty() {

		return description;
	}

	@Override
	public String getDescription() {

		return description.get();
	}

	@Override
	public String getType() {

		return type.get();
	}

	@Override
	public String getValue() {

		return value.get();
	}

	@Override
	public boolean isSelected() {

		return selected.get();
	}

	@Override
	public BooleanProperty selectedProperty() {

		return selected;
	}

	@Override
	public void setDescription(String description) {

		this.description.set(description);
	}

	@Override
	public void setSelected(boolean selected) {

		this.selected.set(selected);
	}

	@Override
	public void setType(String type) {

		this.type.set(type);
	}

	@Override
	public void setValue(String value) {

		this.value.set(value);
	}

	@Override
	public StringProperty typeProperty() {

		return type;
	}

	@Override
	public StringProperty valueProperty() {

		return value;
	}
}
