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

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
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

	public static final String CATEGORY = "Peak Filter";

	/**
	 * Filters the given Collection of {@link IPeak}s with this filter and returns the result.
	 * The resulting Collection could either be the same or a new collection, might have more or less items
	 * 
	 * @param filterItems
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * @param resultTransformer
	 *            the transformer to invoke for producing the desired output result, filter might use this to produce results and then take some more actions with it or even produce alternative results or in msot cases simply return the result as is
	 * @param messageConsumer
	 *            Filters are meant to not throwing checked exceptions nor return no result if something goes wrong but report problems to the {@link MessageConsumer} this allows the upstream caller to decide what to do
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @return the result of the processing or <code>null</code> if processing was canceled
	 * @throws IllegalArgumentException
	 *             if the given {@link IPeak}s are incompatible with this filter ({@link #acceptsIPeaks(IPeak)} returns <code>false</code>)
	 */
	<X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, ConfigType configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link IPeak} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 * 
	 * @param item
	 *            the {@link IPeak} to check
	 * @return <code>true</code> if this {@link IPeak} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsIPeaks(Collection<? extends IPeak> items);

	/**
	 * Creates a new configuration that is specially suited for the given {@link IPeak} types
	 * 
	 * @param item
	 * @return a new configuration for this items or the default config if items is empty or no suitable configuration can be created
	 * @throws IllegalArgumentException
	 *             if the given {@link IPeak}s are incompatible with this filter ({@link #acceptsIPeaks(IPeak)} returns <code>false</code>)
	 */
	default ConfigType createConfiguration(Collection<? extends IPeak> items) throws IllegalArgumentException {

		if(acceptsIPeaks(items)) {
			return createNewConfiguration();
		} else {
			throw new IllegalArgumentException("incompatible items in collection");
		}
	}
}