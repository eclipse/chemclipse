/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public abstract class AbstractIntegrationEntry implements IIntegrationEntry {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 7699124444314965496L;
	//
	final private double integratedArea;

	public AbstractIntegrationEntry(double integratedArea) {
		this.integratedArea = integratedArea;
	}

	@Override
	public double getIntegratedArea() {

		return integratedArea;
	}
}
