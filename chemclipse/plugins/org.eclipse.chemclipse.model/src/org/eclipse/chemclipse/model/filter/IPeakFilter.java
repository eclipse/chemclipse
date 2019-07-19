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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.processing.core.IProcessingResult;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.filter.FilterList;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A {@link Filter} Extension interface for filters that can work on {@link IPeak}s.
 * This is part of the Chemclipse FilterFramework, to make the Filter available simply register it with the OSGi Servicefactory under the {@link Filter} interface, implementors are encouraged to also register each filter under all sub(filter) interface.
 * 
 * @author Christoph Läubrich
 *
 * @param <ConfigType>
 */
public interface IPeakFilter<ConfigType> extends Filter<ConfigType> {

	/**
	 * Filters the given Collection of {@link IPeak}s with this filter, the collection must be modifiable so the filter can either remove an item from the list or filter the item individually
	 * 
	 * @param filterItems
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @return a {@link IProcessingResult} that describes the outcome of the filtering, the result will be {@link Boolean#TRUE} if any item in the list was filter or {@link Boolean#FALSE} if no item was filtered or there was an error. The messages of the {@link IProcessingResult} may contain further information
	 * @throws IllegalArgumentException
	 *             if any of the given {@link IPeak} are incompatible with this filter ({@link #acceptsIPeak(IPeak)} returns <code>false</code> for them)
	 */
	IProcessingResult<Boolean> filterIPeaks(FilterList<IPeak> filterItems, ConfigType configuration, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link IPeak} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 * 
	 * @param item
	 *            the {@link IPeak} to check
	 * @return <code>true</code> if this {@link IPeak} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsIPeak(IPeak item);

	/**
	 * Creates a new configuration that is specially suited for the given {@link IPeak} type
	 * 
	 * @param item
	 * @return
	 */
	default ConfigType createConfiguration(IPeak item) {

		return createNewConfiguration();
	}

	@Override
	default Class<ConfigType> getConfigClass() {

		return null;
	}
}