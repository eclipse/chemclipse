/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add new version
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.ocx.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.csd.converter.supplier.ocx.io.ChromatogramReaderCSD;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0701;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0801;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0802;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0803;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0901;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0902;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_0903;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1001;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1002;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1003;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1004;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1005;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1006;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1007;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1100;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1300;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1301;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1400;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1500;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1501;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.internal.io.ChromatogramReader_1502;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.IVendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.ocx.model.chromatogram.VendorScan;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.Format;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.internal.support.ReaderHelper;
import org.eclipse.chemclipse.xxd.converter.supplier.ocx.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class ChromatogramReaderMSD extends AbstractChromatogramMSDReader implements IChromatogramMSDZipReader {

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
		IChromatogramMSDZipReader chromatogramReader = getChromatogramReader(version);
		if(chromatogramReader != null) {
			try {
				chromatogramOverview = chromatogramReader.readOverview(file, monitor);
			} catch(Exception e) {
				chromatogramOverview = createChromatogramMSDFromFID(AbstractIon.TIC_ION, file, monitor);
			}
		} else {
			chromatogramOverview = createChromatogramMSDFromFID(AbstractIon.TIC_ION, file, monitor);
		}
		return chromatogramOverview;
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogramMSD;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 */
		IChromatogramMSDZipReader chromatogramReader = getChromatogramReader(version);
		if(chromatogramReader != null) {
			try {
				chromatogramMSD = chromatogramReader.read(file, monitor);
			} catch(Exception e) {
				System.out.println(e);
				chromatogramMSD = createChromatogramMSDFromFID(18.0d, file, monitor);
			}
		} else {
			chromatogramMSD = createChromatogramMSDFromFID(18.0d, file, monitor);
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
				final IChromatogramMSD chromatogram = chromatogramMSD;
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {

						chromatogram.enforceLoadScanProxies(new NullProgressMonitor());
					}
				});
				t.start();
			}
		}
		//
		return chromatogramMSD;
	}

	@Override
	public IChromatogramMSD read(ZipInputStream zipInputStream, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readChromatogram(zipInputStream, directoryPrefix, monitor);
	}

	@Override
	public IChromatogramMSD read(ZipFile zipFile, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		return readChromatogram(zipFile, directoryPrefix, monitor);
	}

	private IChromatogramMSD readChromatogram(Object object, String directoryPrefix, IProgressMonitor monitor) throws IOException {

		IChromatogramMSDZipReader chromatogramReader = null;
		IChromatogramMSD chromatogramMSD = null;
		ReaderHelper readerHelper = new ReaderHelper();
		//
		String version = readerHelper.getVersion(object, directoryPrefix);
		chromatogramReader = getChromatogramReader(version);
		//
		if(chromatogramReader != null) {
			if(object instanceof ZipInputStream zipInputStream) {
				chromatogramMSD = chromatogramReader.read(zipInputStream, directoryPrefix, monitor);
			} else if(object instanceof ZipFile zipFile) {
				chromatogramMSD = chromatogramReader.read(zipFile, directoryPrefix, monitor);
			}
		}
		//
		return chromatogramMSD;
	}

	private IChromatogramMSDZipReader getChromatogramReader(String version) {

		IChromatogramMSDZipReader chromatogramReader = null;
		//
		if(version.equals(Format.CHROMATOGRAM_VERSION_0701)) {
			chromatogramReader = new ChromatogramReader_0701();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_0801)) {
			chromatogramReader = new ChromatogramReader_0801();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_0802)) {
			chromatogramReader = new ChromatogramReader_0802();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_0803)) {
			chromatogramReader = new ChromatogramReader_0803();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_0901)) {
			chromatogramReader = new ChromatogramReader_0901();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_0902)) {
			chromatogramReader = new ChromatogramReader_0902();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_0903)) {
			chromatogramReader = new ChromatogramReader_0903();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1001)) {
			chromatogramReader = new ChromatogramReader_1001();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1002)) {
			chromatogramReader = new ChromatogramReader_1002();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1003)) {
			chromatogramReader = new ChromatogramReader_1003();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1004)) {
			chromatogramReader = new ChromatogramReader_1004();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1005)) {
			chromatogramReader = new ChromatogramReader_1005();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1006)) {
			chromatogramReader = new ChromatogramReader_1006();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1007)) {
			chromatogramReader = new ChromatogramReader_1007();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1100)) {
			chromatogramReader = new ChromatogramReader_1100();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1300)) {
			chromatogramReader = new ChromatogramReader_1300();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1301)) {
			chromatogramReader = new ChromatogramReader_1301();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1400)) {
			chromatogramReader = new ChromatogramReader_1400();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1500)) {
			chromatogramReader = new ChromatogramReader_1500();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1501)) {
			chromatogramReader = new ChromatogramReader_1501();
		} else if(version.equals(Format.CHROMATOGRAM_VERSION_1502)) {
			chromatogramReader = new ChromatogramReader_1502();
		}
		//
		return chromatogramReader;
	}

	private IChromatogramMSD createChromatogramMSDFromFID(double mz, File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IChromatogramMSD chromatogramMSD = null;
		/*
		 * Is the force modus used?
		 */
		if(PreferenceSupplier.isForceLoadAlternateDetector()) {
			ChromatogramReaderCSD chromatogramReaderFID = new ChromatogramReaderCSD();
			IChromatogramCSD chromatogramFID = chromatogramReaderFID.read(file, monitor);
			if(chromatogramFID != null) {
				chromatogramMSD = new VendorChromatogram();
				for(IScan scan : chromatogramFID.getScans()) {
					IVendorScan massSpectrum = new VendorScan();
					massSpectrum.setRetentionTime(scan.getRetentionTime());
					massSpectrum.setRelativeRetentionTime(scan.getRelativeRetentionTime());
					massSpectrum.setRetentionIndex(scan.getRetentionIndex());
					try {
						IVendorIon ion = new VendorIon(mz, scan.getTotalSignal());
						massSpectrum.addIon(ion);
					} catch(Exception e1) {
						//
					}
					chromatogramMSD.addScan(massSpectrum);
				}
				//
				chromatogramMSD.setConverterId(Format.CONVERTER_ID_CHROMATOGRAM);
				File fileConverted = new File(file.getAbsolutePath().replace(".ocb", "-fromFID.ocb"));
				chromatogramMSD.setFile(fileConverted);
				// Delay
				int startRetentionTime = chromatogramMSD.getStartRetentionTime();
				int scanDelay = startRetentionTime;
				chromatogramMSD.setScanDelay(scanDelay);
				// Interval
				int endRetentionTime = chromatogramMSD.getStopRetentionTime();
				int scanInterval = endRetentionTime / chromatogramMSD.getNumberOfScans();
				chromatogramMSD.setScanInterval(scanInterval);
			}
		}
		//
		return chromatogramMSD;
	}
}