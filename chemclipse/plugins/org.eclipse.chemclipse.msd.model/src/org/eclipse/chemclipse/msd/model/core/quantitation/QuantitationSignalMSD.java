/*******************************************************************************
 * Copyright (c) 2013, 2016 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.quantitation;

public class QuantitationSignalMSD extends AbstractQuantitationSignalMSD implements IQuantitationSignalMSD {

	public QuantitationSignalMSD(double ion, float relativeResponse) {
		super(ion, relativeResponse);
	}

	public QuantitationSignalMSD(double ion, float relativeResponse, double uncertainty) {
		super(ion, relativeResponse, uncertainty);
	}

	public QuantitationSignalMSD(double ion, float relativeResponse, double uncertainty, boolean use) {
		super(ion, relativeResponse, uncertainty, use);
	}
}
