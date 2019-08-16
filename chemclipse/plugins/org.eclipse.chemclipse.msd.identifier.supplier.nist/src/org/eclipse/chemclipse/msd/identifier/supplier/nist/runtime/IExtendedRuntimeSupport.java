/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import org.eclipse.chemclipse.support.runtime.IRuntimeSupport;

public interface IExtendedRuntimeSupport extends IRuntimeSupport {

	/**
	 * Returns the NIST specific settings instance.
	 * 
	 * @return {@link INistSupport}
	 */
	INistSupport getNistSupport();
}
