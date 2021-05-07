/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - support instruments
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.processing.methods.AbstractProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;

public class ListProcessEntryContainer extends AbstractProcessEntryContainer {

	private final List<IProcessEntry> entries = new ArrayList<>();
	private final List<IProcessEntry> view = Collections.unmodifiableList(entries);
	//
	private boolean readOnly = false;
	private String description = "";
	private String name = "";
	//
	/*
	 * The default instrument is used for backward compatibility purposes.
	 */
	private final Set<String> profiles = new HashSet<>();
	private String activeProfile = DEFAULT_PROFILE;

	public ListProcessEntryContainer() {

		this(null);
		addDefaultProfile();
	}

	public ListProcessEntryContainer(ProcessEntryContainer other) {

		if(other != null) {
			profiles.addAll(other.getProfiles());
			activeProfile = other.getActiveProfile();
			other.forEach(this::addProcessEntry);
		}
		/*
		 * Add the default profile to be sure, that at least
		 * the "" profile is set for legacy purposes.
		 */
		addDefaultProfile();
	}

	@Override
	public Iterator<IProcessEntry> iterator() {

		return getEntries().iterator();
	}

	@Override
	public int getNumberOfEntries() {

		return entries.size();
	}

	@Override
	public String getDescription() {

		if(description == null) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	@Override
	public String getName() {

		if(name == null) {
			return "";
		}
		return name;
	}

	public void setName(String name) throws IllegalStateException {

		this.name = name;
	}

	/**
	 * 
	 * @return an empty entry, already added to this container
	 */
	public ProcessEntry createEntry() {

		return createEntry(null, null);
	}

	/**
	 * 
	 * @return a new entry initialized with the , already added to this container
	 */
	public <T> ProcessEntry createEntry(IProcessSupplier<T> supplier, T settings) {

		ProcessEntry entry = new ProcessEntry(this);
		if(supplier != null) {
			entry.setProcessorId(supplier.getId());
			entry.setName(supplier.getName());
			entry.setDescription(supplier.getDescription());
			if(settings != null) {
				try {
					entry.setSettings(entry.getPreferences(supplier).getSerialization().toString(settings));
				} catch(IOException e) {
					throw new RuntimeException("The creation of settings failed", e);
				}
			}
		}
		//
		entry.setActiveProfile(getActiveProfile());
		getEntries().add(entry);
		return entry;
	}

	public List<IProcessEntry> getEntries() {

		if(readOnly) {
			return view;
		}
		return entries;
	}

	/**
	 * Adds/imports the given entry to this container, the entry is copied and this container becomes the new parent, if that is not desired, {@link #getEntries()}.add(...) can be used, but care must be taken to not confuse parent references
	 * 
	 * @param processEntry
	 * @return
	 */
	public IProcessEntry addProcessEntry(IProcessEntry processEntry) {

		ProcessEntry newEntry = new ProcessEntry(processEntry, this);
		newEntry.setActiveProfile(getActiveProfile());
		getEntries().add(newEntry);
		return newEntry;
	}

	public void removeProcessEntry(IProcessEntry processEntry) {

		getEntries().remove(processEntry);
	}

	public boolean isReadOnly() {

		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;
	}

	@Override
	public String getActiveProfile() {

		return activeProfile;
	}

	@Override
	public void setActiveProfile(String activeProfile) {

		this.activeProfile = (activeProfile == null) ? DEFAULT_PROFILE : activeProfile;
		this.profiles.add(this.activeProfile);
		//
		for(IProcessEntry processEntry : getEntries()) {
			processEntry.setActiveProfile(this.activeProfile);
		}
	}

	@Override
	public void addProfile(String profile) {

		if(profile != null) {
			profiles.add(profile);
		}
	}

	@Override
	public void deleteProfile(String profile) {

		if(profile != null && !DEFAULT_PROFILE.equals(profile)) {
			profiles.remove(profile);
			for(IProcessEntry processEntry : getEntries()) {
				processEntry.deleteProfile(profile);
			}
			setActiveProfile(DEFAULT_PROFILE);
		}
	}

	@Override
	public Set<String> getProfiles() {

		return Collections.unmodifiableSet(profiles);
	}

	private void addDefaultProfile() {

		this.profiles.add(DEFAULT_PROFILE);
	}
}
