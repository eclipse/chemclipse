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
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
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
			ChromatogramType tic = createTIC(totalSignals, retentionTimes);
			chromatogramList.getChromatogram().add(tic);
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

	private ChromatogramType createTIC(float[] totalSignals, float[] retentionTimes) {

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
		ChromatogramType tic = new ChromatogramType();
		tic.setId("tic");
		tic.setIndex(BigInteger.valueOf(0));
		tic.getCvParam().add(createTotalIonCurrrentType());
		tic.setDefaultArrayLength(totalSignals.length);
		tic.setBinaryDataArrayList(binaryDataArrayList);
		return tic;
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
			i++;
			spectrum.getCvParam().add(createTotalIonCurrentType(scan));
			IScanMSD scanMSD = (IScanMSD)scan;
			// full spectra
			//
			spectrum.getCvParam().add(createBasePeakMassType(scanMSD));
			spectrum.getCvParam().add(createBasePeakIntensity(scanMSD));
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
			boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
			BinaryDataArrayType ionsBinaryDataArrayType = XmlWriter110.createBinaryData(ions, compression);
			ionsBinaryDataArrayType.getCvParam().add(createIonType());
			binaryDataArrayList.getBinaryDataArray().add(ionsBinaryDataArrayType);
			//
			BinaryDataArrayType abundancesBinaryDataArrayType = XmlWriter110.createBinaryData(abundances, compression);
			abundancesBinaryDataArrayType.getCvParam().add(XmlWriter110.createIntensityArrayType());
			binaryDataArrayList.getBinaryDataArray().add(abundancesBinaryDataArrayType);
			//
			ScanType scanType = new ScanType();
			scanType.getCvParam().add(createScanStartTimeType(scanMSD));
			//
			ScanListType scanList = new ScanListType();
			scanList.getCvParam().add(createCombinationType());
			scanList.setCount(BigInteger.valueOf(1));
			scanList.getScan().add(scanType);
			//
			spectrum.setScanList(scanList);
			spectrum.setBinaryDataArrayList(binaryDataArrayList);
			IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)scanMSD;
			spectrum.getCvParam().add(createSpectrumDimension(massSpectrum));
			spectrum.getCvParam().add(createSpectrumLevel(massSpectrum));
			spectrum.getCvParam().add(createSpectrumType(massSpectrum));
			spectrum.setDefaultArrayLength(ions.length);
			spectrumList.getSpectrum().add(spectrum);
		}
	}

	private CVParamType createBasePeakIntensity(IScanMSD scanMSD) {

		CVParamType cvParamBasePeakIntensity = new CVParamType();
		cvParamBasePeakIntensity.setCvRef(XmlWriter110.MS);
		cvParamBasePeakIntensity.setAccession("MS:1000505");
		cvParamBasePeakIntensity.setName("base peak intensity");
		cvParamBasePeakIntensity.setUnitCvRef(XmlWriter110.MS);
		cvParamBasePeakIntensity.setUnitAccession("MS:1000131");
		cvParamBasePeakIntensity.setUnitName("number of detector counts");
		cvParamBasePeakIntensity.setValue(String.valueOf(scanMSD.getBasePeakAbundance()));
		return cvParamBasePeakIntensity;
	}

	private CVParamType createBasePeakMassType(IScanMSD scanMSD) {

		CVParamType cvParamBasePeak = new CVParamType();
		cvParamBasePeak.setCvRef(XmlWriter110.MS);
		cvParamBasePeak.setAccession("MS:1000504");
		cvParamBasePeak.setName("base peak m/z");
		cvParamBasePeak.setUnitCvRef(XmlWriter110.MS);
		cvParamBasePeak.setUnitAccession("MS:1000040");
		cvParamBasePeak.setUnitName("m/z");
		cvParamBasePeak.setValue(String.valueOf(scanMSD.getBasePeak()));
		return cvParamBasePeak;
	}

	private CVParamType createTotalIonCurrentType(IScan scan) {

		CVParamType cvParamTotalIonCurrent = new CVParamType();
		cvParamTotalIonCurrent.setCvRef(XmlWriter110.MS);
		cvParamTotalIonCurrent.setAccession("MS:1000285");
		cvParamTotalIonCurrent.setName("total ion current");
		cvParamTotalIonCurrent.setValue(String.valueOf(scan.getTotalSignal()));
		return cvParamTotalIonCurrent;
	}

	private CVParamType createCombinationType() {

		CVParamType cvParamCombination = new CVParamType();
		cvParamCombination.setCvRef(XmlWriter110.MS);
		cvParamCombination.setAccession("MS:1000795");
		cvParamCombination.setName("no combination");
		return cvParamCombination;
	}

	private CVParamType createTotalIonCurrrentType() {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000235");
		cvParam.setName("total ion current chromatogram");
		cvParam.setValue("");
		return cvParam;
	}

	private CVParamType createScanStartTimeType(IScan scan) {

		CVParamType cvParamScanStartTime = new CVParamType();
		cvParamScanStartTime.setCvRef(XmlWriter110.MS);
		cvParamScanStartTime.setAccession("MS:1000016");
		cvParamScanStartTime.setName("scan start time");
		cvParamScanStartTime.setUnitCvRef(XmlWriter110.UO);
		cvParamScanStartTime.setUnitAccession("UO:0000031");
		cvParamScanStartTime.setUnitName("minute");
		cvParamScanStartTime.setValue(String.valueOf(scan.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
		return cvParamScanStartTime;
	}

	private CVParamType createIonType() {

		CVParamType cvParamIons = new CVParamType();
		cvParamIons.setCvRef(XmlWriter110.MS);
		cvParamIons.setAccession("MS:1000514");
		cvParamIons.setName("m/z array");
		cvParamIons.setUnitCvRef(XmlWriter110.MS);
		cvParamIons.setUnitAccession("MS:1000040");
		cvParamIons.setUnitName("m/z");
		return cvParamIons;
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
		ParamGroupType fileContent = new ParamGroupType();
		IScanMSD scanMSD = (IScanMSD)chromatogram.getScan(1);
		IRegularMassSpectrum massSpectrum = (IRegularMassSpectrum)scanMSD;
		fileContent.getCvParam().add(createSpectrumDimension(massSpectrum));
		fileContent.getCvParam().add(createSpectrumType(massSpectrum));
		fileDescriptionType.setFileContent(fileContent);
		ParamGroupType paramGroupType = XmlWriter110.getOperator(chromatogram);
		if(paramGroupType != null) {
			fileDescriptionType.getContact().add(paramGroupType);
		}
		return fileDescriptionType;
	}

	private CVParamType createSpectrumDimension(IRegularMassSpectrum massSpectrum) {

		CVParamType cvParamSpectrum = new CVParamType();
		if(massSpectrum.getMassSpectrometer() == 1) {
			cvParamSpectrum.setCvRef(XmlWriter110.MS);
			cvParamSpectrum.setAccession("MS:1000579");
			cvParamSpectrum.setName("MS1 spectrum");
		} else {
			cvParamSpectrum.setCvRef(XmlWriter110.MS);
			cvParamSpectrum.setAccession("MS:1000580");
			cvParamSpectrum.setName("MSn spectrum");
		}
		cvParamSpectrum.setValue("");
		return cvParamSpectrum;
	}

	private CVParamType createSpectrumLevel(IRegularMassSpectrum massSpectrum) {

		CVParamType cvParamLevel = new CVParamType();
		cvParamLevel.setCvRef(XmlWriter110.MS);
		cvParamLevel.setAccession("MS:1000511");
		cvParamLevel.setName("ms level");
		cvParamLevel.setValue(String.valueOf(massSpectrum.getMassSpectrometer()));
		return cvParamLevel;
	}

	private CVParamType createSpectrumType(IRegularMassSpectrum massSpectrum) {

		CVParamType cvParamSpectrumType = new CVParamType();
		if(massSpectrum.getMassSpectrumType() == MassSpectrumType.CENTROID) {
			cvParamSpectrumType.setCvRef(XmlWriter110.MS);
			cvParamSpectrumType.setAccession("MS:1000127");
			cvParamSpectrumType.setName("centroid spectrum");
		} else if(massSpectrum.getMassSpectrumType() == MassSpectrumType.PROFILE) {
			cvParamSpectrumType.setCvRef(XmlWriter110.MS);
			cvParamSpectrumType.setAccession("MS:1000128");
			cvParamSpectrumType.setName("profile spectrum");
		}
		cvParamSpectrumType.setValue("");
		return cvParamSpectrumType;
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
