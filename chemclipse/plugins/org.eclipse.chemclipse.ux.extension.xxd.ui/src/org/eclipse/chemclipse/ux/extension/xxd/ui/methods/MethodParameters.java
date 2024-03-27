/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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

import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

public class MethodParameters {

	private String profile = ProcessEntryContainer.DEFAULT_PROFILE;
	private int resumeIndex = ProcessEntryContainer.DEFAULT_RESUME_INDEX;

	public String getProfile() {

		return profile;
	}

	public void setProfile(String profile) {

		this.profile = profile;
	}

	public int getResumeIndex() {

		return resumeIndex;
	}

	public void setResumeIndex(int resumeIndex) {

		this.resumeIndex = resumeIndex;
	}
}