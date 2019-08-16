/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.processing.core;

import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

import junit.framework.TestCase;

public class ProcessingInfo_8_Test extends TestCase {

	private interface MyInterface {

		String getData();
	}

	private class MyClass implements MyInterface {

		private String data = "";

		public MyClass(String data) {

			this.data = data;
		}

		@Override
		public String getData() {

			return data;
		}
	}

	private IProcessingInfo<Object> processingInfo;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		processingInfo = new ProcessingInfo<>();
		MyClass myClass = new MyClass("Test");
		processingInfo.setProcessingResult(myClass);
		// processingInfo.setProcessingResult(null);
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testProcessingInfo_1() {

		try {
			MyInterface result = (MyInterface)processingInfo.getProcessingResult();
			assertNotNull(result);
			assertEquals("Test", result.getData());
		} catch(TypeCastException e) {
			assertTrue(true);
		}
	}

	public void testProcessingInfo_2() {

		try {
			processingInfo.getProcessingResult(Integer.class);
		} catch(TypeCastException e) {
			assertTrue(true);
		}
	}
}
