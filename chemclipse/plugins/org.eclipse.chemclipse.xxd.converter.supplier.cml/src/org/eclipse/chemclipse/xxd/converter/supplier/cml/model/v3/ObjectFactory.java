/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3;

import java.lang.reflect.Parameter;

import javax.xml.namespace.QName;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	private static final QName _PeakList_QNAME = new QName("http://www.xml-cml.org/schema", "peakList");
	private static final QName _Formula_QNAME = new QName("http://www.xml-cml.org/schema", "formula");
	private static final QName _Peak_QNAME = new QName("http://www.xml-cml.org/schema", "peak");
	private static final QName _MetadataList_QNAME = new QName("http://www.xml-cml.org/schema", "metadataList");
	private static final QName _Dimension_QNAME = new QName("http://www.xml-cml.org/schema", "dimension");
	private static final QName _Parameter_QNAME = new QName("http://www.xml-cml.org/schema", "parameter");
	private static final QName _Array_QNAME = new QName("http://www.xml-cml.org/schema", "array");
	private static final QName _Xaxis_QNAME = new QName("http://www.xml-cml.org/schema", "xaxis");
	private static final QName _SpectrumData_QNAME = new QName("http://www.xml-cml.org/schema", "spectrumData");
	private static final QName _Name_QNAME = new QName("http://www.xml-cml.org/schema", "name");
	private static final QName _Unit_QNAME = new QName("http://www.xml-cml.org/schema", "unit");
	private static final QName _ConditionList_QNAME = new QName("http://www.xml-cml.org/schema", "conditionList");
	private static final QName _AnyCml_QNAME = new QName("http://www.xml-cml.org/schema", "anyCml");
	private static final QName _Sample_QNAME = new QName("http://www.xml-cml.org/schema", "sample");
	private static final QName _PeakGroup_QNAME = new QName("http://www.xml-cml.org/schema", "peakGroup");
	private static final QName _Yaxis_QNAME = new QName("http://www.xml-cml.org/schema", "yaxis");
	private static final QName _Cml_QNAME = new QName("http://www.xml-cml.org/schema", "cml");
	private static final QName _Molecule_QNAME = new QName("http://www.xml-cml.org/schema", "molecule");
	private static final QName _Spectrum_QNAME = new QName("http://www.xml-cml.org/schema", "spectrum");
	private static final QName _System_QNAME = new QName("http://www.xml-cml.org/schema", "system");
	private static final QName _Scalar_QNAME = new QName("http://www.xml-cml.org/schema", "scalar");
	private static final QName _Module_QNAME = new QName("http://www.xml-cml.org/schema", "module");
	private static final QName _Metadata_QNAME = new QName("http://www.xml-cml.org/schema", "metadata");

	public ObjectFactory() {

	}

	public Metadata createMetadata() {

		return new Metadata();
	}

	public Scalar createScalar() {

		return new Scalar();
	}

	public Spectrum createSpectrum() {

		return new Spectrum();
	}

	public Molecule createMolecule() {

		return new Molecule();
	}

	public Cml createCml() {

		return new Cml();
	}

	public Yaxis createYaxis() {

		return new Yaxis();
	}

	public PeakGroup createPeakGroup() {

		return new PeakGroup();
	}

	public Sample createSample() {

		return new Sample();
	}

	public ConditionList createConditionList() {

		return new ConditionList();
	}

	public Unit createUnit() {

		return new Unit();
	}

	public Name createName() {

		return new Name();
	}

	public SpectrumData createSpectrumData() {

		return new SpectrumData();
	}

	public Xaxis createXaxis() {

		return new Xaxis();
	}

	public Array createArray() {

		return new Array();
	}

	public Dimension createDimension() {

		return new Dimension();
	}

	public MetadataList createMetadataList() {

		return new MetadataList();
	}

	public Peak createPeak() {

		return new Peak();
	}

	public Formula createFormula() {

		return new Formula();
	}

	public PeakList createPeakList() {

		return new PeakList();
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "peakList", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<PeakList> createPeakList(PeakList value) {

		return new JAXBElement<>(_PeakList_QNAME, PeakList.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "formula", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Formula> createFormula(Formula value) {

		return new JAXBElement<>(_Formula_QNAME, Formula.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "peak", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Peak> createPeak(Peak value) {

		return new JAXBElement<>(_Peak_QNAME, Peak.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "metadataList", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<MetadataList> createMetadataList(MetadataList value) {

		return new JAXBElement<>(_MetadataList_QNAME, MetadataList.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "dimension", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Dimension> createDimension(Dimension value) {

		return new JAXBElement<>(_Dimension_QNAME, Dimension.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "parameter", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Parameter> createParameter(Parameter value) {

		return new JAXBElement<>(_Parameter_QNAME, Parameter.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "array", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Array> createArray(Array value) {

		return new JAXBElement<>(_Array_QNAME, Array.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "xaxis", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Xaxis> createXaxis(Xaxis value) {

		return new JAXBElement<>(_Xaxis_QNAME, Xaxis.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "spectrumData", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<SpectrumData> createSpectrumData(SpectrumData value) {

		return new JAXBElement<>(_SpectrumData_QNAME, SpectrumData.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "name", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Name> createName(Name value) {

		return new JAXBElement<>(_Name_QNAME, Name.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "unit", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Unit> createUnit(Unit value) {

		return new JAXBElement<>(_Unit_QNAME, Unit.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "conditionList", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<ConditionList> createConditionList(ConditionList value) {

		return new JAXBElement<>(_ConditionList_QNAME, ConditionList.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "anyCml")
	public JAXBElement<java.lang.Object> createAnyCml(java.lang.Object value) {

		return new JAXBElement<>(_AnyCml_QNAME, java.lang.Object.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "sample", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Sample> createSample(Sample value) {

		return new JAXBElement<>(_Sample_QNAME, Sample.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "peakGroup", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<PeakGroup> createPeakGroup(PeakGroup value) {

		return new JAXBElement<>(_PeakGroup_QNAME, PeakGroup.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "yaxis", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Yaxis> createYaxis(Yaxis value) {

		return new JAXBElement<>(_Yaxis_QNAME, Yaxis.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "cml", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Cml> createCml(Cml value) {

		return new JAXBElement<>(_Cml_QNAME, Cml.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "molecule", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Molecule> createMolecule(Molecule value) {

		return new JAXBElement<>(_Molecule_QNAME, Molecule.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "spectrum", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Spectrum> createSpectrum(Spectrum value) {

		return new JAXBElement<>(_Spectrum_QNAME, Spectrum.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "system", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<System> createSystem(System value) {

		return new JAXBElement<>(_System_QNAME, System.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "scalar", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Scalar> createScalar(Scalar value) {

		return new JAXBElement<>(_Scalar_QNAME, Scalar.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "module", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Module> createModule(Module value) {

		return new JAXBElement<>(_Module_QNAME, Module.class, null, value);
	}

	@XmlElementDecl(namespace = "http://www.xml-cml.org/schema", name = "metadata", substitutionHeadNamespace = "http://www.xml-cml.org/schema", substitutionHeadName = "anyCml")
	public JAXBElement<Metadata> createMetadata(Metadata value) {

		return new JAXBElement<>(_Metadata_QNAME, Metadata.class, null, value);
	}
}
