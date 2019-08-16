/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.statistics;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.inference.OneWayAnova;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.INamedScanMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.numeric.statistics.model.AnovaStatistics;
import org.eclipse.chemclipse.numeric.statistics.model.IAnovaStatistics;
import org.eclipse.chemclipse.numeric.statistics.model.IStatistics;
import org.eclipse.chemclipse.numeric.statistics.model.IStatisticsElement;
import org.eclipse.chemclipse.numeric.statistics.model.StatisticsElement;
import org.eclipse.chemclipse.numeric.statistics.model.UnivariateStatistics;

public class StatisticsCalculator {

	public IStatisticsElement<IScanMSD> calculateStatistics(List<IScanMSD> massSpectra, StatisticsInputTypes id) {

		/*
		 * Create root statistics element
		 */
		IStatisticsElement<IScanMSD> statisticsElementRoot = new StatisticsElement<IScanMSD>("RootElement", massSpectra);
		int capacity = massSpectra.size();
		/*
		 * Creating a HashSet for the statistics
		 */
		Map<Double, List<IIon>> mzAbundances = new HashMap<Double, List<IIon>>();
		switch(id) {
			case STATISTICS_ABUNDANCE:
				for(IScanMSD massSpectrum : massSpectra) {
					for(IIon ion : massSpectrum.getIons()) {
						double mz = ion.getIon();
						if(mzAbundances.containsKey(mz)) {
							mzAbundances.get(mz).add(ion);
						} else {
							List<IIon> ions = new ArrayList<IIon>(capacity);
							ions.add(ion);
							mzAbundances.put(mz, ions);
						}
					}
				}
				break;
			default:
				// Should we throw here an exception?
				break;
		}
		/*
		 * Create leaves for the root statistics element
		 */
		List<IStatisticsElement<IIon>> statisticsElements = new ArrayList<IStatisticsElement<IIon>>();
		for(Double mz : mzAbundances.keySet()) {
			switch(id) {
				case STATISTICS_ABUNDANCE:
					List<IIon> ions = mzAbundances.get(mz);
					StatisticsElement<IIon> statisticsElementLeaf = new StatisticsElement<IIon>(mz, ions);
					/*
					 * Define the statistics object
					 */
					int sampleSize = ions.size();
					double[] abundances = new double[sampleSize];
					for(int i = 0; i < sampleSize; i++) {
						abundances[i] = ions.get(i).getAbundance();
					}
					IStatistics statistics = new UnivariateStatistics(abundances);
					statisticsElementLeaf.setStatisticsContent(statistics);
					statisticsElements.add(statisticsElementLeaf);
					break;
				default:
					// Should we throw here an exception?
					break;
			}
		}
		statisticsElementRoot.setStatisticsElements(statisticsElements);
		return statisticsElementRoot;
	}

