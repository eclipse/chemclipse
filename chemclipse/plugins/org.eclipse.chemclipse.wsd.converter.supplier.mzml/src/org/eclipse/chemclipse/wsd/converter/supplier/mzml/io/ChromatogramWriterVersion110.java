/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.mzml.io;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.wsd.converter.io.IChromatogramWSDWriter;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.Activator;
import org.eclipse.chemclipse.wsd.converter.supplier.mzml.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.XmlReader110;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.XmlWriter110;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.BinaryDataArrayListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.BinaryDataArrayType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.CVListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.CVParamType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ChromatogramListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ChromatogramType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.DataProcessingListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.DataProcessingType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.FileDescriptionType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.InstrumentConfigurationListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.InstrumentConfigurationType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.MzMLType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ObjectFactory;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ParamGroupType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ProcessingMethodType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.RunType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SoftwareListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SoftwareRefType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SoftwareType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SourceFileListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SourceFileType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SpectrumListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SpectrumType;
import org.eclipse.core.runtime.IProgressMonitor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class ChromatogramWriterVersion110 extends AbstractChromatogramWriter implements IChromatogramWSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriterVersion110.class);

	@Override
	public void writeChromatogram(File file, IChromatogramWSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			//
			RunType run = new RunType();
			SoftwareListType softwareList = XmlWriter110.createSoftwareList();
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
			writeScans(chromatogram, totalSignals, retentionTimes, spectrumList);
			run.setSpectrumList(spectrumList);
			//
			BinaryDataArrayListType binaryDataArrayList = new BinaryDataArrayListType();
			binaryDataArrayList.setCount(BigInteger.valueOf(2));
			boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
			//
			BinaryDataArrayType totalSignalsBinaryDataArrayType = XmlWriter110.createBinaryData(totalSignals, compression);
			totalSignalsBinaryDataArrayType.getCvParam().add(XmlWriter110.createIntensityArrayType());
			binaryDataArrayList.getBinaryDataArray().add(totalSignalsBinaryDataArrayType);
			//
			BinaryDataArrayType retentionTimesBinaryDataArrayType = XmlWriter110.createBinaryData(retentionTimes, compression);
			retentionTimesBinaryDataArrayType.getCvParam().add(XmlWriter110.createRetentionTimeType());
			binaryDataArrayList.getBinaryDataArray().add(retentionTimesBinaryDataArrayType);
			//
			ChromatogramType defaultPDA = new ChromatogramType();
			float wavelength = chromatogram.getWavelengths().iterator().next(); // TODO: not default wavelength
			defaultPDA.setId(wavelength + "nm");
			defaultPDA.setIndex(BigInteger.valueOf(0));
			defaultPDA.getCvParam().add(createAbsorptionChromatogramType());
			defaultPDA.setDefaultArrayLength(totalSignals.length);
			defaultPDA.setBinaryDataArrayList(binaryDataArrayList);
			chromatogramList.getChromatogram().add(defaultPDA);
			run.setChromatogramList(chromatogramList);
			//
			XMLGregorianCalendar date = XmlWriter110.createDate(chromatogram);
			if(date != null) {
				run.setStartTimeStamp(date);
			}
			MzMLType mzML = new MzMLType();
			mzML.setId(chromatogram.getFile().getName());
			//
			CVListType cvList = new CVListType();
			cvList.setCount(BigInteger.valueOf(2));
			cvList.getCv().add(XmlWriter110.MS);
			cvList.getCv().add(XmlWriter110.UO);
			mzML.setCvList(cvList);
			//
			mzML.setFileDescription(createFileDescription(chromatogram, sourceFileList));
			mzML.setInstrumentConfigurationList(instrumentConfigurationList);
			mzML.setSoftwareList(softwareList);
			mzML.setDataProcessingList(dataProcessingList);
			mzML.setVersion(XmlReader110.VERSION);
			mzML.setRun(run);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.0.xsd");
			marshaller.marshal(mzML, file);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(DatatypeConfigurationException e) {
			logger.warn(e);
		}
	}

	private void writeScans(IChromatogramWSD chromatogram, float[] totalSignals, float[] retentionTimes, SpectrumListType spectrumList) {

		int i = 0;
		for(IScan scan : chromatogram.getScans()) {
			SpectrumType spectrum = new SpectrumType();
			spectrum.setId("scan=" + scan.getScanNumber());
			spectrum.setIndex(BigInteger.valueOf((scan.getScanNumber() - 1)));
			// TIC
			totalSignals[i] = scan.getTotalSignal();
			retentionTimes[i] = (float)(scan.getRetentionTime() / IChromatogramOverview.SECOND_CORRELATION_FACTOR);
			i++;
			spectrum.getCvParam().add(createAbsorptionType());
			spectrum.getCvParam().add(createProfileType());
			IScanWSD scanWSD = (IScanWSD)scan;
			// full spectra
			//
			spectrum.getCvParam().add(createHighestObservedWavelength(scanWSD));
			spectrum.getCvParam().add(createLowestObservedWavelength(scanWSD));
			//
			List<IScanSignalWSD> scanSignals = scanWSD.getScanSignals();
			float[] wavelength = new float[scanSignals.size()];
			float[] absorbance = new float[scanSignals.size()];
			int j = 0;
			for(IScanSignalWSD scanSignal : scanSignals) {
				wavelength[j] = scanSignal.getWavelength();
				absorbance[j] = scanSignal.getAbsorbance();
				j++;
			}
			BinaryDataArrayListType binaryDataArrayList = new BinaryDataArrayListType();
			binaryDataArrayList.setCount(BigInteger.valueOf(2));
			//
			boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
			BinaryDataArrayType wavelengthsBinaryDataArrayType = XmlWriter110.createBinaryData(wavelength, compression);
			wavelengthsBinaryDataArrayType.getCvParam().add(createWavelengthType());
			binaryDataArrayList.getBinaryDataArray().add(wavelengthsBinaryDataArrayType);
			//
			BinaryDataArrayType absorbancesBinaryDataArrayType = XmlWriter110.createBinaryData(absorbance, compression);
			absorbancesBinaryDataArrayType.getCvParam().add(XmlWriter110.createIntensityArrayType());
			binaryDataArrayList.getBinaryDataArray().add(absorbancesBinaryDataArrayType);
			//
			spectrum.setBinaryDataArrayList(binaryDataArrayList);
			spectrum.setDefaultArrayLength(wavelength.length);
			spectrumList.getSpectrum().add(spectrum);
		}
	}

	private CVParamType createWavelengthType() {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000617");
		cvParam.setName("wavelength array");
		setUnitNanometer(cvParam);
		return cvParam;
	}

	private CVParamType createHighestObservedWavelength(IScanWSD scanWSD) {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000618");
		cvParam.setName("highest observed wavelength");
		cvParam.setValue(String.valueOf(scanWSD.getWavelengthBounds().getHighestWavelength().getWavelength()));
		setUnitNanometer(cvParam);
		return cvParam;
	}

	private CVParamType createLowestObservedWavelength(IScanWSD scanWSD) {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000619");
		cvParam.setName("lowest observed wavelength");
		cvParam.setValue(String.valueOf(scanWSD.getWavelengthBounds().getLowestWavelength().getWavelength()));
		setUnitNanometer(cvParam);
		return cvParam;
	}

	private void setUnitNanometer(CVParamType cvParam) {

		cvParam.setUnitAccession("UO:0000018");
		cvParam.setUnitName("nanometer");
		cvParam.setUnitCvRef(XmlWriter110.UO);
	}

	private CVParamType createProfileType() {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000128");
		cvParam.setName("profile spectrum");
		cvParam.setValue("");
		return cvParam;
	}

	private CVParamType createAbsorptionChromatogramType() {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000812");
		cvParam.setName("absorption chromatogram");
		cvParam.setValue("");
		return cvParam;
	}

	private SourceFileListType createSourceFileList(IChromatogram<?> chromatogram) {

		SourceFileListType sourceFileListType = new SourceFileListType();
		sourceFileListType.setCount(BigInteger.valueOf(1));
		SourceFileType sourceFile = XmlWriter110.createSourceFile(chromatogram);
		if(chromatogram.getConverterId().equals("org.eclipse.chemclipse.xxd.converter.supplier.chemclipse")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1003374");
			cvParamFileFormat.setName("Open Chromatography Binary OCB format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		}
		sourceFileListType.getSourceFile().add(sourceFile);
		return sourceFileListType;
	}

	private FileDescriptionType createFileDescription(IChromatogramWSD chromatogram, SourceFileListType sourceFiles) {

		FileDescriptionType fileDescriptionType = new FileDescriptionType();
		fileDescriptionType.setSourceFileList(sourceFiles);
		ParamGroupType fileContent = new ParamGroupType();
		fileContent.getCvParam().add(createAbsorptionType());
		fileDescriptionType.setFileContent(fileContent);
		ParamGroupType paramGroupType = XmlWriter110.getOperator(chromatogram);
		if(paramGroupType != null) {
			fileDescriptionType.getContact().add(paramGroupType);
		}
		return fileDescriptionType;
	}

	private CVParamType createAbsorptionType() {

		CVParamType cvParamSpectrum = new CVParamType();
		cvParamSpectrum.setCvRef(XmlWriter110.MS);
		cvParamSpectrum.setAccession("MS:1000806");
		cvParamSpectrum.setName("absorption spectrum");
		cvParamSpectrum.setValue("");
		return cvParamSpectrum;
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
		exportParam.setCvRef(XmlWriter110.MS);
		exportParam.setAccession("MS:1000544");
		exportParam.setName("Conversion to mzML");
		exportParam.setValue("");
		processingMethod.getCvParam().add(exportParam);
		dataProcessing.getProcessingMethod().add(processingMethod);
		dataProcessingList.getDataProcessing().add(dataProcessing);
		return dataProcessingList;
	}
}
