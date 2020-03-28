/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.matrix;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonBounds;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class ExtractedMatrix {

	private IChromatogramSelectionMSD selection;
	private List<IScanMSD> scans;
	private int startIon;
	private int stopIon;
	private float[][] signal;

	public ExtractedMatrix(IChromatogramSelectionMSD chromatogramSelection) {

		this.selection = chromatogramSelection;
		this.scans = extractScans();
		if(checkHighRes(10)) {
			throw new IllegalArgumentException("HighRes MSD is currently not suported");
		} else {
			int[] minMaxMz = getMinMaxMz();
			this.startIon = minMaxMz[0];
			this.stopIon = minMaxMz[1];
			int numberOfScans = selection.getStopScan() - selection.getStartScan() + 1;
			int numberOfIons = this.stopIon - this.startIon + 1;
			signal = new float[numberOfScans][numberOfIons];
			List<IIon> currentIons;
			for(int scanIndex = 0; scanIndex < numberOfScans; scanIndex++) {
				currentIons = scans.get(scanIndex).getIons();
				for(IIon ion : currentIons) {
					signal[scanIndex][((int)Math.round(ion.getIon() - this.startIon))] = ion.getAbundance();
				}
			}
		}
	}

	private Boolean checkHighRes(int limit) {

		IIonBounds bounds;
		double rangeAbs;
		for(IScanMSD scan : scans) {
			bounds = scan.getIonBounds();
			rangeAbs = bounds.getHighestIon().getIon() - bounds.getLowestIon().getIon();
			if(rangeAbs + limit < scan.getIons().size()) {
				return (true);
			}
		}
		return false;
	}

	private List<IScanMSD> extractScans() {

		List<IScanMSD> scans;
		int startRT;
		int stopRT;
		startRT = selection.getStartRetentionTime();
		stopRT = selection.getStopRetentionTime();
		scans = selection.getChromatogram() //
				.getScans() //
				.stream() //
				.filter(s -> s instanceof IScanMSD) //
				.map(IScanMSD.class::cast) //
				.filter(s -> s.getRetentionTime() >= startRT) //
				.filter(s -> s.getRetentionTime() <= stopRT) //
				.collect(Collectors.toList()); //
		return (scans);
	}

	private int[] getMinMaxMz() {

		int[] minMaxMz = new int[2];
		Stream<Double> s = scans.stream() //
				.flatMap(scan -> scan.getIons().stream()) //
				.map(x -> (Double)x.getIon());
		DoubleSummaryStatistics d = s.collect(Collectors.summarizingDouble(value -> value));
		minMaxMz[0] = AbstractIon.getIon(d.getMin());
		minMaxMz[1] = AbstractIon.getIon(d.getMax());
		return (minMaxMz);
	}

	/**
	 * Returns the scans/ions array
	 * Scans along rows, ions along columns
	 * 
	 * @return data array scans x ions.
	 */
	public float[][] getMatrix() {

		return this.signal;
	}

	public void updateSignal() {

		IScanMSD currentScan;
		IIon currentIon;
		try {
			for(int i = 1; i <= signal.length; i++) {
				currentScan = (IScanMSD)selection.getChromatogram().getScan(i);
				currentScan.removeAllIons();
				for(int j = startIon; j < stopIon; j++) {
					if(signal[i - 1][j - startIon] != 0.0) {
						currentIon = new Ion(j, signal[i - 1][j - startIon]);
						currentScan.addIon(currentIon);
					}
				}
			}
		} catch(Exception e) {
			throw new RuntimeException("Updating the Signal failed:", e);
		}
	}

	public int[] getScanNumbers() {

		int[] scanNumbers = scans.stream().mapToInt(scan -> scan.getScanNumber()).toArray();
		return scanNumbers;
	}

	public int[] getRetentionTimes() {

		int[] retentionTimes = scans.stream().mapToInt(scan -> scan.getRetentionTime()).toArray();
		return retentionTimes;
	}
}
