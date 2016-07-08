/*******************************************************************************
 * Copyright (c) 2014, 2016 Dr. Philip Wenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.model.implementation;

import org.eclipse.chemclipse.csd.model.core.AbstractIntegrationEntryCSD;
import org.eclipse.chemclipse.csd.model.core.IIntegrationEntryCSD;

public class IntegrationEntryCSD extends AbstractIntegrationEntryCSD implements IIntegrationEntryCSD {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = 4606649360612794845L;

	public IntegrationEntryCSD(double integratedArea) {
		super(integratedArea);
	}
}
