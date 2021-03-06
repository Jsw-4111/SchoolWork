# Produced by John Wu
# Segments of code adapted from towardsdatascience.com
# At: https://towardsdatascience.com/k-nearest-neighbor-python-2fccc47d2a55

import math
import random

data = []
scores = []
errors = []

def distance(point1, train): # 
    distances = []
    
    for fold in train:
        i = 1
        for point in fold:
            distances.append([math.sqrt((point[2] - point1[2])**2+(point[1] - point1[1])**2), point[0]])
            i = i + 1
    return distances

def getNN(point, n, train): # Takes point and finds the n nearest neighbors
    neighbors = [] # Creates an empty list to store only the neighbors we want
    distances = distance(point, train) # Finds distance between this point and all others
    distances.sort() # Sorts the distances received
    if len(distances) < n: # Make sure we aren't asking for neighbors we don't have
        n = len(distances) - 1
    for i in range(n):
        neighbors.append(distances[i]) # place neighbors into the list
    return neighbors

def classify(point, neighbors):
    classA = 0;
    classB = 0;
    for neighbor in neighbors:
        if neighbor[1] == 0:
            classA += 1;
        else:
            classB += 1;
    if classA > classB:
        point[0] = 0;
    else:
        point[0] = 1;

def validate(folds):
    for fold in folds:
        train = list(folds)
        train.remove(fold)
        prediction = list()
        true = []
        for point in fold:
            true.append(point)
            neighbors = getNN(point, 5, train)
            temp = list(point)
            classify(temp, neighbors)
            prediction.append(temp)
        checkAcc(prediction, true)
        
        
def checkAcc(prediction, true):
    correct = 0
    for i in range(len(true)):
        if true[i][0] == prediction[i][0]:
            correct += 1
    scores.append(correct/float(len(true)) * 100.0)
    print(correct/float(len(true)) * 100.0)

def nFold(n): # Takes dataset, shuffles it, and divides it into n groups
    if n != 1: # Make sure we aren't just using the data as is
        random.shuffle(data) # Shuffles dataset
        folds = []  # List for the groupings
        for i in range(n): # Make sub-lists for folds
            folds.append([])
        i = 0 # Used to keep track of which fold we're putting this data into
        for point in data: # Iterate through all points in our data
            folds[i%n].append(point) # Append points into our data
            i = i + 1
        return folds
    else:
        return data


if __name__ == "__main__":
    with open("labeled-examples") as file:
        data = file.readlines()
    
    for i in range(len(data)):
        line = data[i]
        x = line.split()
        temp = list()
        temp.append(int(x[0]))
        for j in range(1,3):
            temp.append(float(x[j]))
        data[i] = temp
    folds = nFold(5)
    validate(folds)
    avg = 0
    for score in scores:
        avg += score
    avg = avg/len(scores)
    print("The average score for our runs is: ", avg)