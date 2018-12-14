/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.nmr.model.core;

import java.util.Collections;
import java.util.NavigableSet;
import java.util.TreeSet;

public class ScanFID extends AbstractScanNMRInfo implements IScanFID {

	/**
	 *
	 */
	private static final long serialVersionUID = 4636733448738115239L;
	private final TreeSet<ISignalFID> signalsFID = new TreeSet<>();
	private double sweepWidth;
	private double firstDataPointOffset;
	private double firstFIDDataPointMultiplicationFactor;

	public ScanFID() {
	}

	@Override
	public NavigableSet<ISignalFID> getSignalsFID() {

		return Collections.unmodifiableNavigableSet(signalsFID);
	}

	@Override
	public void addSignalFID(ISignalFID signalFID) {

		signalsFID.add(signalFID);
	}

	@Override
	public void removeSignalFID(ISignalFID signalFID) {

		signalsFID.remove(signalFID);
	}

	@Override
	public void removeAllSignalsFID() {

		signalsFID.clear();
	}

	@Override
	public long getAcqusitionTime() {

		if(signalsFID.isEmpty()) {
			return 0;
		}
		return signalsFID.last().getAcquisitionTime();
	}

	@Override
	public int getSignalsFidSize() {

		return signalsFID.size();
	}

	@Override
	public double getSweepWidth() {

		return sweepWidth;
	}

	@Override
	public void setSweepWidth(double sweepWidth) {

		this.sweepWidth = sweepWidth;
	}

	@Override
	public double getFirstDataPointOffset() {

		return firstDataPointOffset;
	}

	@Override
	public void setFirstDataPointOffset(double firstDataPointOffset) {

		this.firstDataPointOffset = firstDataPointOffset;
	}

	@Override
	public void setFirstFIDDataPointMultiplicationFactor(double firstFIDDataPointMultiplicationFactor) {

		this.firstFIDDataPointMultiplicationFactor = firstFIDDataPointMultiplicationFactor;
	}

	@Override
	public double getFirstFIDDataPointMultiplicationFactor() {

		return firstFIDDataPointMultiplicationFactor;
	}
}
