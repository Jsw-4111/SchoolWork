import math
import random

data = []
scores = []
errors = []
corrections = []
correct = 0

def distance(point1, train): # 
    distances = []
    
    for fold in train:
        for point in fold:
            distances.append([math.sqrt((point[2] - point1[2])**2+(point[1] - point1[1])**2), point[0]])
    for point in corrections:
        distances.append([math.sqrt((point[2] - point1[2])**2+(point[1] - point1[1])**2), point[0]])
    return distances

def getNN(point, train, n): # Takes point and finds the n nearest neighbors
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

def validate(folds, file, n, k):
    foldMaxElements = 1000/(n) + 1
    for fold in folds:
        train = list(folds)
        train.remove(fold)
        prediction = list()
        true = []
        notDone = True
        global correct
        correct = 0
        count = 0
        while notDone and count + k < foldMaxElements:
            temp = file.readline()
            if temp == "":
                notDone = False
            else:
                point = temp.split()
                point[0] = int(point[0])
                for i in range(1, 3):
                    point[i] = float(point[i])
                true = list(point)
                neighbors = getNN(point, train, n)
                prediction = list(point)
                classify(prediction, neighbors)
                checkAcc(prediction, true)
                count += 1
        scores.append(correct/float(count) * 100.0)
        print(correct/float(count) * 100.0)
        
        
def checkAcc(prediction, true):
    global correct
    if true[0] == prediction[0]:
        correct += 1
    else:
        errors.append(prediction)
        corrections.append(true)

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
    n = 5 # Number of folds
    k = 5 # Number of neighbors
    temp = list()
    file = open("labeled-examples")
    for i in range(n*k):
        x = file.readline()
        data.append(x.split())
        data[i][0] = int(data[i][0])
        for j in range(1, 3):
            data[i][j] = float(data[i][j])
    folds = nFold(n)
    validate(folds, file, n, k)
    avg = 0
    for score in scores:
        avg += score
    avg = avg/len(scores)
    print("The average score for our runs is: " + str(avg))