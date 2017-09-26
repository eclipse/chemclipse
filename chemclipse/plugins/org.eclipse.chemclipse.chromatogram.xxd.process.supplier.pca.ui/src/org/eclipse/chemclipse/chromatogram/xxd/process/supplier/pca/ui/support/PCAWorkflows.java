/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.swt.widgets.Composite;

public class PCAWorkflows {

	final private List<PCAWorkflow> pcaWorkflows = new ArrayList<>();

	public PCAWorkflows() {
	}

	public PCAWorkflow getNewPCAWorkflow(Composite parent, Object layoutData, PcaEditor pcaEditor) {

		PCAWorkflow pcaWorkflow = null;
		if(pcaWorkflows.isEmpty()) {
			pcaWorkflow = new PCAWorkflow(parent, layoutData, pcaEditor);
		} else {
			pcaWorkflow = new PCAWorkflow(parent, layoutData, pcaEditor, pcaWorkflows.get(0));
		}
		pcaWorkflows.add(pcaWorkflow);
		return pcaWorkflow;
	}

	public int getStatusFilters() {

		if(!pcaWorkflows.isEmpty()) {
			return pcaWorkflows.get(0).getStatusFilters();
		}
		return -1;
	}

	public int getStatusOverview() {

		if(!pcaWorkflows.isEmpty()) {
			return pcaWorkflows.get(0).getStatusOverview();
		}
		return -1;
	}

	public int getStatusPreprocessing() {

		if(!pcaWorkflows.isEmpty()) {
			return pcaWorkflows.get(0).getStatusPreprocessing();
		}
		return -1;
	}

	public int getStatusSampleOverview() {

		if(!pcaWorkflows.isEmpty()) {
			return pcaWorkflows.get(0).getStatusSampleOverview();
		}
		return -1;
	}

	public void setStatuses(int status) {

		pcaWorkflows.forEach(w -> w.setStatuses(status));
	}

	public void setStatusFilters(int status) {

		pcaWorkflows.forEach(w -> w.setStatusFilters(status));
	}

	public void setStatusFilters(int status, String toolTip) {

		pcaWorkflows.forEach(w -> w.setStatusFilters(status, toolTip));
	}

	public void setStatusOverview(int status) {

		pcaWorkflows.forEach(w -> w.setStatusOverview(status));
	}

	public void setStatusOverview(int status, String tooltip) {

		pcaWorkflows.forEach(w -> w.setStatusOverview(status, tooltip));
	}

	public void setStatusPreprocessing(int status) {

		pcaWorkflows.forEach(w -> w.setStatusPreprocessing(status));
	}

	public void setStatusPreprocessing(int status, String toolTip) {

		pcaWorkflows.forEach(w -> w.setStatusPreprocessing(status, toolTip));
	}

	public void setStatusSamplesOverview(int status) {

		pcaWorkflows.forEach(w -> w.setStatusSamplesOverview(status));
	}

	public void setStatusSamplesOverview(int status, String tooltip) {

		pcaWorkflows.forEach(w -> w.setStatusSamplesOverview(status, tooltip));
	}
}
