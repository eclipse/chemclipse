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
 *******************************************************************************/
package org.eclipse.chemclipse.model.filter;

import org.eclipse.chemclipse.filter.Filter;
import org.eclipse.chemclipse.filter.FilterList;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A {@link Filter} Extension interface for filters that can work on {@link ITotalScanSignals}s.
 * This is part of the Chemclipse FilterFramework, to make the Filter available simply register it with the OSGi Servicefactory under the {@link Filter} interface, implementors are encouraged to also register each filter under all sub(filter) interface.
 * 
 * @author Christoph Läubrich
 *
 * @param <ConfigType>
 */
public interface ITotalScanSignalsFilter<ConfigType> extends Filter<ConfigType> {

	/**
	 * Filters the given Collection of {@link ITotalScanSignals}s with this filter, the collection must be modifiable so the filter can either remove an item from the list or filter the item individually
	 * 
	 * @param filterItems
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @return a {@link IProcessingResult} that describes the outcome of the filtering, the result will be {@link Boolean#TRUE} if any item in the list was filter or {@link Boolean#FALSE} if no item was filtered or there was an error. The messages of the {@link IProcessingResult} may contain further information
	 * @throws IllegalArgumentException
	 *             if any of the given {@link ITotalScanSignals} are incompatible with this filter ({@link #acceptsITotalScanSignals(ITotalScanSignals)} returns <code>false</code> for them)
	 */
	IProcessingResult<Boolean> filterITotalScanSignalss(FilterList<ITotalScanSignals> filterItems, ConfigType configuration, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link ITotalScanSignals} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 * 
	 * @param item
	 *            the {@link ITotalScanSignals} to check
	 * @return <code>true</code> if this {@link ITotalScanSignals} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsITotalScanSignals(ITotalScanSignals item);

	/**
	 * Creates a new configuration that is specially suited for the given {@link ITotalScanSignals} type
	 * 
	 * @param item
	 * @return
	 */
	default ConfigType createConfiguration(ITotalScanSignals item) {

		return createNewConfiguration();
	}
}