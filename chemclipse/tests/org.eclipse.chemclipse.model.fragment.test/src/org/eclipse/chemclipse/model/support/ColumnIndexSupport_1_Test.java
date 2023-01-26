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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.columns.SeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.chemclipse.model.identifier.ColumnIndexMarker;
import org.eclipse.chemclipse.model.identifier.IColumnIndexMarker;

import junit.framework.TestCase;

public class ColumnIndexSupport_1_Test extends TestCase {

	private List<IColumnIndexMarker> columnIndexMarkers = new ArrayList<>();

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		columnIndexMarkers.add(new ColumnIndexMarker(new SeparationColumn("DB 1701", SeparationColumnType.SEMI_POLAR), 1230.8f));
		columnIndexMarkers.add(new ColumnIndexMarker(new SeparationColumn("FFAP X", SeparationColumnType.POLAR), 1456.7f));
		columnIndexMarkers.add(new ColumnIndexMarker(new SeparationColumn("DB 1", SeparationColumnType.NON_POLAR), 1302.8f));
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void test1() {

		assertEquals(1456.7f, ColumnIndexSupport.getRetentionIndex(columnIndexMarkers, "ffap x", false, false));
	}

	public void test2() {

		assertEquals(1456.7f, ColumnIndexSupport.getRetentionIndex(columnIndexMarkers, "FFAP X", true, false));
	}

	public void test3() {

		assertEquals(1456.7f, ColumnIndexSupport.getRetentionIndex(columnIndexMarkers, "ffapx", false, true));
	}

	public void test4() {

		assertEquals(1456.7f, ColumnIndexSupport.getRetentionIndex(columnIndexMarkers, "FFAPX", true, true));
	}
}