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
package org.eclipse.chemclipse.wsd.converter.supplier.cml.model;

import org.eclipse.chemclipse.wsd.model.core.ISpectrumWSD;

public interface IVendorSpectrumWSD extends ISpectrumWSD { // TODO: ILibraryInformation

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
}