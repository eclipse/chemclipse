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
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

import junit.framework.TestCase;

public class ProcessMethod_1_Test extends TestCase {

	private IProcessMethod processMethod;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		Set<DataCategory> dataCategories = new HashSet<>();
		dataCategories.add(DataCategory.MSD);
		processMethod = new ProcessMethod(dataCategories);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processMethod.getActiveProfile());
		Set<String> profiles = processMethod.getProfiles();
		assertEquals(1, profiles.size());
		assertTrue(profiles.contains(ProcessEntryContainer.DEFAULT_PROFILE));
	}

	public void test2() {

		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processMethod.getActiveProfile());
		processMethod.addProfile("Test");
		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processMethod.getActiveProfile());
		Set<String> profiles = processMethod.getProfiles();
		assertEquals(2, profiles.size());
		assertTrue(profiles.contains(ProcessEntryContainer.DEFAULT_PROFILE));
		assertTrue(profiles.contains("Test"));
	}

	public void test3() {

		processMethod.setActiveProfile("Test");
		assertEquals("Test", processMethod.getActiveProfile());
		Set<String> profiles = processMethod.getProfiles();
		assertEquals(2, profiles.size());
		assertTrue(profiles.contains(ProcessEntryContainer.DEFAULT_PROFILE));
		assertTrue(profiles.contains("Test"));
	}

	public void test4() {

		processMethod.setActiveProfile(null);
		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processMethod.getActiveProfile());
		Set<String> profiles = processMethod.getProfiles();
		assertEquals(1, profiles.size());
		assertTrue(profiles.contains(ProcessEntryContainer.DEFAULT_PROFILE));
	}

	public void test5() {

		processMethod.setActiveProfile("Test");
		assertEquals("Test", processMethod.getActiveProfile());
		Set<String> profiles = processMethod.getProfiles();
		assertEquals(2, profiles.size());
		assertTrue(profiles.contains(ProcessEntryContainer.DEFAULT_PROFILE));
		assertTrue(profiles.contains("Test"));
		processMethod.deleteProfile("Test");
		assertEquals(1, processMethod.getProfiles().size());
		assertEquals(ProcessEntryContainer.DEFAULT_PROFILE, processMethod.getActiveProfile());
	}
}
