#!/usr/bin/perl

use JSON::Parse 'json_to_perl';
use Data::Dumper;
use Net::Whois::IP qw(whoisip_query);
require LWP::UserAgent;
use Data::Validate::IP qw( is_ipv4 is_ipv6 is_public_ipv4 );
use Socket;
use POSIX qw(strftime);

my $date = strftime "%m/%d/%Y %T %Z", localtime;
print "perfSONAR LS Report started at : " , $date , "\n\n";

my $ua = LWP::UserAgent->new;
$ua->timeout(10);
$ua->env_proxy;

my $VERBOSE = 0;

my @urls = ();
my $bootstrap = "http://ps1.es.net:8096/lookup/activehosts.json";
my $response = $ua->get($bootstrap);
if ($response->is_success) {
	my $hosts = json_to_perl ($response->decoded_content);
	foreach $element ( @{ $hosts->{"hosts"} } ) {
		if ( exists $element->{"status"} and $element->{"status"} eq "alive" ) {
			push @urls, $element->{"locator"}."?type=host" if exists $element->{"locator"} and defined $element->{"locator"} ;
		}
	}
}
else {
	print "*** http://ps1.es.net:8096/lookup/activehosts.json is unreachable, using static list ***\n\n";

	# JZ: 9/4/2013
	# 	Only get all 'host' data.  A blank query returns non-host things (e.g. topology)
	#  
	@urls = (
		"http://ndb1.internet2.edu:8090/lookup/records?type=host",
		"http://ls.monipe.rnp.br:8090/lookup/records?type=host",
		"http://ps-west.es.net:8090/lookup/records?type=host",
		"http://ps-east.es.net:8090/lookup/records?type=host",
		"http://sls.geant.net:8090/lookup/records?type=host",
		"http://ps-sls.sanren.ac.za:8090/lookup/records?type=host"
	);

	# JZ: 9/4/2013
	# 	Only get 3.3x data
	#  
	#@urls = (
	#	"http://ndb1.internet2.edu:8090/lookup/records?pshost-toolkitversion=3.3*"
	#);
}

my %hosts = ();
my %versions = ();
my %domains = ();
my %tldomains = ();
my $livect = 0;
my %interfaces = ();
my $public = 0;
my $private = 0;
my %arch = ();

# special counter - try 10 times to reach a host that may be 'down', then give up (2/7/2014)
my $sc = 0;

my $otherctr = 0;
my $totalctr = 0;

