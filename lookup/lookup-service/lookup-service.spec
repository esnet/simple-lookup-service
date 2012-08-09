%define package_name lookup-service 
%define mvn_project_list %{package_name}
%define install_base /opt/%{package_name}
%define config_base /etc/%{package_name}
%define log_dir /var/log/lookup-service
%define run_dir /var/run/lookup-service
%define data_dir /var/lib/lookup-service/
%define relnum 3 

Name:           %{package_name}
Version:        0.1
Release:        %{relnum}
Summary:        Lookup Service
License:        distributable, see LICENSE
Group:          Development/Libraries
URL:            http://code.google.com/p/esnet-perfsonar
Source0:        lookup-service.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
BuildRequires:  java-1.6.0-openjdk
BuildRequires:  java-1.6.0-openjdk-devel
BuildRequires:  sed 
BuildArch:      noarch
Requires:       java-1.6.0-openjdk
Requires:       chkconfig
Requires:	mongo-10gen
%description
Lookup Service is used to find registered services. 
This package provides a server that allows clients to register and query services 
via REST interface.

%pre
/usr/sbin/groupadd lookup 2> /dev/null || :
/usr/sbin/useradd -g lookup -r -s /sbin/nologin -c "Lookup Service User" -d /tmp lookup 2> /dev/null || :

%prep
%setup -q -n lookup-service

%clean
rm -rf %{buildroot}

%build
mvn -DskipTests --projects %{mvn_project_list} clean package

%install
#Clean out previous build
rm -rf %{buildroot}

#Run install target
mvn -DskipTests --projects %{mvn_project_list} install 

#Create directory structure for build root
mkdir -p %{buildroot}/%{install_base}/target
mkdir -p %{buildroot}/%{install_base}/bin
mkdir -p %{buildroot}/%{config_base}
mkdir -p %{buildroot}/etc/init.d

#Copy jar files and scripts
cp %{package_name}/target/*.jar %{buildroot}/%{install_base}/target/
install -m 755 %{package_name}/bin/* %{buildroot}/%{install_base}/bin/
install -m 755 %{package_name}/scripts/%{package_name} %{buildroot}/etc/init.d/%{package_name}

# Copy default config file
cp %{package_name}/etc/lookupservice.yaml %{buildroot}/%{config_base}/lookup-service.yaml

#Update log locations
sed -e s,%{package_name}.log,%{log_dir}/%{package_name}.log, < %{package_name}/etc/log4j.properties > %{buildroot}/%{config_base}/log4j.properties

%post
#Create directory for PID files
mkdir -p %{run_dir}
chown lookup:lookup %{run_dir}

#Create directory for logs
mkdir -p %{log_dir}
chown lookup:lookup %{log_dir}

#Create database directory
mkdir -p %{data_dir}
chown lookup:lookup %{data_dir}

#Create symbolic links to latest version of jar files
##if update then delete old links
if [ "$1" = "2" ]; then
  unlink %{install_base}/target/%{package_name}.one-jar.jar
  unlink %{install_base}/target/%{package_name}.jar
fi
ln -s %{install_base}/target/%{package_name}-%{version}.one-jar.jar %{install_base}/target/%{package_name}.one-jar.jar
chown lookup:lookup %{install_base}/target/%{package_name}.one-jar.jar
ln -s %{install_base}/target/%{package_name}-%{version}.jar %{install_base}/target/%{package_name}.jar
chown lookup:lookup %{install_base}/target/%{package_name}.jar

#Configure service to start when machine boots
/sbin/chkconfig --add %{package_name}

%files
%defattr(-,lookup,lookup,-)
%config(noreplace) %{config_base}/*
%{install_base}/target/*
%{install_base}/bin/*
/etc/init.d/%{package_name}

%preun
if [ $1 -eq 0 ]; then
    /sbin/chkconfig --del %{package_name}
    /sbin/service %{package_name} stop
    unlink %{install_base}/target/%{package_name}.one-jar.jar
    unlink %{install_base}/target/%{package_name}.jar
fi
