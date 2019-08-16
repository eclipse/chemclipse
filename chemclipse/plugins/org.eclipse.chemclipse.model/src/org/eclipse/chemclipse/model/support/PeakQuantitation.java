/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to summarize the peak quantitation entries.
 *
 */
public class PeakQuantitation {

	private int retentionTime;
	private double integratedArea;
	private List<Double> concentrations;

	public PeakQuantitation() {
		concentrations = new ArrayList<Double>();
	}

	public int getRetentionTime() {

		return retentionTime;
	}

	public void setRetentionTime(int retentionTime) {

		this.retentionTime = retentionTime;
	}

	public double getIntegratedArea() {

		return integratedArea;
	}

	public void setIntegratedArea(double integratedArea) {

		this.integratedArea = integratedArea;
	}

	public List<Double> getConcentrations() {

		return concentrations;
	}
}
