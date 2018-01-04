/*******************************************************************************
 * Copyright (c) 2016, 2018 Florian Ernst.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.IonSignals;

import java.util.List;

public interface IIonSignals {

	int getIon();

	double getMean();

	double[] getIonSignals();

	void setIonSignals(double[] IonSignals);

	int size();

	IIonSignal getSegmentValue(int value);

	List<IIonSignal> getSegmentSignals();

	int sizeSegmentSum();

	void addSegmentValue(IIonSignal value);

	void setCounterSteinAccepted(int value);

	int getCounterSteinAccepted();

	void setCounterSteinDenied(int value);

	int getCounterSteinDenied();
}
