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

/**
 * a processor performs a specific task
 * 
 * @author Christoph Läubrich
 *
 */
public interface Processor<ConfigType> {

	/**
	 * 
	 * @return a human readable name for this {@link Processor}
	 */
	String getName();

	/**
	 * 
	 * @return a brief description what this {@link Processor} does or an empty string if no description is available
	 */
	default String getDescription() {

		return "";
	}

	/**
	 * The ID of a {@link Processor} can be used to identify the {@link Processor} and allow for example to store a reference to the Processor in RDBMS or config files, for this a {@link Processor} must always return the same ID.
	 * A Processor might choose to include a version if it has changed dramatically and is no longer compatible with the way it has worked before.
	 * 
	 * @return a persistent id that allows to identify this processor among others
	 */
	default String getID() {

		return "processor:class:" + getClass().getName();
	}

	/**
	 * A {@link Processor} can advertise the general category of data it is able to process, this can be used to narrow down {@link Processor} presented to a user
	 * 
	 * @return the default implementation returns {@link DataType#AUTO_DETECT} as the only choice to indicate that the caller has to determine the type by means of content type sensing
	 */
	default DataCategory[] getDataCategories() {

		return new DataCategory[]{DataCategory.AUTO_DETECT};
	}

	/**
	 * Creates a new instance of the ConfigType for this {@link Processor}, probably initialized with some sensible defaults. A {@link Processor} might then be later called with this config or the config might first be edited by the user.
	 * 
	 * @return the default-config for this filter
	 */
	default ConfigType createNewConfiguration() {

		try {
			Class<ConfigType> configClass = getConfigClass();
			if(configClass == null) {
				return null;
			}
			return configClass.newInstance();
		} catch(InstantiationException | IllegalAccessException e) {
			throw new UnsupportedOperationException("can't create default config class and the filter does not overrides createNewConfiguration");
		}
	}

	Class<ConfigType> getConfigClass();

	ProcessorCategory[] getProcessorCategories();
}
