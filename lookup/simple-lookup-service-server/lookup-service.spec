%define package_name lookup-service
%define mvn_project_name simple-lookup-service
%define mvn_project_list %{mvn_project_name}-common,%{mvn_project_name}-client,%{mvn_project_name}-server
%define install_base /opt/%{package_name}
%define config_base /etc/%{package_name}
%define log_dir /var/log/%{package_name}
%define run_dir /var/run/%{package_name}
%define data_dir /var/lib/%{package_name}
%define init_script lookup-service
%define relnum 4

Name:           %{package_name}
Version:        2.2
Release:        %{relnum}%{?dist}
Summary:        Lookup Service
License:        distributable, see LICENSE
Group:          Development/Libraries
URL:            https://github.com/esnet/simple-lookup-service
Source0:        %{mvn_project_name}-%{version}-%{relnum}.tar.gz
BuildRoot:      %{_tmppath}/-%{version}-%{release}-root-%(%{__id_u} -n)
BuildRequires:  java-openjdk >= 1.6.0
BuildRequires:  sed 
BuildArch:      noarch
Requires:       java-openjdk >= 1.6.0

%if 0%{?el7}
BuildRequires: systemd
BuildRequires:  maven
%{?systemd_requires: %systemd_requires}
%else
BuildRequires:  apache-maven
Requires:		chkconfig
%endif
Requires:	mongodb-org-server

%description
Lookup Service is used to find registered services. 
This package provides a server that allows clients to register and query services 
via REST interface.

%pre
/usr/sbin/groupadd lookup 2> /dev/null || :
/usr/sbin/useradd -g lookup -r -s /sbin/nologin -c "Lookup Service User" -d /tmp lookup 2> /dev/null || :

%prep
%setup -q -n  %{mvn_project_name}

%clean
rm -rf %{buildroot}

%build
mvn --projects %{mvn_project_list} clean

%install
#Clean out previous build
rm -rf %{buildroot}

#Run install target
mvn -DskipTests --projects %{mvn_project_list} install 

#Create directory structure for build root
mkdir -p %{buildroot}/%{install_base}/target
mkdir -p %{buildroot}/%{install_base}/bin
mkdir -p %{buildroot}/%{config_base}
%if 0%{?el7}
mkdir -p %{buildroot}%{_unitdir}
%else
mkdir -p %{buildroot}/etc/init.d
%endif

#Copy jar files and scripts
cp %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/target/*.jar %{buildroot}/%{install_base}/target/
install -m 755 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/bin/* %{buildroot}/%{install_base}/bin/

%if 0%{?el7}
install -m 755 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/scripts/lookup-service %{buildroot}/%{_unitdir}/%{init_script}.service
%else
install -m 755 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/scripts/lookup-service %{buildroot}/etc/init.d/%{init_script}
%endif

# Copy default config file
cp %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/etc/lookupservice.yaml %{buildroot}/%{config_base}/lookupservice.yaml
cp %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/etc/queueservice.yaml %{buildroot}/%{config_base}/queueservice.yaml
cp %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/etc/process.yaml %{buildroot}/%{config_base}/process.yaml
#Update log locations
sed -e s,%{package_name}.log,%{log_dir}/%{package_name}.log, < %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-server/etc/log4j.properties > %{buildroot}/%{config_base}/log4j.properties


# Copy LICENSE file
cp %{_builddir}/%{mvn_project_name}/LICENSE %{buildroot}/%{install_base}/LICENSE

%post
#Create directory for PID files
mkdir -p %{run_dir}
chown lookup:lookup %{run_dir}
chmod 700 %{run_dir}

#Create directory for logs
mkdir -p %{log_dir}
chown lookup:lookup %{log_dir}

#Create database directory
mkdir -p %{data_dir}
chown lookup:lookup %{data_dir}

#Create queuedir
mkdir -p %{install_base}/data
chown lookup:lookup %{install_base}/data
#Create symbolic links to latest version of jar files
##if update then delete old links
if [ $1 == 2 ]; then
  if [ -e %{config_base}/lookup-service.yaml ]; then
     rm %{config_base}/lookup-service.yaml
  fi
  if [ -L %{install_base}/target/%{package_name}.one-jar.jar ]; then
      unlink %{install_base}/target/%{package_name}.one-jar.jar
  fi
  if [ -L %{install_base}/target/%{package_name}-server.one-jar.jar ]; then
     unlink %{install_base}/target/%{package_name}-server.one-jar.jar
  fi
fi
   ln -s %{install_base}/target/%{mvn_project_name}-server-%{version}-SNAPSHOT.one-jar.jar %{install_base}/target/%{package_name}-server.one-jar.jar
chown lookup:lookup %{install_base}/target/%{package_name}-server.one-jar.jar
#ln -s %{install_base}/target/%{mvn_project_name}-server-%{version}.jar %{install_base}/target/%{package_name}.jar

#Configure service to start when machine boots

%if 0%{?el7}
%systemd_post %{init_script}.service
if [ "$1" = "1" ]; then
    #if new install, then enable
    systemctl enable %{init_script}.service
    systemctl start %{init_script}.service
fi
%else
/sbin/chkconfig --add %{package_name}
%endif

%preun
%if 0%{?el7}
%systemd_preun %{init_script}.service
%else
if [ $1 == 0 ]; then
    /sbin/chkconfig --del %{package_name}
    /sbin/service %{package_name} stop
    if [ -L %{install_base}/target/%{package_name}-server.one-jar.jar ]; then
        unlink %{install_base}/target/%{package_name}-server.one-jar.jar
    fi
    if [ -L %{install_base}/target/%{package_name}.one-jar.jar ]; then
        unlink %{install_base}/target/%{package_name}.one-jar.jar
    fi
fi
%endif

%files
%defattr(-,lookup,lookup,-)
%config(noreplace) %{config_base}/*
%{install_base}/target/*
%{install_base}/bin/*
%%license %{install_base}/LICENSE
%if 0%{?el7}
%attr(0644,root,root) %{_unitdir}/%{init_script}.service
%else
%attr(0755,lookup,lookup) /etc/init.d/%{init_script}
%endif


%changelog
* Mon Sep 24 2018 sowmya@es.net 2.2-9
- Updated spec file to support Centos 7