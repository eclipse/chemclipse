/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make UI configurable, support selection of existing process methods, support for init with different datatypes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.autoComplete;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createColumn;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createTreeTable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.model.methods.ListProcessEntryContainer;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.MethodListLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.TableConfigSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.ProcessingWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ConfigurableUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.MethodUIConfig;
import org.eclipse.chemclipse.xxd.process.ui.preferences.PreferencePageReportExport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposal;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class ExtendedMethodUI extends Composite implements ConfigurableUI<MethodUIConfig> {

	private Composite toolbarHeader;
	private Label labelDataInfo;
	private Text textName;
	private Text textCategory;
	private Text textOperator;
	private Text textDescription;
	private ToolItem buttonAdd;
	private ToolItem buttonCopy;
	private ToolItem buttonRemove;
	private ToolItem buttonMoveUp;
	private ToolItem buttonMoveDown;
	private ToolItem buttonModifySettings;
	private StructuredViewer listUI;
	//
	private ProcessMethod processMethod;
	private IModificationHandler modificationHandler;
	private Composite toolbarMain;
	private Composite buttons;
	protected boolean showSettingsOnAdd;
	private final ProcessSupplierContext processingSupport;
	private Button buttonFinalize;
	private Collection<ProcessEntryContainer> postActions;
	private final TreeViewerColumn[] columns = new TreeViewerColumn[MethodListLabelProvider.TITLES.length];
	private final DataCategory[] dataCategories;
	private final BiFunction<IProcessEntry, ProcessSupplierContext, ProcessorPreferences<?>> preferencesSupplier;
	private final boolean readonly;
	private String[] knownCategories;

	public ExtendedMethodUI(Composite parent, int style, ProcessSupplierContext processingSupport, DataCategory[] dataCategories) {

		this(parent, style, processingSupport, (entry, context) -> entry.getPreferences(context), dataCategories);
	}

	public ExtendedMethodUI(Composite parent, int style, ProcessSupplierContext processingSupport, BiFunction<IProcessEntry, ProcessSupplierContext, ProcessorPreferences<?>> preferencesSupplier, DataCategory[] dataCategories) {

		super(parent, style);
		this.readonly = (style & SWT.READ_ONLY) != 0;
		this.processingSupport = processingSupport;
		this.preferencesSupplier = preferencesSupplier;
		this.dataCategories = dataCategories;
		createControl();
	}

	public void setProcessMethod(IProcessMethod processMethod) {

		setInputs(processMethod, Collections.emptyList());
	}

	public void setInputs(IProcessMethod processMethod, Collection<ProcessEntryContainer> postActions) {

		this.postActions = postActions;
		this.processMethod = new ProcessMethod(processMethod);
		updateProcessMethod();
	}

	public IProcessMethod getProcessMethod() {

		return processMethod;
	}

	public String getMethodName() {

		if(!textName.isDisposed()) {
			return textName.getText();
		}
		return "";
	}

	public void setModificationHandler(IModificationHandler modificationHandler) {

		this.modificationHandler = modificationHandler;
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(composite);
		toolbarHeader = createToolbarHeader(composite);
		createTable(composite);
		buttons = createToolbarBottom(composite);
		//
		PartSupport.setCompositeVisibility(toolbarHeader, false);
		updateTableButtons();
	}

	public void setHeaderToolbarVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarHeader, visible);
	}

	public Composite getToolbarMain() {

		return toolbarMain;
	}

	private void createToolbarMain(Composite parent) {

		toolbarMain = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		toolbarMain.setLayoutData(gridData);
		toolbarMain.setLayout(new GridLayout(3, false));
		//
		createDataInfoLabel(toolbarMain);
		createButtonToggleToolbarHeader(toolbarMain);
		createSettingsButton(toolbarMain);
	}

	private void createDataInfoLabel(Composite parent) {

		labelDataInfo = new Label(parent, SWT.NONE);
		labelDataInfo.setText("");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		labelDataInfo.setLayoutData(gridData);
	}

	private Button createButtonToggleToolbarHeader(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the header toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEADER_DATA, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarHeader);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEADER_DATA, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEADER_DATA, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage[] preferencePages = getConfig().getPreferencePages();
				PreferenceManager preferenceManager = new PreferenceManager();
				for(int i = 0; i < preferencePages.length; i++) {
					preferenceManager.addToRoot(new PreferenceNode(String.valueOf(i + 1), preferencePages[i]));
				}
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(parent.getShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		setDirty(true);
	}

	private Text createNameSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Name:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The name of this method that is used for display");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setName(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Text createCategorySection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Category:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The category groups similar methods under a common name");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setCategory(text.getText().trim());
					setDirty(true);
				}
			}
		});
		autoComplete(text, new IContentProposalProvider() {

			@Override
			public IContentProposal[] getProposals(String contents, int position) {

				List<ContentProposal> list = new ArrayList<>();
				if(contents != null) {
					String[] items = getItems();
					for(String item : items) {
						if(item.toLowerCase().contains(contents.toLowerCase())) {
							list.add(new ContentProposal(item));
						}
					}
				}
				return list.toArray(new IContentProposal[0]);
			}

			private String[] getItems() {

				if(knownCategories == null) {
					Set<String> categories = new TreeSet<>();
					processingSupport.visitSupplier(new Consumer<IProcessSupplier<?>>() {

						@Override
						public void accept(IProcessSupplier<?> supplier) {

							String category = supplier.getCategory();
							if(category != null && !category.isEmpty()) {
								categories.add(category);
							}
						}
					});
					knownCategories = categories.toArray(new String[0]);
				}
				return knownCategories;
			}
		});
		return text;
	}

	private Text createOperatorSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Operator:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("The operator is the person who has created / currently manages this method");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setOperator(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Text createDescriptionSection(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Description:");
		//
		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Description");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(processMethod != null) {
					processMethod.setDescription(text.getText().trim());
					setDirty(true);
				}
			}
		});
		//
		return text;
	}

	private Composite createToolbarHeader(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		//
		textName = createNameSection(composite);
		textOperator = createOperatorSection(composite);
		textDescription = createDescriptionSection(composite);
		textCategory = createCategorySection(composite);
		buttonFinalize = createFinalize(composite);
		//
		return composite;
	}

	private Button createFinalize(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Finalized:");
		Button button = new Button(parent, SWT.CHECK);
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openConfirm(parent.getShell(), "Finalize Process Method", "Finalize a method prevents further modifications to this method, are you sure?")) {
					updateProcessMethod();
				} else {
					button.setSelection(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		return button;
	}

	private void createTable(Composite parent) {

		TreeViewer treeViewer = createTreeTable(parent, false);
		for(int i = 0; i < MethodListLabelProvider.TITLES.length; i++) {
			columns[i] = createColumn(treeViewer, MethodListLabelProvider.TITLES[i], MethodListLabelProvider.BOUNDS[i], null);
		}
		treeViewer.setLabelProvider(new MethodListLabelProvider(processingSupport, preferencesSupplier));
		treeViewer.setContentProvider(new ITreeContentProvider() {

			@Override
			public boolean hasChildren(Object element) {

				if(element instanceof IProcessEntry) {
					IProcessEntry entry = (IProcessEntry)element;
					IProcessSupplier<?> supplier = processingSupport.getSupplier(entry.getProcessorId());
					if(supplier instanceof ProcessEntryContainer) {
						return ((ProcessEntryContainer)supplier).getNumberOfEntries() > 0;
					}
				}
				if(element instanceof ProcessEntryContainer) {
					return ((ProcessEntryContainer)element).getNumberOfEntries() > 0;
				}
				return false;
			}

			@Override
			public Object getParent(Object element) {

				if(element instanceof IProcessEntry) {
					return ((IProcessEntry)element).getParent();
				}
				return null;
			}

			@Override
			public Object[] getElements(Object inputElement) {

				if(inputElement instanceof ProcessEntryContainer) {
					ProcessEntryContainer container = (ProcessEntryContainer)inputElement;
					return entryList(container, false);
				}
				if(inputElement instanceof Object[]) {
					return (Object[])inputElement;
				}
				return new Object[0];
			}

			private Object[] entryList(Iterable<? extends IProcessEntry> iterable, boolean detatch) {

				List<Object> list = new ArrayList<>();
				if(detatch) {
					iterable.forEach(new Consumer<IProcessEntry>() {

						@Override
						public void accept(IProcessEntry entry) {

							list.add(new ProcessEntry(entry, null));
						}
					});
				} else {
					iterable.forEach(list::add);
				}
				return list.toArray();
			}

			@Override
			public Object[] getChildren(Object parentElement) {

				if(parentElement instanceof IProcessEntry) {
					IProcessEntry entry = (IProcessEntry)parentElement;
					IProcessSupplier<?> supplier = processingSupport.getSupplier(entry.getProcessorId());
					if(supplier instanceof ProcessEntryContainer) {
						return entryList((ProcessEntryContainer)supplier, true);
					}
				}
				if(parentElement instanceof ProcessEntryContainer) {
					return entryList((ProcessEntryContainer)parentElement, false);
				}
				return new Object[0];
			}
		});
		treeViewer.getTree();
		treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.addSelectionChangedListener(event -> updateTableButtons());
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				if(preferencesSupplier == null) {
					return;
				}
				Object firstElement = treeViewer.getStructuredSelection().getFirstElement();
				if(firstElement instanceof IProcessEntry) {
					IProcessEntry entry = (IProcessEntry)firstElement;
					if(modifyProcessEntry(treeViewer.getControl().getShell(), entry, IProcessEntry.getContext(entry, processingSupport), true)) {
						updateProcessMethod();
					}
				}
			}
		});
		listUI = treeViewer;
	}

	private ToolBar createToolbarBottom(Composite parent) {

		ToolBar toolBar = new ToolBar(parent, SWT.FLAT);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		toolBar.setLayoutData(gridData);
		buttonAdd = createAddButton(toolBar);
		buttonRemove = createRemoveButton(toolBar);
		buttonCopy = createCopyButton(toolBar);
		buttonMoveUp = createMoveUpButton(toolBar);
		buttonMoveDown = createMoveDownButton(toolBar);
		buttonModifySettings = createModifySettingsButton(toolBar);
		return toolBar;
	}

	private ToolItem createAddButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Add a process method.");
		final Menu menu = new Menu(toolBar.getShell(), SWT.POP_UP);
		toolBar.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {

				menu.dispose();
			}
		});
		item.addListener(SWT.Selection, event -> {
			if(event.detail == SWT.ARROW) {
				Rectangle rect = item.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay(pt);
				for(MenuItem menuItem : menu.getItems()) {
					menuItem.dispose();
				}
				Collection<IProcessMethod> userMethods = MethodConverter.getUserMethods();
				for(IProcessMethod method : userMethods) {
					MenuItem menuItem = new MenuItem(menu, SWT.NONE);
					menuItem.setText(method.getName());
					menuItem.addSelectionListener(new SelectionListener() {

						@Override
						public void widgetSelected(SelectionEvent e) {

							loadMethodFile(method);
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {

						}
					});
				}
				if(!userMethods.isEmpty()) {
					new MenuItem(menu, SWT.SEPARATOR);
				}
				MenuItem loadItem = new MenuItem(menu, SWT.NONE);
				loadItem.setText("Load from file...");
				loadItem.addSelectionListener(new SelectionListener() {

					@Override
					public void widgetSelected(SelectionEvent e) {

						FileDialog fileDialog = new FileDialog(toolBar.getShell(), SWT.OPEN);
						fileDialog.setText("Select Process Method file");
						fileDialog.setFileName(MethodConverter.DEFAULT_METHOD_FILE_NAME);
						fileDialog.setFilterExtensions(MethodConverter.DEFAULT_METHOD_FILE_EXTENSIONS);
						fileDialog.setFilterNames(MethodConverter.DEFAULT_METHOD_FILE_NAMES);
						//
						String filePath = fileDialog.open();
						if(filePath != null) {
							File file = new File(filePath);
							loadMethodFile(Adapters.adapt(file, IProcessMethod.class));
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {

					}
				});
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			} else {
				if(processMethod != null) {
					Map<ProcessSupplierContext, String> contextList = new LinkedHashMap<>();
					Object element = listUI.getStructuredSelection().getFirstElement();
					ProcessEntry selectedEntry = null;
					if(element instanceof ProcessEntry) {
						selectedEntry = (ProcessEntry)element;
						String id = selectedEntry.getProcessorId();
						IProcessSupplier<?> supplier = processingSupport.getSupplier(id);
						if(supplier instanceof ProcessSupplierContext) {
							contextList.put((ProcessSupplierContext)supplier, supplier.getName());
						}
					}
					contextList.put(processingSupport, processMethod.getName());
					Map<ProcessSupplierContext, IProcessEntry> map = ProcessingWizard.open(getShell(), contextList, dataCategories);
					if(map != null) {
						for(Entry<ProcessSupplierContext, IProcessEntry> entry : map.entrySet()) {
							ProcessSupplierContext supplierContext = entry.getKey();
							IProcessEntry editedEntry = entry.getValue();
							boolean edit = modifyProcessEntry(getShell(), editedEntry, supplierContext, false);
							if(!edit) {
								continue;
							}
							IProcessEntry newEntry;
							if(supplierContext == processingSupport) {
								// add to global context
								newEntry = processMethod.addProcessEntry(editedEntry);
							} else {
								// add to local context
								newEntry = selectedEntry.addProcessEntry(editedEntry);
							}
							updateProcessMethod();
							select(Collections.singletonList(newEntry));
						}
					}
				}
			}
		});
		return item;
	}

	public void loadMethodFile(IProcessMethod method) {

		if(method != null) {
			List<IProcessEntry> copied = new ArrayList<>();
			method.forEach(entry -> {
				copied.add(processMethod.addProcessEntry(entry));
			});
			updateProcessMethod();
			select(copied);
		}
	}

	private void select(Iterable<? extends IProcessEntry> entries) {

		ArrayList<IProcessEntry> list = new ArrayList<>();
		entries.forEach(list::add);
		StructuredSelection structuredSelection = new StructuredSelection(list);
		listUI.setSelection(structuredSelection);
		Object firstElement = structuredSelection.getFirstElement();
		if(firstElement != null) {
			listUI.reveal(firstElement);
		}
	}

	private ToolItem createCopyButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_COPY, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Copy a process method.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Iterator<?> selection = listUI.getStructuredSelection().iterator();
				while(selection.hasNext()) {
					Object object = selection.next();
					ListProcessEntryContainer container = MethodSupport.getContainer(object);
					if(container != null) {
						List<IProcessEntry> entries = container.getEntries();
						int index = entries.indexOf(object);
						if(index > -1) {
							IProcessEntry processEntry = entries.get(index);
							IProcessEntry processEntryCopy = new ProcessEntry(processEntry, container);
							entries.add(index, processEntryCopy);
						}
					}
				}
				updateProcessMethod();
			}
		});
		//
		return item;
	}

	private ToolItem createRemoveButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DELETE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Remove the selected process method(s).");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(MessageDialog.openQuestion(toolBar.getShell(), "Delete Process Method(s)", "Would you like to delete the selected processor(s)?")) {
					for(Object object : listUI.getStructuredSelection().toArray()) {
						ListProcessEntryContainer container = MethodSupport.getContainer(object);
						if(container != null) {
							container.removeProcessEntry((IProcessEntry)object);
						}
					}
					updateProcessMethod();
					select(Collections.emptyList());
				}
			}
		});
		return item;
	}

	private ToolItem createMoveUpButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_UP_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the process method(s) up.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = listUI.getStructuredSelection();
				Iterator<?> iterator = selection.iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					ListProcessEntryContainer container = MethodSupport.getContainer(object);
					if(container != null) {
						List<IProcessEntry> entries = container.getEntries();
						int index = entries.indexOf(object);
						if(index > 0) {
							Collections.swap(entries, index, index - 1);
						}
					}
				}
				updateProcessMethod();
				listUI.setSelection(selection);
			}
		});
		//
		return item;
	}

	private ToolItem createMoveDownButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ARROW_DOWN_2, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Move the process method(s) down.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection selection = listUI.getStructuredSelection();
				Iterator<?> iterator = selection.iterator();
				while(iterator.hasNext()) {
					Object object = iterator.next();
					ListProcessEntryContainer container = MethodSupport.getContainer(object);
					if(container != null) {
						List<IProcessEntry> entries = container.getEntries();
						int index = entries.indexOf(object);
						if(index > -1 && index < entries.size() - 1) {
							Collections.swap(entries, index, index + 1);
						}
					}
				}
				updateProcessMethod();
				listUI.setSelection(selection);
			}
		});
		//
		return item;
	}

	private ToolItem createModifySettingsButton(ToolBar toolBar) {

		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);
		item.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		item.setToolTipText("Modify the process method settings.");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(processMethod != null) {
					Object object = listUI.getStructuredSelection().getFirstElement();
					if(object instanceof IProcessEntry) {
						IProcessEntry processEntry = (IProcessEntry)object;
						modifyProcessEntry(toolBar.getShell(), processEntry, IProcessEntry.getContext(processEntry, processingSupport), true);
						updateProcessMethod();
					}
				}
			}
		});
		//
		return item;
	}

	private void updateProcessMethod() {

		if(processMethod != null) {
			textOperator.setText(processMethod.getOperator());
			textDescription.setText(processMethod.getDescription());
			textCategory.setText(processMethod.getCategory());
			textName.setText(processMethod.getName());
			boolean readOnly = buttonFinalize.getSelection();
			if(readOnly) {
				processMethod.setReadOnly(readOnly);
				textOperator.setEnabled(false);
				textDescription.setEnabled(false);
				textCategory.setEnabled(false);
				textName.setEnabled(false);
				buttonFinalize.setEnabled(false);
			}
		} else {
			textOperator.setText("");
			textDescription.setText("");
			textCategory.setText("");
			textName.setText("");
		}
		boolean expand = false;
		if(postActions == null || postActions.isEmpty()) {
			listUI.setInput(processMethod);
		} else {
			ArrayList<Object> list = new ArrayList<>();
			list.add(processMethod);
			list.addAll(postActions);
			listUI.setInput(list.toArray());
			expand = true;
		}
		listUI.refresh();
		if(listUI instanceof TreeViewer) {
			if(expand) {
				((TreeViewer)listUI).expandToLevel(1);
			}
		}
		updateTableButtons();
		setDirty(true);
	}

	private void updateTableButtons() {

		buttonAdd.setEnabled(processMethod != null && !processMethod.isFinal() && !readonly);
		//
		IStructuredSelection selection = listUI.getStructuredSelection();
		boolean writeable = processMethod != null && !processMethod.isFinal() && !selection.isEmpty();
		Iterator<?> iterator = selection.iterator();
		while(iterator.hasNext() && writeable) {
			Object object = iterator.next();
			ListProcessEntryContainer container = MethodSupport.getContainer(object);
			if(container == null) {
				writeable = false;
			}
		}
		buttonCopy.setEnabled(writeable && !readonly);
		buttonRemove.setEnabled(writeable && !readonly);
		buttonMoveUp.setEnabled(writeable && !readonly);
		buttonMoveDown.setEnabled(writeable && !readonly);
		buttonModifySettings.setEnabled(writeable && preferencesSupplier != null);
	}

	@Override
	public void setEnabled(boolean enabled) {

		super.setEnabled(enabled);
		listUI.getControl().setEnabled(enabled);
		PartSupport.setCompositeVisibility(buttons, enabled);
	}

	private void setDirty(boolean dirty) {

		if(modificationHandler != null) {
			modificationHandler.setDirty(dirty);
		}
	}

	@Override
	public MethodUIConfig getConfig() {

		return new MethodUIConfig() {

			TableConfigSupport support = new TableConfigSupport(Arrays.asList(columns));

			@Override
			public void setToolbarVisible(boolean visible) {

				PartSupport.setCompositeVisibility(toolbarMain, visible);
			}

			@Override
			public boolean isToolbarVisible() {

				return toolbarMain.isVisible();
			}

			@Override
			public void setVisibleColumns(Set<String> visibleColumns) {

				support.setVisibleColumns(visibleColumns);
			}

			@Override
			public Set<String> getColumns() {

				return new HashSet<>(Arrays.asList(MethodListLabelProvider.TITLES));
			}

			@Override
			public void setShowSettingsOnAdd(boolean showSettingsOnAdd) {

				ExtendedMethodUI.this.showSettingsOnAdd = showSettingsOnAdd;
			}

			@Override
			public IPreferencePage[] getPreferencePages() {

				IPreferencePage preferencePageProcessing = new PreferencePageReportExport();
				preferencePageProcessing.setTitle("Processing");
				return new IPreferencePage[]{preferencePageProcessing};
			}

			@Override
			public void applySettings() {

				ExtendedMethodUI.this.applySettings();
			}

			@Override
			public int getColumWidth(String column) {

				return support.getColumWidth(column);
			}

			@Override
			public void setColumWidth(String column, int width) {

				support.setColumWidth(column, width);
			}
		};
	}

	private boolean modifyProcessEntry(Shell shell, IProcessEntry processEntry, ProcessSupplierContext supplierContext, boolean showHint) {

		ProcessorPreferences<?> preferences = preferencesSupplier.apply(processEntry, supplierContext);
		if(preferences == null) {
			// handle like cancel
			return false;
		}
		if(preferences.getSupplier().getSettingsParser().getInputValues().isEmpty()) {
			if(showHint) {
				MessageDialog.openInformation(shell, "No Settings avaiable", "This processor does not offer any options");
			}
			// nothing to do then, like ok
			return true;
		}
		try {
			return SettingsWizard.openEditPreferencesWizard(shell, preferences);
		} catch(IOException e) {
			// like cancel...
			return false;
		}
	}
}
