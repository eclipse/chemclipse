/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;

public class ProcessMethod implements IProcessMethod {

	private String id = "";
	private String name = "";
	private String description = "";
	private String jsonSettings = "{}";
	private List<DataType> supportedDataTypes = new ArrayList<>();
	private Class<? extends IProcessSettings> processSettingsClass = null;

	public ProcessMethod(String id, String name, String description) {
		this.id = id;
		this.name = name;
		this.description = description;
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public void setId(String id) {

		this.id = id;
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

	@Override
	public void setProcessSettingsClass(Class<? extends IProcessSettings> processSettingsClass) {

		this.processSettingsClass = processSettingsClass;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ProcessMethod other = (ProcessMethod)obj;
		if(id == null) {
			if(other.id != null)
				return false;
		} else if(!id.equals(other.id))
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

		return "ProcessMethod [id=" + id + ", name=" + name + ", description=" + description + ", jsonSettings=" + jsonSettings + ", supportedDataTypes=" + supportedDataTypes + ", processSettingsClass=" + processSettingsClass + "]";
	}
}