foreach my $url (@urls) {

	print "Trying LS: " , $url , "\n";

	my $response = $ua->get($url);
 
	if ($response->is_success) {
		my $perl = json_to_perl ($response->decoded_content);
               	my $tcount = 0;

		foreach $element (@$perl) {

			next if ( defined $hosts{lc($element->{"host-name"}->[0])} and $hosts{lc($element->{"host-name"}->[0])} == 1 );

			# 6/5/2014 - Have to put an exception to skip the APAN host (for now)
			unless ( $url eq "http://ps1.jp.apan.net:8090/lookup/records?type=host" ) {

				my @arc = split(/\./ , $element->{"host-os-kernel"}->[0]);
				$arch{$arc[$#arc]}++;

				my $sc2 = 0;

				my $url2 = $url;
				$url2 =~ s/lookup.*//g;

				foreach my $int ( @{$element->{"host-net-interfaces"}} ) {

					my $url3 = "";
					$url3 = $url2 . $int;

					my $response2 = $ua->get($url3);
					if ($response2->is_success) {

						my $perl2 = json_to_perl ($response2->decoded_content);

						$interfaces{"mtu"}{$perl2->{"interface-mtu"}->[0]}++;
						if ($perl2->{"interface-capacity"}->[0] eq "") {
							$interfaces{"speed"}{"0"}++;
						}
						else {
							$interfaces{"speed"}{$perl2->{"interface-capacity"}->[0]}++;
						}

						foreach my $addr ( @{$perl2->{"interface-addresses"}} ) {
							if ( is_ipv4( $addr ) ) {
								$interfaces{"ipv4"}++;
								if (is_public_ipv4( $addr )) {
									$public++;
								}
								else {
									$private++;
								}
							}
							elsif ( is_ipv6( $addr ) ) {
								$interfaces{"ipv6"}++;
								$public++;
							}
							else {
								my $lookup1 = inet_aton( $addr );
								if ( $lookup1 eq undef ) {
									$otherctr;
										# this assumes the AAAA case, but I could be wrong ...
									$public++;
									next;
								}
								my $lookup = inet_ntoa( $lookup1 );
	                                                        if ( is_ipv4( $lookup ) ) {
                                                                	$interfaces{"ipv4"}++;
                                                                	if (is_public_ipv4( $lookup )) {
                                                                        	$public++;
                                                                	}
                                                                	else {
                                                        	              	$private++;
                                                	                }
                                        	                }
                                	                        elsif ( is_ipv6( $lookup ) ) {
                        	                                        $interfaces{"ipv6"}++;
                	                                                $public++;
        	                                                }
								else {
									$otherctr++;
										# anomoly
									$private++;
								}
							}
							$totalctr++;
						}
					}
					else {
						print "\tError with LS (try $sc2) '$url3': " , $response2->status_line , " - " , $response2->content , "\n";

#use Data::Dumper;
#print Dumper($element->{"uri"}) , "\n";

						my $parent = $url;
						$parent =~ s/lookup.*//g;
						$parent .= $element->{"uri"};
						print "\t\tParent: " , $parent , "\n";

						# try again? (2/7/2014)
						if ($sc2 < 1) {
							sleep(5);
							$sc2++; 
							redo;
						}
						else {
							print "\n\n";
						}
					}
				}
			}

			foreach my $community ( @{$element->{"group-communities"}} ) {
				$livect++ if $community eq "pS-NPToolkit-LiveCD";
			}

			$versions{ $element->{"pshost-toolkitversion"}->[0] }++;
			if ( is_ipv4( $element->{"host-name"}->[0] ) ) {
				my $iaddr   = Socket::inet_aton( $element->{"host-name"}->[0] );
				my $shost   = gethostbyaddr( $iaddr, Socket::AF_INET );
				if ( defined $shost ) {
					$element->{"host-name"}->[0] = lc($shost);
					print "\t" , $shost  , "\n" if $VERBOSE;
					$hosts{lc($shost)} = 1;
				}
				else {
					if ( is_public_ipv4( $element->{"host-name"}->[0] ) ) {

						my ($response,$array_of_responses) = whoisip_query( $element->{"host-name"}->[0] );

						if (defined $response->{"OrgName"}) {
							print "\t" , $element->{"host-name"}->[0] , " - " , $response->{"OrgName"} , "\n" if $VERBOSE;
							$hosts{lc($element->{"host-name"}->[0]." (".$response->{"OrgName"}.")")} = 1;
						}
						elsif (defined $response->{"netname"}) {
							print "\t" , $element->{"host-name"}->[0] , " - " , $response->{"netname"} , "\n" if $VERBOSE;
							$hosts{lc($element->{"host-name"}->[0]." (".$response->{"netname"}.")")} = 1;
						}
						elsif (defined $response->{"netname"}) {
							print "\t" , $element->{"host-name"}->[0] , " - " , $response->{"NetName"} , "\n" if $VERBOSE;
							$hosts{lc($element->{"host-name"}->[0]." (".$response->{"NetName"}.")")} = 1;
						}
						else {
							print "\t" , $element->{"host-name"}->[0] , " - UNDEFINED\n" if $VERBOSE;
							$hosts{lc($element->{"host-name"}->[0]." (UNDEFINED)")} = 1;
						}
					}
					else {
						print "\t" , $element->{"host-name"}->[0] , " - PRIVATE\n" if $VERBOSE;
						$hosts{lc($element->{"host-name"}->[0]." (PRIVATE)")} = 1;
					}

				}
			}
			elsif ( is_ipv6( $element->{"host-name"}->[0] ) ) {
				print "\t" , $element->{"host-name"}->[0] , "\n" if $VERBOSE;
				$hosts{lc($element->{"host-name"}->[0])} = 1;
			}
			else {
				print "\t" , $element->{"host-name"}->[0] , "\n" if $VERBOSE;
				$hosts{lc($element->{"host-name"}->[0])} = 1;
			}

			unless ( is_ipv6( $element->{"host-name"}->[0] ) or is_ipv4( $element->{"host-name"}->[0] ) ) {
	 			my @dmn = split(/\./, $element->{"host-name"}->[0] );
				if ( lc($dmn[$#dmn]) eq "edu" or lc($dmn[$#dmn]) eq "org" or lc($dmn[$#dmn]) eq "net" or lc($dmn[$#dmn]) eq "gov" or lc($dmn[$#dmn]) eq "com" ) {
 					my $tld = lc(pop(@dmn));
	 				my $bd = lc(pop(@dmn));
					if ( $tld and $bd ) {
 						$domains{join("\.", ( $bd, $tld ) )}++;
 						$tldomains{$tld}++;
					}
					else {
						$domains{"?"}++;
					}
				}
				elsif ( $#dmn >= 3 ) {
 					my $tld = lc(pop(@dmn));
	 				my $bd = lc(pop(@dmn));
	 				my $sd = lc(pop(@dmn));
					if ( $tld and $bd and $sd ) {
 						$domains{join("\.", ( $sd, $bd, $tld ) )}++;
 						$tldomains{$tld}++;
					}
					else {
						$domains{"?"}++;
					}
				}
				else {
 					my $tld = lc(pop(@dmn));
	 				my $bd = lc(pop(@dmn));
					if ( $tld and $bd ) {
 						$domains{join("\.", ( $bd, $tld ) )}++;
 						$tldomains{$tld}++;
					}
					else {
						$domains{"?"}++;
					}
				}
			}

		       $tcount++;
		}
		print "\tTotal: " , $tcount , "\n";
	}
	else {
		print "\tError with LS '$url': " , $response->status_line , " - " , $response->content;
		# try again? (2/7/2014)
		if ($sc < 2) {
			sleep(30);
			$sc++; 
			redo;
		}
	}
}

print "\nSummary of LS Registration TL Domains:\n\n";

foreach my $d ( sort keys %tldomains ) {
 	print "\t" , $d , "\t-\t" , $tldomains{$d} , "\n";
}

print "\nSummary of LS Registration Domains:\n\n";

print "\t" , join( "\n\t", sort {
	$a = lc( $a );
	$b = lc( $b );
	if ( $a eq $b ) {
		return 0;
	}
	my @a = reverse( split( /\./, $a ) );
	my @b = reverse( split( /\./, $ b ) );
	my $max = ( scalar( @a ), scalar( @b ) )[@a < @b];
	for ( my $i=0; $i < $max; $i++ ) {
		if ( ( $i < @a ) && ( $i < @b ) ) {
			if ( my $c = $a[$i] cmp $b[$i] ) {
				return $c;
			}
		}
		else {
			return scalar( @a ) <=> scalar( @b );
		}
	}
	return 0;
} keys %domains ) . "\n";

my $dcounter = 0;
foreach my $d ( sort keys %domains ) {
	$dcounter++;
}

print "Total Domains: ", $dcounter , "\n\n";

print "\nSummary of LS Registration Hosts:\n\n";

print "\t" , join( "\n\t", sort {
	$a = lc( $a );
	$b = lc( $b );
	if ( $a eq $b ) {
		return 0;
	}
	my @a = reverse( split( /\./, $a ) );
	my @b = reverse( split( /\./, $ b ) );
	my $max = ( scalar( @a ), scalar( @b ) )[@a < @b];
	for ( my $i=0; $i < $max; $i++ ) {
		if ( ( $i < @a ) && ( $i < @b ) ) {
			if ( my $c = $a[$i] cmp $b[$i] ) {
				return $c;
			}
		}
		else {
			return scalar( @a ) <=> scalar( @b );
		}
	}
	return 0;
} keys %hosts ) . "\n";

my $counter = 0;
foreach my $h ( sort keys %hosts ) {
	$counter++;
}
print "\nTotal Hosts: ", $counter , "\n";

print "\nSummary of Architectures:\n\n";

foreach my $a ( sort keys %arch ) {
	print "\t" , $a , "\t-\t" , $arch{$a} , "\n";
}

print "\nSummary of LS Registration Versions:\n\n";

$versions{"Live"} = $livect;
foreach my $v ( sort keys %versions ) {
	print "\t" , $v , "\t-\t" , $versions{$v} , "\t-\t";
       printf("%.3f %\n", ($versions{$v}/$counter)*100 );     
	$counter++;
}

print "\nPublic vs. Private:\n\n";
print "\tPublic Addresses: " , ($public/($public+$private))*100 , " %\n";
print "\tPrivate Addresses: " , ($private/($public+$private))*100 , " %\n";

print "\nSummary of Interfaces:\n\n";

printf("\t%.3f", (($interfaces{"ipv4"}+$otherctr)/$totalctr)*100);
print " % of hosts have an IPV4 Address\n";
printf("\t%.3f", ($interfaces{"ipv6"}/$totalctr)*100);
print " % of hosts have an IPV6 Address\n\n";

my $t2 = 0;
foreach my $m ( keys(%{$interfaces{"mtu"}}) ) {
	$t2 += $interfaces{"mtu"}{$m};
}
foreach my $m ( keys(%{$interfaces{"mtu"}}) ) {
	printf("\tMTU of \"$m\" is seen %.3f", ($interfaces{"mtu"}{$m}/$t2)*100 );
        print " % of the time\n";
}
print "\n";

my %lookup = ();
$lookup{"40000000000"} = "40Gbps";
$lookup{"20000000000"} = "20Gbps";
$lookup{"10000000000"} = "10Gbps";
$lookup{"1000000000"} = "1Gbps";
$lookup{"2000000000"} = "2Gbps";
$lookup{"100000000"} = "100Mbps";
$lookup{"10000000"} = "10Mbps";
$lookup{"0"} = "undefined";

my $t3 = 0;
	foreach my $s ( keys(%{$interfaces{"speed"}}) ) {
	$t3 += $interfaces{"speed"}{$s};
}
foreach my $s ( keys(%{$interfaces{"speed"}}) ) {
	printf("\tSpeed of \"$lookup{$s}\" is seen %.3f", ($interfaces{"speed"}{$s}/$t3)*100 );
        print " % of the time\n";
}
print "\n";

$date = strftime "%m/%d/%Y %T %Z", localtime;
print "perfSONAR LS Report completed at : " , $date , "\n\n";

exit;

