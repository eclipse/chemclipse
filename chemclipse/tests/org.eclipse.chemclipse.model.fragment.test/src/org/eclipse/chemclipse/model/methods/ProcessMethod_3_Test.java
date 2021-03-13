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

import junit.framework.TestCase;

public class ProcessMethod_3_Test extends TestCase {

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

		String profile = "Test";
		processMethod.setActiveProfile(profile);
		assertEquals(profile, processMethod.getActiveProfile());
		assertEquals(2, processMethod.getProfiles().size());
		//
		IProcessMethod processMethod2 = new ProcessMethod(processMethod);
		assertEquals(profile, processMethod2.getActiveProfile());
		assertEquals(2, processMethod2.getProfiles().size());
	}
}
