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

public class ProcessMethod implements IProcessMethod {

	private String id = "";
	private String name = "";
	private String description = "";
	private String settings = "";
	private String type = ""; // MSD, CSD, ...

	public ProcessMethod() {
	}

	public ProcessMethod(String id, String name, String description, String settings, String type) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.settings = settings;
		this.type = type;
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
	public String getSettings() {

		return settings;
	}

	@Override
	public void setSettings(String settings) {

		this.settings = settings;
	}

	@Override
	public String getType() {

		return type;
	}

	@Override
	public void setType(String type) {

		this.type = type;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((settings == null) ? 0 : settings.hashCode());
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
		if(settings == null) {
			if(other.settings != null)
				return false;
		} else if(!settings.equals(other.settings))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "ProcessMethod [id=" + id + ", name=" + name + ", description=" + description + ", settings=" + settings + "]";
	}
}
