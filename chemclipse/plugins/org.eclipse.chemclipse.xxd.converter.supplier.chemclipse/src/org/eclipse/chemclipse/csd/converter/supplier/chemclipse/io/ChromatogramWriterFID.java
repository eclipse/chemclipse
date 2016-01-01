/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.csd.converter.io.IChromatogramCSDWriter;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1001;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1002;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1003;
import org.eclipse.chemclipse.csd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1004;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriterFID extends AbstractChromatogramWriter implements IChromatogramCSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramCSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		String versionSave = PreferenceSupplier.getVersionSave();
		IChromatogramCSDWriter chromatogramWriter;
		/*
		 * Check the requested version of the file to be exported.
		 * TODO Optimize
		 */
		if(versionSave.equals(IFormat.VERSION_1001)) {
			chromatogramWriter = new ChromatogramWriter_1001();
		} else if(versionSave.equals(IFormat.VERSION_1002)) {
			chromatogramWriter = new ChromatogramWriter_1002();
		} else if(versionSave.equals(IFormat.VERSION_1003)) {
			chromatogramWriter = new ChromatogramWriter_1003();
		} else {
			chromatogramWriter = new ChromatogramWriter_1004();
		}
		//
		chromatogramWriter.writeChromatogram(file, chromatogram, monitor);
	}
}
