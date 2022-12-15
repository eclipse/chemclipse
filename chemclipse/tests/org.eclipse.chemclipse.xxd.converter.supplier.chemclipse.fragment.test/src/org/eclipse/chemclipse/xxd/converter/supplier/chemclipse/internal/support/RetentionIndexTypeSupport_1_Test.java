/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;

import junit.framework.TestCase;

public class RetentionIndexTypeSupport_1_Test extends TestCase {

	public void test1() {

		assertEquals("POLAR", RetentionIndexTypeSupport.getBackwardCompatibleName(null));
	}

	public void test2() {

		assertEquals("POLAR", RetentionIndexTypeSupport.getBackwardCompatibleName(SeparationColumnType.DEFAULT));
	}

	public void test3() {

		assertEquals("POLAR", RetentionIndexTypeSupport.getBackwardCompatibleName(SeparationColumnType.POLAR));
	}

	public void test4() {

		assertEquals("SEMIPOLAR", RetentionIndexTypeSupport.getBackwardCompatibleName(SeparationColumnType.SEMI_POLAR));
	}

	public void test5() {

		assertEquals("APOLAR", RetentionIndexTypeSupport.getBackwardCompatibleName(SeparationColumnType.NON_POLAR));
	}
}