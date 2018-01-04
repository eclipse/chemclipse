/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.io.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.csv.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.csv.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.csv.model.VendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;

/**
 * This class is responsible to read a Agilent Chromatogram from its binary
 * file.
 * 
 * @author eselmeister
 */
public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);
	private static final String ZERO_VALUE = "0.0";
	private static final int Ion_COLUMN_START = 3;

	public ChromatogramReader() {
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, false);
		}
		return null;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, true);
		}
		return null;
	}

	private boolean isValidFileFormat(File file) throws IOException {

		FileReader reader = new FileReader(file);
		ICsvListReader csvListReader = new CsvListReader(reader, PreferenceSupplier.getCsvPreference());
		String[] header = csvListReader.getHeader(true);
		/*
		 * Check the first column header.
		 */
		String firstColumn = header[0];
		csvListReader.close();
		return firstColumn.equals(ChromatogramWriter.RT_MILLISECONDS_COLUMN);
	}

	private IChromatogramMSD readChromatogram(File file, boolean overview) throws IOException {

		FileReader reader = new FileReader(file);
		@SuppressWarnings("resource")
		ICsvListReader csvListReader = new CsvListReader(reader, PreferenceSupplier.getCsvPreference());
		IChromatogramMSD chromatogram = new VendorChromatogram();
		if(!overview) {
			/*
			 * If the chromatogram shall be exportable, set the id otherwise it is null or "".
			 */
			chromatogram.setConverterId("");
			chromatogram.setFile(file);
		}
		/*
		 * Get the header inclusive ion description.
		 */
		String[] header = csvListReader.getHeader(true);
		Map<Integer, Float> ionsMap = getIonMap(header);
		List<String> lineEntries;
		while((lineEntries = csvListReader.read()) != null) {
			IVendorMassSpectrum supplierMassSpectrum = getScan(lineEntries, ionsMap, overview);
			chromatogram.addScan(supplierMassSpectrum);
		}
		int scanDelay = chromatogram.getScan(1).getRetentionTime();
		chromatogram.setScanDelay(scanDelay);
		return chromatogram;
	}

	private Map<Integer, Float> getIonMap(String[] header) {

		Map<Integer, Float> ions = new HashMap<Integer, Float>();
		for(int index = 3; index < header.length; index++) {
			float ion = Float.valueOf(header[index]);
			ions.put(index, ion);
		}
		return ions;
	}

	private IVendorMassSpectrum getScan(List<String> lineEntries, Map<Integer, Float> ionsMap, boolean overview) {

		IVendorMassSpectrum massSpectrum = new VendorScan();
		String retentionTimeInMilliseconds = lineEntries.get(0);
		int retentionTime = Integer.valueOf(retentionTimeInMilliseconds);
		massSpectrum.setRetentionTime(retentionTime);
		/*
		 * The retention time in minutes will be not used.
		 */
		// String retentionTimeInMinutes = lineEntries.get(1);
		String retentionIndexValue = lineEntries.get(2);
		float retentionIndex = Float.valueOf(retentionIndexValue);
		massSpectrum.setRetentionIndex(retentionIndex);
		if(overview) {
			try {
				IIon ion = getIonsOverview(lineEntries);
				massSpectrum.addIon(ion);
			} catch(AbundanceLimitExceededException e) {
				logger.warn(e);
			} catch(IonLimitExceededException e) {
				logger.warn(e);
			}
		} else {
			List<IIon> ions = getIons(lineEntries, ionsMap);
			for(IIon ion : ions) {
				massSpectrum.addIon(ion);
			}
		}
		return massSpectrum;
	}

	private IIon getIonsOverview(List<String> lineEntries) throws AbundanceLimitExceededException, IonLimitExceededException {

		float abundanceTotalSignal = 0.0f;
		for(int index = Ion_COLUMN_START; index < lineEntries.size(); index++) {
			String abundanceValue = lineEntries.get(index);
			if(!abundanceValue.equals(ZERO_VALUE)) {
				float abundance = Float.valueOf(abundanceValue);
				abundanceTotalSignal += abundance;
			}
		}
		IIon ion = new VendorIon(AbstractIon.TIC_ION, abundanceTotalSignal);
		return ion;
	}

	private List<IIon> getIons(List<String> lineEntries, Map<Integer, Float> ionsMap) {

		List<IIon> ions = new ArrayList<IIon>();
		for(int index = Ion_COLUMN_START; index < lineEntries.size(); index++) {
			String abundanceValue = lineEntries.get(index);
			if(!abundanceValue.equals(ZERO_VALUE)) {
				float abundance = Float.valueOf(abundanceValue);
				float ion = ionsMap.get(index);
				try {
					IIon csvIon = new VendorIon(ion, abundance);
					ions.add(csvIon);
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		}
		return ions;
	}
}
