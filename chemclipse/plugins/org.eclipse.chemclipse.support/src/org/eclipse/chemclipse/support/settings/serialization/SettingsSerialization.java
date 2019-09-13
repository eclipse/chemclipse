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
package org.eclipse.chemclipse.support.settings.serialization;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.chemclipse.support.settings.parser.InputValue;

/**
 * Interface that serializer of {@link InputValue}s can implement to support load/store of settings into strings
 *
 */
public interface SettingsSerialization {

	/**
	 * converts a map of values to String
	 * 
	 * @param values
	 * @return
	 * @throws IOException
	 */
	String toString(Map<InputValue, Object> values) throws IOException;

	/**
	 * Maps input values to actual values
	 * 
	 * @param inputValues
	 * @param content
	 * @return
	 * @throws IOException
	 */
	Map<InputValue, Object> fromString(Collection<? extends InputValue> inputValues, String content) throws IOException;

	/**
	 * Reads settings from string an constructs an instance initialized with the values
	 * 
	 * @param settingsClass
	 * @param content
	 * @return
	 * @throws IOException
	 */
	<Settings> Settings fromString(Class<Settings> settingsClass, String content) throws IOException;

	String toString(Object settingsObject) throws IOException;
}
