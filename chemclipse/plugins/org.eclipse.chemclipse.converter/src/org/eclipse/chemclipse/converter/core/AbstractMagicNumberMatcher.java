/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public abstract class AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	protected boolean checkFileExtension(File file, String extension) {

		return checkFileExtension(file, extension, false);
	}

	protected boolean checkFileExtension(File file, String extension, boolean caseSensitive) {

		String fileExtension = (caseSensitive) ? file.getName() : file.getName().toLowerCase();
		return fileExtension.endsWith((caseSensitive) ? extension : extension.toLowerCase());
	}

	protected boolean checkMagicCode(File file, byte[] magicCode) {

		if(magicCode.length == 0 || file.length() < magicCode.length) {
			return false;
		} else {
			try (InputStream inputStream = new FileInputStream(file)) {
				byte[] input = new byte[magicCode.length];
				inputStream.read(input);
				return Arrays.equals(input, magicCode);
			} catch(IOException e) {
				return false;
			}
		}
	}

	protected boolean checkPattern(File file, int length, Map<Integer, Byte> indexMap) {

		if(length <= 0 || indexMap.size() == 0 || file.length() < length) {
			return false;
		} else {
			try (InputStream inputStream = new FileInputStream(file)) {
				byte[] input = new byte[length];
				inputStream.read(input);
				Set<Integer> indices = indexMap.keySet();
				int maxIndex = length - 1;
				for(int index : indices) {
					if(index < 0 || index > maxIndex || input[index] != indexMap.get(index)) {
						return false;
					}
				}
				/*
				 * All checks passed.
				 */
				return true;
			} catch(IOException e) {
				return false;
			}
		}
	}
}
