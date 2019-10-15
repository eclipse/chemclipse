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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

public final class ProcessEntry extends ListProcessEntryContainer implements IProcessEntry {

	private String processorId;
	private String jsonSettings;
	private final ProcessEntryContainer parent;
	private final EnumSet<DataCategory> categories = EnumSet.noneOf(DataCategory.class);

	public ProcessEntry(ProcessEntryContainer parent) {
		this(null, parent);
	}

	public ProcessEntry(IProcessEntry other, ProcessEntryContainer newParent) {
		super(other);
		parent = newParent;
		if(other != null) {
			processorId = other.getProcessorId();
			setName(other.getName());
			setDescription(other.getDescription());
			jsonSettings = other.getSettings();
			categories.addAll(other.getDataCategories());
			// read-only must be set at the end!
			setReadOnly(other.isReadOnly());
		}
	}

	@Override
	public String getProcessorId() {

		if(processorId == null) {
			return "-not set-";
		}
		return processorId;
	}

	public void setProcessorId(String processorId) {

		this.processorId = processorId;
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
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("ProcessEntry [processorId=");
		builder.append(processorId);
		builder.append(", name=");
		builder.append(getName());
		builder.append(", description=");
		builder.append(getDescription());
		builder.append(", jsonSettings=");
		builder.append(jsonSettings);
		builder.append(", parent=");
		builder.append(parent);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public Set<DataCategory> getDataCategories() {

		return Collections.unmodifiableSet(categories);
	}

	public void addDataCategory(DataCategory category) {

		categories.add(category);
	}

	public void removeDataCategory(DataCategory category) {

		categories.remove(category);
	}
}
