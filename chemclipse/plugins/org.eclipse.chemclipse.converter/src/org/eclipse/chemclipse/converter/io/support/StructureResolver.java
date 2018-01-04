/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

public class StructureResolver implements IStructureResolver {

	@Override
	public String formatAsBinary(int value) {

		String binary = Integer.toBinaryString(value);
		String binaryPaddded = getZeroBytes(Integer.SIZE - binary.length()) + binary;
		return format(binaryPaddded);
	}

	@Override
	public String formatAsBinary(long value) {

		String binary = Long.toBinaryString(value);
		String binaryPaddded = getZeroBytes(Long.SIZE - binary.length()) + binary;
		return format(binaryPaddded);
	}

	@Override
	public String formatAsBinary(float value) {

		return formatAsBinary(Float.floatToIntBits(value));
	}

	@Override
	public String formatAsBinary(double value) {

		return formatAsBinary(Double.doubleToLongBits(value));
	}

	private String format(String binaryPaddded) {

		char[] buffer = binaryPaddded.toCharArray();
		int size = buffer.length;
		StringBuilder builder = new StringBuilder();
		for(int i = 1; i <= size; i++) {
			builder.append(buffer[i - 1]);
			/*
			 * Create a caret every 8 bits
			 */
			if(i % 8 == 0 && i < size) {
				builder.append(" |");
			}
			/*
			 * Append except of the last bit a white space.
			 */
			if(i < size) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	private String getZeroBytes(int size) {

		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < size; i++) {
			builder.append("0");
		}
		return builder.toString();
	}
}
