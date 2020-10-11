/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
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
	 * By default, it is operated in background modus.
	 * 
	 * @param applicationFolder
	 * @return {@link IExtendedRuntimeSupport}
	 * @throws FileNotFoundException
	 */
	public static IExtendedRuntimeSupport getRuntimeSupport(File applicationFolder) throws FileNotFoundException {

		return getRuntimeSupport(applicationFolder, true);
	}

	/**
	 * Returns the appropriate runtime support.
	 * 
	 * @param applicationFolder
	 * @param batchModus
	 * @return {@link IExtendedRuntimeSupport}
	 * @throws FileNotFoundException
	 */
	public static IExtendedRuntimeSupport getRuntimeSupport(File applicationFolder, boolean batchModus) throws FileNotFoundException {

		IExtendedRuntimeSupport runtimeSupport;
		String parameter = batchModus ? INistSupport.PARAMETER_BACKGROUND : INistSupport.PARAMETER_FOREGROUND;
		//
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
