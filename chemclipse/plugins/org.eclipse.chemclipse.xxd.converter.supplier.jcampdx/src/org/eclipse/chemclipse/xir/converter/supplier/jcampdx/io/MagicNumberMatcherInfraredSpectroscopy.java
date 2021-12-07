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
package org.eclipse.chemclipse.xir.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;

public class MagicNumberMatcherInfraredSpectroscopy extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		if(!checkFileExtension(file, ".dx")) {
			return false;
		}
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			for(int i = 0; i < 3; i++) {
				if(bufferedReader.readLine().contains("##DATA TYPE=INFRARED SPECTRUM")) {
					return true;
				}
			}
		} catch(IOException e) {
			return false;
		}
		return false;
	}
}
