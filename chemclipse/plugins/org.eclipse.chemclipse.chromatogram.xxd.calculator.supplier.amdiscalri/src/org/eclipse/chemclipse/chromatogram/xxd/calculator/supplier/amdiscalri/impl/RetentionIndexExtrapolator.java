/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.supplier.amdiscalri.model.RetentionIndexMarker;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.columns.RetentionIndexEntry;

public class RetentionIndexExtrapolator {

	private static final Logger logger = Logger.getLogger(RetentionIndexExtrapolator.class);
	private String[] standards = RetentionIndexCalculator.getStandards();

	public void extrapolateMissingAlkaneRanges(RetentionIndexMarker retentionIndexMarker) {

		/*
		 * At least two entries must exists to extrapolate
		 * the missing alkane entries.
		 */
		if(retentionIndexMarker != null && retentionIndexMarker.size() >= 2) {
			/*
			 * Map the entries.
			 */
			TreeMap<Integer, IRetentionIndexEntry> definedAlkanes = new TreeMap<>();
			Set<Integer> missingAlkanes = mapRanges(retentionIndexMarker, definedAlkanes);
			//
			extrapolateMiddleSection(retentionIndexMarker, definedAlkanes, missingAlkanes);
			extrapolateStartSection(retentionIndexMarker, definedAlkanes, missingAlkanes);
			extrapolateEndSection(retentionIndexMarker, definedAlkanes, missingAlkanes);
		}
	}

