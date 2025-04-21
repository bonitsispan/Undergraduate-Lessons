"""
This programm is responsible for plotting the points from trainingSet.txt and centroids.txt.
"""
import os
import matplotlib.pyplot as plt


xCoordinate=[]
yCoordinate=[]

for line in open('trainingSet.txt','r'):    #reading the file.
    lines=[i for i in line.split()]
    xCoordinate.append(float(lines[0]))     #add the xCoordinates to the xCoordinate list.
    yCoordinate.append(float(lines[1]))     #add the yCoordinates to the yCoordinate list.

xCoordinateC3=[]
yCoordinateC3=[]

for line in open('centroids3.txt','r'):  #reading the file.
    lines=[i for i in line.split()]
    xCoordinateC3.append(float(lines[0]))    #add the xCoordinates to the xCoordinate list.
    yCoordinateC3.append(float(lines[1]))    #add the yCoordinates to the yCoordinate list.

xCoordinateC6=[]
yCoordinateC6=[]

for line in open('centroids6.txt','r'):  #reading the file.
    lines=[i for i in line.split()]
    xCoordinateC6.append(float(lines[0]))    #add the xCoordinates to the xCoordinate list.
    yCoordinateC6.append(float(lines[1]))    #add the yCoordinates to the yCoordinate list.


xCoordinateC9=[]
yCoordinateC9=[]

for line in open('centroids9.txt','r'):  #reading the file.
    lines=[i for i in line.split()]
    xCoordinateC9.append(float(lines[0]))    #add the xCoordinates to the xCoordinate list.
    yCoordinateC9.append(float(lines[1]))    #add the yCoordinates to the yCoordinate list.


xCoordinateC12=[]
yCoordinateC12=[]

for line in open('centroids12.txt','r'):  #reading the file.
    lines=[i for i in line.split()]
    xCoordinateC12.append(float(lines[0]))    #add the xCoordinates to the xCoordinate list.
    yCoordinateC12.append(float(lines[1]))    #add the yCoordinates to the yCoordinate list.


fig, axs = plt.subplots(2, 2)
fig.set_size_inches(16, 8.5)

axs[0,0].scatter(xCoordinate,yCoordinate,marker='+',s=50)    #plot the trainingSet.
axs[0,0].scatter(xCoordinateC3,yCoordinateC3,marker='*',s=30,color="red"); #plot the cetroids.
axs[0,0].set_title('K-Means 3 Clusters')


axs[0,1].scatter(xCoordinate,yCoordinate,marker='+',s=50)    #plot the trainingSet.
axs[0,1].scatter(xCoordinateC6,yCoordinateC6,marker='*',s=30,color="red"); #plot the cetroids.
axs[0,1].set_title('K-Means 6 Clusters')

axs[1,0].scatter(xCoordinate,yCoordinate,marker='+',s=50)    #plot the trainingSet.
axs[1,0].scatter(xCoordinateC9,yCoordinateC9,marker='*',s=30,color="red"); #plot the cetroids.
axs[1,0].set_title('K-Means 9 Clusters')


axs[1,1].scatter(xCoordinate,yCoordinate,marker='+',s=50)    #plot the trainingSet.
axs[1,1].scatter(xCoordinateC12,yCoordinateC12,marker='*',s=30,color="red"); #plot the cetroids.
axs[1,1].set_title('K-Means 12 Clusters')

fig.savefig("result.png")

plt.figure(2)   #new figure.
error = []
cluster= []
for line in open('errors.txt', 'r'):    #read the file.
    lines = [i for i in line.split()]
    cluster.append(float(lines[1])) #add the cluster to the cluser List.
    error.append(float(lines[0]))   #add the error to the error List.

plt.title("SSE")    #Name of the plot.
plt.xlabel('Cluster')
plt.ylabel('SSE')
plt.plot(cluster, error, marker = 'o', c = 'g')
plt.show()
plt.savefig("error.png")

#delete all the files.
#os.remove("centroids3.txt")
#os.remove("centroids6.txt")
#os.remove("centroids9.txt")
#os.remove("centroids12.txt")
#os.remove("trainingSet.txt")
#os.remove("errors.txt")
