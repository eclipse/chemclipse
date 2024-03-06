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
package org.eclipse.chemclipse.converter.methods;

import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.processing.DataCategory;

public class MethodProcessSupport_1_Test extends MethodProcessSupportTestCase {

	public void test1() {

		DataCategory[] methodCategories = new DataCategory[]{};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(4, dataCategories.length);
		assertEquals("CSD", dataCategories[0].name());
		assertEquals("MSD", dataCategories[1].name());
		assertEquals("VSD", dataCategories[2].name());
		assertEquals("WSD", dataCategories[3].name());
	}

	public void test2() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(1, dataCategories.length);
		assertEquals("CSD", dataCategories[0].name());
	}

	public void test3() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(2, dataCategories.length);
		assertEquals("CSD", dataCategories[0].name());
		assertEquals("MSD", dataCategories[1].name());
	}

	public void test4() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(3, dataCategories.length);
		assertEquals("CSD", dataCategories[0].name());
		assertEquals("MSD", dataCategories[1].name());
		assertEquals("WSD", dataCategories[2].name());
	}

	public void test5() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD, DataCategory.VSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(4, dataCategories.length);
		assertEquals("CSD", dataCategories[0].name());
		assertEquals("MSD", dataCategories[1].name());
		assertEquals("VSD", dataCategories[2].name());
		assertEquals("WSD", dataCategories[3].name());
	}

	public void test6() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.MSD, DataCategory.WSD, DataCategory.VSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(3, dataCategories.length);
		assertEquals("MSD", dataCategories[0].name());
		assertEquals("VSD", dataCategories[1].name());
		assertEquals("WSD", dataCategories[2].name());
	}

	public void test7() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.WSD, DataCategory.VSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(2, dataCategories.length);
		assertEquals("VSD", dataCategories[0].name());
		assertEquals("WSD", dataCategories[1].name());
	}

	public void test8() {

		DataCategory[] methodCategories = new DataCategory[]{DataCategory.VSD};
		List<DataCategory[]> processCategories = Collections.emptyList();
		DataCategory[] dataCategories = MethodProcessSupport.getDataTypes(getProcessMethod(methodCategories, processCategories));
		//
		assertNotNull(dataCategories);
		assertEquals(1, dataCategories.length);
		assertEquals("VSD", dataCategories[0].name());
	}
}