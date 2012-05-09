import urllib2
import urllib,httplib
import os
import tarfile
from shutil import rmtree
import json
import codecs

#serviceFiles = ["bwctl","owamp","ndt","npad","ping","pinger","psb.bwctl","psb.owamp","snmpma","traceroute","traceroute_ma","phoebus","gridftp","hls"]
attributes = ["access-point", "description", "service-type","service-location","domain","timestamp"]
#retrieve cache file
u = urllib2.urlopen(' http://ps1.es.net:8096/cache.tgz')
myfile=os.path.join(os.path.dirname(__file__),"..","input",'lscache.tgz')
localFile = open(myfile, 'w')
localFile.write(u.read())
localFile.close()

#extract contents
tar = tarfile.open(myfile, "r:gz")
dir = os.path.join(os.path.dirname(__file__),"..","input","tmp")
tar.extractall(dir)


#path = 'sequences/'
fdata = []
listing = os.listdir(dir)
count=0
filesprocessed=0
for infile in listing:
    count = count+1
    #print "current file is: " + infile
    #if any (infile in "list."+s for s in serviceFiles):
    fullpath = os.path.join(dir,infile)
    f = open(fullpath,'r')
    for line in f:
        tmpDict = {}
        line=line.strip()
        #print line
        s= line.split("|")
        #print s
        #keywordValues=[]
        keywordValues=""
        if(len(s)==6):
            keywords = s[4].split(",")
            k=keywords[0].split(":")
            if (len(k)==2):
                keywordValues = k[1]
            else:
                keywordValues=k[0]
            #commenting out till LS can support lists
            #for keyword in keywords:
             #   k = keyword.split(":")
              #  if(len(k)==2):
               #     keywordValues.append(k[1])
               # else:
                #    keywordValues.append(k[0]))
        else:
            break;
        if (len(s) == len(attributes)):
            for i in range(len(s)-1):
                if(i==4):
                    if (keywordValues):
                        tmpDict[attributes[i]] = str(keywordValues)
                else:
                    tmpDict[attributes[i]] = s[i]
        
        else:
            break;

        fdata.append(tmpDict)
    f.close()
#delete directory
rmtree(dir)

print len(fdata)
print filesprocessed
print count

for d in fdata:
    print d
    # This hack is for data from Brazilians which does not get decoded by the default utf8
    try:
        params = json.dumps(d)
    except UnicodeDecodeError:
        params = json.dumps(d,encoding="cp860")
    #print params
    headers = {"Content-type": "application/json", "Accept": "application/json"}
    conn = httplib.HTTPConnection("ps4.es.net:8085")
    conn.request("POST", "lookup/services", params, headers)
    response = conn.getresponse()
    #print response.status, response.reason

#print "Done"