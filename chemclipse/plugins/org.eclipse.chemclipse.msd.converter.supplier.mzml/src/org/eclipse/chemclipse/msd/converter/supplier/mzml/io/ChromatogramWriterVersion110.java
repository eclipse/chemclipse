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
package org.eclipse.chemclipse.msd.converter.supplier.mzml.io;

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
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.Activator;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
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
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ScanListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ScanType;
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

public class ChromatogramWriterVersion110 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriterVersion110.class);

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://psi.hupo.org/ms/mzml http://psidev.info/files/ms/mzML/xsd/mzML1.1.0.xsd");
			marshaller.marshal(createMzML(chromatogram), file);
		} catch(JAXBException e) {
			logger.warn(e);
		}
	}

	private MzMLType createMzML(IChromatogramMSD chromatogram) {

		MzMLType mzML = new MzMLType();
		mzML.setVersion(XmlReader110.VERSION);
		mzML.setId(chromatogram.getFile().getName());
		SourceFileListType sourceFileList = createSourceFileList(chromatogram);
		mzML.setFileDescription(createFileDescription(chromatogram, sourceFileList));
		SoftwareListType softwareList = XmlWriter110.createSoftwareList();
		mzML.setSoftwareList(softwareList);
		InstrumentConfigurationListType instrumentConfigurationList = createInstrumentConfigurationList(softwareList.getSoftware().get(0));
		mzML.setInstrumentConfigurationList(instrumentConfigurationList);
		DataProcessingListType dataProcessingList = createDataProcessingList(softwareList.getSoftware().get(0));
		mzML.setDataProcessingList(dataProcessingList);
		mzML.setRun(createRun(chromatogram, dataProcessingList, sourceFileList, instrumentConfigurationList));
		mzML.setCvList(createCvList());
		return mzML;
	}

	private RunType createRun(IChromatogramMSD chromatogram, DataProcessingListType dataProcessingList, SourceFileListType sourceFileList, InstrumentConfigurationListType instrumentConfigurationList) {

		RunType run = new RunType();
		run.setDefaultInstrumentConfigurationRef(instrumentConfigurationList.getInstrumentConfiguration().get(0));
		run.setDefaultSourceFileRef(sourceFileList.getSourceFile().get(0));
		run.setId(chromatogram.getName());
		SpectrumListType spectrumList = createSpectrumList(chromatogram, dataProcessingList);
		run.setSpectrumList(spectrumList);
		int scans = chromatogram.getNumberOfScans();
		float[] totalSignals = new float[scans];
		float[] retentionTimes = new float[scans];
		writeScans(chromatogram, totalSignals, retentionTimes, spectrumList);
		run.setChromatogramList(createChromatogramListType(dataProcessingList, totalSignals, retentionTimes));
		try {
			XMLGregorianCalendar date = XmlWriter110.createDate(chromatogram);
			if(date != null) {
				run.setStartTimeStamp(date);
			}
		} catch(DatatypeConfigurationException e) {
			logger.warn(e);
		}
		return run;
	}

	private CVListType createCvList() {

		CVListType cvList = new CVListType();
		cvList.setCount(BigInteger.valueOf(2));
		cvList.getCv().add(XmlWriter110.MS);
		cvList.getCv().add(XmlWriter110.UO);
		return cvList;
	}

	private ChromatogramListType createChromatogramListType(DataProcessingListType dataProcessingList, float[] totalSignals, float[] retentionTimes) {

		ChromatogramListType chromatogramList = new ChromatogramListType();
		chromatogramList.setDefaultDataProcessingRef(dataProcessingList.getDataProcessing().get(0));
		chromatogramList.setCount(BigInteger.valueOf(1)); // TODO export referenced chromatograms
		ChromatogramType tic = createTIC(totalSignals, retentionTimes);
		chromatogramList.getChromatogram().add(tic);
		return chromatogramList;
	}

	private SpectrumListType createSpectrumList(IChromatogramMSD chromatogram, DataProcessingListType dataProcessingList) {

		SpectrumListType spectrumList = new SpectrumListType();
		spectrumList.setCount(BigInteger.valueOf(chromatogram.getNumberOfScans()));
		spectrumList.setDefaultDataProcessingRef(dataProcessingList.getDataProcessing().get(0));
		return spectrumList;
	}

	private ChromatogramType createTIC(float[] totalSignals, float[] retentionTimes) {

		ChromatogramType tic = new ChromatogramType();
		tic.setId("tic");
		tic.setIndex(BigInteger.valueOf(0));
		tic.getCvParam().add(XmlWriter110.createTotalIonCurrrentType());
		tic.setDefaultArrayLength(totalSignals.length);
		tic.setBinaryDataArrayList(createTotalSignalBinaryDataArrayListType(totalSignals, retentionTimes));
		return tic;
	}

	private BinaryDataArrayListType createTotalSignalBinaryDataArrayListType(float[] totalSignals, float[] retentionTimes) {

		BinaryDataArrayListType binaryDataArrayList = new BinaryDataArrayListType();
		binaryDataArrayList.setCount(BigInteger.valueOf(2));
		binaryDataArrayList.getBinaryDataArray().add(createTotalSignalsBinaryDataArrayType(totalSignals));
		binaryDataArrayList.getBinaryDataArray().add(createRetentionTimesBinaryDataArrayType(retentionTimes));
		return binaryDataArrayList;
	}

	private BinaryDataArrayType createRetentionTimesBinaryDataArrayType(float[] retentionTimes) {

		boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
		BinaryDataArrayType retentionTimesBinaryDataArrayType = XmlWriter110.createBinaryData(retentionTimes, compression);
		retentionTimesBinaryDataArrayType.getCvParam().add(XmlWriter110.createRetentionTimeType());
		return retentionTimesBinaryDataArrayType;
	}

	private BinaryDataArrayType createTotalSignalsBinaryDataArrayType(float[] totalSignals) {

		boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
		BinaryDataArrayType totalSignalsBinaryDataArrayType = XmlWriter110.createBinaryData(totalSignals, compression);
		totalSignalsBinaryDataArrayType.getCvParam().add(XmlWriter110.createIntensityArrayType());
		return totalSignalsBinaryDataArrayType;
	}

	private void writeScans(IChromatogramMSD chromatogram, float[] totalSignals, float[] retentionTimes, SpectrumListType spectrumList) {

		int i = 0;
		for(IScan scan : chromatogram.getScans()) {
			SpectrumType spectrum = new SpectrumType();
			spectrum.setId("scan=" + scan.getScanNumber());
			spectrum.setIndex(BigInteger.valueOf((scan.getScanNumber() - 1)));
			// TIC
			totalSignals[i] = scan.getTotalSignal();
			retentionTimes[i] = (float)(scan.getRetentionTime() / IChromatogramOverview.SECOND_CORRELATION_FACTOR);
			spectrum.getCvParam().add(XmlWriter110.createTotalIonCurrentType(scan));
			if(scan instanceof IScanMSD scanMSD) {
				spectrum.getCvParam().add(XmlWriter110.createBasePeakMassType(scanMSD));
				spectrum.getCvParam().add(XmlWriter110.createBasePeakIntensity(scanMSD));
				// full spectra
				spectrum.setScanList(createScanList(scanMSD));
				spectrum.setBinaryDataArrayList(createFullSpectrumBinaryDataArrayList(scanMSD));
				if(scanMSD instanceof IRegularMassSpectrum massSpectrum) {
					spectrum.getCvParam().add(XmlWriter110.createSpectrumDimension(massSpectrum));
					spectrum.getCvParam().add(XmlWriter110.createSpectrumLevel(massSpectrum));
					spectrum.getCvParam().add(XmlWriter110.createSpectrumType(massSpectrum));
				}
				spectrum.setDefaultArrayLength(scanMSD.getNumberOfIons());
				spectrumList.getSpectrum().add(spectrum);
			}
			i++;
		}
	}

	private BinaryDataArrayListType createFullSpectrumBinaryDataArrayList(IScanMSD scanMSD) {

		List<IIon> ionList = scanMSD.getIons();
		double[] ions = new double[ionList.size()];
		float[] abundances = new float[ionList.size()];
		int i = 0;
		for(IIon ion : ionList) {
			ions[i] = ion.getIon();
			abundances[i] = ion.getAbundance();
			i++;
		}
		BinaryDataArrayListType binaryDataArrayList = new BinaryDataArrayListType();
		binaryDataArrayList.setCount(BigInteger.valueOf(2));
		binaryDataArrayList.getBinaryDataArray().add(createIonsBinaryDataArrayType(ions));
		binaryDataArrayList.getBinaryDataArray().add(createAbundancesBinaryDataArrayType(abundances));
		return binaryDataArrayList;
	}

	private BinaryDataArrayType createAbundancesBinaryDataArrayType(float[] abundances) {

		boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
		BinaryDataArrayType abundancesBinaryDataArrayType = XmlWriter110.createBinaryData(abundances, compression);
		abundancesBinaryDataArrayType.getCvParam().add(XmlWriter110.createIntensityArrayType());
		return abundancesBinaryDataArrayType;
	}

	private BinaryDataArrayType createIonsBinaryDataArrayType(double[] ions) {

		boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
		BinaryDataArrayType ionsBinaryDataArrayType = XmlWriter110.createBinaryData(ions, compression);
		ionsBinaryDataArrayType.getCvParam().add(XmlWriter110.createIonType());
		return ionsBinaryDataArrayType;
	}

	private ScanListType createScanList(IScanMSD scanMSD) {

		ScanListType scanList = new ScanListType();
		scanList.getCvParam().add(XmlWriter110.createCombinationType());
		scanList.setCount(BigInteger.valueOf(1));
		scanList.getScan().add(createScanType(scanMSD));
		return scanList;
	}

	private ScanType createScanType(IScanMSD scanMSD) {

		ScanType scanType = new ScanType();
		scanType.getCvParam().add(XmlWriter110.createScanStartTimeType(scanMSD));
		return scanType;
	}

	private SourceFileListType createSourceFileList(IChromatogram<?> chromatogram) {

		SourceFileListType sourceFileListType = new SourceFileListType();
		sourceFileListType.setCount(BigInteger.valueOf(1));
		SourceFileType sourceFile = XmlWriter110.createSourceFile(chromatogram);
		//
		if(chromatogram.getConverterId().equals("org.eclipse.chemclipse.xxd.converter.supplier.chemclipse")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1003374");
			cvParamFileFormat.setName("Open Chromatography Binary OCB format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.mz5")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000560");
			cvParamFileFormat.setName("mz5 format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.cdf")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1002443");
			cvParamFileFormat.setName("Andi-CHROM format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.mzmlb")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000560");
			cvParamFileFormat.setName("mzMLb format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.shimadzu.lcd")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1003009");
			cvParamFileFormat.setName("Shimadzu Biotech LCD format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.waters.micromass")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000526");
			cvParamFileFormat.setName("Waters raw format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.absciex")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000562");
			cvParamFileFormat.setName("ABI WIFF format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.finnigan.raw")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000563");
			cvParamFileFormat.setName("Thermo RAW format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("org.eclipse.chemclipse.msd.converter.supplier.mzdata")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000564");
			cvParamFileFormat.setName("PSI mzData format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("org.eclipse.chemclipse.msd.converter.supplier.mzxml.chromatogram")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000566");
			cvParamFileFormat.setName("ISB mzXML format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.bruker.baf")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000815");
			cvParamFileFormat.setName("Bruker BAF format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.bruker.flex.chromatogram")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1000825");
			cvParamFileFormat.setName("Bruker FID format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("org.eclipse.chemclipse.msd.converter.supplier.mgf")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1001062");
			cvParamFileFormat.setName("Mascot MGF format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		} else if(chromatogram.getConverterId().equals("net.openchrom.msd.converter.supplier.masshunter.msd")) {
			CVParamType cvParamFileFormat = new CVParamType();
			cvParamFileFormat.setCvRef(XmlWriter110.MS);
			cvParamFileFormat.setAccession("MS:1001509");
			cvParamFileFormat.setName("Agilent MassHunter format");
			cvParamFileFormat.setValue("");
			sourceFile.getCvParam().add(cvParamFileFormat);
			sourceFileListType.setCount(BigInteger.valueOf(2));
		}
		sourceFileListType.getSourceFile().add(sourceFile);
		return sourceFileListType;
	}

	private FileDescriptionType createFileDescription(IChromatogramMSD chromatogram, SourceFileListType sourceFiles) {

		FileDescriptionType fileDescriptionType = new FileDescriptionType();
		fileDescriptionType.setSourceFileList(sourceFiles);
		fileDescriptionType.setFileContent(createFileContent(chromatogram));
		ParamGroupType paramGroupType = XmlWriter110.getOperator(chromatogram);
		if(paramGroupType != null) {
			fileDescriptionType.getContact().add(paramGroupType);
		}
		return fileDescriptionType;
	}

	private ParamGroupType createFileContent(IChromatogramMSD chromatogram) {

		ParamGroupType fileContent = new ParamGroupType();
		IScan firstScan = chromatogram.getScan(1);
		if(firstScan instanceof IRegularMassSpectrum massSpectrum) {
			fileContent.getCvParam().add(XmlWriter110.createSpectrumDimension(massSpectrum));
			fileContent.getCvParam().add(XmlWriter110.createSpectrumType(massSpectrum));
		}
		return fileContent;
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
		dataProcessingList.getDataProcessing().add(createDataProcessing(software));
		return dataProcessingList;
	}

	private DataProcessingType createDataProcessing(SoftwareType software) {

		DataProcessingType dataProcessing = new DataProcessingType();
		dataProcessing.setId(Activator.getContext().getBundle().getSymbolicName());
		dataProcessing.getProcessingMethod().add(createProcessingMethod(software));
		return dataProcessing;
	}

	private ProcessingMethodType createProcessingMethod(SoftwareType software) {

		ProcessingMethodType processingMethod = new ProcessingMethodType();
		processingMethod.setSoftwareRef(software);
		processingMethod.setOrder(BigInteger.valueOf(1));
		processingMethod.getCvParam().add(createExportParam());
		return processingMethod;
	}

	private CVParamType createExportParam() {

		CVParamType exportParam = new CVParamType();
		exportParam.setCvRef(XmlWriter110.MS);
		exportParam.setAccession("MS:1000544");
		exportParam.setName("Conversion to mzML");
		exportParam.setValue("");
		return exportParam;
	}
}
