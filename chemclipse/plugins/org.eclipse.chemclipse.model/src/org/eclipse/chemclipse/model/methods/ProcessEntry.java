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
 * Christoph Läubrich - refactoring to new object hierarchy
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

public class ProcessEntry extends ListProcessEntryContainer implements IProcessEntry {

	private String processorId = "";
	private String name = "";
	private String description = "";
	private String jsonSettings = "";
	private final ProcessEntryContainer parent;

	public ProcessEntry(ProcessEntryContainer parent) {
		this.parent = parent;
	}

	public ProcessEntry(IProcessEntry processEntry, ProcessEntryContainer newParent) {
		processorId = processEntry.getProcessorId();
		name = processEntry.getName();
		description = processEntry.getDescription();
		jsonSettings = processEntry.getSettings();
		parent = newParent;
	}

	@Override
	public String getProcessorId() {

		return processorId;
	}

	public void setProcessorId(String processorId) {

		this.processorId = processorId;
	}

	@Override
	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public String getSettings() {

		if(jsonSettings == null) {
			return "";
		}
		return jsonSettings;
	}

	@Override
	public void setSettings(String jsonSettings) {

		this.jsonSettings = jsonSettings;
	}

	@Override
	public ProcessEntryContainer getParent() {

		return parent;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((jsonSettings == null) ? 0 : jsonSettings.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parent == null) ? 0 : System.identityHashCode(parent));
		result = prime * result + ((processorId == null) ? 0 : processorId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		ProcessEntry other = (ProcessEntry)obj;
		if(description == null) {
			if(other.description != null) {
				return false;
			}
		} else if(!description.equals(other.description)) {
			return false;
		}
		if(jsonSettings == null) {
			if(other.jsonSettings != null) {
				return false;
			}
		} else if(!jsonSettings.equals(other.jsonSettings)) {
			return false;
		}
		if(name == null) {
			if(other.name != null) {
				return false;
			}
		} else if(!name.equals(other.name)) {
			return false;
		}
		if(parent == null) {
			if(other.parent != null) {
				return false;
			}
		} else if(parent != other.parent) {
			return false;
		}
		if(processorId == null) {
			if(other.processorId != null) {
				return false;
			}
		} else if(!processorId.equals(other.processorId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ProcessEntry [processorId=");
		builder.append(processorId);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", jsonSettings=");
		builder.append(jsonSettings);
		builder.append(", parent=");
		builder.append(parent);
		builder.append("]");
		return builder.toString();
	}
}
