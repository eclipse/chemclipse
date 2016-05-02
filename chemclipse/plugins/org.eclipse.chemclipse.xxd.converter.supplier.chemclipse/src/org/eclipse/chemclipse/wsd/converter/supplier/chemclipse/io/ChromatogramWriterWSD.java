/*******************************************************************************
 * Copyright (c) 2015, 2016 michaelchang.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * michaelchang - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.wsd.converter.io.IChromatogramWSDWriter;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.internal.io.ChromatogramWriter_1007;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriterWSD extends AbstractChromatogramWriter implements IChromatogramWSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		// not currently being used? needs multiple versions before start using.
		@SuppressWarnings("unused")
		String versionSave = PreferenceSupplier.getVersionSave();
		IChromatogramWSDWriter chromatogramWriter;
		// needs logic here to be able to choose different versions
		chromatogramWriter = new ChromatogramWriter_1007();
		//
		chromatogramWriter.writeChromatogram(file, chromatogram, monitor);
	}
}