	/*
	 * A JoinedScanMSD knows about the Origin/Substance name, e.g. we group the replicate experiments on the substance name
	 */
	public IStatisticsElement<IScanMSD> calculateInputForOneWayAnova(List<INamedScanMSD> groupedMassSpectra, StatisticsInputTypes id) {

		/*
		 * Converting objects
		 */
		List<IScanMSD> massSpectra = new ArrayList<IScanMSD>();
		for(INamedScanMSD groupedMassSpectrum : groupedMassSpectra) {
			massSpectra.add(groupedMassSpectrum);
		}
		// level 1
		IStatisticsElement<IScanMSD> statisticsElementRoot = new StatisticsElement<IScanMSD>("RootElement", massSpectra);
		/*
		 * Creating a HashSet for the statistics
		 */
		Map<Double, Map<String, List<IIon>>> mzSubstancesAbundances = new HashMap<Double, Map<String, List<IIon>>>();
		for(INamedScanMSD groupedMassSpectrum : groupedMassSpectra) {
			switch(id) {
				case ANOVA_ABUNDANCE:
					String substance = groupedMassSpectrum.getSubstanceName();
					for(IIon ion : groupedMassSpectrum.getIons()) {
						double mz = ion.getIon();
						if(mzSubstancesAbundances.containsKey(mz)) {
							if(mzSubstancesAbundances.get(mz).containsKey(substance)) {
								mzSubstancesAbundances.get(mz).get(substance).add(ion);
							} else {
								List<IIon> ions = new ArrayList<IIon>();
								ions.add(ion);
								mzSubstancesAbundances.get(mz).put(substance, ions);
							}
						} else {
							Map<String, List<IIon>> substancesAbundances = new HashMap<String, List<IIon>>();
							List<IIon> ions = new ArrayList<IIon>();
							ions.add(ion);
							substancesAbundances.put(substance, ions);
							mzSubstancesAbundances.put(mz, substancesAbundances);
						}
					}
					break;
				default:
					// Should we throw here an exception?
					break;
			}
		}
		/*
		 * Create the proper data structure for Anova analysis
		 */
		// for level 1
		List<IStatisticsElement<IStatisticsElement<IIon>>> substanceStatisticsElements = new ArrayList<IStatisticsElement<IStatisticsElement<IIon>>>();
		for(Entry<Double, Map<String, List<IIon>>> entry : mzSubstancesAbundances.entrySet()) {
			switch(id) {
				case ANOVA_ABUNDANCE:
					Double mz = entry.getKey();
					List<IStatisticsElement<IIon>> statisticsElements = new ArrayList<IStatisticsElement<IIon>>();
					for(String substance : entry.getValue().keySet()) {
						List<IIon> ions = entry.getValue().get(substance);
						// level 3 - this statistics, will be empty
						IStatisticsElement<IIon> statisticsElementLeaf = new StatisticsElement<IIon>(substance, ions);
						int size = ions.size();
						if(size > 1) {
							statisticsElements.add(statisticsElementLeaf);
						}
					}
					int statisticsElementsSize = statisticsElements.size();
					if(statisticsElementsSize > 1) {
						// level 2
						IStatisticsElement<IStatisticsElement<IIon>> substanceStatisticsElement = new StatisticsElement<IStatisticsElement<IIon>>(mz, statisticsElements);
						try {
							Method getdata = IIon.class.getMethod("getAbundance", new Class[0]);
							IStatistics statistics = new AnovaStatistics(substanceStatisticsElement, getdata);
							substanceStatisticsElement.setStatisticsContent(statistics);
						} catch(NoSuchMethodException e) {
							e.printStackTrace();
						} catch(SecurityException e) {
							e.printStackTrace();
						}
						substanceStatisticsElements.add(substanceStatisticsElement);
					}
					break;
				default:
					// Should we throw here an exception?
					break;
			}
		}
		statisticsElementRoot.setStatisticsElements(substanceStatisticsElements);
		/*
		 * One can use OneWayAnova.anovaFValue() or OneWayAnova.anovaPValue()
		 */
		return statisticsElementRoot;
	}

	/*
	 * A JoinedScanMSD knows about the Origin/Substance name, e.g. we group the replicate experiments on the substance name
	 */
	public Map<Double, Collection<double[]>> calculateInputForOneWayAnovaOLD(List<INamedScanMSD> groupedMassSpectra) {

		Map<Double, Map<String, List<Double>>> mzSubstancesAbundances = new HashMap<Double, Map<String, List<Double>>>();
		for(INamedScanMSD groupedMassSpectrum : groupedMassSpectra) {
			String substance = groupedMassSpectrum.getSubstanceName();
			for(IIon ion : groupedMassSpectrum.getIons()) {
				double mz = ion.getIon();
				double abundance = ion.getAbundance();
				if(mzSubstancesAbundances.containsKey(mz)) {
					if(mzSubstancesAbundances.get(mz).containsKey(substance)) {
						mzSubstancesAbundances.get(mz).get(substance).add(abundance);
					} else {
						List<Double> abundances = new ArrayList<Double>();
						abundances.add(abundance);
						mzSubstancesAbundances.get(mz).put(substance, abundances);
					}
				} else {
					Map<String, List<Double>> substancesAbundances = new HashMap<String, List<Double>>();
					List<Double> abundances = new ArrayList<Double>();
					abundances.add(abundance);
					substancesAbundances.put(substance, abundances);
					mzSubstancesAbundances.put(mz, substancesAbundances);
				}
			}
		}
		/*
		 * Create the proper data structure for OneWayAnova, maybe we need a hashmap based on {mz,Collection<double[]> - grouped by substance}
		 */
		Map<Double, Collection<double[]>> mzAnovaInputPairs = new HashMap<Double, Collection<double[]>>();
		for(Entry<Double, Map<String, List<Double>>> entry : mzSubstancesAbundances.entrySet()) {
			Double mz = entry.getKey();
			Collection<double[]> anovaInput = new ArrayList<double[]>();
			for(String substance : entry.getValue().keySet()) {
				List<Double> valuesList = entry.getValue().get(substance);
				int size = valuesList.size();
				/*
				 * Handle when the number of samples is 1 => no statistics can be done on it
				 */
				if(size > 1) {
					double[] values = new double[size];
					for(int i = 0; i < size; i++) {
						values[i] = valuesList.get(i);
					}
					anovaInput.add(values);
				}
			}
			/*
			 * Add only if at least two categories exists
			 */
			if(anovaInput.size() > 1) {
				mzAnovaInputPairs.put(mz, anovaInput);
			}
		}
		/*
		 * One can use OneWayAnova.anovaFValue() or OneWayAnova.anovaPValue()
		 */
		return mzAnovaInputPairs;
	}

