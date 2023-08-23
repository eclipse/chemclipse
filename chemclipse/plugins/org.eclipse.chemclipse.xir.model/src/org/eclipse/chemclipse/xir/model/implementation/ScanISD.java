/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xir.model.implementation;

import java.util.TreeSet;

import org.eclipse.chemclipse.xir.model.core.AbstractScanISD;
import org.eclipse.chemclipse.xir.model.core.ISignalInfrared;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.chemclipse.xir.model.core.SignalType;

public class ScanISD extends AbstractScanISD {

	private static final long serialVersionUID = 4840397215574246595L;
	//
	private TreeSet<ISignalXIR> processedSignals = new TreeSet<ISignalXIR>();

	@Override
	public SignalType getSignalType() {

		SignalType signalType = SignalType.RAMAN;
		//
		ISignalXIR signalXIR = processedSignals.first();
		if(signalXIR != null) {
			if(signalXIR instanceof ISignalInfrared) {
				signalType = SignalType.FTIR;
			}
		}
		//
		return signalType;
	}

	@Override
	public float getTotalSignal() {

		double totalSignal = 0.0d;
		for(ISignalXIR signalXIR : processedSignals) {
			totalSignal += signalXIR.getIntensity();
		}
		//
		return (float)totalSignal;
	}

	@Override
	public TreeSet<ISignalXIR> getProcessedSignals() {

		return processedSignals;
	}
}