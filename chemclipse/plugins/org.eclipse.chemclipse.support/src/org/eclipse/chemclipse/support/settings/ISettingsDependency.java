/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

import java.util.Map;

/**
 * Referenced via {@link DependencySettingsProperty}. The implementation must
 * provide a public no-argument constructor.
 */
public interface ISettingsDependency {

	/**
	 * Returns the states of the dependent fields for the given value of the
	 * annotated field. The map key is the name of the dependent field, either
	 * the Java field name or the name defined in @JsonProperty(value = "...").
	 * Dependent fields that are not contained in the map keep their state.
	 * 
	 * @param value
	 *            the current value of the annotated field, an enum constant for enum fields
	 * @return Map
	 */
	Map<String, SettingsPropertyState> getFieldStates(Object value);
}