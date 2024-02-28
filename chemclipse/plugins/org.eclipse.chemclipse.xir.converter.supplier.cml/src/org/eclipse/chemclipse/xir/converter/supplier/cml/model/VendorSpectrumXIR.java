/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.converter.supplier.cml.model;

import org.eclipse.chemclipse.xir.model.implementation.SpectrumXIR;

public class VendorSpectrumXIR extends SpectrumXIR implements IVendorSpectrumXIR {

	private static final long serialVersionUID = 703379410586981110L;
	//
	private String casNumber = "";
	private String formula = "";

	@Override
	public String getCasNumber() {

		return casNumber;
	}

	@Override
	public void setCasNumber(String casNumber) {

		this.casNumber = casNumber;
	}

	@Override
	public String getFormula() {

		return formula;
	}

	@Override
	public void setFormula(String formula) {

		if(formula != null) {
			this.formula = formula;
		}
	}
}