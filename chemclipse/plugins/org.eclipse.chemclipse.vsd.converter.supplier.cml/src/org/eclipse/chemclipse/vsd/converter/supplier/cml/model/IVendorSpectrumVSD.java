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
package org.eclipse.chemclipse.vsd.converter.supplier.cml.model;

import org.eclipse.chemclipse.vsd.model.core.ISpectrumVSD;

public interface IVendorSpectrumVSD extends ISpectrumVSD { // TODO: ILibraryInformation

	/**
	 * Returns CAS number of the IR spectrum.
	 * 
	 * @return String
	 */
	String getCasNumber();

	/**
	 * Sets the CAS number of the IR spectrum.
	 */
	void setCasNumber(String casNumber);

	/**
	 * Returns the formula of the library mass spectrum.
	 * 
	 * @return String
	 */
	String getFormula();

	/**
	 * Sets the formula of the library mass spectrum.
	 */
	void setFormula(String formula);
}