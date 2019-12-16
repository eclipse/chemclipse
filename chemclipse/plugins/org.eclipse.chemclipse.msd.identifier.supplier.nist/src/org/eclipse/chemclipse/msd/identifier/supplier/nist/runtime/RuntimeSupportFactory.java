/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - using a path instead of a string
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class RuntimeSupportFactory {

	/**
	 * Returns the appropriate runtime support.
	 * 
	 * @param application
	 * @throws FileNotFoundException
	 * @return {@link IExtendedRuntimeSupport}
	 */
	public static IExtendedRuntimeSupport getRuntimeSupport(File applicationFolder) throws FileNotFoundException {

		IExtendedRuntimeSupport runtimeSupport;
		String parameter = INistSupport.PARAMETER;
		if(OperatingSystemUtils.isWindows()) {
			runtimeSupport = new WindowsSupport(applicationFolder, parameter);
		} else if(OperatingSystemUtils.isMac()) {
			runtimeSupport = new MacWineSupport(applicationFolder, parameter);
		} else {
			runtimeSupport = new LinuxWineSupport(applicationFolder, parameter);
		}
		return runtimeSupport;
	}
}
