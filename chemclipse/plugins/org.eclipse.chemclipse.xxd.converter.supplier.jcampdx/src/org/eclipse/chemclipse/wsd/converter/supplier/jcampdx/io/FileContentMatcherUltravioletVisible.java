/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.jcampdx.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.chemclipse.converter.core.AbstractFileContentMatcher;
import org.eclipse.chemclipse.converter.core.IFileContentMatcher;

public class FileContentMatcherUltravioletVisible extends AbstractFileContentMatcher implements IFileContentMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			for(int i = 0; i < 10; i++) {
				String line = bufferedReader.readLine().trim();
				if(line.startsWith("##DATA TYPE") && line.toUpperCase().contains("UV/VIS SPECTRUM")) {
					return true;
				}
			}
		} catch(IOException e) {
			return false;
		}
		return false;
	}
}
