/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/

package org.eclipse.chemclipse.model.filter;

import java.util.Collection;
import java.util.function.Function;

import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.processing.core.IMessageConsumer;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A {@link Filter} Extension interface for filters that can work on {@link IMeasurement}s.
 * This is part of the Chemclipse FilterFramework, to make the Filter available simply register it with the OSGi Servicefactory under the {@link Filter} interface, implementors are encouraged to also register each filter under all sub(filter) interface.
 * 
 * @author Christoph Läubrich
 *
 * @param <ConfigType>
 */
public interface IMeasurementFilter<ConfigType> extends Filter<ConfigType> {

	/**
	 * Filters the given Collection of {@link IMeasurement}s with this filter and returns the result.
	 * The resulting Collection could either be the same or a new collection, might have more or less items
	 * 
	 * @param filterItems
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * @param resultTransformer
	 *            the transformer to invoke for producing the desired output result, filter might use this to produce results and then take some more actions with it or even produce alternative results or in msot cases simply return the result as is
	 * @param messageConsumer
	 *            Filters are meant to not throwing checked exceptions nor return no result if something goes wrong but report problems to the {@link IMessageConsumer} this allows the upstream caller to decide what to do
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @return the result of the processing or <code>null</code> if processing was canceled
	 * @throws IllegalArgumentException
	 *             if the given {@link IMeasurement}s are incompatible with this filter ({@link #acceptsIMeasurements(IMeasurement)} returns <code>false</code>)
	 */
	<ResultType> ResultType filterIMeasurements(Collection<? extends IMeasurement> filterItems, ConfigType configuration, Function<? super Collection<? extends IMeasurement>, ResultType> resultTransformer, IMessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException;

	/**
	 * Checks if the given {@link IMeasurement} is compatible with this filter, that means that this filter can be applied without throwing an {@link IllegalArgumentException}
	 * 
	 * @param item
	 *            the {@link IMeasurement} to check
	 * @return <code>true</code> if this {@link IMeasurement} can be applied, <code>false</code> otherwise
	 */
	boolean acceptsIMeasurements(Collection<? extends IMeasurement> items);

	/**
	 * Creates a new configuration that is specially suited for the given {@link IMeasurement} types
	 * 
	 * @param item
	 * @return a new configuration for this items or the default config if items is empty or no suitable configuration can be created
	 * @throws IllegalArgumentException
	 *             if the given {@link IMeasurement}s are incompatible with this filter ({@link #acceptsIMeasurements(IMeasurement)} returns <code>false</code>)
	 */
	default ConfigType createConfiguration(Collection<? extends IMeasurement> items) throws IllegalArgumentException {

		if(acceptsIMeasurements(items)) {
			return createNewConfiguration();
		} else {
			throw new IllegalArgumentException("incompatible items in collection");
		}
	}
}
