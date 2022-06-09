#!/usr/bin/env python

from setuptools import setup

setup(
    name="sls-client",
    version="2.0.4",
    description="A python client for the Simple Lookup Service",
    long_description=open("README.rst").read(),
    author="Sowmya Balasubramanian",
    author_email="sowmya@es.net",
    url="https://github.com/esnet/python-sls-client",
    license="3-clause BSD License",
    package_data={
        "sls-client": ["sls-client/LICENSE"]
    },
    packages=['sls_client'],
    scripts=[
        'clients/sls_dig',
        'clients/find_ps_ma',
        'clients/sls_text_search',
        'clients/sls_report',
        'clients/sls_cache_report'
    ],
    install_requires=[
        "YURL",
        "isodate",
        "futures",
        "requests",
        "voluptuous",
        "IPy==0.83",
        "tldextract==1.7.4",
        "Jinja2>=2.10.1",
        "elasticsearch>=5.0"
    ],
    zip_safe=False,
    classifiers=[
        "Development Status :: 3 - Alpha",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: BSD License",
        "Operating System :: OS Independent",
        "Programming Language :: Python",
        "Programming Language :: Python :: 3",
        "Topic :: Software Development :: Libraries :: Application Frameworks",
        "Topic :: Software Development :: Libraries :: Python Modules",
        "Topic :: System :: Networking"
    ],
)
