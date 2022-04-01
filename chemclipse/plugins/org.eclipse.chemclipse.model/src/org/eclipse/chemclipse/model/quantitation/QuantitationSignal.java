/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.quantitation;

public class QuantitationSignal extends AbstractQuantitationSignal implements IQuantitationSignal {

	public QuantitationSignal(double signal, double relativeResponse) {

		super(signal, relativeResponse);
	}

	public QuantitationSignal(double signal, double relativeResponse, double uncertainty) {

		super(signal, relativeResponse, uncertainty);
	}

	public QuantitationSignal(double signal, double relativeResponse, double uncertainty, boolean use) {

		super(signal, relativeResponse, uncertainty, use);
	}
}