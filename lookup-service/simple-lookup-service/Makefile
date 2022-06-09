PACKAGE=simple-lookup-service
PERFSONAR_AUTO_VERSION=4.4.1
PERFSONAR_AUTO_RELNUM=1
VERSION=${PERFSONAR_AUTO_VERSION}
RELEASE=${PERFSONAR_AUTO_RELNUM}

default:
	@echo No need to build the package. Just run \"make install\"

dist:
	git archive --format=tar.gz --prefix=${PACKAGE}-${VERSION}/ HEAD