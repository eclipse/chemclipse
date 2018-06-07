/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.wizards;

import org.eclipse.chemclipse.ux.extension.msd.ui.provider.PeakFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.msd.ui.provider.PeakFileExplorerLabelProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerContentProvider;
import org.eclipse.chemclipse.ux.extension.ui.provider.SupplierFileExplorerLabelProvider;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;

public class InputWizardSettings {

	public enum DataType {
		MSD_CHROMATOGRAM, //
		MSD_PEAKS, //
		CSD_CHROMATOGRAM, //
		WSD_CHROMATOGRAM;
	}

	private String title = "Title";
	private String description = "Description";
	private IBaseLabelProvider labelProvider = null;
	private IContentProvider contentProvider = null;
	private IEclipsePreferences eclipsePreferences = null;
	private String nodeName = "";

	public InputWizardSettings(DataType dataType) {
		switch(dataType) {
			case MSD_CHROMATOGRAM:
				this.labelProvider = new SupplierFileExplorerLabelProvider(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceIdentifier());
				this.contentProvider = new SupplierFileExplorerContentProvider(org.eclipse.chemclipse.ux.extension.msd.ui.support.ChromatogramSupport.getInstanceIdentifier());
				break;
			case MSD_PEAKS:
				this.labelProvider = new PeakFileExplorerLabelProvider();
				this.contentProvider = new PeakFileExplorerContentProvider();
				break;
			case CSD_CHROMATOGRAM:
				this.labelProvider = new SupplierFileExplorerLabelProvider(org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceIdentifier());
				this.contentProvider = new SupplierFileExplorerContentProvider(org.eclipse.chemclipse.ux.extension.csd.ui.support.ChromatogramSupport.getInstanceIdentifier());
				break;
			case WSD_CHROMATOGRAM:
				this.labelProvider = new SupplierFileExplorerLabelProvider(org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceIdentifier());
				this.contentProvider = new SupplierFileExplorerContentProvider(org.eclipse.chemclipse.ux.extension.wsd.ui.support.ChromatogramSupport.getInstanceIdentifier());
				break;
			default:
				break;
		}
	}

	public String getTitle() {

		return title;
	}

	public void setTitle(String title) {

		this.title = title;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public IBaseLabelProvider getLabelProvider() {

		return labelProvider;
	}

	public IContentProvider getContentProvider() {

		return contentProvider;
	}

	public IEclipsePreferences getEclipsePreferences() {

		return eclipsePreferences;
	}

	public void setEclipsePreferences(IEclipsePreferences eclipsePreferences) {

		this.eclipsePreferences = eclipsePreferences;
	}

	public String getNodeName() {

		return nodeName;
	}

	public void setNodeName(String nodeName) {

		this.nodeName = nodeName;
	}
}
