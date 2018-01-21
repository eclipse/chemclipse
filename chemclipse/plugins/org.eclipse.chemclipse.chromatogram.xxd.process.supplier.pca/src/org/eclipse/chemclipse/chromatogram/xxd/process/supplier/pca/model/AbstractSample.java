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

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractSample<D extends ISampleData> implements ISample<D> {

	private StringProperty groupName;
	private StringProperty name;
	private List<D> sampleData;
	private ReadOnlyLongWrapper sampleDataHasBeenChanged;
	private BooleanProperty selected;

	public AbstractSample(String name) {
		sampleData = new ArrayList<>();
		this.name = new SimpleStringProperty(name);
		this.groupName = new SimpleStringProperty();
		this.selected = new SimpleBooleanProperty(true);
		this.sampleDataHasBeenChanged = new ReadOnlyLongWrapper(0);
	}

	@Override
	public boolean equals(Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		ISample<?> other = (ISample<?>)otherObject;
		return name.equals(other.getName());
	}

	@Override
	public String getGroupName() {

		return groupName.get();
	}

	@Override
	public String getName() {

		return name.get();
	}

	@Override
	public List<D> getSampleData() {

		return sampleData;
	}

	@Override
	public long getSampleDataHasBeenChanged() {

		return sampleDataHasBeenChanged.get();
	}

	@Override
	public StringProperty groupNameProperty() {

		return groupName;
	}

	@Override
	public boolean isSelected() {

		return selected.get();
	}

	@Override
	public StringProperty nameProperty() {

		return name;
	}

	@Override
	public ReadOnlyLongProperty sampleDataHasBeenChangedProperty() {

		return sampleDataHasBeenChanged.getReadOnlyProperty();
	}

	@Override
	public BooleanProperty selectedProperty() {

		return selected;
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName.set(groupName);
	}

	@Override
	public void setName(String name) {

		this.name.set(name);
	}

	@Override
	public void setSampleDataHasBeenChanged() {

		sampleDataHasBeenChanged.set(sampleDataHasBeenChanged.get() + 1);
	}

	@Override
	public void setSelected(boolean selected) {

		this.selected.set(selected);
	}
}
