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

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public abstract class AbstractSampleVisualization<D extends ISampleData> implements ISampleVisualization<D> {

	private IntegerProperty color;
	private ISample<D> delegator;

	public AbstractSampleVisualization(ISample<D> sampleModel) {
		color = new SimpleIntegerProperty();
		this.delegator = sampleModel;
		setColorRgba(255, 0, 0, 1.0);
	}

	public AbstractSampleVisualization(ISample<D> sampleModel, int r, int g, int b, double alpha) {
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

		return delegator.getGroupName();
	}

	@Override
	public String getName() {

		return delegator.getName();
	}

	@Override
	public List<D> getSampleData() {

		return delegator.getSampleData();
	}

	@Override
	public long getSampleDataHasBeenChanged() {

		return delegator.getSampleDataHasBeenChanged();
	}

	@Override
	public StringProperty groupNameProperty() {

		return delegator.groupNameProperty();
	}

	@Override
	public boolean isSelected() {

		return delegator.isSelected();
	}

	@Override
	public StringProperty nameProperty() {

		return delegator.nameProperty();
	}

	@Override
	public ReadOnlyLongProperty sampleDataHasBeenChangedProperty() {

		return delegator.sampleDataHasBeenChangedProperty();
	}

	@Override
	public BooleanProperty selectedProperty() {

		return delegator.selectedProperty();
	}

	@Override
	public void setColor(final int sampleColor) {

		this.colorProperty().set(sampleColor);
	}

	@Override
	public void setGroupName(String groupName) {

		delegator.setGroupName(groupName);
	}

	@Override
	public void setName(String name) {

		delegator.setName(name);
	}

	@Override
	public void setSampleDataHasBeenChanged() {

		delegator.setSampleDataHasBeenChanged();
	}

	@Override
	public void setSelected(boolean selected) {

		delegator.setSelected(selected);
	}
}
