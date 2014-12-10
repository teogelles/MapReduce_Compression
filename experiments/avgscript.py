def getAvgTime(timesTuples):

    totTime = 0
    for (minutes, seconds) in timesTuples:
        totTime += (minutes*60 + seconds)

    avgTime = totTime/4
    numMin = int(avgTime/60)
    numSec = float(avgTime - (numMin*60))

    numMinStr = str(numMin)
    numSecStr = "%.3f" %numSec
    
    avgTimeStr = numMinStr + "m" + numSecStr + "s"
    avgTimeStrSecs = "%.3f" %avgTime
    
    return (avgTimeStr, avgTimeStrSecs)
    
filename = "results.txt"
resultsFile = open(filename)

for line in resultsFile:
    parts = line.split()
    if (len(parts) == 0):
        continue
    if (parts[0] != "Time:"):
        continue

    times = [parts[1], parts[3], parts[5], parts[7]]
    timesTuples = []
    for i in range(len(times)):
        timeparts = times[i].split('m')
        timeparts[1] = timeparts[1][:-1]
        timesTuples.append((float(timeparts[0]),
                            float(timeparts[1])))
    avgTime = getAvgTime(timesTuples)
    print(avgTime)
