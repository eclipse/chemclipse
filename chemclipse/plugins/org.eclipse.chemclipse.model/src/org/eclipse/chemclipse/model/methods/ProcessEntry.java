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

public class ProcessEntry implements IProcessEntry {

	private String processorId = "";
	private String name = "";
	private String description = "";
	private String jsonSettings = "";

	public ProcessEntry() {
	}

	public ProcessEntry(IProcessEntry processEntry) {
		processorId = processEntry.getProcessorId();
		name = processEntry.getName();
		description = processEntry.getDescription();
		jsonSettings = processEntry.getJsonSettings();
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
			return "";
		}
		return jsonSettings;
	}

	@Override
	public void setJsonSettings(String jsonSettings) {

		this.jsonSettings = jsonSettings;
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

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		ProcessEntry other = (ProcessEntry)obj;
		if(processorId == null) {
			if(other.processorId != null) {
				return false;
			}
		} else if(!processorId.equals(other.processorId)) {
			return false;
		}
		if(jsonSettings == null) {
			if(other.jsonSettings != null) {
				return false;
			}
		} else if(!jsonSettings.equals(other.jsonSettings)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		return "ProcessMethod [processorId=" + processorId + ", name=" + name + ", description=" + description + ", jsonSettings=" + jsonSettings + "]";
	}
}
