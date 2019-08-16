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
package org.eclipse.chemclipse.model.implementation;

import org.eclipse.chemclipse.model.core.AbstractScan;
import org.eclipse.chemclipse.model.core.IScan;

public class Scan extends AbstractScan implements IScan {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = 8733112087675991152L;
	private float totalSignal;

	public Scan(float totalSignal) {
		if(totalSignal >= 0) {
			this.totalSignal = totalSignal;
		}
	}

	@Override
	public float getTotalSignal() {

		return totalSignal;
	}

	@Override
	public void adjustTotalSignal(float totalSignal) {

		if(totalSignal >= 0) {
			this.totalSignal = totalSignal;
		}
	}
}
