/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.result;

import org.eclipse.chemclipse.chromatogram.msd.classifier.result.AbstractChromatogramClassifierResult;
import org.eclipse.chemclipse.chromatogram.msd.classifier.result.ResultStatus;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;

public class WncClassifierResult extends AbstractChromatogramClassifierResult implements IWncClassifierResult {

	private IWncIons wncIons;

	public WncClassifierResult(ResultStatus resultStatus, String description) {
		super(resultStatus, description);
	}

	public WncClassifierResult(ResultStatus resultStatus, String description, IWncIons wncIons) {
		super(resultStatus, description);
		this.wncIons = wncIons;
	}

	public IWncIons getWNCIons() {

		return wncIons;
	}
}
