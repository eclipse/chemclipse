/*******************************************************************************
 * Copyright (c) 2008, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.ux.extension.ui.provider.AbstractChromatogramIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.IChromatogramIdentifier;

public class ChromatogramIdentifier extends AbstractChromatogramIdentifier implements IChromatogramIdentifier {

	public ChromatogramIdentifier() {
		super(ChromatogramConverterMSD.getChromatogramConverterSupport().getSupplier());
	}
}
