/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io;

import java.io.File;

import org.eclipse.chemclipse.msd.converter.database.IDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLDatabaseImportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Ignore;

import junit.framework.TestCase;

@SuppressWarnings("rawtypes")
@Ignore
public class ImportConverterMslTestCase extends TestCase {

	protected File importFile;
	protected IMassSpectra massSpectra;
	protected IDatabaseImportConverter importConverter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		importConverter = new MSLDatabaseImportConverter();
		IProcessingInfo processingInfo = importConverter.convert(importFile, new NullProgressMonitor());
		massSpectra = (IMassSpectra)processingInfo.getProcessingResult();
	}

	@Override
	protected void tearDown() throws Exception {

		importFile = null;
		massSpectra = null;
		importConverter = null;
		super.tearDown();
	}
}
