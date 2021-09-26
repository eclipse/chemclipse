/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Matthias Mailänder - add even/odd validation
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
	 * @param object:
	 *            the intermediate settings object to cast to for proper deserialization
	 * @return
	 * @throws IOException
	 */
	Map<InputValue, Object> fromObject(Collection<? extends InputValue> inputValues, Object object) throws IOException;

	/**
	 * Reads the settings from string and updates the given object with the values
	 * 
	 * @param settingsObject
	 * @param content
	 * @throws IOException
	 */
	void updateFromString(Object settingsObject, String content) throws IOException;

	String toString(Object settingsObject) throws IOException;
}
