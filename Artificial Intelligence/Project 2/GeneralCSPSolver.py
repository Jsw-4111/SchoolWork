import json
import math

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

def sudokuDFS(board, answer, boardCols):
    n = int(len(board)**.5)

def sudokuForward(board, answer, boardCols):
    pass

def sudokuAC3(board, answer, boardCols):
    unsolved = True
    # Here we create a while loop that populates our answer board with potential solutions
    # We will eliminate our potentials based on our constraints and accept guaranteed solutions
    n = int(len(board)**.5)
    while(unsolved):
        for potentials in list(answer): # Apparently adding a slice makes a copy of answer, which is pretty nice when removing things at the same time
            for i in range(1, len(board) + 1): # Start at 1 and end at 10 because we cannot have a 0 value in sudoku and want to go up to 9
                if i not in board[potentials[0]] and i not in boardCols[potentials[1]] and i not in potentials[2:]:
                    potentials.append(i)
                elif i in potentials[2:] and (i in board[potentials[0]] or i in boardCols[potentials[1]]):
                    tempIndex = 0
                    tempIndex = potentials.index(i, 2)
                    del potentials[tempIndex]
            if len(answer) < 40:
                yay = True
            # These variables will be used for selecting our region
            # Divide by integer so that our result truncates, then multiply by n to get the index that we start at
            rowStart = int((potentials[0]//n)*n)
            colStart = int((potentials[1]//n)*n)
            for i in range(rowStart, rowStart + n):
                for j in range(colStart, colStart + n):
                    if board[i][j] in potentials[2:]: # See if the value is in our list of potentials excluding the indices
                        tempIndex = potentials.index(board[i][j], 2)
                        del potentials[tempIndex]
                        # print(potentials[2:])
                        # potentials[1:].remove(board[i][j]) # For some reason the last two cells are unable to delete their values here, after testing, it might be because they have more than 3 elements.
                        # print(potentials[2:])
                        # print(board[i][j])
            if len(potentials) == 3: # If by now there is only 1 potential answer, then we have our answer for this cell
                board[potentials[0]][potentials[1]] = potentials[2]
                boardCols[potentials[1]][potentials[0]] = potentials[2]
                answer.remove(potentials)
        if len(answer) == 0:
            unsolved = False
    return board

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

def mapColorDFS(solution):
    firstEl = list(solution.keys())[0]
    solution[firstEl]["Color"] = [0]
    unmatchedCities = list(solution.keys())[1:]
    unavailableColors = []
    for i in range(len(unmatchedCities)):
        unavailableColors.append([])
    city = 0
    while city <= len(unmatchedCities):
        ct = str(city)
        if city != firstEl:
            notDone = True
        else:
            notDone = False
        i = 0
        neighbors = solution[ct]["Neighbors"]
        while(notDone):
            solution[ct]["Color"] = [i]
            notTaken = True
            neighbor = 0
            while neighbor <= len(neighbors)-1 and notTaken:
                if i in solution[neighbors[neighbor]]["Color"]:
                    i += 1
                    notTaken = False # End the for loop if this color is taken
                elif neighbor == len(neighbors)-1:
                    notDone = False
                neighbor += 1
        city += 1
    for city in solution:
        print(city, " is of color ", solution[city]["Color"])

def mapColorForward(solution): # This one needs to be changed from AC3
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
    while notDone:
        for city in solution:
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
    print("We need at least", numColors, "colors")
    for city in solution:
        print(city, " is of color ", solution[city]["Color"])

def mapColorAC3(solution):
    firstEl = list(solution.keys())[0]
    numColors = 1
    solution[firstEl]["Color"] = [0]
    notDone = True
    complete = 0
    while notDone:
        for city in solution:
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
    print("We need at least", numColors, "colors")
    for city in solution:
        print(city, " is of color ", solution[city]["Color"])

            

if __name__ == "__main__":
    solution = mapColorPopulate()
    mapColorForward(solution)
    board, answer, boardCols = sudokuPopulate()
    board = sudokuAC3(board, answer, boardCols)
    print("\nOur solution is")
    for i in range(len(board)):
        print(board[i])