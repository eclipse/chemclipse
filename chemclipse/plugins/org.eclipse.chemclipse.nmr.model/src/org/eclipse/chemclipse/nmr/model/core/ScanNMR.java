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

import java.util.Arrays;
import java.util.TreeSet;

import org.apache.commons.math3.complex.Complex;
import org.eclipse.chemclipse.model.core.AbstractMeasurementInfo;

public class ScanNMR extends AbstractMeasurementInfo implements IScanNMR {

	private static final long serialVersionUID = -4448729586928333575L;
	//
	private double[] rawSignals = new double[0];
	private Complex[] modifiedSignals = new Complex[0];
	private TreeSet<ISignalNMR> processedSignals = new TreeSet<ISignalNMR>();

	@Override
	public double[] getRawSignals() {

		return rawSignals;
	}

	@Override
	public void setRawSignals(double[] rawSignals) {

		this.rawSignals = rawSignals;
	}

	@Override
	public Complex[] getModifiedSignals() {

		return modifiedSignals;
	}

	@Override
	public void setModifiedSignals(Complex[] modifiedSignals) {

		this.modifiedSignals = modifiedSignals;
	}

	@Override
	public TreeSet<ISignalNMR> getProcessedSignals() {

		return processedSignals;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Arrays.hashCode(rawSignals);
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		ScanNMR other = (ScanNMR)obj;
		if(!Arrays.equals(rawSignals, other.rawSignals))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "ScanNMR [rawSignals=" + Arrays.toString(rawSignals) + "]";
	}
}
