/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - refactor for use in generic processor framework
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.support.settings.parser.SettingsClassParser;
import org.eclipse.chemclipse.support.settings.parser.SettingsParser;

public abstract class AbstractProcessSupplier<SettingsClass> implements IProcessSupplier<SettingsClass> {

	private final String id;
	private final String name;
	private final String description;
	private final Class<SettingsClass> settingsClass;
	private final Set<DataCategory> dataTypes;
	private final IProcessTypeSupplier parent;
	//
	private SettingsClassParser<SettingsClass> classParser;
	private String category;

	public AbstractProcessSupplier(String id, String name, String description, Class<SettingsClass> settingsClass, IProcessTypeSupplier parent, DataCategory... dataTypes) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.settingsClass = settingsClass;
		this.parent = parent;
		//
		if(dataTypes.length == 0) {
			this.dataTypes = EnumSet.of(DataCategory.AUTO_DETECT);
		} else {
			this.dataTypes = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(dataTypes)));
		}
	}

	@Override
	public String getCategory() {

		if(category != null) {
			return category;
		}
		return IProcessSupplier.super.getCategory();
	}

	public void setCategory(String category) {

		this.category = category;
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public Class<SettingsClass> getSettingsClass() {

		return settingsClass;
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

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		AbstractProcessSupplier<?> other = (AbstractProcessSupplier<?>)obj;
		if(id == null) {
			if(other.id != null) {
				return false;
			}
		} else if(!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public Set<DataCategory> getSupportedDataTypes() {

		return dataTypes;
	}

	@Override
	public String toString() {

		return "ProcessorSupplier [id=" + id + ", name=" + name + ", description=" + description + ", settingsClass=" + settingsClass + "]";
	}

	@Override
	public IProcessTypeSupplier getTypeSupplier() {

		return parent;
	}

	@Override
	public SettingsParser<SettingsClass> getSettingsParser() {

		if(classParser == null) {
			classParser = new SettingsClassParser<>(getSettingsClass(), this);
		}
		return classParser;
	}
}