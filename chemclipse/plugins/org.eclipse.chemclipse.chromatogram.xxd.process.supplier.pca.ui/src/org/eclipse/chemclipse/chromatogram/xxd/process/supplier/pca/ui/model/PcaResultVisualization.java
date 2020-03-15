/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.model.statistics.ISample;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class PcaResultVisualization implements IPcaResultVisualization {

	private IntegerProperty color;
	private IResultPCA delegator;
	private ISampleVisualization sampleVisualization;

	public PcaResultVisualization(IResultPCA dataModel) {
		super();
		this.color = new SimpleIntegerProperty();
		this.delegator = dataModel;
		ISample sample = dataModel.getSample();
		if(sample instanceof ISampleVisualization) {
			this.sampleVisualization = (ISampleVisualization)sample;
		} else {
			this.sampleVisualization = new SampleVisualization(dataModel.getSample());
		}
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
	public double[] getScoreVector() {

		return delegator.getScoreVector();
	}

	@Override
	public double getErrorMemberShip() {

		return delegator.getErrorMemberShip();
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
	public ISampleVisualization getSample() {

		return sampleVisualization;
	}

	@Override
	public double[] getSampleData() {

		return delegator.getSampleData();
	}

	@Override
	public boolean isDisplayed() {

		return delegator.isDisplayed();
	}

	@Override
	public void setColor(final int color) {

		this.colorProperty().set(color);
	}

	@Override
	public void setDisplayed(boolean displayed) {

		delegator.setDisplayed(displayed);
	}

	@Override
	public void setScoreVector(double[] scoreVector) {

		delegator.setScoreVector(scoreVector);
	}

	@Override
	public void setErrorMemberShip(double errorMemberShip) {

		delegator.setErrorMemberShip(errorMemberShip);
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
	public void setSampleData(double[] sampleData) {

		delegator.setSampleData(sampleData);
	}
}
