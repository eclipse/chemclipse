/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable profiles
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.chemclipse.model.methods.ListProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@SystemSettings(SystemSettingsStrategy.NEW_INSTANCE)
public class MetaProcessorSettings {

	@JsonProperty(value = "Settings", defaultValue = "")
	private Map<String, String> settingsMap = new HashMap<>();
	@JsonProperty(value = "Defaults", defaultValue = "")
	private Map<String, Boolean> defaultMap = new HashMap<>();
	@JsonIgnore
	private final IProcessMethod processMethod;

	public MetaProcessorSettings(MetaProcessorProcessSupplier processSupplier) {

		this.processMethod = processSupplier.getProcessMethod();
	}

	/**
	 * 
	 * @return the {@link IProcessMethod} this settings are meant for
	 */
	@JsonIgnore
	public IProcessMethod getProcessMethod() {

		return processMethod;
	}

	public <T> IProcessorPreferences<T> getProcessorPreferences(IProcessEntry processEntry, IProcessorPreferences<T> processorPreferences) {

		if(processorPreferences == null) {
			return null;
		}
		/*
		 * Get the specific element position and active profile marker.
		 * .metadata/.plugins/org.eclipse.core.runtime/.settings/org.eclipse.chemclipse.processing.supplier.IProcessSupplier.prefs
		 */
		String processEntryIdentifier = getProcessEntryIdentifier(processEntry);
		return new IProcessorPreferences<T>() {

			@Override
			public DialogBehavior getDialogBehaviour() {

				return DialogBehavior.NONE;
			}

			@Override
			public void setAskForSettings(boolean askForSettings) {

				/*
				 * Ignore this setting.
				 */
			}

			@Override
			public void setUserSettings(String settings) {

				if(settings == null || settings.isEmpty()) {
					settingsMap.remove(processEntryIdentifier);
				} else {
					settingsMap.put(processEntryIdentifier, settings);
				}
			}

			@Override
			public boolean isUseSystemDefaults() {

				return defaultMap.getOrDefault(processEntryIdentifier, processorPreferences.isUseSystemDefaults());
			}

			@Override
			public void setUseSystemDefaults(boolean useSystemDefaults) {

				defaultMap.put(processEntryIdentifier, useSystemDefaults);
			}

			@Override
			public void reset() {

				settingsMap.remove(processEntryIdentifier);
				defaultMap.remove(processEntryIdentifier);
			}

			@Override
			public IProcessSupplier<T> getSupplier() {

				return processorPreferences.getSupplier();
			}

			@Override
			public String getUserSettingsAsString() {

				return settingsMap.getOrDefault(processEntryIdentifier, processorPreferences.getUserSettingsAsString());
			}
		};
	}

	/**
	 * Create an unique id. The active profile is tracked too.
	 * 
	 * @param processEntry
	 * @return String
	 */
	private static String getProcessEntryIdentifier(IProcessEntry processEntry) {

		ProcessEntryContainer parent = processEntry.getParent();
		String activeProfile = parent.getActiveProfile().replaceAll(" ", "").replaceAll("\\P{InBasic_Latin}", "");
		//
		if(parent instanceof ListProcessEntryContainer) {
			String identifier = String.valueOf(((ListProcessEntryContainer)parent).getEntries().indexOf(processEntry));
			if(parent instanceof IProcessEntry) {
				return getProcessEntryIdentifier((IProcessEntry)parent) + "." + activeProfile + "." + identifier;
			} else {
				return identifier + "." + activeProfile;
			}
		} else {
			throw new IllegalArgumentException("The parent processor type is not supported.");
		}
	}
}
