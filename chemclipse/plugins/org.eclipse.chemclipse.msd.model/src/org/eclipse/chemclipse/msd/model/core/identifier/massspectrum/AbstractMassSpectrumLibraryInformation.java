/*******************************************************************************
 * Copyright (c) 2010, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core.identifier.massspectrum;

import org.eclipse.chemclipse.model.identifier.AbstractLibraryInformation;

public abstract class AbstractMassSpectrumLibraryInformation extends AbstractLibraryInformation implements IMassSpectrumLibraryInformation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -7713691807280350760L;
}
