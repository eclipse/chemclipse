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

import org.eclipse.chemclipse.model.quantitation.AbstractQuantitationEntry;

public abstract class AbstractQuantitationEntryMSD extends AbstractQuantitationEntry implements IQuantitationEntryMSD {

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
