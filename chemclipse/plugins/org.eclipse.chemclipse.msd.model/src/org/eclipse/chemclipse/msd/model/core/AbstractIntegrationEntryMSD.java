/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import org.eclipse.chemclipse.model.core.AbstractIntegrationEntry;

public abstract class AbstractIntegrationEntryMSD extends AbstractIntegrationEntry implements IIntegrationEntryMSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -1040348962621857624L;
	//
	final private double ion;

	public AbstractIntegrationEntryMSD(double ion, double integratedArea) {
		super(integratedArea);
		this.ion = ion;
	}

	@Override
	public double getIon() {

		return ion;
	}
}
