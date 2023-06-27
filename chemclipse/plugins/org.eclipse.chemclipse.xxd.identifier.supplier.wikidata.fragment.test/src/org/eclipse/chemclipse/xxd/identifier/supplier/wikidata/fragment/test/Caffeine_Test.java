/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.fragment.test;

import org.eclipse.chemclipse.xxd.identifier.supplier.wikidata.query.QueryEntity;

import junit.framework.TestCase;

public class Caffeine_Test extends TestCase {

	public void testCAS() {

		String item = QueryEntity.fromCAS("58-08-2");
		assertEquals("http://www.wikidata.org/entity/Q60235", item);
	}

	public void testName() {

		String url = QueryEntity.fromName("caffeine");
		assertEquals("http://www.wikidata.org/entity/Q60235", url);
	}
}
