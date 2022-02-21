/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mmass.converter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;

public class MassSpectrumMagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		if(!file.exists()) {
			return isValidFormat;
		}
		if(!checkFileExtension(file, ".msd")) {
			return isValidFormat;
		}
		try (FileReader fileReader = new FileReader(file)) {
			final char[] charBuffer = new char[60];
			fileReader.read(charBuffer);
			final String header = new String(charBuffer);
			if(header.contains("<mSD version")) {
				isValidFormat = true;
			}
		} catch(IOException e) {
			// fail silently
		}
		return isValidFormat;
	}
}
