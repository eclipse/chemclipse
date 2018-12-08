/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

public class ScanNMR extends AbstractScanNMRInfo implements IScanNMR {

	private static final long serialVersionUID = -4448729586928333575L;
	//
	private final TreeSet<ISignalNMR> processedSignals = new TreeSet<>();
	private double firstOrderPhaseCorrection;
	private double zeroOrderPhaseCorrection;

	@Override
	public int getNumberOfFourierPoints() {

		return processedSignals.size();
	}

	@Override
	public NavigableSet<ISignalNMR> getSignalsNMR() {

		return Collections.unmodifiableNavigableSet(processedSignals);
	}

	@Override
	public void addSignalNMR(ISignalNMR signalNMR) {

		processedSignals.add(signalNMR);
	}

	@Override
	public void removeSignalNMR(ISignalNMR signalNMR) {

		processedSignals.remove(signalNMR);
	}

	@Override
	public void removeAllSignalsNMR() {

		processedSignals.clear();
	}

	@Override
	public double getSweepWidth() {

		if(processedSignals.isEmpty()) {
			return 0;
		}
		return processedSignals.first().getChemicalShift() - processedSignals.last().getChemicalShift();
	}

	@Override
	public double getSweepOffset() {

		if(processedSignals.isEmpty()) {
			return 0;
		}
		return processedSignals.first().getChemicalShift();
	}

	@Override
	public double getFirstOrderPhaseCorrection() {

		return firstOrderPhaseCorrection;
	}

	@Override
	public void setFirstOrderPhaseCorrection(double firstOrderPhaseCorrection) {

		this.firstOrderPhaseCorrection = firstOrderPhaseCorrection;
	}

	@Override
	public double getZeroOrderPhaseCorrection() {

		return zeroOrderPhaseCorrection;
	}

	@Override
	public void setZeroOrderPhaseCorrection(double zeroOrderPhaseCorrection) {

		this.zeroOrderPhaseCorrection = zeroOrderPhaseCorrection;
	}

	@Override
	public double getFirstDataPointOffset() {

		if(processedSignals.isEmpty()) {
			return 0;
		}
		return processedSignals.first().getChemicalShift();
	}
}
