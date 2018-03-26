/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core.implementation;

import org.eclipse.chemclipse.wsd.model.core.AbstractIntegrationEntryWSD;
import org.eclipse.chemclipse.wsd.model.core.IIntegrationEntryWSD;

public class IntegrationEntryWSD extends AbstractIntegrationEntryWSD implements IIntegrationEntryWSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 8771833314816644824L;

	public IntegrationEntryWSD(double integratedArea) {
		super(integratedArea);
	}
}
