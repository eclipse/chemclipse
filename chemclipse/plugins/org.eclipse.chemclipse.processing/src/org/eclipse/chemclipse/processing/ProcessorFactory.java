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
package org.eclipse.chemclipse.processing;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * A {@link ProcessorFactory} service allows access to all currently known {@link Filter} in the system
 * 
 * @author Christoph Läubrich
 *
 */
public interface ProcessorFactory {

	/**
	 * Returns all processors know to this {@link ProcessorFactory} that match the given processorType and acceptor (if given)
	 * 
	 * @param processorType
	 *            the subtype of the {@link Processor} to fetch
	 * @param acceptor
	 *            an acceptor function that can narrow the result or <code>null</code> if all {@link Processor}s should be returned
	 * @return the filters that are matched
	 */
	<T extends Processor<?>> Collection<T> getProcessors(Class<T> processorType, BiPredicate<? super T, Map<String, ?>> acceptor);

	/**
	 * Helper method to create generic Class types for subinterfaces that satisfy the {@link #getFilters(Class, BiFunction)} method, e.g.
	 * <pre>Collection&lt;IScanFilter<?>> scanFilter = filterFactory.getFilters(FilterFactory.genericClass(IScanFilter.class), new BiFunction&lt;IScanFilter<?>, Map&lt;String, ?>, Boolean>() { ...});</pre>
	 * 
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	static <T> Class<T> genericClass(Class<?> cls) {

		return (Class<T>)cls;
	}
}
