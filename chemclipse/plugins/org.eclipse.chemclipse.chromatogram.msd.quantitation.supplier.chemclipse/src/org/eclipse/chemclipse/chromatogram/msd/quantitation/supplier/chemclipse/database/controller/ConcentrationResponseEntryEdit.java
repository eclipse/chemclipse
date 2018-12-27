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
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.database.controller;

import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;

public class ConcentrationResponseEntryEdit {

	private IConcentrationResponseEntry concentrationResponseEntryMSD;
	private String concentrationUnit;

	public ConcentrationResponseEntryEdit(String concentrationUnit) {
		this.concentrationUnit = concentrationUnit;
	}

	public IConcentrationResponseEntry getConcentrationResponseEntryMSD() {

		return concentrationResponseEntryMSD;
	}

	public void setConcentrationResponseEntryMSD(IConcentrationResponseEntry concentrationResponseEntryMSD) {

		this.concentrationResponseEntryMSD = concentrationResponseEntryMSD;
	}

	public String getConcentrationUnit() {

		return concentrationUnit;
	}
}
