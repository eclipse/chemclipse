/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.csv.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.csv.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.csv.model.VendorScan;
import org.eclipse.chemclipse.msd.converter.supplier.csv.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramReader extends AbstractChromatogramMSDReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReader.class);
	//
	private static final String ZERO_VALUE = "0.0";
	private static final int ION_COLUMN_START = 3;

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, false);
		}
		return null;
	}

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws IOException {

		if(isValidFileFormat(file)) {
			return readChromatogram(file, true);
		}
		return null;
	}

	public static boolean isValidFileFormat(File file) throws IOException {

		/*
		 * Check the first column header.
		 */
		FileReader reader = new FileReader(file);
		char[] chars = new char[ChromatogramWriter.RT_MILLISECONDS_COLUMN.length()];
		reader.read(chars);
		reader.close();
		//
		String firstColumn = new String(chars);
		return firstColumn.equals(ChromatogramWriter.RT_MILLISECONDS_COLUMN);
	}

	private IChromatogramMSD readChromatogram(File file, boolean overview) throws IOException {

		IChromatogramMSD chromatogram = null;
		//
		try (FileReader fileReader = new FileReader(file)) {
			String zeroMarker = PreferenceSupplier.getImportZeroMarker();
			zeroMarker = zeroMarker.startsWith(ZERO_VALUE) ? zeroMarker : ZERO_VALUE;
			char delimiter = PreferenceSupplier.getImportDelimiter().getCharacter();
			CSVParser csvParser = new CSVParser(fileReader, CSVFormat.newFormat(delimiter).builder().setHeader().build());
			chromatogram = new VendorChromatogram();
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
			Map<Integer, Float> ionsMap = getIonMap(csvParser);
			for(CSVRecord csvRecord : csvParser.getRecords()) {
				IVendorMassSpectrum supplierMassSpectrum = getScan(csvRecord, ionsMap, zeroMarker, overview);
				chromatogram.addScan(supplierMassSpectrum);
			}
			//
			int scanDelay = chromatogram.getScan(1).getRetentionTime();
			chromatogram.setScanDelay(scanDelay);
			csvParser.close();
		}
		return chromatogram;
	}

	private Map<Integer, Float> getIonMap(CSVParser csvParser) {

		Map<Integer, Float> ions = new HashMap<>();
		Map<String, Integer> headerMap = csvParser.getHeaderMap();
		for(Map.Entry<String, Integer> entry : headerMap.entrySet()) {
			try {
				int index = entry.getValue();
				ions.put(index, Float.valueOf(entry.getKey()));
			} catch(Exception e) {
				logger.warn(e);
			}
		}
		return ions;
	}

	private IVendorMassSpectrum getScan(CSVRecord csvRecord, Map<Integer, Float> ionsMap, String zeroMarker, boolean overview) {

		IVendorMassSpectrum massSpectrum = new VendorScan();
		String retentionTimeInMilliseconds = csvRecord.get(0);
		int retentionTime = Integer.parseInt(retentionTimeInMilliseconds);
		massSpectrum.setRetentionTime(retentionTime);
		/*
		 * The retention time in minutes will be not used.
		 */
		// String retentionTimeInMinutes = lineEntries.get(1);
		String retentionIndexValue = csvRecord.get(2);
		float retentionIndex = Float.parseFloat(retentionIndexValue);
		massSpectrum.setRetentionIndex(retentionIndex);
		if(overview) {
			try {
				IIon ion = getIonsOverview(csvRecord, zeroMarker);
				massSpectrum.addIon(ion);
			} catch(Exception e) {
				// logger.warn(e); - Don't log this.
			}
		} else {
			List<IIon> ions = getIons(csvRecord, ionsMap, zeroMarker);
			for(IIon ion : ions) {
				massSpectrum.addIon(ion);
			}
		}
		//
		return massSpectrum;
	}

	private IIon getIonsOverview(CSVRecord csvRecord, String zeroMarker) throws AbundanceLimitExceededException, IonLimitExceededException {

		float abundanceTotalSignal = 0.0f;
		for(int index = ION_COLUMN_START; index < csvRecord.size(); index++) {
			String abundanceValue = csvRecord.get(index);
			if(!abundanceValue.startsWith(zeroMarker)) {
				float abundance = Float.parseFloat(abundanceValue);
				abundanceTotalSignal += abundance;
			}
		}
		//
		return new VendorIon(IIon.TIC_ION, abundanceTotalSignal);
	}

	private List<IIon> getIons(CSVRecord csvRecord, Map<Integer, Float> ionsMap, String zeroMarker) {

		List<IIon> ions = new ArrayList<>();
		for(int index = ION_COLUMN_START; index < csvRecord.size(); index++) {
			String abundanceValue = csvRecord.get(index);
			if(!abundanceValue.startsWith(zeroMarker)) {
				try {
					float abundance = Float.parseFloat(abundanceValue);
					float ion = ionsMap.get(index);
					IIon csvIon = new VendorIon(ion, abundance);
					ions.add(csvIon);
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		//
		return ions;
	}
}
