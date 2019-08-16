/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.csv.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.msd.converter.supplier.csv.internal.converter.SpecificationValidator;
import org.eclipse.chemclipse.msd.converter.supplier.csv.io.core.ChromatogramReader;

public class MagicNumberMatcherChromatogram extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			file = SpecificationValidator.validateSpecification(file, "csv");
			if(file.exists() && readTest(file)) {
				isValidFormat = true;
				if(isValidFormat) {
					int wait = 0;
				}
			}
		} catch(Exception e) {
			// Print no exception.
		}
		return isValidFormat;
	}

	public static boolean readTest(File file) {

		try {
			return ChromatogramReader.isValidFileFormat(file);
		} catch(IOException e) {
			// failed to parse
			return false;
		}
	}
}
