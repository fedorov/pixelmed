PATHTOROOT = ../../..

OBJS =

all:	${OBJS}

include ${PATHTOROOT}/Makefile.common.mk

archive:
	tar -cvf - Makefile *.java | gzip -best > ../../../dicomweb.`date '+%Y%m%d'`.tar.gz
