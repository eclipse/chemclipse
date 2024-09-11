/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.mzml.io;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramReader;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.wsd.converter.io.IChromatogramWSDReader;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.model.IVendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.model.IVendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.model.IVendorScanSignal;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.model.VendorChromatogram;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.model.VendorScan;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.model.VendorScanSignal;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.BinaryReader110;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.MetadataReader110;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.XmlReader110;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.BinaryDataArrayType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.CVParamType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ChromatogramType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.MzMLType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.RunType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SpectrumType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;

public class ChromatogramReaderVersion110 extends AbstractChromatogramReader implements IChromatogramWSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReaderVersion110.class);

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
		try {
			chromatogram = new VendorChromatogram();
			chromatogram.setFile(file);
			MzMLType mzML = XmlReader110.getMzML(file);
			chromatogram = (IVendorChromatogram)MetadataReader110.readMetadata(mzML, chromatogram);
			RunType run = mzML.getRun();
			readSingleWavelengthSignal(run, chromatogram);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		}
		return chromatogram;
	}

	@Override
	public IChromatogramWSD read(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
		try {
			chromatogram = new VendorChromatogram();
			chromatogram.setFile(file);
			MzMLType mzML = XmlReader110.getMzML(file);
			chromatogram = (IVendorChromatogram)MetadataReader110.readMetadata(mzML, chromatogram);
			RunType run = mzML.getRun();
			readSingleWavelengthSignal(run, chromatogram);
			readFullSpectrum(run, chromatogram);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(DataFormatException e) {
			logger.warn(e);
		}
		return chromatogram;
	}

	private void readFullSpectrum(RunType run, IVendorChromatogram chromatogram) throws DataFormatException {

		int i = 1;
		for(SpectrumType spectrum : run.getSpectrumList().getSpectrum()) {
			double[] wavelengths = new double[0];
			double[] intensities = new double[0];
			for(BinaryDataArrayType binaryDataArrayType : spectrum.getBinaryDataArrayList().getBinaryDataArray()) {
				Pair<String, double[]> binaryData = BinaryReader110.parseBinaryData(binaryDataArrayType);
				if(binaryData.getKey().equals("wavelength")) {
					wavelengths = binaryData.getValue();
				} else if(binaryData.getKey().equals("intensity")) {
					intensities = binaryData.getValue();
				}
			}
			IVendorScan scan = (IVendorScan)chromatogram.getSupplierScan(i);
			scan.deleteScanSignals(); // otherwise the total signal is added upon
			addSpectrum(wavelengths, intensities, scan);
			i++;
		}
	}

	private void readSingleWavelengthSignal(RunType run, IVendorChromatogram chromatogram) {

		double[] retentionTimes = new double[0];
		double[] intensities = new double[0];
		float lowestWavelength = 0f;
		float highestWavelength = 0f;
		try {
			for(ChromatogramType chromatogramType : run.getChromatogramList().getChromatogram()) {
				for(CVParamType cvParam : chromatogramType.getCvParam()) {
					if(cvParam.getAccession().equals("MS:1000618") && cvParam.getName().equals("highest observed wavelength")) {
						highestWavelength = Float.parseFloat(cvParam.getValue());
					} else if(cvParam.getAccession().equals("MS:1000619") && cvParam.getName().equals("lowest observed wavelength")) {
						lowestWavelength = Float.parseFloat(cvParam.getValue());
					} else if(cvParam.getAccession().equals("MS:1000812") && cvParam.getName().equals("absorption chromatogram")) {
						for(BinaryDataArrayType binaryDataArrayType : chromatogramType.getBinaryDataArrayList().getBinaryDataArray()) {
							Pair<String, double[]> binaryData = BinaryReader110.parseBinaryData(binaryDataArrayType);
							if(binaryData.getKey().equals("time")) {
								retentionTimes = binaryData.getValue();
							} else if(binaryData.getKey().equals("intensity")) {
								intensities = binaryData.getValue();
							}
						}
					}
				}
			}
			if(lowestWavelength != highestWavelength) {
				logger.warn("Not a single wavelength chromatogram.");
			}
			float wavelength = Math.max(lowestWavelength, highestWavelength);
			addScans(wavelength, intensities, retentionTimes, chromatogram);
		} catch(DataFormatException e) {
			logger.warn(e);
		}
	}

	private void addScans(float wavelength, double[] intensities, double[] retentionTimes, IVendorChromatogram chromatogram) {

		int rt = Math.min(retentionTimes.length, intensities.length);
		for(int i = 0; i < rt; i++) {
			IVendorScan scan = new VendorScan();
			int retentionTime = (int)(retentionTimes[i]);
			scan.setRetentionTime(retentionTime);
			float intensity = (float)intensities[i];
			IVendorScanSignal signal = new VendorScanSignal();
			signal.setAbsorbance(intensity);
			signal.setWavelength(wavelength);
			scan.addScanSignal(signal);
			chromatogram.addScan(scan);
		}
	}

	private void addSpectrum(double[] wavelengths, double[] intensities, IVendorScan scan) {

		int max = Math.min(wavelengths.length, intensities.length);
		for(int i = 0; i < max; i++) {
			IVendorScanSignal signal = new VendorScanSignal();
			signal.setAbsorbance((float)intensities[i]);
			signal.setWavelength((float)wavelengths[i]);
			scan.addScanSignal(signal);
		}
	}
}
