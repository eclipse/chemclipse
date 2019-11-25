/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.support;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.model.IChromatogramOutputEntry;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.wsd.converter.chromatogram.ChromatogramConverterWSD;

// TODO merge with Converter Plug-in
public class ConverterTypeSupportWSD {

	private static final Logger logger = Logger.getLogger(ConverterTypeSupportWSD.class);
	public static final String NOT_AVAILABLE = "n.a.";

	public String getConverterName(IChromatogramOutputEntry entry) {

		String converterName = NOT_AVAILABLE;
		try {
			ISupplier converterSupplier = ChromatogramConverterWSD.getInstance().getChromatogramConverterSupport().getSupplier(entry.getConverterId());
			converterName = converterSupplier.getFilterName();
		} catch(NoConverterAvailableException e) {
			logger.warn(e);
		}
		return converterName;
	}
}
