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
}
