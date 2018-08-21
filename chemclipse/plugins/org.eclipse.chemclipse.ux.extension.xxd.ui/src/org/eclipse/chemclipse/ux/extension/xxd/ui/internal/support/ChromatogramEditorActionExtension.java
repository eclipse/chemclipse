/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import org.eclipse.chemclipse.ux.extension.xxd.ui.support.IChromatogramEditorAction;

public class ChromatogramEditorActionExtension {

	private String label = "";
	private String description = "";
	private IChromatogramEditorAction chromatogramEditorAction = null;
	private boolean msd = false;
	private boolean csd = false;
	private boolean wsd = false;

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public IChromatogramEditorAction getChromatogramEditorAction() {

		return chromatogramEditorAction;
	}

	public void setChromatogramEditorAction(IChromatogramEditorAction chromatogramEditorAction) {

		this.chromatogramEditorAction = chromatogramEditorAction;
	}

	public boolean isMSD() {

		return msd;
	}

	public void setMSD(boolean msd) {

		this.msd = msd;
	}

	public boolean isCSD() {

		return csd;
	}

	public void setCSD(boolean csd) {

		this.csd = csd;
	}

	public boolean isWSD() {

		return wsd;
	}

	public void setWSD(boolean wsd) {

		this.wsd = wsd;
	}

	public String getUniqueId() {

		if(chromatogramEditorAction != null) {
			return Integer.toString(chromatogramEditorAction.getClass().getName().hashCode());
		} else {
			return "";
		}
	}

	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	@Override
	public int hashCode() {

		return super.hashCode();
	}

	@Override
	public String toString() {

		return "ChromatogramEditorActionExtension [label=" + label + ", description=" + description + ", chromatogramEditorAction=" + chromatogramEditorAction + ", msd=" + msd + ", csd=" + csd + ", wsd=" + wsd + "]";
	}
}
