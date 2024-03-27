/*******************************************************************************
 * Copyright (c) 2021, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceSupplier;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class ResumeMethodSupport {

	public static MethodParameters selectMethodParameters(Shell shell, IProcessMethod processMethod) throws MethodCancelException {

		MethodParameters methodParameters = new MethodParameters();
		//
		if(processMethod != null) {
			/*
			 * Set the active profile as this is used to
			 * parameterize the process method even if
			 * no support resume was activated. The resume index
			 * is 0 by default, so that all items are processed.
			 */
			methodParameters.setProfile(processMethod.getActiveProfile());
			if(processMethod.isSupportResume()) {
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				if(preferenceStore.getBoolean(PreferenceSupplier.P_SHOW_RESUME_METHOD_DIALOG)) {
					/*
					 * Show the dialog.
					 */
					ResumeMethodDialog resumeMethodDialog = new ResumeMethodDialog(shell);
					resumeMethodDialog.setInput(processMethod);
					resumeMethodDialog.create();
					//
					if(resumeMethodDialog.open() == Window.OK) {
						methodParameters.setProfile(resumeMethodDialog.getProfile());
						methodParameters.setResumeIndex(resumeMethodDialog.getResumeIndex());
					} else {
						throw new MethodCancelException();
					}
				}
			}
		}
		//
		return methodParameters;
	}
}