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
package org.eclipse.chemclipse.processing.filter;

/**
 * Object can implement this interface to promote that they are filtered
 * 
 * @author Christoph Läubrich
 *
 * @param <FilteredType>
 */
public interface Filtered<FilteredType, ConfigType> {

	/**
	 * 
	 * @return the context that was responsible for filtering this object
	 */
	FilterContext<FilteredType, ConfigType> getFilterContext();
}
