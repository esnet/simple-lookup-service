#!/usr/bin/env python

from setuptools import setup
from setuptools import find_packages

setup(
    name="sls-client",
    version="1.0.4",
    description="A python client for the Simple Lookup Service",
    long_description=open("README.rst").read(),
    author=["Andrew Sides","Sowmya Balasubramanian"],
    author_email=["asides@es.net","sowmya@es.net"],
    url="https://github.com/esnet/python-sls-client",
    license="3-clause BSD License",
    package_data={
        "sls-client":["sls-client/LICENSE"]
    },
    packages=find_packages(),
    install_requires=[
        "IPy",
        "YURL",
        "isodate",
        "futures",
        "requests",
        "voluptuous"
    ],
    zip_safe=False,
    classifiers=[
        "Development Status :: 3 - Alpha",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: BSD License",
        "Operating System :: OS Independent",
        "Programming Language :: Python",
        "Programming Language :: Python :: 2",
        "Programming Language :: Python :: 2.6",
        "Programming Language :: Python :: 2.7",
        "Topic :: Software Development :: Libraries :: Application Frameworks",
        "Topic :: Software Development :: Libraries :: Python Modules",
        "Topic :: System :: Networking"
    ],
)
