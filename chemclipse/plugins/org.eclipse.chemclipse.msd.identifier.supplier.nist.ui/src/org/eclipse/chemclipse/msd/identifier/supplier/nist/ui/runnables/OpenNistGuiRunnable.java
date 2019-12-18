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
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.IExtendedRuntimeSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.RuntimeSupportFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * @author Dr. Philip Wenig
 */
public class OpenNistGuiRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(OpenNistGuiRunnable.class);
	private static final String DESCRIPTION = "Open NIST-DB GUI";

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(DESCRIPTION, IProgressMonitor.UNKNOWN);
			File folder = PreferenceSupplier.getNistInstallationFolder();
			if(PreferenceSupplier.validateLocation(folder).isOK()) {
				IExtendedRuntimeSupport runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(folder);
				runtimeSupport.executeOpenCommand();
			}
		} catch(Exception e) {
			logger.error(e);
		} finally {
			monitor.done();
		}
	}
}
