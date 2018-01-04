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
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.amdis.runtime;

import java.io.FileNotFoundException;

import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class RuntimeSupportFactory {

	public static IExtendedRuntimeSupport getRuntimeSupport(String application, String chromatogram) throws FileNotFoundException {

		IExtendedRuntimeSupport runtimeSupport;
		String parameter = chromatogram + " " + IAmdisSupport.PARAMETER;
		if(OperatingSystemUtils.isWindows()) {
			runtimeSupport = new WindowsSupport(application, parameter);
		} else if(OperatingSystemUtils.isMac()) {
			runtimeSupport = new MacWineSupport(application, parameter);
		} else {
			/*
			 * wine AMDIS32\$.exe C:\\tmp\\C2.CDF /S
			 */
			runtimeSupport = new LinuxWineSupport(application, parameter);
		}
		return runtimeSupport;
	}
}
