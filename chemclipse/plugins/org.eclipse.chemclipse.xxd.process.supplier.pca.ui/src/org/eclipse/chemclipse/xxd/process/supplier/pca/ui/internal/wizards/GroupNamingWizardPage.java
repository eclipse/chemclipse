/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.SampleGroupAssignerListUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class GroupNamingWizardPage extends AbstractAnalysisWizardPage {

	private AtomicReference<SampleGroupAssignerListUI> sampleGroupAssignerListControl = new AtomicReference<>();
	private ISamplesPCA<IVariable, ISample> samples;
	private AtomicReference<Text> groupName = new AtomicReference<Text>();

	public GroupNamingWizardPage(ISamplesPCA<IVariable, ISample> samples) {

		super(GroupNamingWizardPage.class.getName());
		setTitle("Group Naming Tool");
		setDescription("Tool for quickly naming/creating/assigning Groups");
		this.samples = samples;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		createToolbar(composite);
		createSampleGroupAssignerListUI(composite);
		setControl(parent);
	}

	private void createSampleGroupAssignerListUI(Composite composite) {

		SampleGroupAssignerListUI sampleGroupAssignerListUI = new SampleGroupAssignerListUI(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Table table = sampleGroupAssignerListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		sampleGroupAssignerListControl.set(sampleGroupAssignerListUI);
		sampleGroupAssignerListUI.updateInput(extractSamples(samples.getSamples()));
	}

	private List<ISample> extractSamples(List<ISample> samples) {

		List<ISample> groupNamingSamples = new ArrayList<ISample>();
		for(ISample sample : samples) {
			ISample groupNamingSample = new Sample(sample.getSampleName(), sample.getGroupName());
			groupNamingSample.setSelected(false);
			groupNamingSamples.add(groupNamingSample);
		}
		return groupNamingSamples;
	}

	private void createToolbar(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.BEGINNING;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(5, false));
		//
		createButtonAssign(composite);
		createLabel(composite, "Group Name:");
		createGroupNameEntry(composite);
		createButtonZeroSelection(composite);
		createButtonInverseSelection(composite);
	}

	private Label createLabel(Composite parent, String text) {

		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Button createButtonAssign(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Assign Groups to selected Samples.");
		button.setText("Assign Groups");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				sampleGroupAssignerListControl.get().assignGroups(groupName.get().getText());
			}
		});
		//
		return button;
	}

	private void createGroupNameEntry(Composite parent) {

		Text text = new Text(parent, SWT.NONE);
		text.setToolTipText("Enter Groupname to be assigned to Samples");
		GridData gridData = new GridData();
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		groupName.set(text);
	}

	private Button createButtonZeroSelection(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Unselect all.");
		button.setText("Unselect");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				@SuppressWarnings("unchecked")
				List<ISample> groupNamingSamples = (List<ISample>)sampleGroupAssignerListControl.get().getInput();
				for(ISample sample : groupNamingSamples) {
					sample.setSelected(false);
				}
				sampleGroupAssignerListControl.get().updateInput(groupNamingSamples);
			}
		});
		//
		return button;
	}

	private Button createButtonInverseSelection(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Inverse Select Samples.");
		button.setText("Inverse");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				@SuppressWarnings("unchecked")
				List<ISample> groupNamingSamples = (List<ISample>)sampleGroupAssignerListControl.get().getInput();
				for(ISample sample : groupNamingSamples) {
					if(sample.isSelected()) {
						sample.setSelected(false);
					} else {
						sample.setSelected(true);
					}
				}
				sampleGroupAssignerListControl.get().updateInput(groupNamingSamples);
			}
		});
		//
		return button;
	}

	@SuppressWarnings("unchecked")
	public List<ISample> getGroupSamples() {

		return (List<ISample>)sampleGroupAssignerListControl.get().getInput();
	}
}
