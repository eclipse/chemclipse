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
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

/**
 * a {@link SubProcessEntry} is an entry that has attached child entries, this is like a sub-method call in programming languages
 *
 */
public class SubProcessEntry extends ProcessEntry implements ProcessEntryContainer {

	private final List<IProcessEntry> subEntries = new ArrayList<>();

	public SubProcessEntry() {
		super();
	}

	public SubProcessEntry(IProcessEntry processEntry) {
		super(processEntry);
	}

	@Override
	public Iterator<IProcessEntry> iterator() {

		return subEntries.iterator();
	}

	@Override
	public int getNumberOfEntries() {

		return subEntries.size();
	}

	public List<IProcessEntry> getSubEntries() {

		return subEntries;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((subEntries == null) ? 0 : subEntries.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		SubProcessEntry other = (SubProcessEntry)obj;
		if(subEntries == null) {
			if(other.subEntries != null)
				return false;
		} else if(!subEntries.equals(other.subEntries))
			return false;
		return true;
	}
}
