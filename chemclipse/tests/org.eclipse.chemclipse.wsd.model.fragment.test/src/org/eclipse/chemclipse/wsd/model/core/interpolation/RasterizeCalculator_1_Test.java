/*******************************************************************************
 * Copyright (c) 2022, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.interpolation;

import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

public class RasterizeCalculator_1_Test extends TestCase {

	private TreeMap<Float, Float> dataOriginal = new TreeMap<>();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		dataOriginal.put(200.0f, 500.0f);
		dataOriginal.put(202.0f, 1000.0f);
		dataOriginal.put(204.0f, 1500.0f);
	}

	@Override
	protected void tearDown() throws Exception {

		dataOriginal = null;
		super.tearDown();
	}

	public void test1() {

		Map<Integer, Float> dataRasterized = RasterizeCalculator.apply(dataOriginal, 0);
		assertNull(dataRasterized);
	}
}
