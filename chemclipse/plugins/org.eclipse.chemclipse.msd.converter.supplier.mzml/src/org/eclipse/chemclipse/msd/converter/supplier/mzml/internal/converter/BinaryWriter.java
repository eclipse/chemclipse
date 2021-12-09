/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.converter;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.zip.Deflater;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.preferences.PreferenceSupplier;

public class BinaryWriter {

	public static BinaryDataArrayType createBinaryData(float[] values) {

		FloatBuffer floatBuffer = FloatBuffer.wrap(values);
		ByteBuffer byteBuffer = ByteBuffer.allocate(floatBuffer.capacity() * Float.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.asFloatBuffer().put(floatBuffer);
		BinaryDataArrayType binaryDataArrayType = createBinaryDataArray(byteBuffer);
		binaryDataArrayType.setArrayLength(BigInteger.valueOf(values.length));
		CVParamType cvParamDataType = new CVParamType();
		cvParamDataType.setAccession("MS:1000521");
		cvParamDataType.setName("32-bit float");
		binaryDataArrayType.getCvParam().add(cvParamDataType);
		return binaryDataArrayType;
	}

	public static BinaryDataArrayType createBinaryData(double[] values) {

		DoubleBuffer doubleBuffer = DoubleBuffer.wrap(values);
		ByteBuffer byteBuffer = ByteBuffer.allocate(doubleBuffer.capacity() * Double.BYTES);
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		byteBuffer.asDoubleBuffer().put(doubleBuffer);
		BinaryDataArrayType binaryDataArrayType = createBinaryDataArray(byteBuffer);
		binaryDataArrayType.setArrayLength(BigInteger.valueOf(values.length));
		CVParamType cvParamDataType = new CVParamType();
		cvParamDataType.setAccession("MS:1000523");
		cvParamDataType.setName("64-bit float");
		binaryDataArrayType.getCvParam().add(cvParamDataType);
		return binaryDataArrayType;
	}

	private static BinaryDataArrayType createBinaryDataArray(ByteBuffer byteBuffer) {

		BinaryDataArrayType binaryDataArrayType = new BinaryDataArrayType();
		boolean compression = PreferenceSupplier.getChromatogramSaveCompression();
		if(compression) {
			CVParamType cvParamCompression = new CVParamType();
			cvParamCompression.setAccession("MS:1000574");
			cvParamCompression.setName("zlib compression");
			binaryDataArrayType.getCvParam().add(cvParamCompression);
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
}
