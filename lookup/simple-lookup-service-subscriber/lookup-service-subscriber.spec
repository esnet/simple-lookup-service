%define package_name lookup-service-subscriber
%define mvn_project_name simple-lookup-service
%define mvn_project_list %{mvn_project_name}-common,%{mvn_project_name}-client,%{mvn_project_name}-subscriber
%define install_base /opt/%{package_name}
%define config_base /etc/%{package_name}
%define log_dir /var/log/%{package_name}
%define run_dir /var/run/%{package_name}
%define init_script lookup-service-subscriber
%define apacheconf apache_sls_cache.conf
%define relnum 9

Name:           %{package_name}
Version:        2.2
Release:        %{relnum}
Summary:        Lookup Service
License:        BSD
Group:          Development/Libraries
URL:            https://github.com/esnet/simple-lookup-service
Source0:        %{mvn_project_name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)
BuildArch:      noarch
Requires:       java-openjdk >= 1.6.0
Requires:       chkconfig
Requires:       elasticsearch >= 5.0
Requires(post): httpd
%if 0%{?el7}
BuildRequires: systemd
%{?systemd_requires: %systemd_requires}
%else
Requires:		chkconfig
%endif
BuildRequires:  java-openjdk >= 1.6.0
BuildRequires:  sed
%description
Lookup Service Cache aggregates data from all lookup service nodes.

%pre
/usr/sbin/groupadd lookup 2> /dev/null || :
/usr/sbin/useradd -g lookup -r -s /sbin/nologin -c "Lookup Service User" -d /tmp lookup 2> /dev/null || :

%prep
%setup -q -n  %{mvn_project_name}

#Clean out previous build
%clean
rm -rf %{buildroot}

%build
mvn --projects %{mvn_project_list} clean

%install
#Run install target
mvn -DskipTests --projects %{mvn_project_list} install

#Create directory structure for build root
mkdir -p %{buildroot}/%{install_base}/target
mkdir -p %{buildroot}/%{install_base}/bin
mkdir -p %{buildroot}/%{config_base}
mkdir -p %{buildroot}/etc/init.d
mkdir -p %{buildroot}/etc/httpd/conf.d

#Copy jar files and scripts
cp %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/target/*.jar %{buildroot}/%{install_base}/target/
install -m 755 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/bin/* %{buildroot}/%{install_base}/bin/
install -D -m 0644 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/etc/%{apacheconf} %{buildroot}/etc/httpd/conf.d/%{apacheconf}
rm -f %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/etc/%{apacheconf}

%if 0%{?el7}
install -m 755 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/scripts/lookup-service-subscriber %{buildroot}/%{_unitdir}/%{init_script}.service
%else
install -m 755 %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/scripts/lookup-service-subscriber %{buildroot}/etc/init.d/%{init_script}
%endif

# Copy default config file
cp %{_builddir}/%{mvn_project_name}/LICENSE %{buildroot}/%{install_base}/LICENSE
#cp %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/etc/subscriber.yaml %{buildroot}/%{config_base}/subscriber.yaml
cp -r %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/etc/* %{buildroot}/%{config_base}/
#Update log locations
sed -e s,%{package_name}.log,%{log_dir}/%{package_name}.log, < %{_builddir}/%{mvn_project_name}/%{mvn_project_name}-subscriber/etc/log4j.properties > %{buildroot}/%{config_base}/log4j.properties

%post
#Create directory for PID files
mkdir -p %{run_dir}
chown lookup:lookup %{run_dir}
chmod 700 %{run_dir}

#Create directory for logs
mkdir -p %{log_dir}
chown lookup:lookup %{log_dir}


#Create symbolic links to latest version of jar files
##if update then delete old links
if [ $1 == 2 ]; then
  if [ -L %{install_base}/target/%{package_name}.one-jar.jar ]; then
      unlink %{install_base}/target/%{package_name}.one-jar.jar
  fi
fi
ln -s %{install_base}/target/%{mvn_project_name}-subscriber-%{version}-SNAPSHOT.one-jar.jar %{install_base}/target/%{package_name}.one-jar.jar
chown lookup:lookup %{install_base}/target/%{package_name}.one-jar.jar

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
    /sbin/chkconfig --del %{init_script}
    /sbin/service %{package_name} stop
    if [ -L %{install_base}/target/%{package_name}.one-jar.jar ]; then
        unlink %{install_base}/target/%{package_name}.one-jar.jar
    fi
    if [ -L %{install_base}/target/%{package_name}.one-jar.jar ]; then
        unlink %{install_base}/target/%{package_name}.one-jar.jar
    fi
fi
%endif

%files
%defattr(-,lookup,lookup,-)
%%license %{install_base}/LICENSE
%config(noreplace) %{config_base}/*
%exclude %{config_base}/%{apacheconf}
/etc/httpd/conf.d/%{apacheconf}
%{install_base}/target/*
%{install_base}/bin/*


%if 0%{?el7}
%attr(0644,root,root) %{_unitdir}/%{init_script}.service
%else
%attr(0755,lookup,lookup) /etc/init.d/%{init_script}
%endif


%changelog
* Fri Jul 13 2018 sowmya@es.net 2.2-9
- Added apache rules conf
- Updated spec file to support Centos 7
