/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.core.IPeaks;

public class PcaResult implements IPcaResult {

	private ISlopes slopes;
	private IPeaks peaks;
	private double[] sampleData;
	private double[] eigenSpace;
	private double errorMemberShip;

	@Override
	public ISlopes getSlopes() {

		return slopes;
	}

	@Override
	public void setSlopes(ISlopes slopes) {

		this.slopes = slopes;
	}

	@Override
	public IPeaks getPeaks() {

		return peaks;
	}

	@Override
	public void setPeaks(IPeaks peaks) {

		this.peaks = peaks;
	}

	@Override
	public double[] getSampleData() {

		return sampleData;
	}

	@Override
	public void setSampleData(double[] sampleData) {

		this.sampleData = sampleData;
	}

	@Override
	public double[] getEigenSpace() {

		return eigenSpace;
	}

	@Override
	public void setEigenSpace(double[] eigenSpace) {

		this.eigenSpace = eigenSpace;
	}

	@Override
	public double getErrorMemberShip() {

		return errorMemberShip;
	}

	@Override
	public void setErrorMemberShip(double errorMemberShip) {

		this.errorMemberShip = errorMemberShip;
	}
}
