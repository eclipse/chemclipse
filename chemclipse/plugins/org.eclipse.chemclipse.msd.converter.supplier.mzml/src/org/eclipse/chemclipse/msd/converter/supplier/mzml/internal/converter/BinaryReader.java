/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
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

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.BinaryDataArrayType;
import org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v110.model.CVParamType;

public class BinaryReader {

	public static Pair<String, double[]> parseBinaryData(BinaryDataArrayType binaryDataArrayType) throws DataFormatException {

		double[] values = new double[0];
		String content = "";
		if(binaryDataArrayType.getArrayLength() == BigInteger.ZERO) {
			return new ImmutablePair<>(content, values);
		}
		byte[] binary = binaryDataArrayType.getBinary();
		if(binary == null) {
			return new ImmutablePair<>(content, values);
		}
		ByteBuffer byteBuffer = ByteBuffer.wrap(binary);
		boolean compressed = false;
		boolean doublePrecision = false;
		int multiplicator = 1;
		for(CVParamType cvParam : binaryDataArrayType.getCvParam()) {
			if(cvParam.getAccession().equals("MS:1000574")) {
				if(cvParam.getName().equals("zlib compression")) {
					compressed = true;
				}
			}
			if(cvParam.getAccession().equals("MS:1000521") && cvParam.getName().equals("32-bit float")) {
				doublePrecision = false;
			} else if(cvParam.getAccession().equals("MS:1000523") && cvParam.getName().equals("64-bit float")) {
				doublePrecision = true;
			}
			if(cvParam.getAccession().equals("MS:1000514")) {
				if(cvParam.getName().equals("m/z array")) {
					content = "m/z";
				}
			}
			if(cvParam.getAccession().equals("MS:1000515")) {
				if(cvParam.getName().equals("intensity array")) {
					content = "intensity";
				}
			}
			if(cvParam.getAccession().equals("MS:1000595")) {
				if(cvParam.getName().equals("time array")) {
					content = "time";
					multiplicator = XmlReader.getTimeMultiplicator(cvParam);
				}
			}
		}
		if(compressed) {
			Inflater inflater = new Inflater();
			inflater.setInput(byteBuffer.array());
			byte[] byteArray = new byte[byteBuffer.capacity() * 10];
			int decodedLength = inflater.inflate(byteArray);
			byteBuffer = ByteBuffer.wrap(byteArray, 0, decodedLength);
		}
		byteBuffer.order(ByteOrder.LITTLE_ENDIAN); // this is always the case
		if(doublePrecision) {
			DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
			values = new double[doubleBuffer.capacity()];
			for(int index = 0; index < doubleBuffer.capacity(); index++) {
				values[index] = Double.valueOf(doubleBuffer.get(index)) * multiplicator;
			}
		} else {
			FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
			values = new double[floatBuffer.capacity()];
			for(int index = 0; index < floatBuffer.capacity(); index++) {
				values[index] = Double.valueOf(floatBuffer.get(index)) * multiplicator;
			}
		}
		return new ImmutablePair<>(content, values);
	}
}
