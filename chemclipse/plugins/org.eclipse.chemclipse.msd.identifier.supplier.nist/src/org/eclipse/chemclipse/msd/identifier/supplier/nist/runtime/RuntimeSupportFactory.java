/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
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
	public static IExtendedRuntimeSupport getRuntimeSupport(String application) throws FileNotFoundException {

		IExtendedRuntimeSupport runtimeSupport;
		String parameter = INistSupport.PARAMETER;
		if(OperatingSystemUtils.isWindows()) {
			runtimeSupport = new WindowsSupport(application, parameter);
		} else if(OperatingSystemUtils.isMac()) {
			runtimeSupport = new MacWineSupport(application, parameter);
		} else {
			runtimeSupport = new LinuxWineSupport(application, parameter);
		}
		return runtimeSupport;
	}
}
