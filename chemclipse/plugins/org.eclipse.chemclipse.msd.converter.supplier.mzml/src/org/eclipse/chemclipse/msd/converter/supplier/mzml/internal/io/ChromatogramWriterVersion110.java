/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.io.IFormat;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.BinaryWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ChromatogramListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ChromatogramType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzML;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.RunType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ScanListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ScanType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriterVersion110 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriterVersion110.class);

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzML.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			//
			RunType run = new RunType();
			SpectrumListType spectrumList = new SpectrumListType();
			ChromatogramListType chromatogramList = new ChromatogramListType();
			//
			int scans = chromatogram.getNumberOfScans();
			float[] totalSignals = new float[scans];
			float[] retentionTimes = new float[scans];
			int i = 0;
			for(IScan scan : chromatogram.getScans()) {
				// TIC
				totalSignals[i] = scan.getTotalSignal();
				retentionTimes[i] = scan.getRetentionTime();
				i++;
				// full spectra
				IScanMSD scanMSD = (IScanMSD)scan;
				List<IIon> ionList = scanMSD.getIons();
				double[] ions = new double[ionList.size()];
				float[] abundances = new float[ionList.size()];
				int j = 0;
				for(IIon ion : ionList) {
					ions[j] = ion.getIon();
					abundances[j] = ion.getAbundance();
					j++;
				}
				BinaryDataArrayListType binaryDataArrayList = new BinaryDataArrayListType();
				//
				BinaryDataArrayType ionsBinaryDataArrayType = BinaryWriter.createBinaryData(ions);
				CVParamType cvParamIons = new CVParamType();
				cvParamIons.setAccession("MS:1000514");
				cvParamIons.setName("m/z array");
				ionsBinaryDataArrayType.getCvParam().add(cvParamIons);
				binaryDataArrayList.getBinaryDataArray().add(ionsBinaryDataArrayType);
				//
				BinaryDataArrayType abundancesBinaryDataArrayType = BinaryWriter.createBinaryData(abundances);
				CVParamType cvParamAbundances = new CVParamType();
				cvParamAbundances.setAccession("MS:1000515");
				cvParamAbundances.setName("intensity array");
				abundancesBinaryDataArrayType.getCvParam().add(cvParamAbundances);
				binaryDataArrayList.getBinaryDataArray().add(abundancesBinaryDataArrayType);
				//
				CVParamType cvParamRetentionTime = new CVParamType();
				cvParamRetentionTime.setAccession("MS:1000016");
				cvParamRetentionTime.setName("scan start time");
				cvParamRetentionTime.setUnitAccession("UO:0000028");
				cvParamRetentionTime.setUnitName("millisecond");
				cvParamRetentionTime.setValue(String.valueOf(scanMSD.getRetentionTime()));
				ScanType scanType = new ScanType();
				scanType.getCvParam().add(cvParamRetentionTime);
				ScanListType scanList = new ScanListType();
				scanList.getScan().add(scanType);
				//
				SpectrumType spectrum = new SpectrumType();
				spectrum.setScanList(scanList);
				spectrum.setBinaryDataArrayList(binaryDataArrayList);
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scanMSD;
				CVParamType cvParamLevel = new CVParamType();
				cvParamLevel.setAccession("MS:1000511");
				cvParamLevel.setName("ms level");
				cvParamLevel.setValue(String.valueOf(massSpectrum.getMassSpectrometer()));
				spectrum.getCvParam().add(cvParamLevel);
				if(massSpectrum.getMassSpectrumType() == 0) {
					CVParamType cvParamType = new CVParamType();
					cvParamType.setAccession("MS:1000127");
					cvParamType.setName("centroid spectrum");
					spectrum.getCvParam().add(cvParamType);
				} else if(massSpectrum.getMassSpectrumType() == 1) {
					CVParamType cvParamType = new CVParamType();
					cvParamType.setAccession("MS:1000127");
					cvParamType.setName("centroid spectrum");
					spectrum.getCvParam().add(cvParamType);
				}
				spectrumList.getSpectrum().add(spectrum);
			}
			run.setSpectrumList(spectrumList);
			//
			BinaryDataArrayListType binaryDataArrayList = new BinaryDataArrayListType();
			//
			BinaryDataArrayType totalSignalsBinaryDataArrayType = BinaryWriter.createBinaryData(totalSignals);
			CVParamType cvParamTotalSignals = new CVParamType();
			cvParamTotalSignals.setUnitAccession("MS:1000515");
			cvParamTotalSignals.setUnitName("intensity array");
			totalSignalsBinaryDataArrayType.getCvParam().add(cvParamTotalSignals);
			binaryDataArrayList.getBinaryDataArray().add(totalSignalsBinaryDataArrayType);
			//
			BinaryDataArrayType retentionTimesBinaryDataArrayType = BinaryWriter.createBinaryData(retentionTimes);
			CVParamType cvParamRetentionTime = new CVParamType();
			cvParamRetentionTime.setAccession("MS:1000595");
			cvParamRetentionTime.setName("time array");
			cvParamRetentionTime.setUnitAccession("UO:0000028");
			cvParamRetentionTime.setUnitName("millisecond");
			retentionTimesBinaryDataArrayType.getCvParam().add(cvParamRetentionTime);
			binaryDataArrayList.getBinaryDataArray().add(retentionTimesBinaryDataArrayType);
			//
			ChromatogramType tic = new ChromatogramType();
			tic.setBinaryDataArrayList(binaryDataArrayList);
			chromatogramList.getChromatogram().add(tic);
			run.setChromatogramList(chromatogramList);
			//
			Date date = chromatogram.getDate();
			if(date != null) {
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTime(date);
				run.setStartTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			}
			MzML mzML = new MzML();
			mzML.setVersion(IFormat.MZML_V_110);
			mzML.setRun(run);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.0.xsd");
			marshaller.marshal(mzML, file);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(DatatypeConfigurationException e) {
			logger.warn(e);
		}
	}
}
