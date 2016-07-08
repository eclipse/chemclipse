/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

import org.eclipse.chemclipse.msd.model.core.IPeakMSD;

public abstract class AbstractQuantitationPeakMSD implements IQuantitationPeakMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -3649217581379583706L;
	//
	private double concentration;
	private String concentrationUnit;
	private IPeakMSD referencePeakMSD;

	public AbstractQuantitationPeakMSD(IPeakMSD referencePeakMSD, double concentration, String concentrationUnit) {
		this.referencePeakMSD = referencePeakMSD;
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
	public IPeakMSD getReferencePeakMSD() {

		return referencePeakMSD;
	}
}
