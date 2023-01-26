/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.support;

import junit.framework.TestCase;

public class ColumnIndexSupport_2_Test extends TestCase {

	public void test1() {

		assertEquals("db 5", ColumnIndexSupport.adjustValue("DB 5", false, false));
	}

	public void test2() {

		assertEquals("DB 5", ColumnIndexSupport.adjustValue("DB 5", true, false));
	}

	public void test3() {

		assertEquals("db5", ColumnIndexSupport.adjustValue("DB 5", false, true));
	}

	public void test4() {

		assertEquals("DB5", ColumnIndexSupport.adjustValue("DB 5", true, true));
	}
}