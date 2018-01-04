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
package org.eclipse.chemclipse.msd.model.core.quantitation;

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationEntry;

public abstract class AbstractQuantitationEntryMSD extends AbstractQuantitationEntry implements IQuantitationEntryMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 868711028892956240L;
	//
	private double ion;

	public AbstractQuantitationEntryMSD(String name, double concentration, String concentrationUnit, double area, double ion) {
		super(name, concentration, concentrationUnit, area);
		this.ion = ion;
	}

	@Override
	public double getIon() {

		return ion;
	}
}
