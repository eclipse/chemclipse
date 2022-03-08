/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
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
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ObjectFactory;
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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class ChromatogramWriterVersion110 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriterVersion110.class);

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			//
			RunType run = new RunType();
			run.setId(chromatogram.getName());
			SpectrumListType spectrumList = new SpectrumListType();
			ChromatogramListType chromatogramList = new ChromatogramListType();
			chromatogramList.setCount(BigInteger.valueOf(1));
			//
			int scans = chromatogram.getNumberOfScans();
			float[] totalSignals = new float[scans];
			float[] retentionTimes = new float[scans];
			int i = 0;
			for(IScan scan : chromatogram.getScans()) {
				SpectrumType spectrum = new SpectrumType();
				spectrum.setId("scan=" + scan.getScanNumber());
				spectrum.setIndex(BigInteger.valueOf((scan.getScanNumber() - 1)));
				// TIC
				totalSignals[i] = scan.getTotalSignal();
				retentionTimes[i] = scan.getRetentionTime();
				i++;
				//
				// full spectra
				IScanMSD scanMSD = (IScanMSD)scan;
				//
				CVParamType cvParamTotalIonCurrent = new CVParamType();
				cvParamTotalIonCurrent.setAccession("MS:1000285");
				cvParamTotalIonCurrent.setName("total ion current");
				cvParamTotalIonCurrent.setUnitAccession("MS:1000131");
				cvParamTotalIonCurrent.setUnitName("number of detector counts");
				cvParamTotalIonCurrent.setValue(String.valueOf(scanMSD.getTotalSignal()));
				spectrum.getCvParam().add(cvParamTotalIonCurrent);
				//
				CVParamType cvParamBasePeak = new CVParamType();
				cvParamBasePeak.setAccession("MS:1000504");
				cvParamBasePeak.setName("base peak m/z");
				cvParamBasePeak.setUnitAccession("MS:1000040");
				cvParamBasePeak.setUnitName("m/z");
				cvParamBasePeak.setValue(String.valueOf(scanMSD.getBasePeak()));
				spectrum.getCvParam().add(cvParamBasePeak);
				//
				CVParamType cvParamBasePeakIntensity = new CVParamType();
				cvParamBasePeakIntensity.setAccession("MS:1000505");
				cvParamBasePeakIntensity.setName("base peak intensity");
				cvParamBasePeakIntensity.setUnitAccession("MS:1000131");
				cvParamBasePeakIntensity.setUnitName("number of detector counts");
				cvParamBasePeakIntensity.setValue(String.valueOf(scanMSD.getBasePeakAbundance()));
				spectrum.getCvParam().add(cvParamBasePeakIntensity);
				//
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
				spectrum.setScanList(scanList);
				spectrum.setBinaryDataArrayList(binaryDataArrayList);
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scanMSD;
				CVParamType cvParamType = new CVParamType();
				if(massSpectrum.getMassSpectrometer() == 1) {
					cvParamType.setAccession("MS:1000579");
					cvParamType.setName("MS1 spectrum");
				} else {
					cvParamType.setAccession("MS:1000580");
					cvParamType.setName("MSn spectrum");
				}
				spectrum.getCvParam().add(cvParamType);
				CVParamType cvParamLevel = new CVParamType();
				cvParamLevel.setAccession("MS:1000511");
				cvParamLevel.setName("ms level");
				cvParamLevel.setValue(String.valueOf(massSpectrum.getMassSpectrometer()));
				spectrum.getCvParam().add(cvParamLevel);
				if(massSpectrum.getMassSpectrumType() == 0) {
					CVParamType cvSpectrumType = new CVParamType();
					cvSpectrumType.setAccession("MS:1000127");
					cvSpectrumType.setName("centroid spectrum");
					spectrum.getCvParam().add(cvSpectrumType);
				} else if(massSpectrum.getMassSpectrumType() == 1) {
					CVParamType cvSpectrumType = new CVParamType();
					cvSpectrumType.setAccession("MS:1000128");
					cvSpectrumType.setName("profile spectrum");
					spectrum.getCvParam().add(cvSpectrumType);
				}
				spectrum.setDefaultArrayLength(ions.length);
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
			tic.setDefaultArrayLength(totalSignals.length);
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
