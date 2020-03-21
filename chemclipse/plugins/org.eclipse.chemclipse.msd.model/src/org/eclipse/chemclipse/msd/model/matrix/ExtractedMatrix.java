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


import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonBounds;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.implementation.Ion;

public class ExtractedMatrix implements IExtractedMatrix {
	
	private IChromatogramSelectionMSD selection;
	private List<IScanMSD> scans;
	private int numberOfScans;
	private int numberOfIons;
	private int startIon;
	private int stopIon;
	private float[] signal;
	private boolean highRes = false;
	
	public ExtractedMatrix(IChromatogramSelectionMSD chromatogramSelection) {
		this.selection = chromatogramSelection;
		this.numberOfScans = selection.getStopScan() - selection.getStartScan() - 1;
		this.scans = extractScans();
		if(checkHighRes( 10)) {
			highRes = true;
			System.out.println("This is highres MS Data and should be binned first");
		} else {
			this.startIon = getMinMz();
			this.stopIon = getMaxMz();
			this.numberOfIons = this.stopIon - this.startIon + 1;
			signal = new float[numberOfScans * (this.stopIon - this.startIon + 1)];
			try {
				for(int i = 0; i < numberOfScans; i++) {
					for(int j = 0; j < scans.get(i).getIons().size(); j++) {
						signal[ ((int) Math.round(scans.get(i).getIons().get(j).getIon() - this.startIon)) * numberOfScans + i    ] =  scans.get(i).getIons().get(j).getAbundance();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Boolean checkHighRes( int limit ) {
		boolean moreIonsThanFullMz = false;
		Iterator<IScanMSD> iterator = scans.iterator();
		IIonBounds bounds;
		double rangeAbs;
		IScanMSD currentScan;
		
		while(moreIonsThanFullMz == false) {
			currentScan = iterator.next();
			bounds = currentScan.getIonBounds();
			rangeAbs = bounds.getHighestIon().getIon() - bounds.getLowestIon().getIon();
			if(rangeAbs + limit < currentScan.getIons().size()) {
				moreIonsThanFullMz = true;
			}
			if(!iterator.hasNext()) {
				break;
			}
		}
		
		return moreIonsThanFullMz;
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
				.filter(s -> s.getRetentionTime() > startRT) //
				.filter(s -> s.getRetentionTime() < stopRT) //
				.collect(Collectors.toList()); //
		return (scans);
	}
	
	private int getMinMz() {

		double min = scans.stream() //
				.flatMap(scan -> scan.getIons().stream()) //
				.min(Comparator.comparing(IIon::getIon)) //
				.get() //
				.getIon();
		int minMz = (int)Math.round(min);
		return (minMz);
	}
	
	private int getMaxMz() {

		double max = scans.stream() //
				.flatMap(scan -> scan.getIons().stream()) //
				.max(Comparator.comparing(IIon::getIon)) //
				.get() //
				.getIon();
		int maxMz = (int)Math.round(max);
		return (maxMz);
	}

	@Override
	public float[] getMatrix() {
		if(highRes == true) {
			return null;			
		}
		return this.signal;
	}

	@Override
	public int getNumberOfScans() {
		if(highRes == true) {
			return 0;			
		}
		return this.numberOfScans;
	}

	@Override
	public int getNumberOfIons() {
		if(highRes == true) {
			return 0;			
		}
		return this.numberOfIons;
	}

	@Override
	public void updateSignal(float[] signal, int numberOfScans, int nubmerOfIons ) {
		
		IScanMSD currentScan;
		IIon currentIon;
		
		if(highRes != true) {
			try {
				for(int i = 1; i <= numberOfScans; i++) {
					currentScan = (IScanMSD) selection.getChromatogram().getScan(i);
					currentScan.removeAllIons();
					for(int j = startIon; j < stopIon; j++) {
						if(signal[(j - startIon) * numberOfScans + (i-1)] != 0.0) {
							currentIon = new Ion(j, signal[(j - startIon) * numberOfScans + (i - 1)] ); 
							currentScan.addIon(currentIon);
						}		
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}

	@Override
	public int[] getScanNumbers() {
		if(highRes == true) {
			return null;			
		}
		int[] scanNumbers = scans.stream().mapToInt(scan -> scan.getScanNumber()).toArray();
		return scanNumbers;
	}

	@Override
	public int[] getRetentionTimes() {
		if(highRes == true) {
			return null;			
		}
		int[] retentionTimes = scans.stream().mapToInt(scan -> scan.getRetentionTime()).toArray();
		return retentionTimes;
	}

}
