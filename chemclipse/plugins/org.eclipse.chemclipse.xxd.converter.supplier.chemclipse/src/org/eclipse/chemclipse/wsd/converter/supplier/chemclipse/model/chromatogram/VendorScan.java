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

import org.eclipse.chemclipse.wsd.model.core.AbstractScanWSD;

public class VendorScan extends AbstractScanWSD implements IVendorScan {

	private static final long serialVersionUID = -6913519950824952048L;
	private float totalSignal = 0.0f;

	public VendorScan() {
		super();
	}

	@Override
	public float getTotalSignal() {

		return totalSignal;
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		this.totalSignal = totalSignal;
	}

	@Override
	public void setTotalSignal(float totalSignal) {

		this.totalSignal = totalSignal;
	}
}
