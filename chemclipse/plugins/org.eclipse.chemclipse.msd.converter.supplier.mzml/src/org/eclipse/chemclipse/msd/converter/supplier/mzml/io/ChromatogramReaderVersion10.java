/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.io;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramReader;
import org.eclipse.chemclipse.converter.l10n.ConverterMessages;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.IVendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorChromatogram;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorIon;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.IRegularMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.MassSpectrumType;
import org.eclipse.chemclipse.msd.model.implementation.IonTransition;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.chemclipse.support.history.EditInformation;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.BinaryReader10;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.io.XmlReader10;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.BinaryDataArrayType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.CVParamType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.ChromatogramType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.DataProcessingType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.InstrumentConfigurationType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.MzMLType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.ParamGroupType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.PrecursorType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.ProcessingMethodType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.RunType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.SampleListType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.SampleType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.ScanType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.SoftwareType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.SpectrumDescriptionType;
import org.eclipse.chemclipse.xxd.converter.supplier.mzml.model.v10.SpectrumType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;

public class ChromatogramReaderVersion10 extends AbstractChromatogramReader implements IChromatogramMSDReader {

	private static final Logger logger = Logger.getLogger(ChromatogramReaderVersion10.class);

	@Override
	public IChromatogramOverview readOverview(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
		try {
			chromatogram = new VendorChromatogram();
			MzMLType mzML = XmlReader10.getMzML(file);
			readTIC(mzML.getRun(), chromatogram);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(SAXException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		}
		return chromatogram;
	}

	@Override
	public IChromatogramMSD read(File file, IProgressMonitor monitor) throws IOException {

		IVendorChromatogram chromatogram = null;
		try {
			chromatogram = new VendorChromatogram();
			chromatogram.setFile(file);
			MzMLType mzML = XmlReader10.getMzML(file);
			readContact(mzML, chromatogram);
			readSample(mzML, chromatogram);
			readInstrument(mzML, chromatogram);
			readEditHistory(mzML, chromatogram);
			readSpectrum(mzML, chromatogram, monitor);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		}
		return chromatogram;
	}

	private IRegularMassSpectrum readMassSpectrum(SpectrumType spectrum) {

		IRegularMassSpectrum massSpectrum = new VendorMassSpectrum();
		for(CVParamType cvParam : spectrum.getCvParam()) {
			if(cvParam.getAccession().equals("MS:1000127") && cvParam.getName().equals("centroid spectrum")) {
				massSpectrum.setMassSpectrumType(MassSpectrumType.CENTROID);
			} else if(cvParam.getAccession().equals("MS:1000128") && cvParam.getName().equals("profile spectrum")) {
				massSpectrum.setMassSpectrumType(MassSpectrumType.PROFILE);
			}
			if(cvParam.getAccession().equals("MS:1000511") && cvParam.getName().equals("ms level")) {
				short msLevel = Short.parseShort(cvParam.getValue());
				massSpectrum.setMassSpectrometer(msLevel);
			}
		}
		return massSpectrum;
	}

	private void readSpectrum(MzMLType mzML, IVendorChromatogram chromatogram, IProgressMonitor monitor) {

		monitor.beginTask(ConverterMessages.readScans, mzML.getRun().getSpectrumList().getCount().intValue());
		int cycleNumber = isMultiStageMassSpectrum(mzML) ? 1 : 0;
		for(SpectrumType spectrum : mzML.getRun().getSpectrumList().getSpectrum()) {
			ScanType scanType = spectrum.getSpectrumDescription().getScan();
			if(scanType == null) {
				continue;
			}
			IRegularMassSpectrum massSpectrum = readMassSpectrum(spectrum);
			if(massSpectrum.getMassSpectrometer() < 2) {
				cycleNumber++;
			}
			if(cycleNumber >= 1) {
				massSpectrum.setCycleNumber(cycleNumber);
			}
			setRetentionTime(scanType, massSpectrum);
			readIons(spectrum, massSpectrum, chromatogram);
			chromatogram.addScan(massSpectrum);
			monitor.worked(1);
		}
	}

	private double getSelectedIon(SpectrumDescriptionType spectrumDescription) {

		if(spectrumDescription.getPrecursorList() != null) {
			for(PrecursorType precursorType : spectrumDescription.getPrecursorList().getPrecursor()) {
				if(precursorType.getSelectedIonList() != null) {
					for(ParamGroupType paramGroupType : precursorType.getSelectedIonList().getSelectedIon()) {
						for(CVParamType cvParam : paramGroupType.getCvParam()) {
							if(cvParam.getAccession().equals("MS:1000744") && cvParam.getName().equals("selected ion m/z")) {
								return Double.parseDouble(cvParam.getValue());
							}
						}
					}
				}
			}
		}
		return 0;
	}

	private double getSelectedIonPeakIntensity(SpectrumDescriptionType spectrumDescription) {

		if(spectrumDescription.getPrecursorList() != null) {
			for(PrecursorType precursorType : spectrumDescription.getPrecursorList().getPrecursor()) {
				if(precursorType.getSelectedIonList() != null) {
					for(ParamGroupType paramGroupType : precursorType.getSelectedIonList().getSelectedIon()) {
						for(CVParamType cvParam : paramGroupType.getCvParam()) {
							if(cvParam.getAccession().equals("MS:1000042") && cvParam.getName().equals("peak intensity")) {
								return Double.parseDouble(cvParam.getValue());
							}
						}
					}
				}
			}
		}
		return 0;
	}

	private double getCollisionEnergy(SpectrumDescriptionType spectrumDescription) {

		if(spectrumDescription.getPrecursorList() != null) {
			for(PrecursorType precursorType : spectrumDescription.getPrecursorList().getPrecursor()) {
				for(CVParamType cvParam : precursorType.getActivation().getCvParam()) {
					if(cvParam.getAccession().equals("MS:1000045") && cvParam.getName().equals("collision energy")) {
						return Double.parseDouble(cvParam.getValue());
					}
				}
			}
		}
		return 0;
	}

	private void readIons(SpectrumType spectrum, IRegularMassSpectrum massSpectrum, IVendorChromatogram chromatogram) {

		double[] intensities = null;
		double[] mzs = null;
		for(BinaryDataArrayType binaryDataArrayType : spectrum.getBinaryDataArrayList().getBinaryDataArray()) {
			try {
				Pair<String, double[]> binaryData = BinaryReader10.parseBinaryData(binaryDataArrayType);
				if(binaryData.getKey().equals("m/z")) {
					mzs = binaryData.getValue();
				} else if(binaryData.getKey().equals("intensity")) {
					intensities = binaryData.getValue();
				}
			} catch(DataFormatException e) {
				logger.error(e);
			}
		}
		SpectrumDescriptionType spectrumDescription = spectrum.getSpectrumDescription();
		double selectedIon = getSelectedIon(spectrumDescription);
		massSpectrum.setPrecursorIon(selectedIon);
		massSpectrum.setPrecursorBasePeak(getSelectedIonPeakIntensity(spectrumDescription));
		double collisionEnergy = getCollisionEnergy(spectrumDescription);
		int ions = Math.min(mzs.length, intensities.length);
		for(int i = 0; i < ions; i++) {
			if(selectedIon != 0) {
				IIonTransition ionTransition = new IonTransition(selectedIon, mzs[i], collisionEnergy, 1, 1, 0);
				massSpectrum.addIon(new VendorIon(mzs[i], (float)intensities[i], ionTransition), false);
				chromatogram.getIonTransitionSettings().getIonTransitions().add(ionTransition);
			} else {
				massSpectrum.addIon(new VendorIon(mzs[i], (float)intensities[i]), false);
			}
		}
	}

	private void setRetentionTime(ScanType scanType, IRegularMassSpectrum massSpectrum) {

		for(CVParamType cvParam : scanType.getCvParam()) {
			if(cvParam.getAccession().equals("MS:1000016") && cvParam.getName().equals("scan time")) {
				int multiplicator = XmlReader10.getTimeMultiplicator(cvParam);
				int retentionTime = Math.round(Float.parseFloat(cvParam.getValue()) * multiplicator);
				massSpectrum.setRetentionTime(retentionTime);
			}
		}
	}

	private void readEditHistory(MzMLType mzML, IVendorChromatogram chromatogram) {

		for(DataProcessingType dataProcessing : mzML.getDataProcessingList().getDataProcessing()) {
			SoftwareType software = (SoftwareType)dataProcessing.getSoftwareRef();
			for(ProcessingMethodType processingMethod : dataProcessing.getProcessingMethod()) {
				for(CVParamType cvParam : processingMethod.getCvParam()) {
					String operation = cvParam.getName();
					String editor = software.getId();
					chromatogram.getEditHistory().add(new EditInformation(operation, editor));
				}
			}
		}
	}

	private void readTIC(RunType run, IVendorChromatogram chromatogram) {

		double[] retentionTimes = null;
		double[] intensities = null;
		for(ChromatogramType chromatogramType : run.getChromatogramList().getChromatogram()) {
			if(chromatogramType.getId().equals("TIC")) {
				if(chromatogramType.getCvParam().stream().anyMatch(n -> n.getAccession().equals("MS:1000235") && n.getName().equals("total ion current chromatogram"))) {
					for(BinaryDataArrayType binaryDataArrayType : chromatogramType.getBinaryDataArrayList().getBinaryDataArray()) {
						try {
							Pair<String, double[]> binaryData = BinaryReader10.parseBinaryData(binaryDataArrayType);
							if(binaryData.getKey().equals("time")) {
								retentionTimes = binaryData.getValue();
							} else if(binaryData.getKey().equals("intensity")) {
								intensities = binaryData.getValue();
							}
						} catch(DataFormatException e) {
							logger.warn(e);
						}
					}
				}
			}
		}
		XmlMassSpectrumReader.addTotalSignals(intensities, retentionTimes, chromatogram);
	}

	private void readContact(MzMLType mzML, IVendorChromatogram chromatogram) {

		for(ParamGroupType contact : mzML.getFileDescription().getContact()) {
			for(CVParamType cvParam : contact.getCvParam()) {
				if(chromatogram.getOperator().isEmpty()) {
					chromatogram.setOperator(cvParam.getValue());
				} else {
					chromatogram.setOperator(String.join(", ", chromatogram.getOperator(), cvParam.getValue()));
				}
			}
		}
	}

	private void readSample(MzMLType mzML, IVendorChromatogram chromatogram) {

		SampleListType sampleList = mzML.getSampleList();
		if(sampleList != null) {
			for(SampleType sample : sampleList.getSample()) {
				chromatogram.setSampleName(sample.getName());
			}
		}
	}

	private void readInstrument(MzMLType mzML, IVendorChromatogram chromatogram) {

		for(InstrumentConfigurationType instrument : mzML.getInstrumentConfigurationList().getInstrumentConfiguration()) {
			for(CVParamType cvParam : instrument.getCvParam()) {
				if(cvParam.getAccession().equals("MS:1000554")) {
					chromatogram.setInstrument(cvParam.getName());
				}
			}
		}
	}

	private boolean isMultiStageMassSpectrum(MzMLType mzML) {

		for(CVParamType cvParam : mzML.getFileDescription().getFileContent().getCvParam()) {
			if(cvParam.getAccession().equals("MS:1000580") && cvParam.getName().equals("MSn spectrum")) {
				return true;
			}
		}
		return false;
	}
}
