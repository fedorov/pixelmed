PATHTOROOT = ../../..

OBJS = \
FilterPanel.class \
QueryInformationModel.class \
QueryResponseGenerator.class \
QueryTreeModel.class \
QueryTreeBrowser.class \
QueryTreeRecord.class \
RetrieveResponseGenerator.class \
StudyRootQueryInformationModel.class

all:	${OBJS}

include ${PATHTOROOT}/Makefile.common.mk

archive:
	tar -cvf - Makefile *.java | gzip -best > ../../../query.`date '+%Y%m%d'`.tar.gz

testquery: StudyRootQueryInformationModel.class
	java \
		-cp ${PATHTOROOT} \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.network.FindSOPClassSCU=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.QueryTreeModel=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.QueryTreeRecord=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.QueryInformationModel=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.StudyRootQueryInformationModel=debug \
		com.pixelmed.query.StudyRootQueryInformationModel 116.202.173.80 11112 DCM4CHEE us
	#java -cp ${PATHTOROOT} com.pixelmed.query.StudyRootQueryInformationModel localhost 4006 OSIRIX_GRAYTOO us
	#java -cp ${PATHTOROOT} com.pixelmed.query.StudyRootQueryInformationModel localhost 4006 GRAYTOO_DIV_4006 us
	#java -cp ${PATHTOROOT} com.pixelmed.query.StudyRootQueryInformationModel rpleo 104 rpleo GRAYTOO_DIV_4006
	#java -cp ${PATHTOROOT} com.pixelmed.query.StudyRootQueryInformationModel 10.217.142.23 3010 EV1 GRAYTOO_DIV_4006
	#java -cp ${PATHTOROOT} com.pixelmed.query.StudyRootQueryInformationModel 10.217.142.22 104 rpprovDISK GRAYTOO_DIV_4006

testdicomwebquery: StudyRootQueryInformationModel.class
	java \
		-cp ${PATHTOROOT}:${DICOMADDITIONALJARS} \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.dicomweb.useragent.StudyServiceSearchTransactionUserAgent=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.QueryTreeModel=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.QueryTreeRecord=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.QueryInformationModel=debug \
		-Dorg.slf4j.simpleLogger.log.com.pixelmed.query.StudyRootQueryInformationModel=debug \
		com.pixelmed.query.StudyRootQueryInformationModel https://ihe.j4care.com:18443/dcm4chee-arc/aets/DCM4CHEE/rs
