/*******************************************************************************
 * Copyright (c) 2013, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

public abstract class AbstractIntegrationEntry implements IIntegrationEntry {

	private double integratedArea;

	public AbstractIntegrationEntry(double integratedArea) {
		this.integratedArea = integratedArea;
	}

	@Override
	public double getIntegratedArea() {

		return integratedArea;
	}
}
