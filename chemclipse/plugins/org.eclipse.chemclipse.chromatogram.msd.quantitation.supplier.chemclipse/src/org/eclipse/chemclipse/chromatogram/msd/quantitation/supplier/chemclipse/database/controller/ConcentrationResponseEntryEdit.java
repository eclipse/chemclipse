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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller;

import org.eclipse.chemclipse.msd.model.core.quantitation.IConcentrationResponseEntryMSD;

public class ConcentrationResponseEntryEdit {

	private IConcentrationResponseEntryMSD concentrationResponseEntryMSD;
	private String concentrationUnit;

	public ConcentrationResponseEntryEdit(String concentrationUnit) {

		this.concentrationUnit = concentrationUnit;
	}

	public IConcentrationResponseEntryMSD getConcentrationResponseEntryMSD() {

		return concentrationResponseEntryMSD;
	}

	public void setConcentrationResponseEntryMSD(IConcentrationResponseEntryMSD concentrationResponseEntryMSD) {

		this.concentrationResponseEntryMSD = concentrationResponseEntryMSD;
	}

	public String getConcentrationUnit() {

		return concentrationUnit;
	}
}
