/*******************************************************************************
 * Copyright (c) 2017 Jan Holy
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors.PcaEditor;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

public class PCAWorkflow {

	private class HyperLinkStatus {

		private String emptyToolTip = "";
		private String errorToolTip = "Error";
		ImageHyperlink imageHyperlink;
		private String okToolTip = "OK";
		private int status;
		private String warningToolTip = "Data need not be up to date";

		protected HyperLinkStatus(Composite parent, Object layoutData, String name) {
			imageHyperlink = new ImageHyperlink(parent, SWT.None);
			setStatus(STATUS_EMPTY);
			imageHyperlink.setText(name);
			imageHyperlink.addListener(SWT.Selection, e -> {
			});
		}

		public int getStatus() {

			return status;
		}

		void setStatus(HyperLinkStatus hyperLinkStatus) {

			this.warningToolTip = hyperLinkStatus.warningToolTip;
			this.emptyToolTip = hyperLinkStatus.emptyToolTip;
			this.errorToolTip = hyperLinkStatus.errorToolTip;
			this.okToolTip = hyperLinkStatus.okToolTip;
			this.status = hyperLinkStatus.status;
		}

		public void setStatus(int status) {

			Image image;
			switch(status) {
				case STATUS_EMPTY:
					this.status = STATUS_EMPTY;
					image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_EMPTY, IApplicationImageProvider.SIZE_16x16);
					imageHyperlink.setImage(image);
					imageHyperlink.setToolTipText(emptyToolTip);
					break;
				case STATUS_OK:
					this.status = STATUS_OK;
					image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_OK, IApplicationImageProvider.SIZE_16x16);
					imageHyperlink.setImage(image);
					imageHyperlink.setToolTipText(okToolTip);
					break;
				case STATUS_ERROR:
					this.status = STATUS_ERROR;
					image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_ERROR, IApplicationImageProvider.SIZE_16x16);
					imageHyperlink.setImage(image);
					imageHyperlink.setToolTipText(errorToolTip);
					break;
				case STATUS_WARNING:
					this.status = STATUS_WARNING;
					image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_WARN, IApplicationImageProvider.SIZE_16x16);
					imageHyperlink.setImage(image);
					imageHyperlink.setToolTipText(warningToolTip);
					break;
				default:
					break;
			}
		}

		public void setStatus(int status, String tooltip) {

			setStatus(status);
			imageHyperlink.setToolTipText(tooltip);
		}
	}

	public static final int STATUS_EMPTY = 0;
	public static final int STATUS_ERROR = 2;
	public static final int STATUS_OK = 1;
	public static final int STATUS_WARNING = 3;
	private HyperLinkStatus filters;
	private HyperLinkStatus overview;
	private HyperLinkStatus preprocessing;
	private HyperLinkStatus sampleOverview;

	PCAWorkflow(Composite parent, Object layoutData, PcaEditor pcaEditor) {
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(10, false));
		composite.setLayoutData(layoutData);
		GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		overview = new HyperLinkStatus(composite, GridDataFactory.copyData(gridData), "Overview");
		overview.imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showOverviewPage();
			}
		});
		Label label = new Label(composite, SWT.None);
		label.setText(">");
		sampleOverview = new HyperLinkStatus(composite, GridDataFactory.copyData(gridData), "Sample Overview");
		sampleOverview.imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showSamplesOverviewPage();
			}
		});
		label = new Label(composite, SWT.None);
		label.setText(">");
		label.setLayoutData(GridDataFactory.copyData(gridData));
		preprocessing = new HyperLinkStatus(composite, GridDataFactory.copyData(gridData), "Data preproccesing");
		preprocessing.imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showPreprocessingPage();
			}
		});
		label = new Label(composite, SWT.None);
		label.setText(">");
		label.setLayoutData(GridDataFactory.copyData(gridData));
		filters = new HyperLinkStatus(composite, GridDataFactory.copyData(gridData), "Data Fitration");
		filters.imageHyperlink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {

				pcaEditor.showFiltersPage();
			}
		});
	}

	PCAWorkflow(Composite parent, Object layoutData, PcaEditor pcaEditor, PCAWorkflow copySettings) {
		this(parent, layoutData, pcaEditor);
		filters.setStatus(copySettings.filters);
		preprocessing.setStatus(copySettings.preprocessing);
		sampleOverview.setStatus(copySettings.sampleOverview);
		overview.setStatus(copySettings.overview);
	}

	int getStatusFilters() {

		return filters.getStatus();
	}

	int getStatusOverview() {

		return overview.getStatus();
	}

	int getStatusPreprocessing() {

		return preprocessing.getStatus();
	}

	int getStatusSampleOverview() {

		return sampleOverview.getStatus();
	}

	void setStatuses(int status) {

		overview.setStatus(status);
		sampleOverview.setStatus(status);
		filters.setStatus(status);
		preprocessing.setStatus(status);
	}

	void setStatusFilters(int status) {

		filters.setStatus(status);
	}

	void setStatusFilters(int status, String toolTip) {

		filters.setStatus(status, toolTip);
	}

	void setStatusOverview(int status) {

		overview.setStatus(status);
	}

	void setStatusOverview(int status, String toolTip) {

		overview.setStatus(status, toolTip);
	}

	void setStatusPreprocessing(int status) {

		preprocessing.setStatus(status);
	}

	void setStatusPreprocessing(int status, String toolTip) {

		preprocessing.setStatus(status, toolTip);
	}

	void setStatusSamplesOverview(int status) {

		sampleOverview.setStatus(status);
	}

	void setStatusSamplesOverview(int status, String toolTip) {

		sampleOverview.setStatus(status, toolTip);
	}
}
