/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.support;

import org.eclipse.chemclipse.msd.converter.chromatogram.ChromatogramConverterMSD;
import org.eclipse.chemclipse.xxd.process.files.AbstractSupplierFileIdentifier;
import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class ChromatogramIdentifier extends AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	public ChromatogramIdentifier() {
		super(ChromatogramConverterMSD.getInstance().getChromatogramConverterSupport().getSupplier());
	}

	@Override
	public String getType() {

		return TYPE_MSD;
	}
}
