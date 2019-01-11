/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

public class PcaResult implements IPcaResult {

	private double[] scoreVector;
	private double errorMemberShip;
	private String groupName;
	private boolean isDisplayed;
	private String name;
	private ISample sample;
	private double[] sampleData;

	public PcaResult(ISample sample) {

		this.isDisplayed = true;
		this.sample = sample;
	}

	@Override
	public double[] getScoreVector() {

		return scoreVector;
	}

	@Override
	public double getErrorMemberShip() {

		return errorMemberShip;
	}

	@Override
	public String getGroupName() {

		return groupName;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public ISample getSample() {

		return sample;
	}

	@Override
	public double[] getSampleData() {

		return sampleData;
	}

	@Override
	public boolean isDisplayed() {

		return isDisplayed;
	}

	@Override
	public void setDisplayed(boolean displayed) {

		this.isDisplayed = displayed;
	}

	@Override
	public void setScoreVector(double[] scoreVector) {

		this.scoreVector = scoreVector;
	}

	@Override
	public void setErrorMemberShip(double errorMemberShip) {

		this.errorMemberShip = errorMemberShip;
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName = groupName;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public void setSampleData(double[] sampleData) {

		this.sampleData = sampleData;
	}
}
