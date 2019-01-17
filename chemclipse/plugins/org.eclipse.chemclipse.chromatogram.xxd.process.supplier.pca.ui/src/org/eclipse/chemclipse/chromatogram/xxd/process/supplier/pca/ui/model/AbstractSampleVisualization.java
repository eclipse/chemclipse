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

import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISampleData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractSampleVisualization implements ISampleVisualization {

	private IntegerProperty color;
	private ISample delegator;
	private StringProperty groupName;
	private StringProperty name;
	private ReadOnlyLongWrapper sampleDataHasBeenChanged;
	private BooleanProperty selected;

	public AbstractSampleVisualization(ISample sampleModel) {

		this.color = new SimpleIntegerProperty();
		this.delegator = sampleModel;
		this.sampleDataHasBeenChanged = new ReadOnlyLongWrapper();
		setColorRgba(255, 0, 0, 1.0);
	}

	public AbstractSampleVisualization(ISample sampleModel, int r, int g, int b, double alpha) {

		this(sampleModel);
		setColorRgba(r, g, b, alpha);
	}

	@Override
	public IntegerProperty colorProperty() {

		return this.color;
	}

	@Override
	public int getColor() {

		return this.colorProperty().get();
	}

	@Override
	public String getGroupName() {

		if(groupName != null) {
			return groupName.get();
		}
		return delegator.getGroupName();
	}

	@Override
	public String getName() {

		if(name != null) {
			return name.get();
		}
		return delegator.getName();
	}

	@Override
	public List<? extends ISampleData> getSampleData() {

		return delegator.getSampleData();
	}

	@Override
	public long getSampleDataHasBeenChanged() {

		return sampleDataHasBeenChanged.get();
	}

	@Override
	public StringProperty groupNameProperty() {

		if(groupName == null) {
			groupName = new SimpleStringProperty(delegator.getGroupName());
		}
		return groupName;
	}

	@Override
	public boolean isSelected() {

		if(selected != null) {
			return selected.get();
		}
		return delegator.isSelected();
	}

	@Override
	public StringProperty nameProperty() {

		if(name == null) {
			name = new SimpleStringProperty(delegator.getName());
		}
		return name;
	}

	@Override
	public ReadOnlyLongProperty sampleDataHasBeenChangedProperty() {

		return sampleDataHasBeenChanged.getReadOnlyProperty();
	}

	@Override
	public BooleanProperty selectedProperty() {

		if(selected == null) {
			selected = new SimpleBooleanProperty(delegator.isSelected());
		}
		return selected;
	}

	@Override
	public void setColor(final int sampleColor) {

		this.colorProperty().set(sampleColor);
	}

	@Override
	public void setGroupName(String groupName) {

		delegator.setGroupName(groupName);
		if(this.groupName != null) {
			this.groupName.set(groupName);
		}
	}

	@Override
	public void setName(String name) {

		delegator.setName(name);
		if(this.name != null) {
			this.name.set(name);
		}
	}

	@Override
	public void setSampleDataHasBeenChanged() {

		sampleDataHasBeenChanged.add(1);
	}

	@Override
	public void setSelected(boolean selected) {

		delegator.setSelected(selected);
		if(this.selected != null) {
			this.selected.set(selected);
		}
	}
}
