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
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model;

import org.eclipse.chemclipse.wsd.model.core.implementation.SpectrumWSD;

public class VendorSpectrumWSD extends SpectrumWSD implements IVendorSpectrumWSD {

	private static final long serialVersionUID = -5151204264064786648L;
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

		this.formula = formula;
	}
}