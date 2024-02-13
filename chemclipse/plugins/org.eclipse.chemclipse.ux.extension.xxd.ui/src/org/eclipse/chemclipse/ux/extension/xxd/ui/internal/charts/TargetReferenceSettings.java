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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts;

import java.util.Collection;

import org.eclipse.chemclipse.model.targets.ITargetDisplaySettings;
import org.eclipse.chemclipse.model.targets.TargetReference;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedChromatogramUI;
import org.eclipse.swtchart.extensions.core.BaseChart;

public class TargetReferenceSettings {

	private Collection<? extends TargetReference> targetReferences = null;
	private ITargetDisplaySettings targetDisplaySettings = null;
	private boolean showReferenceId = false;
	private int offset = 0;
	//
	private BaseChart baseChart = null; // Null is valid
	private String label = "";
	private String description = "";
	private String referenceSeriesId = ExtendedChromatogramUI.SERIES_ID_CHROMATOGRAM;

	public TargetReferenceSettings(boolean showReferenceId, int offset) {

		this.showReferenceId = showReferenceId;
		this.offset = offset;
	}

	public TargetReferenceSettings(Collection<? extends TargetReference> targetReferences, ITargetDisplaySettings targetDisplaySettings, int offset) {

		this.targetReferences = targetReferences;
		this.targetDisplaySettings = targetDisplaySettings;
		this.showReferenceId = false;
		this.offset = offset;
	}

	public Collection<? extends TargetReference> getTargetReferences() {

		return targetReferences;
	}

	public ITargetDisplaySettings getTargetDisplaySettings() {

		return targetDisplaySettings;
	}

	public boolean isShowReferenceId() {

		return showReferenceId;
	}

	public int getOffset() {

		return offset;
	}

	/**
	 * Could be null.
	 * 
	 * @return {@link BaseChart}
	 */
	public BaseChart getBaseChart() {

		return baseChart;
	}

	public void setBaseChart(BaseChart baseChart) {

		this.baseChart = baseChart;
	}

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

	public String getReferenceSeriesId() {

		return referenceSeriesId;
	}

	public void setReferenceSeriesId(String referenceSeriesId) {

		this.referenceSeriesId = referenceSeriesId;
	}
}