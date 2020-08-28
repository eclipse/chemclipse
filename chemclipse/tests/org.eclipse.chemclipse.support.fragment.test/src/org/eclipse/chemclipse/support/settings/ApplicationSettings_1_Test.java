/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

import junit.framework.TestCase;

public class ApplicationSettings_1_Test extends TestCase {

	public void test1() {

		assertNotNull(ApplicationSettings.getSystemTmpDirectory());
	}

	public void test2() {

		assertTrue(ApplicationSettings.getSystemTmpDirectory().exists());
	}
}
