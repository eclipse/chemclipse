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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

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

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((entries == null) ? 0 : entries.hashCode());
		result = prime * result + (readOnly ? 1231 : 1237);
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
		ListProcessEntryContainer other = (ListProcessEntryContainer)obj;
		if(entries == null) {
			if(other.entries != null) {
				return false;
			}
		} else if(!entries.equals(other.entries)) {
			return false;
		}
		if(readOnly != other.readOnly) {
			return false;
		}
		return true;
	}
}
