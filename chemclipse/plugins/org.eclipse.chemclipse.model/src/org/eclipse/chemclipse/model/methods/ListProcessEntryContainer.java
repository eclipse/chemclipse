/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;

public class ListProcessEntryContainer implements ProcessEntryContainer {

	private final List<IProcessEntry> entries = new ArrayList<>();
	private final List<IProcessEntry> view = Collections.unmodifiableList(entries);
	private boolean readOnly;
	private String description;
	private String name;

	public ListProcessEntryContainer() {
		this(null);
	}

	public ListProcessEntryContainer(ProcessEntryContainer other) {
		if(other != null) {
			other.forEach(this::addProcessEntry);
		}
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
					throw new RuntimeException("creation of setings failed", e);
				}
			}
		}
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
}
