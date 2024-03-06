/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.vsd.model.implementation;

import java.util.TreeSet;

import org.eclipse.chemclipse.vsd.model.core.AbstractScanVSD;
import org.eclipse.chemclipse.vsd.model.core.ISignalInfrared;
import org.eclipse.chemclipse.vsd.model.core.ISignalVSD;
import org.eclipse.chemclipse.vsd.model.core.SignalType;

public class ScanVSD extends AbstractScanVSD {

	private static final long serialVersionUID = 4840397215574246595L;
	//
	private TreeSet<ISignalVSD> processedSignals = new TreeSet<ISignalVSD>();

	@Override
	public SignalType getSignalType() {

		SignalType signalType = SignalType.RAMAN;
		//
		ISignalVSD signal = processedSignals.first();
		if(signal != null) {
			if(signal instanceof ISignalInfrared) {
				signalType = SignalType.FTIR;
			}
		}
		//
		return signalType;
	}

	@Override
	public float getTotalSignal() {

		double totalSignal = 0.0d;
		for(ISignalVSD signal : processedSignals) {
			totalSignal += signal.getIntensity();
		}
		//
		return (float)totalSignal;
	}

	@Override
	public TreeSet<ISignalVSD> getProcessedSignals() {

		return processedSignals;
	}
}
