/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.io;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import org.eclipse.chemclipse.msd.converter.supplier.mzdata.internal.v105.model.SupDataBinaryType.Data;

public class ReaderVersion105 {

	public static final String NODE_MZ_DATA = "mzData";
	public static final String NODE_SPECTRUM_LIST = "spectrumList";

	private ReaderVersion105() {

	}

	public static double[] parseData(Data data) {

		double[] values = new double[0];
		ByteBuffer byteBuffer = ByteBuffer.wrap(data.getValue());
		/*
		 * Byte Order
		 */
		String endian = data.getEndian();
		if(endian != null && endian.equals("big")) {
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
		} else {
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		}
		/*
		 * Data Type
		 */
		int precision = data.getPrecision();
		if(precision == 64) {
			DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
			values = new double[doubleBuffer.capacity()];
			for(int index = 0; index < doubleBuffer.capacity(); index++) {
				values[index] = doubleBuffer.get(index);
			}
		} else if(precision == 32) {
			FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
			values = new double[floatBuffer.capacity()];
			for(int index = 0; index < floatBuffer.capacity(); index++) {
				values[index] = floatBuffer.get(index);
			}
		}
		return values;
	}
}
