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
import org.eclipse.chemclipse.model.columns.SeparationColumnType;

import junit.framework.TestCase;

public class LibraryInformation_6_Test extends TestCase {

	private ILibraryInformation libraryInformation;
	private ISeparationColumn db5 = new SeparationColumn("DB-5", SeparationColumnType.NON_POLAR);
	private ISeparationColumn db1701 = new SeparationColumn("DB-1701", SeparationColumnType.SEMI_POLAR);
	private ISeparationColumn ffap = new SeparationColumn("FFAP", SeparationColumnType.POLAR);

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
	}

	public void test_2() {

		libraryInformation.setRetentionIndex(725.5f);
		assertEquals(725.5f, libraryInformation.getRetentionIndex());
	}

	public void test_3() {

		assertEquals(1, libraryInformation.getColumnIndexMarkers().size());
	}

	public void test_4() {

		libraryInformation.setRetentionIndex(725.5f);
		libraryInformation.add(new ColumnIndexMarker(db5, 715.4f));
		libraryInformation.add(new ColumnIndexMarker(db1701, 729.3f));
		libraryInformation.add(new ColumnIndexMarker(ffap, 717.2f));
		assertEquals(725.5f, libraryInformation.getRetentionIndex());
		assertEquals(4, libraryInformation.getColumnIndexMarkers().size());
	}

	public void test_5() {

		libraryInformation.delete(null);
		assertEquals(1, libraryInformation.getColumnIndexMarkers().size());
	}

	public void test_6() {

		libraryInformation.setRetentionIndex(725.5f);
		//
		IColumnIndexMarker markerDefault = null;
		IColumnIndexMarker markerDB5 = new ColumnIndexMarker(db5, 715.4f);
		IColumnIndexMarker markerDB1701 = new ColumnIndexMarker(db1701, 729.3f);
		IColumnIndexMarker markerFFAP = new ColumnIndexMarker(ffap, 717.2f);
		//
		libraryInformation.add(markerDB5);
		libraryInformation.add(markerDB1701);
		libraryInformation.add(markerFFAP);
		//
		for(IColumnIndexMarker columnIndexMarker : libraryInformation.getColumnIndexMarkers()) {
			if(columnIndexMarker.getSeparationColumn().getSeparationColumnType().equals(SeparationColumnType.DEFAULT)) {
				markerDefault = columnIndexMarker;
			}
		}
		//
		assertEquals(4, libraryInformation.getColumnIndexMarkers().size());
		libraryInformation.delete(markerDefault);
		assertEquals(4, libraryInformation.getColumnIndexMarkers().size());
		libraryInformation.delete(markerDB5);
		assertEquals(3, libraryInformation.getColumnIndexMarkers().size());
		libraryInformation.delete(markerDB1701);
		assertEquals(2, libraryInformation.getColumnIndexMarkers().size());
		libraryInformation.delete(markerFFAP);
		assertEquals(1, libraryInformation.getColumnIndexMarkers().size());
	}
}