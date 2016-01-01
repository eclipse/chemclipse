/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.implementation;

import org.eclipse.chemclipse.msd.model.core.AbstractIntegrationEntryMSD;
import org.eclipse.chemclipse.msd.model.core.IIntegrationEntryMSD;

public class IntegrationEntryMSD extends AbstractIntegrationEntryMSD implements IIntegrationEntryMSD {

	public IntegrationEntryMSD(double ion, double integratedArea) {
		super(ion, integratedArea);
	}
}
