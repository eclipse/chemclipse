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

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.wsd.converter.io.AbstractChromatogramWSDReader;
import org.eclipse.chemclipse.wsd.converter.io.IChromatogramWSDReader;
import org.eclipse.chemclipse.wsd.converter.supplier.chemclipse.internal.io.ChromatogramReader_1005;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.ReaderHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class ChromatogramReaderWSD extends AbstractChromatogramWSDReader implements IChromatogramWSDReader {

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramOverview chromatogramOverview = null;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 * TODO Optimize
		 */
		IChromatogramWSDReader chromatogramReader = null;
		// want version 1004 for now
		chromatogramReader = new ChromatogramReader_1005();
		//
		if(chromatogramReader != null) {
			try {
				chromatogramOverview = chromatogramReader.readOverview(file, monitor);
			} catch(Exception e) {
				// chromatogramOverview = createChromatogramMSDFromFID(AbstractIon.TIC_ION, file, monitor);
			}
		} else {
			// chromatogramOverview = createChromatogramMSDFromFID(AbstractIon.TIC_ION, file, monitor);
		}
		return chromatogramOverview;
	}

	@Override
	public IChromatogramWSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramWSD chromatogramWSD = null;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 * TODO Optimize
		 */
		IChromatogramWSDReader chromatogramReader = null;
		// want version 1004 for now
		chromatogramReader = new ChromatogramReader_1005();
		if(chromatogramReader != null) {
			try {
				chromatogramWSD = chromatogramReader.read(file, monitor);
			} catch(Exception e) {
				// chromatogramWSD = createChromatogramMSDFromFID(18.0d, file, monitor);
			}
		} else {
			// chromatogramWSD = createChromatogramMSDFromFID(18.0d, file, monitor);
		}
		/*
		 * Load scan proxies in the background on demand.
		 * Only load the proxies if the file size is bigger than a
		 * minimum value.
		 */
		IEclipsePreferences preferences = PreferenceSupplier.INSTANCE().getPreferences();
		boolean useScanProxies = preferences.getBoolean(PreferenceSupplier.P_USE_SCAN_PROXIES, PreferenceSupplier.DEF_USE_SCAN_PROXIES);
		boolean loadScanProxiesInBackground = preferences.getBoolean(PreferenceSupplier.P_LOAD_SCAN_PROXIES_IN_BACKGROUND, PreferenceSupplier.DEF_LOAD_SCAN_PROXIES_IN_BACKGROUND);
		int minBytesToLoadInBackground = preferences.getInt(PreferenceSupplier.P_MIN_BYTES_TO_LOAD_IN_BACKGROUND, PreferenceSupplier.DEF_MIN_BYTES_TO_LOAD_IN_BACKGROUND);
		//
		if(useScanProxies) {
			if(loadScanProxiesInBackground && file.length() > minBytesToLoadInBackground) {
				/*
				 * Using the thread could lead to a
				 * java.util.ConcurrentModificationException
				 * if scans are deleted before they are loaded.
				 * We should find a way to handle this.
				 */
				final IChromatogramWSD chromatogram = chromatogramWSD;
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {

						// chromatogram.enforceLoadScanProxies(new NullProgressMonitor());
					}
				});
				t.start();
			}
		}
		//
		return chromatogramWSD;
	}
}
