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
package org.eclipse.chemclipse.wsd.model.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractScan;

public abstract class AbstractScanWSD extends AbstractScan implements IScanWSD {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -8298107894544692691L;
	private List<IScanSignalWSD> scans;

	public AbstractScanWSD() {

		scans = new ArrayList<IScanSignalWSD>();
	}

	@Override
	public IScanSignalWSD getScanSignal(int scan) {

		return scans.get(scan);
	}

	@Override
	public void addScanSignal(IScanSignalWSD scanSignalWSD) {

		scans.add(scanSignalWSD);
	}

	@Override
	public void removeScanSignal(int scan) {

		scans.remove(scan);
	}

	@Override
	public List<IScanSignalWSD> getScanSignals() {

		return scans;
	}

	@Override
	public float getTotalSignal() {

		float totalSignal = 0.0f;
		for(IScanSignalWSD scan : scans) {
			totalSignal += scan.getAbundance();
		}
		return totalSignal;
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

	}
}
