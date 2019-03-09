/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.model.filter;

import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterList;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A {@link Filter} Extension interface for filters that can work on {@link IChromatogramSelection}s.
 * This is part of the Chemclipse FilterFramework, to make the Filter available simply register it with the OSGi Servicefactory under the {@link Filter} interface, implementors are encouraged to also register each filter under all sub(filter) interface.
 *
 * @author Christoph Läubrich
 *
 * @param <ConfigType>
 */
public interface IChromatogramSelectionFilter<ConfigType> extends Filter<ConfigType> {

	/**
	 * Filters the given Collection of {@link IChromatogramSelection}s with this filter, the collection must be modifiable so the filter can either remove an item from the list or filter the item individually
	 *
	 * @param filterItems
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @return a {@link IProcessingResult} that describes the outcome of the filtering, the result will be {@link Boolean#TRUE} if any item in the list was filter or {@link Boolean#FALSE} if no item was filtered or there was an error. The messages of the {@link IProcessingResult} may contain further information
	 * @throws IllegalArgumentException
	 *             if any of the given {@link IChromatogramSelection} are incompatible with this filter ({@link #acceptsIChromatogramSelection(IChromatogramSelection)} returns <code>false</code> for them)
	 */
	IProcessingResult<Boolean> filterIChromatogramSelections(FilterList<IChromatogramSelection<?, ?>> filterItems, ConfigType configuration, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link IChromatogramSelection} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 *
	 * @param item
	 *            the {@link IChromatogramSelection} to check
	 * @return <code>true</code> if this {@link IChromatogramSelection} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsIChromatogramSelection(IChromatogramSelection<?, ?> item);

	/**
	 * Creates a new configuration that is specially suited for the given {@link IChromatogramSelection} type
	 *
	 * @param item
	 * @return
	 */
	default ConfigType createConfiguration(IChromatogramSelection<?, ?> item) {

		return createNewConfiguration();
	}

	static <T extends IPeak> FilterList<IPeak> peakList(IChromatogram<T> chromatogram, IChromatogramSelection<?, ?> selection) {

		List<T> peaks = selection == null ? chromatogram.getPeaks() : chromatogram.getPeaks(selection);
		return new FilterList<IPeak>() {

			@Override
			public Iterator<IPeak> iterator() {

				return FilterList.convert(peaks.iterator());
			}

			@Override
			public int size() {

				return peaks.size();
			}

			@Override
			public <X extends IPeak> void remove(X item) {

				for(T peak : peaks) {
					if(peak == item) {
						chromatogram.removePeak(peak);
						return;
					}
				}
			}
		};
	}

	static FilterList<IScan> scanList(IChromatogram<?> chromatogram) {

		List<IScan> scans = chromatogram.getScans();
		return new FilterList<IScan>() {

			@Override
			public Iterator<IScan> iterator() {

				return FilterList.convert(scans.iterator());
			}

			@Override
			public int size() {

				return scans.size();
			}
		};
	}
}
