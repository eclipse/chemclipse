/*******************************************************************************
 * Copyright (c) 2015 michaelchang.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * michaelchang - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.model.chromatogram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.wsd.model.core.AbstractScanWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;

public class VendorScan extends AbstractScanWSD implements IVendorScan {

	private static final long serialVersionUID = -6913519950824952048L;
	private List<IScanSignalWSD> scans;

	public VendorScan() {

		scans = new ArrayList<IScanSignalWSD>();
	}

	@Override
	public IScanSignalWSD getScanSignal(int scan) {

		return getScanSignals().get(scan);
	}

	@Override
	public void addScanSignal(IScanSignalWSD scanSignalWSD) {

		getScanSignals().add(scanSignalWSD);
	}

	@Override
	public List<IScanSignalWSD> getScanSignals() {

		return scans;
	}

	@Override
	public void removeScanSignal(int scan) {

		getScanSignals().remove(scan);
	}
}
