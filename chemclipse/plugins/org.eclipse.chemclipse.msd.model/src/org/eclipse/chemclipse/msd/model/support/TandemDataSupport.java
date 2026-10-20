/*******************************************************************************
 * Copyright (c) 2024, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.support.ChromatogramSupport;
import org.eclipse.chemclipse.model.support.HeaderUtil;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonMSn;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.implementation.ChromatogramMSD;
import org.eclipse.chemclipse.msd.model.implementation.IonMSn;
import org.eclipse.chemclipse.msd.model.implementation.IonTransition;
import org.eclipse.chemclipse.msd.model.implementation.RegularMassSpectrum;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.traces.TraceTandemMSD;

public class TandemDataSupport {

	public static List<IChromatogramMSD> extractTandemData(IChromatogramMSD chromatogramMSD, HeaderField headerField, CondenseOption condenseOption, boolean enforceFullTimeRange, Set<TraceTandemMSD> traces, boolean separateTraces) {

		List<IChromatogramMSD> chromatograms = new ArrayList<>();
		DecimalFormat decimalFormat = getDecimalFormat(condenseOption);
		Map<Integer, Map<TraceTandemMSD, List<IIon>>> scanRangesMap = new HashMap<>();
		List<Integer> retentionTimes = new ArrayList<>();
		Set<Integer> retentionTimesUnique = new HashSet<>();

		for(IScan scan : chromatogramMSD.getScans()) {
			if(scan instanceof IScanMSD scanMSD) {

				/*
				 * Condense the retention time to alleviate slight time deviations.
				 */
				int retentionTime = -1;
				if(decimalFormat == null) {
					retentionTime = scan.getRetentionTime();
				} else {
					try {
						double retentionTimeMinutes = Double.parseDouble(decimalFormat.format(scan.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
						retentionTime = (int)Math.round(retentionTimeMinutes * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					} catch(NumberFormatException e) {
					}
				}
				/*
				 * Check
				 */
				if(retentionTime >= 0) {
					/*
					 * Harmonize times.
					 */
					if(retentionTimesUnique.add(retentionTime)) {
						retentionTimes.add(retentionTime);
					}
					/*
					 * Collect Traces
					 */
					for(IIon ion : scanMSD.getIons()) {
						if(!(ion instanceof IIonMSn ionMSn)) {
							continue;
						}
						for(TraceTandemMSD trace : traces) {
							if(matches(trace, ionMSn)) {
								/*
								 * Collect
								 */
								Map<TraceTandemMSD, List<IIon>> traceIons = scanRangesMap.get(retentionTime);
								if(traceIons == null) {
									traceIons = new HashMap<>();
									scanRangesMap.put(retentionTime, traceIons);
								}
								/*
								 * 173 > 121.2 @15
								 */
								List<IIon> ions = traceIons.get(trace);
								if(ions == null) {
									ions = new ArrayList<>();
									traceIons.put(trace, ions);
								}
								ions.add(ion);
							}
						}
					}
				}
			}
		}
		/*
		 * Compile
		 */
		if(separateTraces) {
			/*
			 * One Trace per Reference Chromatogram
			 */
			for(TraceTandemMSD trace : traces) {
				Map<Integer, List<IIon>> scanIonsMap = new HashMap<>();
				collectTraces(Arrays.asList(trace), scanRangesMap, retentionTimes, scanIonsMap);
				/*
				 * Several scans could be mapped to the same condensed retention time.
				 * Sum the intensities like in the combined mode. Otherwise, only the
				 * highest intensity would be kept and the original ion would be modified.
				 */
				condenseTraces(scanIonsMap);
				createReferenceChromatogram(chromatograms, chromatogramMSD, retentionTimes, scanIonsMap, headerField, trace.toString(), enforceFullTimeRange);
			}
		} else {
			/*
			 * All Traces in one Reference Chromatogram
			 */
			enforceFullTimeRange = true; // needed to map all traces
			Map<Integer, List<IIon>> scanIonsMap = new HashMap<>();
			collectTraces(new ArrayList<>(traces), scanRangesMap, retentionTimes, scanIonsMap);
			condenseTraces(scanIonsMap);
			createReferenceChromatogram(chromatograms, chromatogramMSD, retentionTimes, scanIonsMap, headerField, "Multiple Traces", enforceFullTimeRange);
		}

		return chromatograms;
	}

	private static void condenseTraces(Map<Integer, List<IIon>> scanIonsMap) {

		for(Map.Entry<Integer, List<IIon>> entry : scanIonsMap.entrySet()) {
			Map<String, IIon> ionMap = new HashMap<>();
			List<IIon> ions = entry.getValue();
			for(IIon ion : ions) {
				if(ion instanceof IIonMSn ionMSn) {
					IIonTransition ionTransition = ionMSn.getIonTransition();
					double parent = ionTransition.getQ1Ion();
					int daughter = AbstractIon.getIon(ionTransition.getQ3Ion());
					int collisionEnergy = (int)ionTransition.getCollisionEnergy();
					String tandemIdentifier = Double.toString(parent) + "_" + Integer.toString(daughter) + "_" + Integer.toString(collisionEnergy);
					IIon ionMapped = ionMap.get(tandemIdentifier);
					if(ionMapped == null) {
						double filter1Resolution = ionTransition.getQ1Resolution();
						double filter3Resolution = ionTransition.getQ3Resolution();
						int transitionGroup = ionTransition.getTransitionGroup();
						IIonTransition ionTransitionCopy = new IonTransition(parent, daughter, collisionEnergy, filter1Resolution, filter3Resolution, transitionGroup);
						ionMapped = new IonMSn(daughter, ion.getAbundance(), ionTransitionCopy);
						ionMap.put(tandemIdentifier, ionMapped);
					} else {
						ionMapped.setAbundance(ionMapped.getAbundance() + ion.getAbundance());
					}
				}
			}
			/*
			 * Clean Ions
			 */
			ions.clear();
			ions.addAll(ionMap.values());
		}
	}

	private static boolean matches(TraceTandemMSD trace, IIonMSn ion) {

		IIonTransition ionTransition = ion.getIonTransition();
		if(ionTransition != null) {
			if(trace.getParentMZ() == ionTransition.getQ1Ion()) {
				if(trace.getDaughterMZ() == ionTransition.getQ3Ion()) {
					if(trace.getCollisionEnergy() == ionTransition.getCollisionEnergy()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * May return null.
	 * 
	 * @param chromatogramFilterSettings
	 * @return {@link DecimalFormat}
	 */
	private static DecimalFormat getDecimalFormat(CondenseOption condenseOption) {

		DecimalFormat decimalFormat = null;
		switch(condenseOption) {
			case COARSE:
			case STANDARD:
			case SENSITIVE:
				decimalFormat = ValueFormat.getDecimalFormatEnglish(condenseOption.decimalPattern());
				break;
			default:
				break;
		}

		return decimalFormat;
	}

	private static void collectTraces(List<TraceTandemMSD> traces, Map<Integer, Map<TraceTandemMSD, List<IIon>>> scanRangesMap, List<Integer> retentionTimes, Map<Integer, List<IIon>> scanIonsMap) {

		for(int retentionTime : retentionTimes) {
			Map<TraceTandemMSD, List<IIon>> traceIons = scanRangesMap.get(retentionTime);
			if(traceIons != null) {
				for(TraceTandemMSD trace : traces) {
					List<IIon> ions = traceIons.get(trace);
					if(ions != null) {
						List<IIon> mappedIons = scanIonsMap.get(retentionTime);
						if(mappedIons == null) {
							mappedIons = new ArrayList<>();
							scanIonsMap.put(retentionTime, mappedIons);
						}
						mappedIons.addAll(ions);
					}
				}
			}
		}
	}

	private static void createReferenceChromatogram(List<IChromatogramMSD> chromatograms, IChromatogramMSD chromatogramMSD, List<Integer> retentionTimes, Map<Integer, List<IIon>> scanIonsMap, HeaderField headerField, String headerData, boolean enforceFullTimeRange) {

		IChromatogramMSD chromatogramReferenceMSD = new ChromatogramMSD();
		chromatogramReferenceMSD.setConverterId(chromatogramMSD.getConverterId());
		HeaderUtil.setHeaderData(chromatogramReferenceMSD, headerField, headerData);
		for(int retentionTime : retentionTimes) {
			RegularMassSpectrum scanMSD = new RegularMassSpectrum();
			scanMSD.setRetentionTime(retentionTime);
			List<IIon> ions = scanIonsMap.get(retentionTime);
			if(ions != null) {
				scanMSD.addIons(ions, false);
			}
			/*
			 * Scan
			 */
			if(enforceFullTimeRange) {
				chromatogramReferenceMSD.addScan(scanMSD);
			} else {
				if(ions != null && !ions.isEmpty()) {
					chromatogramReferenceMSD.addScan(scanMSD);
				}
			}
		}

		ChromatogramSupport.calculateScanIntervalAndDelay(chromatogramReferenceMSD);
		chromatograms.add(chromatogramReferenceMSD);
	}
}