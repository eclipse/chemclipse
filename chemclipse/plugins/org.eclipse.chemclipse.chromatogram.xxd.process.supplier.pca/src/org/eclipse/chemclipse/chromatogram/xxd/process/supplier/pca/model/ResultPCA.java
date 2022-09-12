/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.statistics.ISample;

public class ResultPCA implements IResultPCA {

	private String sampleName;
	private String groupName;
	private ISample sample;
	private boolean isDisplayed;
	private double[] scoreVector;
	private double errorMemberShip;
	private double[] sampleData;
	private String rgb = "255,0,0";

	public ResultPCA(ISample sample) {

		this.isDisplayed = true;
		this.sample = sample;
	}

	@Override
	public String getSampleName() {

		return sampleName;
	}

	@Override
	public void setSampleName(String sampleName) {

		this.sampleName = sampleName;
	}

	@Override
	public String getGroupName() {

		return groupName;
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName = groupName;
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
	public void setSampleData(double[] sampleData) {

		this.sampleData = sampleData;
	}

	public String getRGB() {

		return rgb;
	}

	public void setRGB(String rgb) {

		this.rgb = rgb;
	}
}