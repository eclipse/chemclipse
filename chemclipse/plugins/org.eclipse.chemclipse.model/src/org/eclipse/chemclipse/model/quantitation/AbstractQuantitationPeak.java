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
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

import org.eclipse.chemclipse.model.core.IPeak;

public abstract class AbstractQuantitationPeak implements IQuantitationPeak {

	private static final long serialVersionUID = 195271455326109557L;
	//
	private double concentration;
	private String concentrationUnit;
	private IPeak referencePeak;

	public AbstractQuantitationPeak(IPeak referencePeak, double concentration, String concentrationUnit) {
		this.referencePeak = referencePeak;
		this.concentration = concentration;
		this.concentrationUnit = concentrationUnit;
	}

	@Override
	public double getConcentration() {

		return concentration;
	}

	@Override
	public void setConcentration(double concentration) {

		this.concentration = concentration;
	}

	@Override
	public String getConcentrationUnit() {

		return concentrationUnit;
	}

	@Override
	public IPeak getReferencePeak() {

		return referencePeak;
	}
}