	private Set<Integer> mapRanges(RetentionIndexMarker retentionIndexMarker, TreeMap<Integer, IRetentionIndexEntry> definedAlkanes) {

		/*
		 * Create the alkane range C1 - C99
		 */
		Set<Integer> missingAlkanes = IntStream.rangeClosed(1, 99).boxed().collect(Collectors.toCollection(HashSet::new));
		//
		for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
			int alkaneNumber = extractAlkaneNumber(retentionIndexEntry.getName());
			if(alkaneNumber > RetentionIndexCalculator.ALKANE_MISSING) {
				definedAlkanes.put(alkaneNumber, retentionIndexEntry);
				missingAlkanes.remove(alkaneNumber);
			}
		}
		//
		return missingAlkanes;
	}

	private void extrapolateStartSection(RetentionIndexMarker retentionIndexMarker, TreeMap<Integer, IRetentionIndexEntry> definedAlkanes, Set<Integer> missingAlkanes) {

		Set<Integer> matchedAlkanes = new HashSet<>();
		for(Integer missingAlkane : missingAlkanes) {
			Map.Entry<Integer, IRetentionIndexEntry> entry1 = definedAlkanes.ceilingEntry(missingAlkane);
			if(entry1 != null) {
				Map.Entry<Integer, IRetentionIndexEntry> entry2 = definedAlkanes.higherEntry(entry1.getKey());
				if(entry2 != null) {
					IRetentionIndexEntry retentionIndexEntry = calculateAlkane(retentionIndexMarker, missingAlkane, entry1.getKey(), entry2.getKey());
					if(retentionIndexEntry != null && retentionIndexEntry.getRetentionTime() > 0) {
						definedAlkanes.put(missingAlkane, retentionIndexEntry);
						matchedAlkanes.add(missingAlkane);
						retentionIndexMarker.add(retentionIndexEntry);
					}
				}
			}
		}
		missingAlkanes.removeAll(matchedAlkanes);
	}

	private void extrapolateMiddleSection(RetentionIndexMarker retentionIndexMarker, TreeMap<Integer, IRetentionIndexEntry> definedAlkanes, Set<Integer> missingAlkanes) {

		Set<Integer> matchedAlkanes = new HashSet<>();
		for(Integer missingAlkane : missingAlkanes) {
			Map.Entry<Integer, IRetentionIndexEntry> entry1 = definedAlkanes.floorEntry(missingAlkane);
			Map.Entry<Integer, IRetentionIndexEntry> entry2 = definedAlkanes.ceilingEntry(missingAlkane);
			if(entry1 != null && entry2 != null) {
				IRetentionIndexEntry retentionIndexEntry = calculateAlkane(retentionIndexMarker, missingAlkane, entry1.getKey(), entry2.getKey());
				if(retentionIndexEntry != null && retentionIndexEntry.getRetentionTime() > 0) {
					definedAlkanes.put(missingAlkane, retentionIndexEntry);
					matchedAlkanes.add(missingAlkane);
					retentionIndexMarker.add(retentionIndexEntry);
				}
			}
		}
		missingAlkanes.removeAll(matchedAlkanes);
	}

	private void extrapolateEndSection(RetentionIndexMarker retentionIndexMarker, TreeMap<Integer, IRetentionIndexEntry> definedAlkanes, Set<Integer> missingAlkanes) {

		Set<Integer> matchedAlkanes = new HashSet<>();
		for(Integer missingAlkane : missingAlkanes) {
			Map.Entry<Integer, IRetentionIndexEntry> entry1 = definedAlkanes.floorEntry(missingAlkane);
			if(entry1 != null) {
				Map.Entry<Integer, IRetentionIndexEntry> entry2 = definedAlkanes.lowerEntry(entry1.getKey());
				if(entry2 != null) {
					/*
					 * entry2 is lower than entry1
					 * that's why calculateAlkane is called in the order: entry2.getKey(), entry1.getKey()
					 */
					IRetentionIndexEntry retentionIndexEntry = calculateAlkane(retentionIndexMarker, missingAlkane, entry2.getKey(), entry1.getKey());
					if(retentionIndexEntry != null && retentionIndexEntry.getRetentionTime() > 0) {
						definedAlkanes.put(missingAlkane, retentionIndexEntry);
						matchedAlkanes.add(missingAlkane);
						retentionIndexMarker.add(retentionIndexEntry);
					}
				}
			}
		}
		missingAlkanes.removeAll(matchedAlkanes);
	}

	private int extractAlkaneNumber(String shortcut) {

		int alkaneNumber = RetentionIndexCalculator.ALKANE_MISSING;
		try {
			if(shortcut.startsWith(RetentionIndexCalculator.ALKANE_PREFIX)) {
				String[] parts = shortcut.split(" ");
				if(parts.length > 0) {
					String part = parts[0];
					alkaneNumber = Integer.parseInt(part.replace(RetentionIndexCalculator.ALKANE_PREFIX, "").trim());
				}
			}
		} catch(NumberFormatException e) {
			logger.warn(e);
		}
		return alkaneNumber;
	}

	private IRetentionIndexEntry calculateAlkane(RetentionIndexMarker retentionIndexMarker, int target, int c1, int c2) {

		IRetentionIndexEntry retentionIndexEntry = null;
		if(c1 < c2) {
			IRetentionIndexEntry retentionIndexEntry1 = getRetentionIndexEntry(retentionIndexMarker, RetentionIndexCalculator.ALKANE_PREFIX + c1);
			IRetentionIndexEntry retentionIndexEntry2 = getRetentionIndexEntry(retentionIndexMarker, RetentionIndexCalculator.ALKANE_PREFIX + c2);
			if(retentionIndexEntry1 != null && retentionIndexEntry2 != null) {
				int distance = getDistance(retentionIndexEntry1, retentionIndexEntry2);
				int numberSections = c2 - c1;
				if(numberSections > 0) {
					int unit = distance / numberSections;
					int width = (retentionIndexEntry1.getRetentionTime() + retentionIndexEntry2.getRetentionTime()) / 2;
					int halfWidth = width / 2;
					int start = getStartRetentionTime(retentionIndexEntry1, retentionIndexEntry2, target, c1, c2, unit, halfWidth);
					int stop = start + width;
					int retentionTime = (int)(start + (stop - start) / 2.0d);
					float retentionIndex = target * RetentionIndexCalculator.ALKANE_FACTOR;
					String name = RetentionIndexCalculator.getAlkaneLabel(standards, target) + " -> Extrapolated";
					retentionIndexEntry = new RetentionIndexEntry(retentionTime, retentionIndex, name);
				}
			}
		}
		//
		return retentionIndexEntry;
	}

	private IRetentionIndexEntry getRetentionIndexEntry(RetentionIndexMarker retentionIndexMarker, String alkaneMarker) {

		for(IRetentionIndexEntry retentionIndexEntry : retentionIndexMarker) {
			if(retentionIndexEntry.getName().startsWith(alkaneMarker)) {
				return retentionIndexEntry;
			}
		}
		//
		return null;
	}

	private int getStartRetentionTime(IRetentionIndexEntry retentionIndexEntry1, IRetentionIndexEntry retentionIndexEntry2, int target, int c1, int c2, int unit, int halfWidth) {

		if(target < c1) {
			/*
			 * Left
			 */
			int offsetLeft = unit * (c1 - target) + halfWidth;
			return retentionIndexEntry1.getRetentionTime() - offsetLeft;
		} else if(target > c2) {
			/*
			 * Right
			 */
			int offsetLeft = unit * (target - c2) - halfWidth;
			return retentionIndexEntry2.getRetentionTime() + offsetLeft;
		} else {
			/*
			 * Center
			 */
			int offsetLeft = unit * (target - c1) - halfWidth;
			return retentionIndexEntry1.getRetentionTime() + offsetLeft;
		}
	}

	private int getDistance(IRetentionIndexEntry retentionIndexEntry1, IRetentionIndexEntry retentionIndexEntry2) {

		int distance = 0; // milliseconds
		if(retentionIndexEntry1 != null && retentionIndexEntry2 != null) {
			distance = retentionIndexEntry2.getRetentionTime() - retentionIndexEntry1.getRetentionTime();
		}
		//
		return distance;
	}
}