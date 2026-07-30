PATHTOROOT = ../../../..

OBJS = \
StudyServiceRetrieveTransactionUserAgent.class \
StudyServiceSearchTransactionUserAgent.class

all:	${OBJS}

include ${PATHTOROOT}/Makefile.common.mk

.java.class:
	export JAVAVERSIONTARGETJARFILE=`/usr/libexec/java_home -v ${JAVAVERSIONTARGET} | tail -1`/jre/lib/rt.jar; javac ${JAVACOPTIONS} \
		-classpath .:${PATHTOROOT}:${SLF4JCOMPILEADDITIONALJAR}:${JSONADDITIONALJAR} \
		-sourcepath . $<

archive:
	tar -cvf - Makefile *.java | gzip -best > ../../../../useragent.`date '+%Y%m%d'`.tar.gz

