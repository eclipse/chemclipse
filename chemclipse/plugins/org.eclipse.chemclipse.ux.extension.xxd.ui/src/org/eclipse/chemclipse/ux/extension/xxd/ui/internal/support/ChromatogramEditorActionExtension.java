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

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((chromatogramEditorAction == null) ? 0 : chromatogramEditorAction.getClass().hashCode());
		result = prime * result + (csd ? 1231 : 1237);
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + (msd ? 1231 : 1237);
		result = prime * result + (wsd ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ChromatogramEditorActionExtension other = (ChromatogramEditorActionExtension)obj;
		if(chromatogramEditorAction == null) {
			if(other.chromatogramEditorAction != null)
				return false;
		} else if(!chromatogramEditorAction.equals(other.chromatogramEditorAction))
			return false;
		if(csd != other.csd)
			return false;
		if(description == null) {
			if(other.description != null)
				return false;
		} else if(!description.equals(other.description))
			return false;
		if(label == null) {
			if(other.label != null)
				return false;
		} else if(!label.equals(other.label))
			return false;
		if(msd != other.msd)
			return false;
		if(wsd != other.wsd)
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "ChromatogramEditorActionExtension [label=" + label + ", description=" + description + ", chromatogramEditorAction=" + chromatogramEditorAction + ", msd=" + msd + ", csd=" + csd + ", wsd=" + wsd + "]";
	}
}
