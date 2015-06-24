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
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.model;

import org.eclipse.chemclipse.model.core.IPeaks;

public class PcaResult {

	private IPeaks peaks;
	private double[] sampleData;
	private double[] eigenSpace;
	private double errorMemberShip;

	public IPeaks getPeaks() {

		return peaks;
	}

	public void setPeaks(IPeaks peaks) {

		this.peaks = peaks;
	}

	public double[] getSampleData() {

		return sampleData;
	}

	public void setSampleData(double[] sampleData) {

		this.sampleData = sampleData;
	}

	public double[] getEigenSpace() {

		return eigenSpace;
	}

	public void setEigenSpace(double[] eigenSpace) {

		this.eigenSpace = eigenSpace;
	}

	public double getErrorMemberShip() {

		return errorMemberShip;
	}

	public void setErrorMemberShip(double errorMemberShip) {

		this.errorMemberShip = errorMemberShip;
	}
}
