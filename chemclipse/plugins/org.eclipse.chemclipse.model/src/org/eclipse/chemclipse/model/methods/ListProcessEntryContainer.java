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

	@Override
	public Iterator<IProcessEntry> iterator() {

		return getEntries().iterator();
	}

	@Override
	public int getNumberOfEntries() {

		return entries.size();
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
		addProcessEntry(entry);
		return entry;
	}

	public List<IProcessEntry> getEntries() {

		if(readOnly) {
			return view;
		}
		return entries;
	}

	public void addProcessEntry(IProcessEntry processEntry) {

		getEntries().add(processEntry);
	}

	public void removeProcessEntry(IProcessEntry processEntry) {

		getEntries().remove(processEntry);
	}

	public void replaceProcessEntry(IProcessEntry oldEntry, IProcessEntry newEntry) {

		List<IProcessEntry> list = getEntries();
		int indexOf = list.indexOf(oldEntry);
		if(indexOf > -1) {
			list.set(indexOf, newEntry);
		} else {
			list.add(newEntry);
		}
	}

	public boolean isReadOnly() {

		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {

		this.readOnly = readOnly;
	}
}
