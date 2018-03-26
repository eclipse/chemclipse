/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.model.core;

import org.eclipse.chemclipse.model.core.AbstractIntegrationEntry;

public abstract class AbstractIntegrationEntryWSD extends AbstractIntegrationEntry implements IIntegrationEntryWSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 5641506064051809641L;

	public AbstractIntegrationEntryWSD(double integratedArea) {
		super(integratedArea);
	}
}
