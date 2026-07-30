OBJS = \
	Anatomy.class \
	Concept.class \
	CodedConcept.class \
	CombinedAnatomicConcepts.class \
	CTAnatomy.class \
	DictionaryOfConcepts.class \
	DisplayableAnatomicConcept.class \
	DisplayableConcept.class \
	DisplayableLateralityConcept.class \
	DisplayableViewConcept.class \
	MammographyLaterality.class \
	ProjectionXRayAnatomy.class \
	SchemeAndValuePair.class \
	anatomyfromannexl.xml

all:	${OBJS}

PATHTOROOT = ../../..

include ${PATHTOROOT}/Makefile.common.mk

clean:
	rm -f *~ *.class core *.bak anatomyfromannexl.xml

testanatregionseq:	ProjectionXRayAnatomy.class
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.ProjectionXRayAnatomy \
		"$${HOME}/Pictures/Medical/CRDXLUTs/anon/1.3.6.1.4.1.5962.1.1.0.0.0.1160129313.5958.0.15.dcm"

testbodypart:	ProjectionXRayAnatomy.class
	dcsmpte /tmp/crap -nodisclaimer -r BodyPartExamined "CHEST"
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.ProjectionXRayAnatomy /tmp/crap
	rm /tmp/crap

testseriesdesc:	ProjectionXRayAnatomy.class
	dcsmpte /tmp/crap -nodisclaimer -r SeriesDescription "CHEST XRAY 1 VIEW"
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.ProjectionXRayAnatomy /tmp/crap
	rm /tmp/crap

testseriesdesclongest:	ProjectionXRayAnatomy.class
	dcsmpte /tmp/crap -nodisclaimer -r SeriesDescription "Head, neck"
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.ProjectionXRayAnatomy /tmp/crap
	rm /tmp/crap

testview:	ProjectionXRayAnatomy.class
	dcsmpte /tmp/crap -nodisclaimer -r ViewPosition "PA"
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.ProjectionXRayAnatomy /tmp/crap
	rm /tmp/crap

testctstudydesccombined:	CTAnatomy.class
	dcsmpte /tmp/crap -nodisclaimer -r StudyDescription "Chest, abdomen and pelvis"
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.CTAnatomy /tmp/crap
	rm /tmp/crap

testctstudydescabbrev:	CTAnatomy.class
	dcsmpte /tmp/crap -nodisclaimer -r StudyDescription "CAP"
	java -cp ${PATHTOROOT}:${COMMONSCODECADDITIONALJAR} com.pixelmed.anatproc.CTAnatomy /tmp/crap
	rm /tmp/crap

anatomyfromannexl.xml:	${HOME}/${PATHTOSTANDARDFROMHOME}/support/stylesheets/extractanatomyfromannexl.xsl ${HOME}/${PATHTOSTANDARDFROMHOME}/source/docbook/part16/part16.xml
	java -cp ${PATHTOROOT} ${XALANJAVAOPTS} \
		com.pixelmed.utils.ExecuteTranslet \
		"$${HOME}/${PATHTOSTANDARDFROMHOME}/support/stylesheets/extractanatomyfromannexl.xsl" \
		"$${HOME}/${PATHTOSTANDARDFROMHOME}/source/docbook/part16/part16.xml" \
		$@
