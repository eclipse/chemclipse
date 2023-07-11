/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.processors.Processor;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.PreferencesProcessSupport;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ProcessorToolbarUI extends Composite {

	private BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener;
	private DataCategory dataCategory = DataCategory.AUTO_DETECT;
	private List<Processor> processors = new ArrayList<>();
	private List<Button> buttons = new ArrayList<>();
	//
	private IProcessSupplierContext processSupplierContext = new ProcessTypeSupport();
	private PreferencesProcessSupport preferencesProcessSupport = new PreferencesProcessSupport(DataCategory.AUTO_DETECT);
	//
	private Composite control;

	public ProcessorToolbarUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void updateToolbar(DataCategory dataCategory) {

		this.dataCategory = dataCategory;
		preferencesProcessSupport.setDataCategory(dataCategory);
		//
		if(this.isVisible()) {
			updateInput();
		}
	}

	public void setInput(BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener) {

		this.executionListener = executionListener;
		updateInput();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginRight = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		//
		createToolbarMain(composite);
		//
		initialize();
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginRight = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		/*
		 * The toolbar buttons are set dynamically.
		 */
		this.control = composite;
	}

	private void initialize() {

		updateInput();
	}

	private void updateInput() {

		if(this.isVisible()) {
			clearProcessorButtons(control);
			createProcessorButtons(control);
		}
	}

	private void clearProcessorButtons(Composite parent) {

		processors.clear();
		for(Button button : buttons) {
			if(!button.isDisposed()) {
				button.dispose();
			}
		}
		buttons.clear();
		updateLayout(parent);
	}

	private void createProcessorButtons(Composite parent) {

		processors.addAll(preferencesProcessSupport.getStoredProcessors());
		for(Processor processor : processors) {
			if(processor != null && processor.isActive() && isActiveDataCategory(processor)) {
				buttons.add(createButton(parent, processor, executionListener, processSupplierContext));
			}
		}
		//
		updateLayout(parent);
	}

	private void updateLayout(Composite parent) {

		if(parent.getLayout() instanceof GridLayout gridLayout) {
			gridLayout.numColumns = buttons.size();
			Composite composite = parent.getParent();
			composite.layout(true);
		}
		/*
		 * This is needed, otherwise the icons
		 * are displayed only partly when the
		 * processor toolbar was empty initially.
		 */
		PartSupport.setCompositeVisibility(this, true);
	}

	private boolean isActiveDataCategory(Processor processor) {

		if(dataCategory != null) {
			return processor.getProcessSupplier().getSupportedDataTypes().contains(dataCategory);
		}
		//
		return false;
	}

	private Button createButton(Composite parent, Processor processor, BiConsumer<IProcessSupplier<?>, IProcessSupplierContext> executionListener, IProcessSupplierContext processSupplierContext) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText(getToolTipText(processor));
		button.setImage(ApplicationImageFactory.getInstance().getImage(processor.getImageFileName(), IApplicationImageProvider.SIZE_16x16));
		//
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				executionListener.accept(processor.getProcessSupplier(), processSupplierContext);
			}
		});
		//
		return button;
	}

	private String getToolTipText(Processor processor) {

		StringBuilder builder = new StringBuilder();
		IProcessSupplier<?> processSupplier = processor.getProcessSupplier();
		builder.append(processSupplier.getName());
		String description = processSupplier.getDescription();
		if(description != null && !description.isEmpty()) {
			builder.append(": ");
			builder.append(description);
		}
		//
		return builder.toString();
	}
}