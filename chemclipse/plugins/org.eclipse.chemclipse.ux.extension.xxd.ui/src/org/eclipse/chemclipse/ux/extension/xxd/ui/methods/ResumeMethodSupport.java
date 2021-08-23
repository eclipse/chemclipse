/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
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
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class ResumeMethodSupport {

	public static int selectResumeIndex(Shell shell, IProcessMethod processMethod) throws MethodCancelException {

		int resumeIndex = ProcessEntryContainer.DEFAULT_RESUME_INDEX;
		if(processMethod != null) {
			if(processMethod.isSupportResume()) {
				IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
				if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_RESUME_METHOD_DIALOG)) {
					/*
					 * Show the dialog.
					 */
					ResumeMethodDialog resumeMethodDialog = new ResumeMethodDialog(shell);
					resumeMethodDialog.setInput(processMethod);
					resumeMethodDialog.create();
					//
					if(resumeMethodDialog.open() == Window.OK) {
						resumeIndex = resumeMethodDialog.getResumeIndex();
					} else {
						throw new MethodCancelException();
					}
				}
			}
		}
		//
		return resumeIndex;
	}
}