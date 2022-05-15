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
package org.eclipse.chemclipse.model.support;

import org.eclipse.chemclipse.model.core.IChromatogram;

import junit.framework.TestCase;

public class RetentionIndexMap_5_Test extends TestCase {

	private RetentionIndexMap retentionIndexMap = new RetentionIndexMap();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		IChromatogram<?> chromatogram = null;
		retentionIndexMap.update(chromatogram);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(-1, retentionIndexMap.getRetentionTime(1000));
	}

	public void test2() {

		assertEquals(0.0f, retentionIndexMap.getRetentionIndex(1000));
	}
}