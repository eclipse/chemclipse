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
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.CvParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData.SpectrumList;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData.SpectrumList.Spectrum;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.PeakListBinaryType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.SpectrumDescType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.SpectrumSettingsType;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.SpectrumSettingsType.SpectrumInstrument;
import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.SupDataBinaryType.Data;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class ChromatogramWriter105 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriter105.class);

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.MzData.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			SpectrumList spectrumList = new SpectrumList();
			for(IScan scan : chromatogram.getScans()) {
				Spectrum spectrum = new Spectrum();
				// Retention Time
				CvParamType retentionTime = new CvParamType();
				retentionTime.setName("TimeInSeconds");
				retentionTime.setValue(String.valueOf(scan.getRetentionTime() / 1000f));
				SpectrumInstrument spectrumInstrument = new SpectrumInstrument();
				spectrumInstrument.getCvParamOrUserParam().add(retentionTime);
				SpectrumSettingsType spectrumSettings = new SpectrumSettingsType();
				spectrumSettings.setSpectrumInstrument(spectrumInstrument);
				SpectrumDescType spectrumDesc = new SpectrumDescType();
				spectrumDesc.setSpectrumSettings(spectrumSettings);
				spectrum.setSpectrumDesc(spectrumDesc);
				// Convert
				IScanMSD scanMSD = (IScanMSD)scan;
				double[] ions = new double[scanMSD.getNumberOfIons()];
				float[] abundances = new float[scanMSD.getNumberOfIons()];
				int i = 0;
				for(IIon ion : scanMSD.getIons()) {
					ions[i] = ion.getIon();
					abundances[i] = ion.getAbundance();
					i++;
				}
				// Ions
				DoubleBuffer doubleBuffer = DoubleBuffer.wrap(ions);
				ByteBuffer byteBuffer = ByteBuffer.allocate(doubleBuffer.capacity() * Double.BYTES);
				byteBuffer.asDoubleBuffer().put(doubleBuffer);
				spectrum.setMzArrayBinary(createBinaryType(byteBuffer, 64));
				// Abundances
				FloatBuffer floatBuffer = FloatBuffer.wrap(abundances);
				byteBuffer = ByteBuffer.allocate(floatBuffer.capacity() * Float.BYTES);
				byteBuffer.asFloatBuffer().put(floatBuffer);
				spectrum.setIntenArrayBinary(createBinaryType(byteBuffer, 32));
				//
				spectrumList.getSpectrum().add(spectrum);
			}
			MzData mzData = new MzData();
			mzData.setVersion(IFormat.V_105);
			mzData.setSpectrumList(spectrumList);
			marshaller.marshal(mzData, file);
		} catch(JAXBException e) {
			logger.warn(e);
		}
	}

	PeakListBinaryType createBinaryType(ByteBuffer byteBuffer, int precision) {

		Data data = new Data();
		data.setPrecision(precision);
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		data.setEndian("big");
		data.setValue(byteBuffer.array());
		data.setLength(byteBuffer.capacity());
		PeakListBinaryType peakListBinaryType = new PeakListBinaryType();
		peakListBinaryType.setData(data);
		return peakListBinaryType;
	}
}
