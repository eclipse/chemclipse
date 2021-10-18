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
import java.io.IOException;
import java.util.zip.DataFormatException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.exceptions.AbundanceLimitExceededException;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.IVendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.IVendorMassSpectra;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorIon;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.converter.model.VendorMassSpectra;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.BinaryReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter.XmlReader;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.FileDescriptionType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.MzML;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.ParamGroupType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.RunType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.SpectrumType;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IVendorMassSpectrum;
import org.eclipse.chemclipse.msd.model.exceptions.IonLimitExceededException;
import org.eclipse.chemclipse.msd.model.implementation.VendorMassSpectrum;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;

public class MassSpectrumReaderVersion110 extends AbstractMassSpectraReader implements IMassSpectraReader {

	private static final Logger logger = Logger.getLogger(MassSpectrumReaderVersion110.class);
	private String contextPath;

	public MassSpectrumReaderVersion110(String contextPath) {

		this.contextPath = contextPath;
	}

	@Override
	public IMassSpectra read(File file, IProgressMonitor monitor) throws IOException {

		IVendorMassSpectrum massSpectrum = null;
		//
		try {
			//
			massSpectrum = new VendorMassSpectrum();
			massSpectrum.setFile(file);
			massSpectrum.setIdentifier(file.getName());
			//
			MzML mzML = XmlReader.getMzML(file, contextPath);
			//
			FileDescriptionType fileDescription = mzML.getFileDescription();
			if(fileDescription != null) {
				ParamGroupType fileContent = fileDescription.getFileContent();
				for(CVParamType cvParam : fileContent.getCvParam()) {
					if(cvParam.getAccession().equals("MS:1000579")) {
						if(cvParam.getName().equals("MS1 spectrum")) {
							massSpectrum.setMassSpectrometer((short)1);
						}
					}
				}
			}
			//
			double[] mzs = null;
			double[] intensities = null;
			//
			RunType run = mzML.getRun();
			for(SpectrumType spectrum : run.getSpectrumList().getSpectrum()) {
				for(CVParamType cvParam : spectrum.getCvParam()) {
					if(cvParam.getAccession().equals("MS:1000127") && cvParam.getName().equals("centroid spectrum")) {
						massSpectrum.setMassSpectrumType((short)0);
					} else if(cvParam.getAccession().equals("MS:1000128") && cvParam.getName().equals("profile spectrum")) {
						massSpectrum.setMassSpectrumType((short)1);
					}
				}
				for(BinaryDataArrayType binaryDataArrayType : spectrum.getBinaryDataArrayList().getBinaryDataArray()) {
					Pair<String, double[]> binaryData = BinaryReader.parseBinaryData(binaryDataArrayType);
					if(binaryData.getKey().equals("m/z")) {
						mzs = binaryData.getValue();
					} else if(binaryData.getKey().equals("intensity")) {
						intensities = binaryData.getValue();
					}
				}
			}
			int ions = Math.min(mzs.length, intensities.length);
			for(int i = 0; i < ions; i++) {
				try {
					double intensity = intensities[i];
					if(intensity >= VendorIon.MIN_ABUNDANCE && intensity <= VendorIon.MAX_ABUNDANCE) {
						double mz = AbstractIon.getIon(mzs[i]);
						IVendorIon ion = new VendorIon(mz, (float)intensity);
						massSpectrum.addIon(ion);
					}
				} catch(AbundanceLimitExceededException e) {
					logger.warn(e);
				} catch(IonLimitExceededException e) {
					logger.warn(e);
				}
			}
		} catch(SAXException e) {
			logger.warn(e);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(ParserConfigurationException e) {
			logger.warn(e);
		} catch(DataFormatException e) {
			logger.warn(e);
		}
		//
		IVendorMassSpectra massSpectra = new VendorMassSpectra();
		massSpectra.setName(file.getName());
		massSpectra.addMassSpectrum(massSpectrum);
		return massSpectra;
	}
}
