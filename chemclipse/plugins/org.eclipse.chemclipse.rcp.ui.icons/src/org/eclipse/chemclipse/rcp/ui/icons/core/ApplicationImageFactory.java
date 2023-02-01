/*******************************************************************************
 * Copyright (c) 2013, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - change to Activator.getDefault().getApplicationImage()
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.ui.icons.core;

import org.eclipse.chemclipse.rcp.ui.icons.Activator;

public class ApplicationImageFactory {

	private static IApplicationImage applicationImage = null;

	private ApplicationImageFactory() {

	}

	/**
	 * Returns an instance of IApplicationImage.
	 * 
	 * @return {@link IApplicationImage}
	 */
	public static IApplicationImage getInstance() {

		if(applicationImage == null) {
			applicationImage = new ApplicationImage(Activator.getDefault().getBundle());
		}
		//
		return applicationImage;
	}
}