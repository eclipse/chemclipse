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

public interface IAllIonSignals {

	int getStartIon();

	int getStopIon();

	int getStartScan();

	int getStopScan();

	List<IIonSignals> getIonSignals();

	IIonSignals getIonSignals(int value);

	void addIonDeconv(IIonSignals value);

	void deleteIonDeconv(int value);

	int size();

	void addRetentionTime(IRetentionTime value);

	void deleteRetentionTime(int whichOne);

	List<IRetentionTime> getRetentionTimeList();

	double getRetentionTime(int whichOne);
}
