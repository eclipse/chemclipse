/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

import junit.framework.TestCase;

public class ProcessEntry_3_Test extends TestCase {

	private ProcessEntry processEntry;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		Set<DataCategory> dataCategories = new HashSet<>();
		dataCategories.add(DataCategory.MSD);
		ProcessEntryContainer processEntryContainer = new ProcessMethod(dataCategories);
		processEntry = new ProcessEntry(processEntryContainer);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		processEntry.setActiveProfile("Test");
		processEntry.setSettings("Hello World");
		processEntry.setActiveProfile("My");
		processEntry.setActiveProfile("Entry");
		//
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		assertEquals("", processEntry.getSettings());
		assertEquals("Hello World", processEntry.getSettings("Test"));
		assertEquals("", processEntry.getSettings("My"));
		assertEquals("", processEntry.getSettings("Entry"));
	}

	public void test2() {

		processEntry.setActiveProfile("Test");
		processEntry.setSettings("Hello World");
		processEntry.setActiveProfile("My");
		processEntry.copySettings(null);
		processEntry.setActiveProfile("Entry");
		processEntry.copySettings("");
		processEntry.setActiveProfile("Extended");
		processEntry.copySettings("Testx");
		//
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		assertEquals("", processEntry.getSettings());
		assertEquals("Hello World", processEntry.getSettings("Test"));
		assertEquals("", processEntry.getSettings("My"));
		assertEquals("", processEntry.getSettings("Entry"));
		assertEquals("", processEntry.getSettings("Extended"));
	}

	public void test3() {

		processEntry.setActiveProfile("Test");
		processEntry.setSettings("Hello World");
		processEntry.setActiveProfile("My");
		processEntry.copySettings("Test");
		processEntry.setActiveProfile("Entry");
		processEntry.copySettings("Test");
		//
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		assertEquals("", processEntry.getSettings());
		assertEquals("Hello World", processEntry.getSettings("Test"));
		assertEquals("Hello World", processEntry.getSettings("My"));
		assertEquals("Hello World", processEntry.getSettings("Entry"));
	}
}
