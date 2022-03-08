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
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.util.List;
import java.util.zip.Deflater;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.converter.io.AbstractChromatogramWriter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.IChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model.MsRun;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model.MzXML;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model.ObjectFactory;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model.Peaks;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v32.model.Scan;
import org.eclipse.chemclipse.msd.converter.supplier.mzxml.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.core.runtime.IProgressMonitor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class ChromatogramWriter32 extends AbstractChromatogramWriter implements IChromatogramMSDWriter {

	private static final Logger logger = Logger.getLogger(ChromatogramWriter32.class);

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			MsRun msRun = new MsRun();
			for(IScan sourceScan : chromatogram.getScans()) {
				Scan exportScan = new Scan();
				exportScan.setTotIonCurrent(sourceScan.getTotalSignal());
				exportScan.setRetentionTime(DatatypeFactory.newInstance().newDuration(sourceScan.getRetentionTime()));
				IScanMSD scanMSD = (IScanMSD)sourceScan;
				List<IIon> ions = scanMSD.getIons();
				double[] values = new double[ions.size() * 2];
				int i = 0;
				for(IIon ion : ions) {
					values[i] = ion.getIon();
					values[i + 1] = ion.getAbundance();
					i += 2;
				}
				exportScan.setMsLevel(BigInteger.valueOf(1)); // TODO
				Peaks peaks = new Peaks();
				DoubleBuffer doubleBuffer = DoubleBuffer.wrap(values);
				ByteBuffer byteBuffer = ByteBuffer.allocate(doubleBuffer.capacity() * Double.BYTES);
				peaks.setPrecision(BigInteger.valueOf(64));
				peaks.setByteOrder("network");
				byteBuffer.order(ByteOrder.BIG_ENDIAN);
				byteBuffer.asDoubleBuffer().put(doubleBuffer);
				boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
				if(compression) {
					peaks.setCompressionType("zlib");
					Deflater compresser = new Deflater();
					compresser.setInput(byteBuffer.array());
					compresser.finish();
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					byte[] readBuffer = new byte[1024];
					int compressedDataLength = 0;
					while(!compresser.finished()) {
						int compressCount = compresser.deflate(readBuffer);
						if(compressCount > 0) {
							compressedDataLength += compressCount;
							outputStream.write(readBuffer, 0, compressCount);
						}
					}
					peaks.setCompressedLen(compressedDataLength);
					peaks.setValue(outputStream.toByteArray());
					compresser.end();
				} else {
					peaks.setValue(byteBuffer.array());
				}
				exportScan.getPeaks().add(peaks);
				msRun.getScan().add(exportScan);
			}
			MzXML mzXML = new MzXML();
			mzXML.setMsRun(msRun);
			marshaller.marshal(mzXML, file);
		} catch(JAXBException e) {
			logger.warn(e);
		} catch(DatatypeConfigurationException e) {
			logger.warn(e);
		}
	}
}
