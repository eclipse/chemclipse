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

import java.util.Date;

/**
 * Object can implement this interface to promote that they are filtered
 * 
 * @author Christoph Läubrich
 *
 * @param <FilteredType>
 */
public interface Filtered<FilteredType> {

	/**
	 * 
	 * @return the filtered object
	 */
	FilteredType getFilteredObject();

	/**
	 * 
	 * @return the time when this filter was applied
	 */
	Date getFilterTime();

	/**
	 * 
	 * @return the filter that was responsible for filtering this object or <code>null</code> if this is not known
	 */
	default Filter<?> getFilter() {

		return null;
	}
}
