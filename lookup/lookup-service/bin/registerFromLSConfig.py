from collections import defaultdict
import urllib, httplib
import json
import os


def readMap(mapFile):
    fileh=open(mapFile)
    myMap={}
    for line in fileh:
        line = line.strip()
        mapping=line.split("=>")
        myMap[mapping[0]] = mapping[1]
    return myMap


def parseData(file):
    f = open(file,'r')

    data=[]
    site=defaultdict(list)
    tagstack=[]


    #data parsing
    for line in f:
        line=line.strip()
        #End tag
        if (line.startswith('</')):
            line=line.strip('<>/')
            #print "End Tag detected"
            mytag=tagstack.pop()
        
            if(len(tagstack)<1):
                site.clear()
        
            if(len(site)>0):
                data.append(site.copy()) #cannot do data.append(site) - assignment creates binding between target and the object
            
            for k,v in site.iteritems():
                mystr = k
                if (mytag in k):
                    site[mystr] = []
        
                #Start tag        
        elif (line.startswith('<')):
            line=line.strip('<>/')
            tagstack.append(line)
            
        else:  
            mytag=''
            s=line.split()     
            if len(s)>2:
                s[1:(len(s))] = [''.join(s[1:(len(s))])] 
                             
            if(len(s)==2) and len(tagstack)>0:
                if(len(tagstack)>0): 
                    mytag = tagstack.pop()
                        
                site[mytag+'_'+s[0]].append(s[1])           
                if(mytag != ''):
                    tagstack.append(mytag)        
    f.close()
    
    newdata=[]
    for d in data:
        newdict={}
        for k,v in d.iteritems():
            newKey = k.split("_",1)
            if(len(newKey)>1):
                newdict[newKey[1]] = v
            else:
                newdict[newKey[0]]=v
        newdata.append(newdict.copy()) 
    
    data = []   
    return newdata
    
def formatData(parsedData, mapFile):
    formattedData = []
    myMapping = readMap(mapFile)
    
    newdata=[]
    for data in parsedData:
        newdict={}
        for k,v in data.iteritems():
            if myMapping.has_key(k):
                tmpKey=myMapping[k]
                newdict[tmpKey] = v
            else:
                newdict[k]=v
        newdata.append(newdict.copy())
        
    parsedData=None
                
    #some configuration may have more than one accesspoint info
    for data in newdata:
        tmp ={};
        #for now forcefully converting to single value strings
        for k,v in data.iteritems():
            tmp[k] = v[0]
        
        print myMapping[data["service-type"][0]]
        if (len(data["access-point"])>1):
            for accesspt in data["access-point"]:
                tmp["access-point"] = None
                print type(myMapping[data["service-type"][0]])
                if (myMapping[data["service-type"][0]] != "NULL"):
                    accesspoint = "tcp://" + accesspt + ":" + myMapping[data["service-type"][0]]
                    tmp["access-point"] = accesspoint
                else:
                    tmp["access-point"] = accesspt
                formattedData.append(tmp.copy())
        else:
            formattedData.append(tmp.copy())
            if (len(data["access-point"])>0) and (myMapping[data["service-type"][0]] != "NULL"):
                accesspoint = "tcp://" + data["access-point"][0] + ":" + myMapping[data["service-type"][0]]
                tmp["access-point"] = accesspoint
            
    newdata=None     
    return formattedData

myfile = os.path.join(os.path.dirname(__file__),"..","input",'ls_registration.conf')
mapFile = os.path.join(os.path.dirname(__file__),"..","input",'configdatamap.txt')
#myfile="/Users/sowmya/Documents/workspace/esnet-lookupservice/lookup-service/input/ls_registration.conf"
#mapFile="/Users/sowmya/Documents/workspace/esnet-lookupservice/lookup-service/input/configdatamap.txt"
mydata=parseData(myfile)

if(len(mydata)>0):
    fdata=formatData(mydata,mapFile)
    print len(fdata)
    
#params = json.dumps(fdata[0])
#headers = {"Content-type": "application/json", "Accept": "application/json"}
#conn = httplib.HTTPConnection("localhost:8080")
#conn.request("POST", "lookup/services", params, headers)
#response = conn.getresponse()
#print response.status, response.reason

for d in fdata:
    params = json.dumps(d)
    headers = {"Content-type": "application/json", "Accept": "application/json"}
    conn = httplib.HTTPConnection("localhost:8080")
    conn.request("POST", "lookup/services", params, headers)
    response = conn.getresponse()
    print response.status, response.reason