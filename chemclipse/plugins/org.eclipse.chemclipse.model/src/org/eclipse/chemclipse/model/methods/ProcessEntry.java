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

public class ProcessEntry extends ListProcessEntryContainer implements IProcessEntry {

	private String processorId = "";
	private String name = "";
	private String description = "";
	private String jsonSettings = "";
	private final ProcessEntryContainer parent;
	private final EnumSet<DataCategory> categories = EnumSet.noneOf(DataCategory.class);

	public ProcessEntry(ProcessEntryContainer parent) {
		this.parent = parent;
	}

	public ProcessEntry(IProcessEntry processEntry, ProcessEntryContainer newParent) {
		processorId = processEntry.getProcessorId();
		name = processEntry.getName();
		description = processEntry.getDescription();
		jsonSettings = processEntry.getSettings();
		parent = newParent;
		categories.addAll(processEntry.getDataCategories());
		setReadOnly(processEntry.isReadOnly());
		if(processEntry instanceof ProcessEntryContainer) {
			ProcessEntryContainer container = (ProcessEntryContainer)processEntry;
			for(IProcessEntry entry : container) {
				addProcessEntry(new ProcessEntry(entry, this));
			}
		}
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
