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
package org.eclipse.chemclipse.filter;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * A {@link FilterFactory} service allows access to all currently known {@link Filter} in the system
 * 
 * @author Christoph Läubrich
 *
 */
public interface FilterFactory {

	/**
	 * Returns all filter know to this {@link FilterFactory} that match the given acceptor (if given) and given FilterType
	 * 
	 * @param filterType
	 *            the subtype of the filter or {@link Filter}.class to fetch any filter that is know to the factory
	 * @param acceptor
	 *            an acceptor function that can narrow the result or <code>null</code> if all Filters should be returned
	 * @return the filters that are matched
	 */
	<FilterType extends Filter<?>> Collection<FilterType> getFilters(Class<FilterType> filterType, BiFunction<FilterType, Map<String, ?>, Boolean> acceptor);
}
