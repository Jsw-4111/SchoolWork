from random import *
import math
import langermann_params
import ga_eval

# This simulated annealing program will take arguments for a knapsack problem of the following format:
    # item = [value, weight]
    # value = x
    # weight = y
# It will return the collection of items with the highest value while keeping the weight below a maximum:
    # sack = [item1, ... , itemn]


probVolatility = 0
sack = []
sackVal = 0
bestSack = []
bestSackVal = 0
items = []
weightMax = 0
temperature = 0
tempMax = 0

def anneal():
    global bestSack, sackVal, bestSackVal, probVolatility
    notDone = True
    val = 0
    while notDone:
        if(len(sack) == len(items)):
            print("The best sack contains all the items, valued at ", sackVal)
        nextNotTaken = True
        count = 0
        if sackVal > bestSackVal:
            bestSack = list(sack)
            bestSackVal = sackVal + 0.0
        while nextNotTaken:
            next = getSuccessor()
            val = assessSack(next)
            nextNotTaken = nextTake(val)
            if nextNotTaken == False:
                sackVal = val
                randPoint = next
            probVolatility += 1
            schedule()
            if temperature <= 0: # We have rejected any further lines so we must be at the global maxima.
                notDone = False
                nextNotTaken = False
                print("The best sack is ", bestSack, " valued at ", bestSackVal)

def schedule():
    global temperature
    temperature = temperature - (probVolatility**2)/5000

# This function determines whether or not we take the next point
def nextTake(next):
    global temperature
    if next < sackVal:
        if randrange(0, 1) <= math.exp((sackVal - next)/temperature):
            return True
        return False
    else:
        return True

def getSuccessor():
    x = []
    weight = 0
    notfull = True
    while(weight < weightMax and notfull):
        index = randrange(len(items))
        if items[index] in x:
            if len(x) != len(items):
                return x
            pass # We don't want repeats, run it again
        elif items[index][0] + weight > 50:
            for i in range(index-1, -1, -1): # We don't want to go overweight, go backwards through the sorted list
                if items[i][0] + weight == 50:
                    x.append(items[i])
                    break
        else:
            x.append(items[index])
            weight += items[index][1]
    return x

def createItems():
    itemNum = randrange(0, 100)
    print("There are a total of ", itemNum, " items to choose from\n")
    print("Each item has a value of up to 10, and a weight of up to 5 \n")
    print("We are only allowed to take up to ", weightMax, " in weight")
    for i in range(itemNum):
        temp = []
        temp.append(randrange(1,10)) # This is the value
        temp.append(randrange(1,10)) # This is the weight
        items.append(temp)
    sack = [len(items)]
    sorted(items, key=lambda x: (x[1], x[0]))

def assessSack(given):
    sum = 0
    for i in range(len(given)):
        sum += given[i][0]
    return sum


if __name__ == "__main__":
    
    tempMax = 500
    temperature = tempMax
    weightMax = 50
    createItems()
    # rangeMin = 0.0
    # absMin = rangeMin + 0.0
    # rangeMax = 800.0
    # absMax = rangeMax + 0.0
    # x = randrange(rangeMin, rangeMax)
    # funcList = [-1.0*10**(-12), 3.0*10**(-9), -3.0*10**(-6), 0.0012, -0.2173, 12.632, 669.62]
    # point = [function(x), x]
    sack = getSuccessor()
    sackVal = assessSack(sack)
    anneal()
    print(items)