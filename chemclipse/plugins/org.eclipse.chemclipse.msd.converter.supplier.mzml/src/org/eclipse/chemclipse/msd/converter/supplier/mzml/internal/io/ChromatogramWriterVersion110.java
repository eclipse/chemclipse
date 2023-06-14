/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
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
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.Activator;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.BinaryWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.XmlReader110;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ChromatogramListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ChromatogramType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.DataProcessingListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.DataProcessingType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.FileDescriptionType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.InstrumentConfigurationListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.InstrumentConfigurationType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzMLType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ObjectFactory;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ParamGroupType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ProcessingMethodType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.RunType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ScanListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ScanType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SoftwareListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SoftwareRefType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SoftwareType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SourceFileListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SourceFileType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumListType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumType;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class ChromatogramWriterVersion110 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriterVersion110.class);
	//
	public static final CVType MS = createMS();
	public static final CVType UO = createUO();

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			//
			RunType run = new RunType();
			SoftwareListType softwareList = createSoftwareList();
			InstrumentConfigurationListType instrumentConfigurationList = createInstrumentConfigurationList(softwareList.getSoftware().get(0));
			run.setDefaultInstrumentConfigurationRef(instrumentConfigurationList.getInstrumentConfiguration().get(0));
			SourceFileListType sourceFileList = createSourceFileList(chromatogram);
			run.setDefaultSourceFileRef(sourceFileList.getSourceFile().get(0));
			run.setId(chromatogram.getName());
			//
			DataProcessingListType dataProcessingList = createDataProcessingList(softwareList.getSoftware().get(0));
			SpectrumListType spectrumList = new SpectrumListType();
			spectrumList.setDefaultDataProcessingRef(dataProcessingList.getDataProcessing().get(0));
			ChromatogramListType chromatogramList = new ChromatogramListType();
			chromatogramList.setDefaultDataProcessingRef(dataProcessingList.getDataProcessing().get(0));
			chromatogramList.setCount(BigInteger.valueOf(1)); // TODO export referenced chromatograms
			//
			int scans = chromatogram.getNumberOfScans();
			spectrumList.setCount(BigInteger.valueOf(scans));
			float[] totalSignals = new float[scans];
			float[] retentionTimes = new float[scans];
			int i = 0;
			for(IScan scan : chromatogram.getScans()) {
				SpectrumType spectrum = new SpectrumType();
				spectrum.setId("scan=" + scan.getScanNumber());
				spectrum.setIndex(BigInteger.valueOf((scan.getScanNumber() - 1)));
				// TIC
				totalSignals[i] = scan.getTotalSignal();
				retentionTimes[i] = (float)(scan.getRetentionTime() / IChromatogramOverview.SECOND_CORRELATION_FACTOR);
				i++;
				//
				// full spectra
				IScanMSD scanMSD = (IScanMSD)scan;
				//
				CVParamType cvParamTotalIonCurrent = new CVParamType();
				cvParamTotalIonCurrent.setCvRef(MS);
				cvParamTotalIonCurrent.setAccession("MS:1000285");
				cvParamTotalIonCurrent.setName("total ion current");
				cvParamTotalIonCurrent.setValue(String.valueOf(scanMSD.getTotalSignal()));
				spectrum.getCvParam().add(cvParamTotalIonCurrent);
				//
				CVParamType cvParamBasePeak = new CVParamType();
				cvParamBasePeak.setCvRef(MS);
				cvParamBasePeak.setAccession("MS:1000504");
				cvParamBasePeak.setName("base peak m/z");
				cvParamBasePeak.setUnitCvRef(MS);
				cvParamBasePeak.setUnitAccession("MS:1000040");
				cvParamBasePeak.setUnitName("m/z");
				cvParamBasePeak.setValue(String.valueOf(scanMSD.getBasePeak()));
				spectrum.getCvParam().add(cvParamBasePeak);
				//
				CVParamType cvParamBasePeakIntensity = new CVParamType();
				cvParamBasePeakIntensity.setCvRef(MS);
				cvParamBasePeakIntensity.setAccession("MS:1000505");
				cvParamBasePeakIntensity.setName("base peak intensity");
				cvParamBasePeakIntensity.setUnitCvRef(MS);
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
				binaryDataArrayList.setCount(BigInteger.valueOf(2));
				//
				BinaryDataArrayType ionsBinaryDataArrayType = BinaryWriter.createBinaryData(ions);
				CVParamType cvParamIons = new CVParamType();
				cvParamIons.setCvRef(MS);
				cvParamIons.setAccession("MS:1000514");
				cvParamIons.setName("m/z array");
				cvParamIons.setUnitCvRef(MS);
				cvParamIons.setUnitAccession("MS:1000040");
				cvParamIons.setUnitName("m/z");
				ionsBinaryDataArrayType.getCvParam().add(cvParamIons);
				binaryDataArrayList.getBinaryDataArray().add(ionsBinaryDataArrayType);
				//
				BinaryDataArrayType abundancesBinaryDataArrayType = BinaryWriter.createBinaryData(abundances);
				CVParamType cvParamAbundances = new CVParamType();
				cvParamAbundances.setCvRef(MS);
				cvParamAbundances.setAccession("MS:1000515");
				cvParamAbundances.setName("intensity array");
				cvParamAbundances.setUnitCvRef(MS);
				cvParamAbundances.setUnitAccession("MS:1000131");
				cvParamAbundances.setUnitName("number of counts");
				abundancesBinaryDataArrayType.getCvParam().add(cvParamAbundances);
				binaryDataArrayList.getBinaryDataArray().add(abundancesBinaryDataArrayType);
				//
				CVParamType cvParamRetentionTime = new CVParamType();
				cvParamRetentionTime.setCvRef(MS);
				cvParamRetentionTime.setAccession("MS:1000016");
				cvParamRetentionTime.setName("scan start time");
				cvParamRetentionTime.setUnitCvRef(UO);
				cvParamRetentionTime.setUnitAccession("UO:0000031");
				cvParamRetentionTime.setUnitName("minute");
				cvParamRetentionTime.setValue(String.valueOf(scanMSD.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				ScanType scanType = new ScanType();
				scanType.getCvParam().add(cvParamRetentionTime);
				//
				ScanListType scanList = new ScanListType();
				CVParamType cvParamCombination = new CVParamType();
				cvParamCombination.setCvRef(MS);
				cvParamCombination.setAccession("MS:1000795");
				cvParamCombination.setName("no combination");
				scanList.getCvParam().add(cvParamCombination);
				scanList.setCount(BigInteger.valueOf(1));
				scanList.getScan().add(scanType);
				//
				spectrum.setScanList(scanList);
				spectrum.setBinaryDataArrayList(binaryDataArrayList);
				IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scanMSD;
				CVParamType cvParamType = new CVParamType();
				if(massSpectrum.getMassSpectrometer() == 1) {
					cvParamType.setCvRef(MS);
					cvParamType.setAccession("MS:1000579");
					cvParamType.setName("MS1 spectrum");
				} else {
					cvParamType.setCvRef(MS);
					cvParamType.setAccession("MS:1000580");
					cvParamType.setName("MSn spectrum");
				}
				spectrum.getCvParam().add(cvParamType);
				CVParamType cvParamLevel = new CVParamType();
				cvParamLevel.setCvRef(MS);
				cvParamLevel.setAccession("MS:1000511");
				cvParamLevel.setName("ms level");
				cvParamLevel.setValue(String.valueOf(massSpectrum.getMassSpectrometer()));
				spectrum.getCvParam().add(cvParamLevel);
				if(massSpectrum.getMassSpectrumType() == 0) {
					CVParamType cvSpectrumType = new CVParamType();
					cvSpectrumType.setCvRef(MS);
					cvSpectrumType.setAccession("MS:1000127");
					cvSpectrumType.setName("centroid spectrum");
					spectrum.getCvParam().add(cvSpectrumType);
				} else if(massSpectrum.getMassSpectrumType() == 1) {
					CVParamType cvSpectrumType = new CVParamType();
					cvSpectrumType.setCvRef(MS);
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
			binaryDataArrayList.setCount(BigInteger.valueOf(2));
			//
			BinaryDataArrayType totalSignalsBinaryDataArrayType = BinaryWriter.createBinaryData(totalSignals);
			CVParamType cvParamTotalSignals = new CVParamType();
			cvParamTotalSignals.setCvRef(MS);
			cvParamTotalSignals.setAccession("MS:1000515");
			cvParamTotalSignals.setName("intensity array");
			cvParamTotalSignals.setUnitCvRef(MS);
			cvParamTotalSignals.setUnitAccession("MS:1000131");
			cvParamTotalSignals.setUnitName("number of counts");
			totalSignalsBinaryDataArrayType.getCvParam().add(cvParamTotalSignals);
			binaryDataArrayList.getBinaryDataArray().add(totalSignalsBinaryDataArrayType);
			//
			BinaryDataArrayType retentionTimesBinaryDataArrayType = BinaryWriter.createBinaryData(retentionTimes);
			CVParamType cvParamRetentionTime = new CVParamType();
			cvParamRetentionTime.setCvRef(MS);
			cvParamRetentionTime.setAccession("MS:1000595");
			cvParamRetentionTime.setName("time array");
			cvParamRetentionTime.setUnitAccession("UO:0000010");
			cvParamRetentionTime.setUnitName("second");
			retentionTimesBinaryDataArrayType.getCvParam().add(cvParamRetentionTime);
			binaryDataArrayList.getBinaryDataArray().add(retentionTimesBinaryDataArrayType);
			//
			ChromatogramType tic = new ChromatogramType();
			tic.setId("tic");
			tic.setIndex(BigInteger.valueOf(0));
			CVParamType cvParam = new CVParamType();
			cvParam.setCvRef(MS);
			cvParam.setAccession("MS:1000235");
			cvParam.setName("total ion current chromatogram");
			cvParam.setValue("");
			tic.getCvParam().add(cvParam);
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
			MzMLType mzML = new MzMLType();
			mzML.setId(chromatogram.getFile().getName());
			//
			CVListType cvList = new CVListType();
			cvList.setCount(BigInteger.valueOf(2));
			cvList.getCv().add(MS);
			cvList.getCv().add(UO);
			mzML.setCvList(cvList);
			//
			mzML.setFileDescription(createFileDescription(chromatogram, sourceFileList));
			mzML.setInstrumentConfigurationList(instrumentConfigurationList);
			mzML.setSoftwareList(softwareList);
			mzML.setDataProcessingList(dataProcessingList);
			mzML.setVersion(XmlReader110.VERSION);
			mzML.setRun(run);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.0.xsd");
			marshaller.marshal(mzML, file);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(DatatypeConfigurationException e) {
			logger.warn(e);
		}
	}

	private SourceFileListType createSourceFileList(IChromatogram<?> chromatogram) {

		SourceFileListType sourceFileListType = new SourceFileListType();
		sourceFileListType.setCount(BigInteger.valueOf(1));
		File file = chromatogram.getFile();
		SourceFileType sourceFile = new SourceFileType();
		sourceFile.setLocation(file.getAbsolutePath());
		sourceFile.setId(file.getName());
		sourceFile.setName(file.getName());
		CVParamType cvParamSHA1 = new CVParamType();
		cvParamSHA1.setCvRef(MS);
		cvParamSHA1.setAccession("MS:1000569");
		cvParamSHA1.setName("SHA-1");
		cvParamSHA1.setValue(calculateSHA1(file));
		sourceFile.getCvParam().add(cvParamSHA1);
		// TODO: source file formats
		sourceFileListType.getSourceFile().add(sourceFile);
		return sourceFileListType;
	}

	private static String calculateSHA1(File file) {

		try (FileInputStream fis = new FileInputStream(file)) {
			return DigestUtils.sha1Hex(fis);
		} catch(IOException e) {
			logger.warn(e);
		}
		return "";
	}

	private FileDescriptionType createFileDescription(IChromatogramMSD chromatogram, SourceFileListType sourceFiles) {

		FileDescriptionType fileDescriptionType = new FileDescriptionType();
		fileDescriptionType.setSourceFileList(sourceFiles);
		ParamGroupType fileContent = new ParamGroupType();
		//
		IScanMSD scanMSD = (IScanMSD)chromatogram.getScan(1);
		IVendorMassSpectrum massSpectrum = (IVendorMassSpectrum)scanMSD;
		//
		CVParamType cvParamSpectrum = new CVParamType();
		if(massSpectrum.getMassSpectrometer() == 1) {
			cvParamSpectrum.setCvRef(MS);
			cvParamSpectrum.setAccession("MS:1000579");
			cvParamSpectrum.setName("MS1 spectrum");
		} else {
			cvParamSpectrum.setCvRef(MS);
			cvParamSpectrum.setAccession("MS:1000580");
			cvParamSpectrum.setName("MSn spectrum");
		}
		cvParamSpectrum.setValue("");
		fileContent.getCvParam().add(cvParamSpectrum);
		//
		CVParamType cvParamSpectrumType = new CVParamType();
		if(massSpectrum.getMassSpectrumType() == 0) {
			cvParamSpectrumType.setCvRef(MS);
			cvParamSpectrumType.setAccession("MS:1000127");
			cvParamSpectrumType.setName("centroid spectrum");
		} else if(massSpectrum.getMassSpectrumType() == 1) {
			cvParamSpectrumType.setCvRef(MS);
			cvParamSpectrumType.setAccession("MS:1000128");
			cvParamSpectrumType.setName("profile spectrum");
		}
		cvParamSpectrumType.setValue("");
		fileContent.getCvParam().add(cvParamSpectrumType);
		fileDescriptionType.setFileContent(fileContent);
		//
		if(!chromatogram.getOperator().isEmpty()) {
			ParamGroupType paramGroupType = new ParamGroupType();
			CVParamType cvParam = new CVParamType();
			cvParam.setCvRef(MS);
			cvParam.setAccession("MS:1000586");
			cvParam.setName("contact name");
			cvParam.setValue(chromatogram.getOperator());
			paramGroupType.getCvParam().add(cvParam);
			fileDescriptionType.getContact().add(paramGroupType);
		}
		return fileDescriptionType;
	}

	private SoftwareListType createSoftwareList() {

		SoftwareListType softwareList = new SoftwareListType();
		softwareList.setCount(BigInteger.valueOf(1));
		SoftwareType software = new SoftwareType();
		IProduct product = Platform.getProduct();
		software.setId("Unknown");
		if(product != null) {
			software.setId(product.getName());
			Version version = product.getDefiningBundle().getVersion();
			software.setVersion(version.getMajor() + "." + version.getMinor() + "." + version.getMicro());
			// TODO: ontology
		}
		softwareList.getSoftware().add(software);
		return softwareList;
	}

	private InstrumentConfigurationListType createInstrumentConfigurationList(SoftwareType software) {

		InstrumentConfigurationListType instrumentConfigurationList = new InstrumentConfigurationListType();
		instrumentConfigurationList.setCount(BigInteger.valueOf(1));
		InstrumentConfigurationType instrumentConfiguration = new InstrumentConfigurationType();
		instrumentConfiguration.setId("unknown");
		SoftwareRefType softwareRef = new SoftwareRefType();
		softwareRef.setRef(software);
		instrumentConfiguration.setSoftwareRef(softwareRef);
		instrumentConfigurationList.getInstrumentConfiguration().add(instrumentConfiguration);
		return instrumentConfigurationList;
	}

	private DataProcessingListType createDataProcessingList(SoftwareType software) {

		DataProcessingListType dataProcessingList = new DataProcessingListType();
		dataProcessingList.setCount(BigInteger.valueOf(1));
		DataProcessingType dataProcessing = new DataProcessingType();
		dataProcessing.setId(Activator.getContext().getBundle().getSymbolicName());
		ProcessingMethodType processingMethod = new ProcessingMethodType();
		processingMethod.setSoftwareRef(software);
		processingMethod.setOrder(BigInteger.valueOf(1));
		CVParamType exportParam = new CVParamType();
		exportParam.setCvRef(MS);
		exportParam.setAccession("MS:1000544");
		exportParam.setName("Conversion to mzML");
		exportParam.setValue("");
		processingMethod.getCvParam().add(exportParam);
		dataProcessing.getProcessingMethod().add(processingMethod);
		dataProcessingList.getDataProcessing().add(dataProcessing);
		return dataProcessingList;
	}

	private static CVType createMS() {

		CVType cvTypeMS = new CVType();
		cvTypeMS.setId("MS");
		cvTypeMS.setFullName("Proteomics Standards Initiative Mass Spectrometry Ontology");
		cvTypeMS.setVersion("4.1.121");
		cvTypeMS.setURI("https://github.com/HUPO-PSI/psi-ms-CV/releases/download/v4.1.121/psi-ms.obo");
		return cvTypeMS;
	}

	private static CVType createUO() {

		CVType cvTypeUnit = new CVType();
		cvTypeUnit.setId("UO");
		cvTypeUnit.setFullName("Unit Ontology");
		cvTypeUnit.setVersion("2023:05:23");
		cvTypeUnit.setURI("https://raw.githubusercontent.com/bio-ontology-research-group/unit-ontology/v2023-05-23/unit-ontology.obo");
		return cvTypeUnit;
	}
}
