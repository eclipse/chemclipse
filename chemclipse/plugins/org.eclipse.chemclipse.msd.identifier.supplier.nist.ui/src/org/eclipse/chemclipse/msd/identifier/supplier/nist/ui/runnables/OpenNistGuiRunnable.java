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
package org.eclipse.chemclipse.msd.identifier.supplier.nist.ui.runnables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.IExtendedRuntimeSupport;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.runtime.RuntimeSupportFactory;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.PeakIdentifierSettings;
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
			/*
			 * Try to open the NIST-DB in GUI mode.
			 */
			try {
				PeakIdentifierSettings peakIdentifierSettings = PreferenceSupplier.getPeakIdentifierSettings();
				String nistApplication = peakIdentifierSettings.getNistApplication();
				IExtendedRuntimeSupport runtimeSupport = RuntimeSupportFactory.getRuntimeSupport(nistApplication);
				try {
					runtimeSupport.executeOpenCommand();
				} catch(IOException e) {
					logger.warn(e);
				}
			} catch(FileNotFoundException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
