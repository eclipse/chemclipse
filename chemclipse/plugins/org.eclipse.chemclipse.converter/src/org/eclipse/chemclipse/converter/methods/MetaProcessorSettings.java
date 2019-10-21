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
package org.eclipse.chemclipse.converter.methods;

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

@SystemSettings(SystemSettingsStrategy.NEW_INSTANCE)
public class MetaProcessorSettings {

	@JsonProperty(value = "Settings", defaultValue = "")
	private Map<String, String> settingsMap;
	@JsonIgnore
	private final IProcessMethod method;

	public MetaProcessorSettings(MetaProcessorProcessSupplier processSupplier) {
		this.method = processSupplier.getMethod();
	}

	public Map<String, String> getSettingsMap() {

		return settingsMap;
	}

	/**
	 * 
	 * @return the {@link IProcessMethod} this settings are meant for
	 */
	@JsonIgnore
	public IProcessMethod getMethod() {

		return method;
	}

	public <T> ProcessorPreferences<T> getProcessorPreferences(IProcessEntry entry, IProcessSupplier<T> supplier) {

		ProcessorPreferences<T> delegate = entry.getPreferences(supplier);
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
					settingsMap.remove(entryId);
				} else {
					settingsMap.put(entryId, settings);
				}
			}

			@Override
			public boolean isUseSystemDefaults() {

				return delegate.isUseSystemDefaults();
			}

			@Override
			public void setUseSystemDefaults(boolean useSystemDefaults) {

			}

			@Override
			public void reset() {

			}

			@Override
			public IProcessSupplier<T> getSupplier() {

				return supplier;
			}

			@Override
			public String getUserSettingsAsString() {

				return settingsMap.getOrDefault(entryId, delegate.getUserSettingsAsString());
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
