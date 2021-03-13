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
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

import junit.framework.TestCase;

public class ProcessEntry_1_Test extends TestCase {

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

		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processEntry.getActiveProfile());
		Map<String, String> settingsMap = processEntry.getSettingsMap();
		assertEquals(0, settingsMap.size()); // Settings are created on-the-fly
	}

	public void test2() {

		processEntry.setActiveProfile("Test");
		assertEquals("Test", processEntry.getActiveProfile());
		Map<String, String> settingsMap = processEntry.getSettingsMap();
		assertEquals(0, settingsMap.size()); // Settings are created on-the-fly
	}

	public void test3() {

		processEntry.setActiveProfile("Test");
		processEntry.setSettings("Hello World");
		assertEquals("Hello World", processEntry.getSettings());
		Map<String, String> settingsMap1 = processEntry.getSettingsMap();
		assertEquals(1, settingsMap1.size());
		assertTrue(settingsMap1.containsKey("Test"));
		//
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		processEntry.setSettings("");
		assertEquals("", processEntry.getSettings());
		Map<String, String> settingsMap2 = processEntry.getSettingsMap();
		assertEquals(2, settingsMap2.size());
		assertTrue(settingsMap2.containsKey("Test"));
		assertTrue(settingsMap2.containsKey(ProcessEntryContainer.DEFAULT_PROFILE));
	}

	public void test4() {

		processEntry.setActiveProfile("Test");
		processEntry.setSettings("Hello World");
		assertEquals("Hello World", processEntry.getSettings());
		Map<String, String> settingsMap1 = processEntry.getSettingsMap();
		assertEquals(1, settingsMap1.size());
		assertTrue(settingsMap1.containsKey("Test"));
		processEntry.deleteProfile("Test");
		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processEntry.getActiveProfile());
		//
		processEntry.setActiveProfile(ProcessEntryContainer.DEFAULT_PROFILE);
		processEntry.setSettings("");
		assertEquals("", processEntry.getSettings());
		Map<String, String> settingsMap2 = processEntry.getSettingsMap();
		assertEquals(1, settingsMap2.size());
		assertFalse(settingsMap2.containsKey("Test"));
		assertTrue(settingsMap2.containsKey(ProcessEntryContainer.DEFAULT_PROFILE));
	}
}
