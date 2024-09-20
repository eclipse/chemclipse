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
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.ISamplesPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.SampleGroupAssignerListUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
	private AtomicReference<Text> textGroupNameControl = new AtomicReference<Text>();
	//
	private List<ISample> samples;

	public GroupNamingWizardPage(ISamplesPCA<IVariable, ISample> samplesPCA) {

		super(GroupNamingWizardPage.class.getName());
		setTitle("Group Naming Tool");
		setDescription("Tool for quickly naming/creating/assigning Groups");
		/*
		 * Make a deep copy of the sample and group name to
		 * avoid modifying the original sample.
		 */
		this.samples = extractSamples(samplesPCA.getSamples());
	}

	public List<ISample> getSamples() {

		return samples;
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		createSampleGroupAssignerListUI(composite);
		//
		setControl(parent);
		initialize();
	}

	private void initialize() {

		sampleGroupAssignerListControl.get().updateInput(samples);
	}

	private void createSampleGroupAssignerListUI(Composite composite) {

		SampleGroupAssignerListUI sampleGroupAssignerListUI = new SampleGroupAssignerListUI(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Table table = sampleGroupAssignerListUI.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		//
		sampleGroupAssignerListControl.set(sampleGroupAssignerListUI);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout(5, false));
		//
		createLabelGroupName(composite);
		createTextGroupName(composite);
		createButtonAssign(composite);
		createButtonInverseSelection(composite);
		createButtonClearSelection(composite);
	}

	private Label createLabelGroupName(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Group Name:");
		//
		return label;
	}

	private Button createButtonAssign(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Assign the group name to the selected samples.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_EXECUTE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				assignGroupName();
			}
		});
		//
		return button;
	}

	private void createTextGroupName(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Enter the group name to be assigned to the selected samples.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		text.setLayoutData(gridData);
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				if(e.keyCode == SWT.LF || e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
					assignGroupName();
				}
			}
		});
		//
		textGroupNameControl.set(text);
	}

	private Button createButtonClearSelection(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Clear all selections.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CLEAR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(ISample sample : samples) {
					sample.setSelected(false);
				}
				refreshSamplesList();
			}
		});
		//
		return button;
	}

	private Button createButtonInverseSelection(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Inverse the sample selection.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				for(ISample sample : samples) {
					sample.setSelected(!sample.isSelected());
				}
				refreshSamplesList();
			}
		});
		//
		return button;
	}

	private void assignGroupName() {

		String groupName = textGroupNameControl.get().getText().trim();
		for(ISample sample : samples) {
			if(sample.isSelected()) {
				sample.setGroupName(groupName);
			}
		}
		refreshSamplesList();
	}

	private void refreshSamplesList() {

		sampleGroupAssignerListControl.get().refresh();
	}

	private List<ISample> extractSamples(List<ISample> samples) {

		List<ISample> samplesCopy = new ArrayList<ISample>();
		//
		for(ISample sample : samples) {
			ISample sampleCopy = new Sample(sample.getSampleName(), sample.getGroupName());
			sampleCopy.setSelected(false);
			samplesCopy.add(sampleCopy);
		}
		//
		return samplesCopy;
	}
}