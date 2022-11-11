/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;

import junit.framework.TestCase;

public class LibraryInformation_7_Test extends TestCase {

	private ILibraryInformation libraryInformation;
	//
	private ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(SeparationColumnType.DEFAULT);
	private ColumnIndexMarker columnIndexMarkerDefault = new ColumnIndexMarker(separationColumn);
	//
	private ISeparationColumn db5 = new SeparationColumn("DB-5", SeparationColumnType.NON_POLAR);
	private ColumnIndexMarker columnIndexMarkerDB5 = new ColumnIndexMarker(db5);

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		libraryInformation = new LibraryInformation();
	}

	@Override
	protected void tearDown() throws Exception {

		libraryInformation = null;
		super.tearDown();
	}

	public void test_1() {

		assertEquals(0.0f, libraryInformation.getRetentionIndex());
		columnIndexMarkerDefault.setRetentionIndex(20.5f);
		libraryInformation.add(columnIndexMarkerDefault);
		assertEquals(1, libraryInformation.getColumnIndexMarkers().size());
		assertEquals(20.5f, libraryInformation.getRetentionIndex());
	}

	public void test_2() {

		libraryInformation.setRetentionIndex(12.1f);
		assertEquals(12.1f, libraryInformation.getRetentionIndex());
		columnIndexMarkerDefault.setRetentionIndex(0.0f);
		libraryInformation.add(columnIndexMarkerDefault);
		assertEquals(1, libraryInformation.getColumnIndexMarkers().size());
		assertEquals(0.0f, libraryInformation.getRetentionIndex());
	}

	public void test_3() {

		assertEquals(0.0f, libraryInformation.getRetentionIndex());
		columnIndexMarkerDB5.setRetentionIndex(20.5f);
		libraryInformation.add(columnIndexMarkerDB5);
		assertEquals(2, libraryInformation.getColumnIndexMarkers().size());
		assertEquals(0.0f, libraryInformation.getRetentionIndex());
		assertEquals(20.5f, libraryInformation.getColumnIndexMarkers().get(1).getRetentionIndex());
	}
}