	public Map<Double, Double> calculateAnovaFValuesOld(Map<Double, Collection<double[]>> mzAnovaInputPairs) {

		OneWayAnova anova = new OneWayAnova();
		Map<Double, Double> mzAnovaFPairs = new HashMap<Double, Double>();
		for(Entry<Double, Collection<double[]>> mzAnovaInputPair : mzAnovaInputPairs.entrySet()) {
			Double mz = mzAnovaInputPair.getKey();
			double fvalue = anova.anovaFValue(mzAnovaInputPair.getValue());
			mzAnovaFPairs.put(mz, fvalue);
		}
		return mzAnovaFPairs;
	}

	public Map<Double, Double> calculateAnovaFValues(IStatisticsElement<IScanMSD> anovaStatistics) {

		Map<Double, IAnovaStatistics> mzStatisticsPairs = getMzStatisticsPairs(anovaStatistics);
		Map<Double, Double> mzAnovaFPairs = new HashMap<Double, Double>();
		for(Entry<Double, IAnovaStatistics> mzAnovaInputPair : mzStatisticsPairs.entrySet()) {
			Double mz = mzAnovaInputPair.getKey();
			double fvalue = mzAnovaInputPair.getValue().getFValue();
			mzAnovaFPairs.put(mz, fvalue);
		}
		return mzAnovaFPairs;
	}

	public Map<Double, Double> calculateAnovaPValues(IStatisticsElement<IScanMSD> anovaStatistics) {

		Map<Double, IAnovaStatistics> mzStatisticsPairs = getMzStatisticsPairs(anovaStatistics);
		Map<Double, Double> mzAnovaPPairs = new HashMap<Double, Double>();
		for(Entry<Double, IAnovaStatistics> mzAnovaInputPair : mzStatisticsPairs.entrySet()) {
			Double mz = mzAnovaInputPair.getKey();
			double fvalue = mzAnovaInputPair.getValue().getPValue();
			mzAnovaPPairs.put(mz, fvalue);
		}
		return mzAnovaPPairs;
	}

	public Map<Double, IAnovaStatistics> calculateAnovaStatistics(IStatisticsElement<?> anovaStatistics) {

		return getMzStatisticsPairs(anovaStatistics);
	}

	private Map<Double, IAnovaStatistics> getMzStatisticsPairs(IStatisticsElement<?> anovaStatistics) {

		Map<Double, IAnovaStatistics> mzStatisticsPairs = new HashMap<Double, IAnovaStatistics>();
		for(IStatisticsElement<Object> elem : anovaStatistics.getStatisticsElements()) {
			Double mz = (Double)elem.getIdentifier();
			IAnovaStatistics value = (IAnovaStatistics)elem.getStatisticsContent();
			mzStatisticsPairs.put(mz, value);
		}
		return mzStatisticsPairs;
	}
}
