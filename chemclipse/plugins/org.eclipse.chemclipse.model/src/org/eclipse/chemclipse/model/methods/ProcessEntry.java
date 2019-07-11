/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove empty settings in case of null
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class ProcessEntry implements IProcessEntry {

	private static final Logger logger = Logger.getLogger(ProcessEntry.class);
	//
	private String processorId = "";
	private String name = "";
	private String description = "";
	private String jsonSettings = EMPTY_JSON_SETTINGS;
	private List<DataType> supportedDataTypes = new ArrayList<>();
	private Class<? extends IProcessSettings> processSettingsClass = null;

	public ProcessEntry() {
	}

	public ProcessEntry(IProcessEntry processEntry) {
		processorId = processEntry.getProcessorId();
		name = processEntry.getName();
		description = processEntry.getDescription();
		jsonSettings = processEntry.getJsonSettings();
		supportedDataTypes = processEntry.getSupportedDataTypes();
		processSettingsClass = processEntry.getProcessSettingsClass();
	}

	@Override
	public String getProcessorId() {

		return processorId;
	}

	@Override
	public void setProcessorId(String processorId) {

		this.processorId = processorId;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public String getJsonSettings() {

		if(jsonSettings == null) {
			return EMPTY_JSON_SETTINGS;
		}
		return jsonSettings;
	}

	@Override
	public void setJsonSettings(String jsonSettings) {

		this.jsonSettings = jsonSettings;
	}

	@Override
	public List<DataType> getSupportedDataTypes() {

		return supportedDataTypes;
	}

	@Override
	public Class<? extends IProcessSettings> getProcessSettingsClass() {

		return processSettingsClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setProcessSettingsClass(String symbolicName, String className) {

		if(symbolicName != null && className != null) {
			if(!symbolicName.equals("") && !className.equals("")) {
				try {
					Bundle bundle = Platform.getBundle(symbolicName);
					Class<IProcessSettings> clazz = (Class<IProcessSettings>)bundle.loadClass(className);
					setProcessSettingsClass(clazz);
				} catch(ClassNotFoundException e) {
					logger.warn(e);
				}
			}
		}
	}

	@Override
	public void setProcessSettingsClass(Class<? extends IProcessSettings> processSettingsClass) {

		this.processSettingsClass = processSettingsClass;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((processorId == null) ? 0 : processorId.hashCode());
		result = prime * result + ((jsonSettings == null) ? 0 : jsonSettings.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ProcessEntry other = (ProcessEntry)obj;
		if(processorId == null) {
			if(other.processorId != null)
				return false;
		} else if(!processorId.equals(other.processorId))
			return false;
		if(jsonSettings == null) {
			if(other.jsonSettings != null)
				return false;
		} else if(!jsonSettings.equals(other.jsonSettings))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "ProcessMethod [processorId=" + processorId + ", name=" + name + ", description=" + description + ", jsonSettings=" + jsonSettings + ", supportedDataTypes=" + supportedDataTypes + ", processSettingsClass=" + processSettingsClass + "]";
	}
}
