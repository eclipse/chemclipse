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
package org.eclipse.chemclipse.xxd.filter.support;

import java.util.Map;

import org.eclipse.chemclipse.support.settings.ISettingsDependency;
import org.eclipse.chemclipse.support.settings.SettingsPropertyState;

public class TargetsDeleteOptionDependency implements ISettingsDependency {

	private static final String PROPERTY = "property";
	private static final String REGEX_LEVEL = "(<|<=|>|>=)?(\\s+)?([0-9]+)";

	@Override
	public Map<String, SettingsPropertyState> getFieldStates(Object value) {

		SettingsPropertyState settingsPropertyState;
		if(value instanceof TargetsDeleteOption targetsDeleteOption) {
			settingsPropertyState = switch(targetsDeleteOption) {
				case ALL_TARGETS -> SettingsPropertyState.create().enabled(false).description("Delete all targets.");
				case UNVERIFIED_TARGETS -> SettingsPropertyState.create().enabled(false).description("Delete the unverified targets only.");
				case EMPTY_SMILES -> SettingsPropertyState.create().enabled(false).description("Define targets with empty SMILES.");
				case PROPERTY_IDENTIFIER -> SettingsPropertyState.create().enabled(true).description("Delete targets with the given identifier.");
				case PROPERTY_LEVEL -> SettingsPropertyState.create().enabled(true).description("Delete the targets given by level.").allowEmpty(false).regExp(REGEX_LEVEL).regExpDescription("must match e.g. '<= 2' or '3'.");
			};
		} else {
			settingsPropertyState = SettingsPropertyState.create();
		}

		return Map.of(PROPERTY, settingsPropertyState);
	}
}