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

import org.eclipse.chemclipse.msd.converter.database.IDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.database.IDatabaseImportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLDatabaseExportConverter;
import org.eclipse.chemclipse.msd.converter.supplier.amdis.converter.msl.MSLDatabaseImportConverter;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;

import junit.framework.TestCase;

/**
 * Imports a msl file.
 *
 * @author eselmeister
 */
public class MassSpectrumExportConverterTestCase extends TestCase {

	protected File exportFile;
	protected IDatabaseExportConverter exportConverter;
	protected File importFile;
	protected IMassSpectra massSpectra;
	protected IDatabaseImportConverter importConverter;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		exportConverter = new MSLDatabaseExportConverter();
		importConverter = new MSLDatabaseImportConverter();
	}

	@Override
	protected void tearDown() throws Exception {

		if(exportFile.exists()) {
			exportFile.delete();
		}
		exportFile = null;
		exportConverter = null;
		importConverter = null;
		massSpectra = null;
		//
		System.gc();
		//
		super.tearDown();
	}
}
