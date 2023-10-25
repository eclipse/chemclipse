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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.fragement.test.io;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.eclipse.chemclipse.pcr.converter.supplier.rdml.PathResolver;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.core.PCRImportConverter;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.fragment.test.TestPathHelper;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;

import junit.framework.TestCase;

public class Test_5_Raw_ITest extends TestCase {

	private IPlate plate;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		File importFile = new File(PathResolver.getAbsolutePath(TestPathHelper.TESTFILE_RAW_5));
		IProcessingInfo<IPlate> importProcessingInfo = PCRImportConverter.getInstance().convert(importFile, new NullProgressMonitor());
		plate = importProcessingInfo.getProcessingResult();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testDate() {

		LocalDateTime localDateTime = LocalDateTime.of(2019, Month.DECEMBER, 1, 5, 32, 51);
		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
		Date date = java.util.Date.from(zonedDateTime.toInstant());
		assertEquals(date, plate.getDate());
	}

	public void testWells() {

		assertEquals(28, plate.getWells().size());
		IWell well = plate.getWell(0);
		assertEquals("A1: 01", well.getLabel());
		IChannel channel = well.getChannels().get(0);
		assertEquals(0, channel.getId());
		assertEquals("dye1", channel.getDetectionName());
		assertEquals(0.10410682111978531, channel.getFluorescence().get(0));
	}
}
