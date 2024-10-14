/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.mzml.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.zip.Deflater;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.BinaryDataArrayType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.CVParamType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.CVType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.ParamGroupType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SoftwareListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SoftwareType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v110.SourceFileType;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Version;

import jakarta.xml.bind.DatatypeConverter;

public class XmlWriter110 {

	public static final CVType MS = createMS();
	public static final CVType UO = createUO();
	//
	private static final Logger logger = Logger.getLogger(XmlWriter110.class);

	public static ParamGroupType getOperator(IChromatogram<?> chromatogram) {

		if(!chromatogram.getOperator().isEmpty()) {
			ParamGroupType paramGroupType = new ParamGroupType();
			CVParamType cvParam = new CVParamType();
			cvParam.setCvRef(XmlWriter110.MS);
			cvParam.setAccession("MS:1000586");
			cvParam.setName("contact name");
			cvParam.setValue(chromatogram.getOperator());
			paramGroupType.getCvParam().add(cvParam);
			return paramGroupType;
		}
		return null;
	}

	public static SoftwareListType createSoftwareList() {

		SoftwareListType softwareList = new SoftwareListType();
		softwareList.setCount(BigInteger.valueOf(1));
		SoftwareType software = new SoftwareType();
		IProduct product = Platform.getProduct();
		software.setId("Unknown");
		if(product != null) {
			software.setId(product.getName());
			Version version = product.getDefiningBundle().getVersion();
			software.setVersion(version.getMajor() + "." + version.getMinor() + "." + version.getMicro());
			if(product.getName().equals("ChemClipse")) {
				CVParamType cvParamSoftware = new CVParamType();
				cvParamSoftware.setCvRef(XmlWriter110.MS);
				cvParamSoftware.setAccession("MS:1003376");
				cvParamSoftware.setName("ChemClipse");
				cvParamSoftware.setValue("");
				software.getCvParam().add(cvParamSoftware);
			}
			if(product.getName().equals("OpenChrom")) {
				CVParamType cvParamSoftware = new CVParamType();
				cvParamSoftware.setCvRef(XmlWriter110.MS);
				cvParamSoftware.setAccession("MS:1003377");
				cvParamSoftware.setName("OpenChrom");
				cvParamSoftware.setValue("");
				software.getCvParam().add(cvParamSoftware);
			}
		}
		softwareList.getSoftware().add(software);
		return softwareList;
	}

	public static CVParamType createSpectrumLevel(IRegularMassSpectrum massSpectrum) {

		CVParamType cvParamLevel = new CVParamType();
		cvParamLevel.setCvRef(XmlWriter110.MS);
		cvParamLevel.setAccession("MS:1000511");
		cvParamLevel.setName("ms level");
		cvParamLevel.setValue(String.valueOf(massSpectrum.getMassSpectrometer()));
		return cvParamLevel;
	}

	public static CVParamType createIntensityArrayType() {

		CVParamType cvParamTotalSignals = new CVParamType();
		cvParamTotalSignals.setCvRef(XmlWriter110.MS);
		cvParamTotalSignals.setAccession("MS:1000515");
		cvParamTotalSignals.setName("intensity array");
		cvParamTotalSignals.setUnitCvRef(XmlWriter110.MS);
		cvParamTotalSignals.setUnitAccession("MS:1000131");
		cvParamTotalSignals.setUnitName("number of counts");
		return cvParamTotalSignals;
	}

	public static CVParamType createRetentionTimeType() {

		CVParamType cvParamRetentionTime = new CVParamType();
		cvParamRetentionTime.setCvRef(XmlWriter110.MS);
		cvParamRetentionTime.setAccession("MS:1000595");
		cvParamRetentionTime.setName("time array");
		cvParamRetentionTime.setUnitAccession("UO:0000010");
		cvParamRetentionTime.setUnitName("second");
		return cvParamRetentionTime;
	}

