import json
import math
import random
import datetime

# Make a program that can solve general CSPs such as Sudoku and Map coloring
    # Program should be able to color nodes in such a way that no two nodes are touching
    # Program should be able to place numbers in such a way that no element is repeated in a column, row, or region

def sudokuPopulate():
    with open('sudoku.json', 'r') as f:
        board = json.load(f)
    print("Attempting to solve a sudoku puzzle of form: ")
    for i in range(len(board)):
        print(board[i])
    answer = [] # potential answers taking the form of [i, j, <potential solutions>]
    boardCols = []
    for i in range(len(board)): # Creates a column major board, for ease of use and readability
        temp = []
        for j in range(len(board)):
            temp.append(board[j][i])
            if board[j][i] == 0:
                answer.append([j, i])
        boardCols.append(temp)
    return board, answer, boardCols

def sudokuDFS(board, answer, boardCols, heuristic):
    n = int(len(board)**.5)
    unsolved = True
    hole = 0 # This integer keeps track of which hole we are referencing
    while(unsolved):
        i = 1
        while i <= 10 and len(answer[hole]) < 3: # Iterate through possible values or until potential solution found
            x = answer[hole][0]
            y = answer[hole][1]
            if i not in board[x] and i not in boardCols[y]:
                rowStart = int((x//n)*n)
                colStart = int((y//n)*n)
                if i not in board[rowStart:rowStart+n][colStart:colStart+n]:
                    answer[hole].append(i)
                    board[x][y] = i
                    boardCols[y][x] = i
            elif i == 9: # We've gone through all possible values and it still didn't work, lets backtrack
                board[x][y]
                hole -= 1
                x = answer[hole][0]
                y = answer[hole][1]
                answer[hole] = [x, y]
                i = board[x][y]
            i += 1
        hole += 1
        if hole == len(answer):
            unsolved = False
    return board

def sudokuForward(board, answer, boardCols, heuristic):
    n = int(len(board)**.5)
    unsolved = True
    while(unsolved):
        if heuristic == 1:
            answer.sort(key=len)
        elif heuristic == 2:
            answer.sort(key=lambda x: (len(x), sudokuInfluence(x, n, board)))
        for potentials in list(answer):
            for i in range(1, len(board) + 1): # Start at 1 and end at 10 because we cannot have a 0 value in sudoku and want to go up to 9
                if i not in board[potentials[0]] and i not in boardCols[potentials[1]] and i not in potentials[2:]:
                    potentials.append(i)
                elif i in potentials[2:] and (i in board[potentials[0]] or i in boardCols[potentials[1]]):
                    tempIndex = 0
                    tempIndex = potentials.index(i, 2)
                    del potentials[tempIndex]
            # These variables will be used for selecting our region
            # Divide by integer so that our result truncates, then multiply by n to get the index that we start at
            rowStart = int((potentials[0]//n)*n)
            colStart = int((potentials[1]//n)*n)
            for i in range(rowStart, rowStart + n):
                for j in range(colStart, colStart + n):
                    if board[i][j] in potentials[2:]: # See if the value is in our list of potentials excluding the indices
                        tempIndex = potentials.index(board[i][j], 2)
                        del potentials[tempIndex]
            if len(potentials) == 3: # If by now there is only 1 potential answer, then we have our answer for this cell
                board[potentials[0]][potentials[1]] = potentials[2]
                boardCols[potentials[1]][potentials[0]] = potentials[2]
                answer.remove(potentials)
        if len(answer) == 0:
            unsolved = False
    return board

def sudokuAC3(board, answer, boardCols, heuristic):
    unsolved = True
    # Here we create a while loop that populates our answer board with potential solutions
    # We will eliminate our potentials based on our constraints and accept guaranteed solutions
    n = int(len(board)**.5)
    while(unsolved):
        if heuristic == 1:
            answer.sort(key=len)
        elif heuristic == 2:
            answer.sort(key=lambda x: (len(x), sudokuInfluence(x, n, board)))
        for potentials in list(answer):
            for i in range(1, len(board) + 1): # Start at 1 and end at 10 because we cannot have a 0 value in sudoku and want to go up to 9
                if i not in board[potentials[0]] and i not in boardCols[potentials[1]] and i not in potentials[2:]:
                    potentials.append(i)
                elif i in potentials[2:] and (i in board[potentials[0]] or i in boardCols[potentials[1]]):
                    tempIndex = 0
                    tempIndex = potentials.index(i, 2)
                    del potentials[tempIndex]
            # These variables will be used for selecting our region
            # Divide by integer so that our result truncates, then multiply by n to get the index that we start at
            rowStart = int((potentials[0]//n)*n)
            colStart = int((potentials[1]//n)*n)
            for i in range(rowStart, rowStart + n):
                for j in range(colStart, colStart + n):
                    if board[i][j] in potentials[2:]: # See if the value is in our list of potentials excluding the indices
                        tempIndex = potentials.index(board[i][j], 2)
                        del potentials[tempIndex]
            if len(potentials) == 3: # If by now there is only 1 potential answer, then we have our answer for this cell
                board[potentials[0]][potentials[1]] = potentials[2]
                boardCols[potentials[1]][potentials[0]] = potentials[2]
                answer.remove(potentials)
        if len(answer) == 0:
            unsolved = False
    return board

def sudokuInfluence(x, n, board):
    count = 0
    for i in range(len(board)):
        if board[x[0]][i] == 0:
            count += 1
        if boardCols[x[1]][i] == 0:
            count += 1
    rowStart = int((x[0]//n)*n)
    colStart = int((x[1]//n)*n)
    for i in range(rowStart, rowStart + n):
        for j in range(colStart, colStart + n):
            if board[i][j] == 0:
                count += 1
    return count
            
# For my map coloring solutions, I enumerate arbitrary colors from index 1 to n. The colors can be decided to the user's discretion.

def mapColorPopulate():
    with open('gcp.json', 'r') as f:
        data = json.load(f)
    numpoints = data.get("num_points")
    cities = data.get("points")
    edges = data.get("edges")
    solution = {} # A dictionary of cities in the format of "city" : {"Color":[<color>], "Neighbors":[<neighbors>]} 
    for city in cities:
        solution[city] = {"Color":[], "Neighbors":[]}
    for edge in edges:
        if edge[0] not in solution[str(edge[0])]["Neighbors"]:
            solution[str(edge[0])]["Neighbors"].append(str(edge[1]))
    return solution

def mapColorDFS(solution, heuristic):
    firstEl = list(solution.keys())[0]
    solution[firstEl]["Color"] = [0]
    unmatchedCities = list(solution.keys())[1:]
    if heuristic == 0:
        random.shuffle(unmatchedCities)
    unavailableColors = []
    for i in range(len(unmatchedCities)):
        unavailableColors.append([])
    i = 0
    while i < len(unmatchedCities):
        city = unmatchedCities[i]
        ct = int(city)
        if city != firstEl:
            notDone = True
        else:
            notDone = False
        j = 0
        neighbors = solution[city]["Neighbors"]
        while(notDone):
            solution[city]["Color"] = [j]
            notTaken = True
            neighbor = 0
            while neighbor <= len(neighbors)-1 and notTaken:
                if j in solution[neighbors[neighbor]]["Color"]:
                    j += 1
                    solution[city]["Color"] = [j]
                    notTaken = False # End the for loop if this color is taken
                elif neighbor == len(neighbors)-1:
                    notDone = False
                neighbor += 1
        i += 1
    # for city in solution:
    #     print(city, " is of color ", solution[city]["Color"])

def mapColorForward(solution, heuristic):
    firstEl = list(solution.keys())[0]
    numColors = 1
    solution[firstEl]["Color"] = [0]
    unavailableColors = {}
    for city in solution:
        unavailableColors[city] = {"Unavailable" : []}
    for neighbor in solution[firstEl]["Neighbors"]:
        unavailableColors[neighbor]["Unavailable"].append(0)
    notDone = True
    complete = 0
    cities = list(solution.keys())[1:]
    if heuristic == 0:
        random.shuffle(cities)
    while notDone:
        if heuristic == 1:
            cities.sort(key = lambda x : (-mapColorMRV(x, unavailableColors)))
        elif heuristic == 2:
            cities.sort(key = lambda x : (-mapColorMRV(x, unavailableColors), mapColorInfluence(x, solution)))
        for city in cities[:]:
            if len(solution[city]["Color"]) != 1:
                i = 0
                while i < numColors and len(solution[city]["Color"]) != 1:
                    if i not in unavailableColors[city]["Unavailable"] and len(solution[city]["Color"]) != 1: # If there's a color missing, then it's safe to make it this color
                        solution[city]["Color"] = [i]
                        for neighbor in solution[city]["Neighbors"]:
                            unavailableColors[neighbor]["Unavailable"].append(solution[city]["Color"][0])
                    elif i == numColors-1: # If all colors are present, then we can't go on without another color
                        solution[city]["Color"] = [i + 1]
                        for neighbor in solution[city]["Neighbors"]:
                            unavailableColors[neighbor]["Unavailable"].append(solution[city]["Color"][0])
                        numColors += 1
                    i += 1
            else:
                complete += 1
            if complete == len(list(solution)):
                notDone = False
    # print("We need at least", numColors, "colors")
    # for city in solution:
    #     print(city, " is of color ", solution[city]["Color"])

def mapColorAC3(solution, heuristic):
    firstEl = list(solution.keys())[0]
    numColors = 1
    solution[firstEl]["Color"] = [0]
    notDone = True
    complete = 0
    cities = list(solution.keys())[1:]
    if heuristic == 0:
        random.shuffle(cities)
    while notDone:
        if heuristic == 1:
            cities.sort(key = lambda x : (-mapNearColors(x, solution)))
        elif heuristic == 2:
            cities.sort(key = lambda x : (-mapNearColors(x, solution), mapColorInfluence(x, solution)))
        for city in cities:
            if len(solution[city]["Color"]) != 1:
                nearColors = []
                if len(solution[city]["Color"]) != 1: # Go through all the unassigned cities
                    for neighbor in solution[city]["Neighbors"]:
                        if len(solution[neighbor]["Color"]) == 1:
                            nearColors.append(solution[neighbor]["Color"][0]) # Adds unpotential colors based on neighbors        
                i = 0
                while i < numColors and len(solution[city]["Color"]) != 1:
                    if i not in nearColors and len(solution[city]["Color"]) != 1: # If there's a color missing, then it's safe to make it this color
                        solution[city]["Color"] = [i]
                    elif i == numColors-1: # If all colors are present, then we can't go on without another color
                        solution[city]["Color"] = [i + 1]
                        numColors += 1
                    i += 1
            else:
                complete += 1
            if complete == len(list(solution)):
                notDone = False
    # print("We need at least", numColors, "colors")
    # for city in solution:
    #     print(city, " is of color ", solution[city]["Color"])

def mapColorMRV(x, unavailableColors):
    return len(unavailableColors[x])

def mapColorInfluence(x, solution):
    return len(solution[x]["Neighbors"])

def mapNearColors(x, solution):
    count = 0
    for neighbor in solution[x]["Neighbors"]:
        if len(solution[neighbor]["Color"]) == 1:
            count += 1
    return count

if __name__ == "__main__":
    # Here you can decide the ordering of the heuristic for the sudoku
    heuristic = 0 # 0 for random, 1 for MRV, 2 for MRV with Degree
    solution = mapColorPopulate()
    for i in range(3):
        heuristic = i
        start_time = datetime.datetime.now()
        for j in range(20):
            mapColorAC3(solution, heuristic)
        end_time = datetime.datetime.now()
        elapsed = end_time - start_time
        print("Average time elapsed for mapColorAC3 using heuristic", heuristic, "is:", elapsed.total_seconds()*1000/20)

        start_time = datetime.datetime.now()
        for j in range(20):
            mapColorDFS(solution, heuristic)
        end_time = datetime.datetime.now()
        elapsed = end_time - start_time
        print("Average time elapsed for mapColorDFS using heuristic", heuristic, "is:", elapsed.total_seconds()*1000/20)

        start_time = datetime.datetime.now()
        for j in range(20):
            mapColorForward(solution, heuristic)
        end_time = datetime.datetime.now()
        elapsed = end_time - start_time
        print("Average time elapsed for mapColorForward using heuristic", heuristic, "is:", elapsed.total_seconds()*1000/20)

    board, answer, boardCols = sudokuPopulate()
    if heuristic == 0:
        random.shuffle(answer)
    for i in range(3):
        heuristic = i
        start_time = datetime.datetime.now()
        for j in range(20):
            tempBoard = [row[:] for row in board]
            tempAnswer = [row[:] for row in answer]
            tempCols = [row[:] for row in boardCols]
            tempBoard = sudokuDFS(tempBoard, tempAnswer, tempCols, heuristic)
        end_time = datetime.datetime.now()
        elapsed = end_time - start_time
        print("Average time elapsed for sudokuDFS using heuristic", heuristic, "is:", elapsed.total_seconds()*1000/20)

        start_time = datetime.datetime.now()
        for j in range(20):
            tempBoard = [row[:] for row in board]
            tempAnswer = [row[:] for row in answer]
            tempCols = [row[:] for row in boardCols]
            tempBoard = sudokuForward(tempBoard, tempAnswer, tempCols, heuristic)
        end_time = datetime.datetime.now()
        elapsed = end_time - start_time
        print("Average time elapsed for sudokuForward using heuristic", heuristic, "is:", elapsed.total_seconds()*1000/20)

        start_time = datetime.datetime.now()
        for j in range(20):
            tempBoard = [row[:] for row in board]
            tempAnswer = [row[:] for row in answer]
            tempCols = [row[:] for row in boardCols]
            tempBoard = sudokuAC3(tempBoard, tempAnswer, tempCols, heuristic)
        end_time = datetime.datetime.now()
        elapsed = end_time - start_time
        print("Average time elapsed for sudokuAC3 using heuristic", heuristic, "is:", elapsed.total_seconds()*1000/20)
        # for i in range(len(board)):
        #     print(tempBoard[i])

    # print("\nOur solution is")
    # for i in range(len(board)):
    #     print(board[i])