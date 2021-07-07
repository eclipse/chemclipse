/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Lorenz Gerber - channel wise subtraction for WSD / MSD
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;

public class ChromatogramSubtractor {

	private final static boolean CHANNEL_WISE_SUBTRACTION = true;

	public void perform(IChromatogram<?> chromatogramMaster, IChromatogram<?> chromatogramSubtract) {

		if(chromatogramMaster != null && chromatogramSubtract != null) {
			int startRetentionTime = chromatogramMaster.getStartRetentionTime();
			int stopRetentionTime = chromatogramMaster.getStopRetentionTime();
			perform(chromatogramMaster, chromatogramSubtract, startRetentionTime, stopRetentionTime);
		}
	}

	public void perform(IChromatogram<?> chromatogramMaster, IChromatogram<?> chromatogramSubtract, int startRetentionTime, int stopRetentionTime) {

		if(chromatogramMaster != null && chromatogramSubtract != null) {
			int startScan = chromatogramMaster.getScanNumber(startRetentionTime);
			int stopScan = chromatogramMaster.getScanNumber(stopRetentionTime);
			//
			for(int i = startScan; i <= stopScan; i++) {
				IScan scanMaster = chromatogramMaster.getScan(i);
				if(scanMaster instanceof IScanMSD && CHANNEL_WISE_SUBTRACTION) {
					IScanMSD scanMasterMSD = (IScanMSD)scanMaster;
					subtractMSD(scanMasterMSD, chromatogramSubtract);
				} else if(scanMaster instanceof IScanWSD && CHANNEL_WISE_SUBTRACTION) {
					IScanWSD scanMasterWSD = (IScanWSD)scanMaster;
					subtractWSD(scanMasterWSD, chromatogramSubtract);
				} else if(scanMaster != null) {
					int scanNumberSubtract = chromatogramSubtract.getScanNumber(scanMaster.getRetentionTime());
					IScan scanSubtract = chromatogramSubtract.getScan(scanNumberSubtract);
					if(scanSubtract != null) {
						/*
						 * Subtract the signal.
						 */
						float totalSignalMaster = scanMaster.getTotalSignal();
						float totalSignalSubstract = scanSubtract.getTotalSignal();
						float totalSignal = totalSignalMaster - totalSignalSubstract;
						//
						if(scanMaster instanceof IScanMSD) {
							if(totalSignal > 0) {
								scanMaster.adjustTotalSignal(totalSignal);
							} else {
								IScanMSD scanMSD = (IScanMSD)scanMaster;
								scanMSD.removeAllIons();
							}
						} else {
							scanMaster.adjustTotalSignal(totalSignal);
						}
					}
				}
			}
		}
	}

	private void subtractWSD(IScanWSD scanMasterWSD, IChromatogram<?> chromatogramSubtract) {

		if(scanMasterWSD != null) {
			int scanNumberSubtract = chromatogramSubtract.getScanNumber(scanMasterWSD.getRetentionTime());
			IScanWSD scanSubtract = (IScanWSD)chromatogramSubtract.getScan(scanNumberSubtract);
			if(scanSubtract != null) {
				List<IScanSignalWSD> signals = scanMasterWSD.getScanSignals();
				for(IScanSignalWSD signal : signals) {
					double wavelength = signal.getWavelength();
					IScanSignalWSD subtract = scanSubtract.getScanSignal(wavelength).orElse(null);
					if(subtract != null) {
						float abundance = signal.getAbundance() - subtract.getAbundance();
						signal.setAbundance(abundance);
					}
				}
			}
		}
	}

	private void subtractMSD(IScanMSD scanMasterMSD, IChromatogram<?> chromatogramSubtract) {

		if(scanMasterMSD != null) {
			int scanNumberSubtract = chromatogramSubtract.getScanNumber(scanMasterMSD.getRetentionTime());
			IScanMSD scanSubtract = (IScanMSD)chromatogramSubtract.getScan(scanNumberSubtract);
			if(scanSubtract != null) {
				List<IIon> ions = scanMasterMSD.getIons();
				List<IIon> zeroAbundanceIons = new ArrayList<>();
				for(IIon ion : ions) {
					double mass = ion.getIon();
					IIon subtract;
					try {
						subtract = scanSubtract.getIon(mass);
						if(subtract != null) {
							float abundance = ion.getAbundance() - subtract.getAbundance();
							if(abundance <= 0.0) {
								zeroAbundanceIons.add(ion);
							} else {
								ion.setAbundance(abundance);
							}
						}
					} catch(AbundanceLimitExceededException e1) {
						System.out.println(e1);
					} catch(IonLimitExceededException e1) {
						System.out.println(e1);
					}
				}
				zeroAbundanceIons.stream().forEach(x -> scanMasterMSD.removeIon(x));
			}
		}
	}
}
