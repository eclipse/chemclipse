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

/**
 * A {@link Filter} is responsible to filter different objects in a configurable manner.
 * This is only a base interface all Filters inherit from, a concrete Filter implementation might implement other interfaces to specify the exact items it is able to filter
 * 
 * @author Christoph Läubrich
 *
 */
public interface Filter<ConfigType> {

	/**
	 * The ID of a {@link Filter} can be used to identify the {@link Filter} an allow for example to store a reference to the filter in RDBMS or config files, for this a {@link Filter} must always return the same ID.
	 * A filter might choose to include a version if it has changed dramatically and is no longer compatible with the way it has worked before.
	 * 
	 * @return a persistent id that allows to identify this filter among others
	 */
	default String getID() {

		return "filterclass:" + getClass().getName();
	}

	/**
	 * 
	 * @return a human readable name for this {@link Filter}
	 */
	String getFilterName();

	/**
	 * 
	 * @return a brief description what this {@link Filter} does or an empty string if no description is available
	 */
	default String getFilterDescription() {

		return "";
	}

	/**
	 * Creates a new instance of the ConfigType for this {@link Filter}, probably initialized with some sensible defaults. A filter might then be later called with this config or the config might first be edited by the user.
	 * 
	 * @return the default-config for this filter
	 */
	ConfigType createNewConfiguration();
}
