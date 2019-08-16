/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.zip.converter;

import java.io.File;

import org.eclipse.chemclipse.converter.core.AbstractMagicNumberMatcher;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.xxd.converter.supplier.zip.internal.converter.SpecificationValidator;

public class MagicNumberMatcher extends AbstractMagicNumberMatcher implements IMagicNumberMatcher {

	@Override
	public boolean checkFileFormat(File file) {

		boolean isValidFormat = false;
		try {
			file = SpecificationValidator.validateSpecification(file);
			if(file.exists()) {
				isValidFormat = true;
			}
		} catch(Exception e) {
			// Print no exception.
		}
		return isValidFormat;
	}
}
