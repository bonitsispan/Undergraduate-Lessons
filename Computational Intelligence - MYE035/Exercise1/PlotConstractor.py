"""
    This programm is responsible for creating the plots from the error.txt,trainingSet.txt,wrong.txt,checkSet.txt.
"""
import matplotlib.pyplot as plt
import os

x1coordinateC1 = []
x2coordinateC1 = []

x1coordinateC2 = []
x2coordinateC2 = []

x1coordinateC3 = []
x2coordinateC3 = []

x1coordinateC1TS = []
x2coordinateC1TS = []

x1coordinateC2TS= []
x2coordinateC2TS= []

x1coordinateC3TS= []
x2coordinateC3TS= []

x1coordinateC1CS = []
x2coordinateC1CS = []

x1coordinateC2CS= []
x2coordinateC2CS= []

x1coordinateC3CS= []
x2coordinateC3CS= []

x1coordinateC1O = []
x2coordinateC1O= []

x1coordinateC2O= []
x2coordinateC2O= []

x1coordinateC3O= []
x2coordinateC3O= []

x1coordinateWC1= []
x2coordinateWC1= []

x1coordinateWC2= []
x2coordinateWC2= []

x1coordinateWC3= []
x2coordinateWC3= []

error=[]
age=[]

for line in open('trainingSet.txt', 'r'):
    lines = [i for i in line.split()]

    if(lines[2]=="C1"):
        x1coordinateC1.append(float(lines[0]))
        x2coordinateC1.append(float(lines[1]))
        x1coordinateC1TS.append(float(lines[0]))
        x2coordinateC1TS.append(float(lines[1]))

    if(lines[2]=="C2"):
        x1coordinateC2.append(float(lines[0]))
        x2coordinateC2.append(float(lines[1]))
        x1coordinateC2TS.append(float(lines[0]))
        x2coordinateC2TS.append(float(lines[1]))

    if(lines[2]=="C3"):
        x1coordinateC3.append(float(lines[0]))
        x2coordinateC3.append(float(lines[1]))
        x1coordinateC3TS.append(float(lines[0]))
        x2coordinateC3TS.append(float(lines[1]))

for line in open('checkSet.txt', 'r'):
    lines = [i for i in line.split()]

    if(lines[2]=="C1"):
        x1coordinateC1.append(float(lines[0]))
        x2coordinateC1.append(float(lines[1]))
        x1coordinateC1CS.append(float(lines[0]))
        x2coordinateC1CS.append(float(lines[1]))


    if(lines[2]=="C2"):
        x1coordinateC2.append(float(lines[0]))
        x2coordinateC2.append(float(lines[1]))
        x1coordinateC2CS.append(float(lines[0]))
        x2coordinateC2CS.append(float(lines[1]))

    if(lines[2]=="C3"):
        x1coordinateC3.append(float(lines[0]))
        x2coordinateC3.append(float(lines[1]))
        x1coordinateC3CS.append(float(lines[0]))
        x2coordinateC3CS.append(float(lines[1]))

for line in open('output.txt', 'r'):
    lines = [i for i in line.split()]

    if(lines[2]=="C1"):
        x1coordinateC1O.append(float(lines[0]))
        x2coordinateC1O.append(float(lines[1]))

    if(lines[2]=="C2"):
        x1coordinateC2O.append(float(lines[0]))
        x2coordinateC2O.append(float(lines[1]))

    if(lines[2]=="C3"):
        x1coordinateC3O.append(float(lines[0]))
        x2coordinateC3O.append(float(lines[1]))

for line in open('wrong.txt', 'r'):
    lines = [i for i in line.split()]

    if(lines[2]=="C1"):
        x1coordinateWC1.append(float(lines[0]))
        x2coordinateWC1.append(float(lines[1]))

    if(lines[2]=="C2"):
        x1coordinateWC2.append(float(lines[0]))
        x2coordinateWC2.append(float(lines[1]))

    if(lines[2]=="C3"):
        x1coordinateWC3.append(float(lines[0]))
        x2coordinateWC3.append(float(lines[1]))



plt.figure(figsize=(8,8))
for line in open('error.txt', 'r'):
    lines = [i for i in line.split()]
    age.append(int(lines[0]))
    error.append(float(lines[1]))
plt.title("Error")
plt.xlabel("Age")
plt.ylabel("Error")
plt.plot(age,error)
plt.savefig("Error.png")
plt.show()

plt.figure(2)
plt.figure(figsize=(16, 8))
plt.subplot(1,2,1)
plt.scatter(x1coordinateC1,x2coordinateC1,marker='+',s=40,color="purple")
plt.scatter(x1coordinateC2,x2coordinateC2,marker='+',s=40,color="green")
plt.scatter(x1coordinateC3,x2coordinateC3,marker='+',s=40,color="red")
plt.title('Training Set And Check Set')

plt.subplot(1,2,2)
plt.scatter(x1coordinateC1TS,x2coordinateC1TS,marker='+',s=40,color="purple")
plt.scatter(x1coordinateC2TS,x2coordinateC2TS,marker='+',s=40,color="green")
plt.scatter(x1coordinateC3TS,x2coordinateC3TS,marker='+',s=40,color="red")
plt.title('Training Set')

plt.savefig("TC.png")
plt.show()

plt.figure(3)   #new figure.
plt.figure(figsize=(16, 8))
plt.subplot(1,2,1)
plt.scatter(x1coordinateC1CS,x2coordinateC1CS,marker='+',s=40,color="purple")
plt.scatter(x1coordinateC2CS,x2coordinateC2CS,marker='+',s=40,color="green")
plt.scatter(x1coordinateC3CS,x2coordinateC3CS,marker='+',s=40,color="red")
plt.title('Check Set')

plt.subplot(1,2,2)
plt.scatter(x1coordinateC1O,x2coordinateC1O,marker='+',s=40,color="purple")
plt.scatter(x1coordinateC2O,x2coordinateC2O,marker='+',s=40,color="green")
plt.scatter(x1coordinateC3O,x2coordinateC3O,marker='+',s=40,color="red")
plt.scatter(x1coordinateWC1,x2coordinateWC1,marker='*',s=20,color="black")
plt.scatter(x1coordinateWC2,x2coordinateWC2,marker='*',s=20,color="black")
plt.scatter(x1coordinateWC3,x2coordinateWC3,marker='*',s=20,color="black")
plt.title('Output')

plt.savefig("CO.png")
plt.show()
os.remove("wrong.txt")
os.remove("output.txt")