	public static XMLGregorianCalendar createDate(IChromatogram<?> chromatogram) throws DatatypeConfigurationException {

		Date date = chromatogram.getDate();
		if(date != null) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
		}
		return null;
	}

	public static BinaryDataArrayType createBinaryData(float[] values, boolean compression) {

		FloatBuffer floatBuffer = FloatBuffer.wrap(values);
		ByteBuffer byteBuffer = ByteBuffer.allocate(floatBuffer.capacity() * Float.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.asFloatBuffer().put(floatBuffer);
		BinaryDataArrayType binaryDataArrayType = createBinaryDataArray(byteBuffer, compression);
		binaryDataArrayType.getCvParam().add(createFloatParamType());
		return binaryDataArrayType;
	}

	public static CVParamType createFloatParamType() {

		CVParamType cvParamData = new CVParamType();
		cvParamData.setCvRef(XmlWriter110.MS);
		cvParamData.setAccession("MS:1000521");
		cvParamData.setName("32-bit float");
		return cvParamData;
	}

	public static CVParamType createTotalIonCurrentType(IScan scan) {

		CVParamType cvParamTotalIonCurrent = new CVParamType();
		cvParamTotalIonCurrent.setCvRef(XmlWriter110.MS);
		cvParamTotalIonCurrent.setAccession("MS:1000285");
		cvParamTotalIonCurrent.setName("total ion current");
		cvParamTotalIonCurrent.setValue(String.valueOf(scan.getTotalSignal()));
		return cvParamTotalIonCurrent;
	}

	public static BinaryDataArrayType createBinaryData(double[] values, boolean compression) {

		DoubleBuffer doubleBuffer = DoubleBuffer.wrap(values);
		ByteBuffer byteBuffer = ByteBuffer.allocate(doubleBuffer.capacity() * Double.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.asDoubleBuffer().put(doubleBuffer);
		BinaryDataArrayType binaryDataArrayType = createBinaryDataArray(byteBuffer, compression);
		binaryDataArrayType.getCvParam().add(createDoubleParamType());
		return binaryDataArrayType;
	}

	public static CVParamType createDoubleParamType() {

		CVParamType cvParamData = new CVParamType();
		cvParamData.setCvRef(XmlWriter110.MS);
		cvParamData.setAccession("MS:1000523");
		cvParamData.setName("64-bit float");
		return cvParamData;
	}

	public static CVParamType createScanStartTimeType(IScan scan) {

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

	public static CVParamType createBasePeakIntensity(IScanMSD scanMSD) {

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

	public static CVParamType createBasePeakMassType(IScanMSD scanMSD) {

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

	public static CVParamType createCombinationType() {

		CVParamType cvParamCombination = new CVParamType();
		cvParamCombination.setCvRef(XmlWriter110.MS);
		cvParamCombination.setAccession("MS:1000795");
		cvParamCombination.setName("no combination");
		return cvParamCombination;
	}

	public static CVParamType createTotalIonCurrrentType() {

		CVParamType cvParam = new CVParamType();
		cvParam.setCvRef(XmlWriter110.MS);
		cvParam.setAccession("MS:1000235");
		cvParam.setName("total ion current chromatogram");
		cvParam.setValue("");
		return cvParam;
	}

	public static CVParamType createIonType() {

		CVParamType cvParamIons = new CVParamType();
		cvParamIons.setCvRef(XmlWriter110.MS);
		cvParamIons.setAccession("MS:1000514");
		cvParamIons.setName("m/z array");
		cvParamIons.setUnitCvRef(XmlWriter110.MS);
		cvParamIons.setUnitAccession("MS:1000040");
		cvParamIons.setUnitName("m/z");
		return cvParamIons;
	}

	public static CVParamType createSpectrumDimension(IRegularMassSpectrum massSpectrum) {

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

	public static CVParamType createSpectrumType(IRegularMassSpectrum massSpectrum) {

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

	private static BinaryDataArrayType createBinaryDataArray(ByteBuffer byteBuffer, boolean compression) {

		BinaryDataArrayType binaryDataArrayType = new BinaryDataArrayType();
		if(compression) {
			binaryDataArrayType.getCvParam().add(createZlibCompressionParamType());
			Deflater compresser = new Deflater();
			compresser.setInput(byteBuffer.array());
			compresser.finish();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] readBuffer = new byte[1024];
			while(!compresser.finished()) {
				int compressCount = compresser.deflate(readBuffer);
				if(compressCount > 0) {
					outputStream.write(readBuffer, 0, compressCount);
				}
			}
			byte[] outputByteArray = outputStream.toByteArray();
			String characters = DatatypeConverter.printBase64Binary(outputByteArray);
			binaryDataArrayType.setEncodedLength(BigInteger.valueOf(characters.length()));
			binaryDataArrayType.setBinary(outputByteArray);
			compresser.end();
		} else {
			binaryDataArrayType.setBinary(byteBuffer.array());
		}
		return binaryDataArrayType;
	}

	public static CVParamType createZlibCompressionParamType() {

		CVParamType cvParamCompression = new CVParamType();
		cvParamCompression.setCvRef(XmlWriter110.MS);
		cvParamCompression.setAccession("MS:1000574");
		cvParamCompression.setName("zlib compression");
		return cvParamCompression;
	}

	private static CVType createMS() {

		CVType cvTypeMS = new CVType();
		cvTypeMS.setId("MS");
		cvTypeMS.setFullName("Proteomics Standards Initiative Mass Spectrometry Ontology");
		cvTypeMS.setVersion("4.1.123");
		cvTypeMS.setURI("https://github.com/HUPO-PSI/psi-ms-CV/releases/download/v4.1.123/psi-ms.obo");
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

	public static SourceFileType createSourceFile(IChromatogram<?> chromatogram) {

		File file = chromatogram.getFile();
		SourceFileType sourceFile = new SourceFileType();
		sourceFile.setLocation(file.getAbsolutePath());
		sourceFile.setId(file.getName());
		sourceFile.setName(file.getName());
		//
		CVParamType cvParamSHA1 = new CVParamType();
		cvParamSHA1.setCvRef(XmlWriter110.MS);
		cvParamSHA1.setAccession("MS:1000569");
		cvParamSHA1.setName("SHA-1");
		cvParamSHA1.setValue(calculateSHA1(file));
		sourceFile.getCvParam().add(cvParamSHA1);
		return sourceFile;
	}

	private static String calculateSHA1(File file) {

		try (FileInputStream fis = new FileInputStream(file)) {
			return DigestUtils.sha1Hex(fis);
		} catch(IOException e) {
			logger.warn(e);
		}
		return "";
	}
}
