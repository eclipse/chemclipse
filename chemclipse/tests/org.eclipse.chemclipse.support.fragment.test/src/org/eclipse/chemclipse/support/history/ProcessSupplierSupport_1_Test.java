/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.history;

import junit.framework.TestCase;

public class ProcessSupplierSupport_1_Test extends TestCase {

	private String PAYLOAD = "VUdWaGEwWnBiSFJsY2pwbWFXeDBaWEk2Y0hKdlkyVnpjMjl5T21Oc1lYTnpPbTl5Wnk1bFkyeHBjSE5sTG1Ob1pXMWpiR2x3YzJVdWVIaGtMbVpwYkhSbGNpNXdaV0ZyY3k1RVpXeGxkR1ZRWldGcmMwWnBiSFJsY2c9PV9SR1ZzWlhSbElGQmxZV3R6X1JVMVFWRms9X2V5SlZibWxrWlc1MGFXWnBaV1FnVDI1c2VTSTZabUZzYzJVc0lrUmxiR1YwWlNCUVpXRnJjeUk2ZEhKMVpYMD0=";

	public void test1() {

		ProcessSupplierEntry processSupplierEntry = new ProcessSupplierEntry();
		processSupplierEntry.setId("PeakFilter:filter:processor:class:org.eclipse.chemclipse.xxd.filter.peaks.DeletePeaksFilter");
		processSupplierEntry.setName("Delete Peaks");
		processSupplierEntry.setDescription("");
		processSupplierEntry.setUserSettings("{\"Unidentified Only\":false,\"Delete Peaks\":true}");
		//
		assertEquals(PAYLOAD, ProcessSupplierSupport.toPayload(processSupplierEntry));
	}

	public void test2() {

		ProcessSupplierEntry processSupplierEntry = ProcessSupplierSupport.fromPayload(PAYLOAD);
		//
		assertEquals("PeakFilter:filter:processor:class:org.eclipse.chemclipse.xxd.filter.peaks.DeletePeaksFilter", processSupplierEntry.getId());
		assertEquals("Delete Peaks", processSupplierEntry.getName());
		assertEquals("", processSupplierEntry.getDescription());
		assertEquals("{\"Unidentified Only\":false,\"Delete Peaks\":true}", processSupplierEntry.getUserSettings());
	}
}