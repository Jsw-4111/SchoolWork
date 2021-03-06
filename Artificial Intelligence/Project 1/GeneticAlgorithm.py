from random import *
import ga_eval
import ga_util
import langermann_params

probability = 0.0
tooLong = 0

def geneticAlgo(pop, fitness):
    global tooLong
    notDone = True
    best = 0
    time = 0
    bestSet = []
    while(notDone):
        newPop = list()
        for i in range(len(pop)):
            x = randomSelect(pop) # Get 2 random individuals from our population
            y = randomSelect(pop)
            child = mate(x, y) # Mate the 2 random individuals to produce two children
            if uniform(0, 1) < probability: # Some chance for child to mutate
                mutate(child)
            newPop.append(child[0])
            newPop.append(child[1])
            time += 1
        pop = list(newPop)
        for element in pop:
            eval = fitEval(element, fitness)
            if eval > best:
                best = eval
                bestSet = element
            if time > tooLong:
                print("Time expired, best result is ", best)
                notDone = False
                break

def fitEval(element, fitness):
    return fitness(element)

def randomSelect(pop): # Here we select random individuals from our population and calculate their fitness
    index = randrange(len(pop))
    return pop[index]


def mate(x, y): # Here we create offspring that inherits a random amount of elements from its parents
    separator = randrange(2, len(x)) # Make a separation randomly in the elements, start at index 2 so we at least keep one element from parent 1
    child = []
    for childNum in range(2):
        temp = [] # used to hold individual children
        for i in range(len(x)):
            if(i < separator):
                if(len(child) == 0): # Check if current child is first or second
                    temp.append(x[i]) # Append first parent's left side
                elif(len(child) == 1):
                    temp.append(y[i]) # Append second parent's left side
            else:
                if(len(child) == 0):
                    temp.append(y[i]) # Append first parent's right side
                elif(len(child) == 1):
                    temp.append(x[i]) # Append second parent's right side
        child.append(temp) # Finished creating child, append it to list of children
    return child

def mutate(children): # Here we mutate our children randomly
    for child in children:
        mutationChance = randrange(0.0, 1.0)
        if(mutationChance < probability):
            index = randrange(len(child))
            change = uniform(0, 1) * 10 # Change this based on the bounds of your function

if __name__ == "__main__":
    probability = .99
    tooLong = 15000
    pop = langermann_params.sample_a
    c = ga_eval
    geneticAlgo(pop, c.langermann)

