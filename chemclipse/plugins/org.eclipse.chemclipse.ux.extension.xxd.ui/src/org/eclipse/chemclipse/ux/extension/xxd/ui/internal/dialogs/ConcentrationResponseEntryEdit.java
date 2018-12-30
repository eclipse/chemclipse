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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.dialogs;

import org.eclipse.chemclipse.model.quantitation.IConcentrationResponseEntry;

public class ConcentrationResponseEntryEdit {

	private IConcentrationResponseEntry concentrationResponseEntry;
	private String concentrationUnit;

	public ConcentrationResponseEntryEdit(String concentrationUnit) {
		this.concentrationUnit = concentrationUnit;
	}

	public IConcentrationResponseEntry getConcentrationResponseEntry() {

		return concentrationResponseEntry;
	}

	public void setConcentrationResponseEntryMSD(IConcentrationResponseEntry concentrationResponseEntry) {

		this.concentrationResponseEntry = concentrationResponseEntry;
	}

	public String getConcentrationUnit() {

		return concentrationUnit;
	}
}
