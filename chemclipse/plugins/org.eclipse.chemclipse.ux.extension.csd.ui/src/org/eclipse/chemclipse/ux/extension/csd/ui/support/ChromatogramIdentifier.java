/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.csd.ui.support;

import org.eclipse.chemclipse.csd.converter.chromatogram.ChromatogramConverterCSD;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractChromatogramIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramIdentifier;

public class ChromatogramIdentifier extends AbstractChromatogramIdentifier implements IChromatogramIdentifier {

	public ChromatogramIdentifier() {
		super(ChromatogramConverterCSD.getChromatogramConverterSupport().getSupplier());
	}

	@Override
	public String getType() {

		return TYPE_CSD;
	}
}
