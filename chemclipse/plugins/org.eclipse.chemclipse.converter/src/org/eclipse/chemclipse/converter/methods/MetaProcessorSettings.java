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
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.support.settings.SystemSettings;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * TODO
 * Adjust this settings to support profiles.
 */
@SystemSettings(SystemSettingsStrategy.NEW_INSTANCE)
public class MetaProcessorSettings {

	@JsonProperty(value = "Settings", defaultValue = "")
	private Map<String, String> settingsMap;
	@JsonProperty(value = "Defaults", defaultValue = "")
	private Map<String, Boolean> useDefaultMap;
	@JsonIgnore
	private final IProcessMethod method;

	public MetaProcessorSettings(MetaProcessorProcessSupplier processSupplier) {

		IProcessMethod processMethod = processSupplier.getMethod();
		this.method = processMethod;
	}

	private Map<String, String> getSettingsMap() {

		if(settingsMap == null) {
			settingsMap = new HashMap<>();
		}
		return settingsMap;
	}

	private Map<String, Boolean> getUseDefaultMap() {

		if(useDefaultMap == null) {
			useDefaultMap = new HashMap<>();
		}
		return useDefaultMap;
	}

	/**
	 * 
	 * @return the {@link IProcessMethod} this settings are meant for
	 */
	@JsonIgnore
	public IProcessMethod getMethod() {

		return method;
	}

	public <T> ProcessorPreferences<T> getProcessorPreferences(IProcessEntry entry, ProcessorPreferences<T> delegate) {

		if(delegate == null) {
			return null;
		}
		String entryId = getId(entry);
		return new ProcessorPreferences<T>() {

			@Override
			public DialogBehavior getDialogBehaviour() {

				// show only on explicit request
				return DialogBehavior.NONE;
			}

			@Override
			public void setAskForSettings(boolean askForSettings) {

			}

			@Override
			public void setUserSettings(String settings) {

				if(settings == null || settings.isEmpty()) {
					getSettingsMap().remove(entryId);
				} else {
					getSettingsMap().put(entryId, settings);
				}
			}

			@Override
			public boolean isUseSystemDefaults() {

				return getUseDefaultMap().getOrDefault(entryId, delegate.isUseSystemDefaults());
			}

			@Override
			public void setUseSystemDefaults(boolean useSystemDefaults) {

				getUseDefaultMap().put(entryId, useSystemDefaults);
			}

			@Override
			public void reset() {

				getSettingsMap().remove(entryId);
				getUseDefaultMap().remove(entryId);
			}

			@Override
			public IProcessSupplier<T> getSupplier() {

				return delegate.getSupplier();
			}

			@Override
			public String getUserSettingsAsString() {

				return getSettingsMap().getOrDefault(entryId, delegate.getUserSettingsAsString());
			}
		};
	}

	public static String getId(IProcessEntry entry) {

		ProcessEntryContainer parent = entry.getParent();
		if(parent instanceof ListProcessEntryContainer) {
			String id = String.valueOf(((ListProcessEntryContainer)parent).getEntries().indexOf(entry));
			if(parent instanceof IProcessEntry) {
				return getId((IProcessEntry)parent) + "." + id;
			} else {
				return id;
			}
		} else {
			throw new IllegalArgumentException("unsupported parent type");
		}
	}
}
