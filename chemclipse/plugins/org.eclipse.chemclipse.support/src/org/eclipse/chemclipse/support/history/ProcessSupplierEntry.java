/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.history;

import java.util.Objects;

public class ProcessSupplierEntry {

	private String id = "";
	private String name = "";
	private String description = "";
	private String userSettings = "";

	public String getId() {

		return id;
	}

	public void setId(String id) {

		this.id = id;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getUserSettings() {

		return userSettings;
	}

	public void setUserSettings(String userSettings) {

		this.userSettings = userSettings;
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ProcessSupplierEntry other = (ProcessSupplierEntry)obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {

		return "ProcessSupplierEntry [id=" + id + ", name=" + name + ", description=" + description + ", userSettings=" + userSettings + "]";
	}
}