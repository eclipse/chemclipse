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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignals;

public class AllIonSignals implements IAllIonSignals {

	private int startIon;
	private int stopIon;
	private int startScan;
	private int stopScan;
	private List<IIonSignals> allIonSignals;
	private List<IRetentionTime> retentionTimes;

	public AllIonSignals(IExtractedIonSignals extractedIonSignals) {
		startIon = extractedIonSignals.getStartIon();
		stopIon = extractedIonSignals.getStopIon();
		startScan = extractedIonSignals.getStartScan();
		stopScan = extractedIonSignals.getStopScan();
		allIonSignals = new ArrayList<IIonSignals>(0);
		retentionTimes = new ArrayList<IRetentionTime>(0);
	}

	public int getStartIon() {

		return startIon;
	}

	public int getStopIon() {

		return stopIon;
	}

	public int getStartScan() {

		return startScan;
	}

	public int getStopScan() {

		return stopScan;
	}

	public List<IIonSignals> getIonSignals() {

		return allIonSignals;
	}

	public IIonSignals getIonSignals(int value) {

		return allIonSignals.get(value);
	}

	public void addIonDeconv(IIonSignals value) {

		allIonSignals.add(value);
	}

	public void deleteIonDeconv(int value) {

		allIonSignals.remove(value);
	}

	public int size() {

		return allIonSignals.size();
	}

	public void addRetentionTime(IRetentionTime value) {

		retentionTimes.add(value);
	}

	public void deleteRetentionTime(int whichOne) {

		retentionTimes.remove(whichOne);
	}

	public List<IRetentionTime> getRetentionTimeList() {

		return retentionTimes;
	}

	public double getRetentionTime(int whichOne) {

		IRetentionTime rTime = retentionTimes.get(whichOne);
		return rTime.getRetentionTime();
	}
}
