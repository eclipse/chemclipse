/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

public final class ProcessEntry extends ListProcessEntryContainer implements IProcessEntry {

	private final ProcessEntryContainer parent;
	private final EnumSet<DataCategory> categories = EnumSet.noneOf(DataCategory.class);
	private final Map<String, String> jsonSettingsMap = new HashMap<>();
	//
	private String processorId;
	private String activeProfile = DEFAULT_PROFILE;

	public ProcessEntry(ProcessEntryContainer parent) {

		this(null, parent);
	}

	public ProcessEntry(IProcessEntry other, ProcessEntryContainer newParent) {

		super(other);
		parent = newParent;
		if(other != null) {
			/*
			 * Transfer the items.
			 */
			processorId = other.getProcessorId();
			setName(other.getName());
			setDescription(other.getDescription());
			jsonSettingsMap.clear();
			jsonSettingsMap.putAll(other.getSettingsMap());
			categories.addAll(other.getDataCategories());
			/*
			 * read-only must be set at the end!
			 */
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
	public String getActiveProfile() {

		return activeProfile;
	}

	@Override
	public void setActiveProfile(String activeProfile) {

		this.activeProfile = (activeProfile == null) ? DEFAULT_PROFILE : activeProfile;
	}

	@Override
	public void deleteProfile(String profile) {

		if(profile != null && !DEFAULT_PROFILE.equals(profile)) {
			jsonSettingsMap.remove(profile);
			setActiveProfile(DEFAULT_PROFILE);
		}
	}

	@Override
	public String getSettings() {

		return jsonSettingsMap.getOrDefault(activeProfile, "");
	}

	@Override
	public String getSettings(String profile) {

		return jsonSettingsMap.getOrDefault(profile, "");
	}

	@Override
	public Map<String, String> getSettingsMap() {

		return Collections.unmodifiableMap(jsonSettingsMap);
	}

	@Override
	public void setSettings(String jsonSettings) {

		jsonSettingsMap.put(activeProfile, jsonSettings);
	}

	@Override
	public void copySettings(String profile) {

		if(profile != null && !profile.isEmpty()) {
			String jsonSettings = jsonSettingsMap.get(profile);
			if(jsonSettings != null) {
				jsonSettingsMap.put(activeProfile, jsonSettings);
			}
		}
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
		builder.append(", jsonSettingsMap=");
		for(Map.Entry<String, String> entry : jsonSettingsMap.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue());
			builder.append(",");
		}
		builder.append("parent=");
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
