/*******************************************************************************
 * Copyright (c) 2015, 2018 Michael Chang.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Michael Chang - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram;

import org.eclipse.chemclipse.wsd.model.core.AbstractChromatogramWSD;

public class VendorChromatogram extends AbstractChromatogramWSD implements IVendorChromatogram {

	private static final long serialVersionUID = -1918459516874863928L;

	public VendorChromatogram() {
		super();
	}

	@Override
	public double getPeakIntegratedArea() {

		return 0;
	}
}
