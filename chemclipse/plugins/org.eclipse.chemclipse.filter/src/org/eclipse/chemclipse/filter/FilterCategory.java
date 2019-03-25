/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.filter;

public enum FilterCategory {
	MSD, CSD, WSD, FID, NMR,
	/**
	 * Suggests that this Filter can support a wide range of datatypes and content-sensing is the only option to check if the filter can really handle the data or not
	 */
	AUTO_DETECT;
}
