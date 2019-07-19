/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.chemclipse.model.types.DataType;

public class ProcessorSupplier implements IProcessSupplier {

	private String id = "";
	private String name = "";
	private String description = "";
	private Class<?> settingsClass;
	private Set<DataType> dataTypes;
	private ProcessorPreferences preferences;

	public ProcessorSupplier(String id, DataType[] dataTypes) {
		this.id = id;
		this.dataTypes = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(dataTypes)));
	}

	@Override
	public String getId() {

		return id;
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
	public Class<?> getSettingsClass() {

		return settingsClass;
	}

	public void setSettingsClass(Class<?> settingsClass) {

		this.settingsClass = settingsClass;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ProcessorSupplier other = (ProcessorSupplier)obj;
		if(id == null) {
			if(other.id != null)
				return false;
		} else if(!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public Set<DataType> getSupportedDataTypes() {

		return dataTypes;
	}

	@Override
	public String toString() {

		return "ProcessorSupplier [id=" + id + ", name=" + name + ", description=" + description + ", settingsClass=" + settingsClass + "]";
	}
}
