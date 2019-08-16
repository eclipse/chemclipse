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
package org.eclipse.chemclipse.xxd.process.support;

import java.util.Set;

import org.eclipse.chemclipse.model.types.DataType;

public interface IProcessSupplier {

	/**
	 * 
	 * @return the ID of this processor
	 */
	String getId();

	/**
	 * 
	 * @return the name of this processor
	 */
	String getName();

	/**
	 * 
	 * @return a brief description of this processor
	 */
	String getDescription();

	/**
	 * 
	 * @return the settingsclass
	 */
	Class<?> getSettingsClass();

	Set<DataType> getSupportedDataTypes();

	/**
	 * 
	 * @return the preferences for this {@link IProcessSupplier}
	 */
	default ProcessorPreferences getPreferences() {

		return ProcessTypeSupport.getWorkspacePreferences(this);
	}
}