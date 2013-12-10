import curses
import curses.wrapper
import time

directions = [(-1, 0), (-1, 1), (0, 1), (1, 1), (1, 0), (1, -1), (0, -1), (-1, -1)]
initialConditions = lambda x, y: x < 3 or y > 14

class Cell(object):
    def __init__(self, neighbours, alive):
        self.neighbours = neighbours
        self.alive = alive
    def addNeighbour(self, neighbour):
        self.neighbours.append(neighbour)
    def isAlive(self):
        return self.alive
    def liveNeighbourCount(self, grid):
        return len([True for neighbourFunction in self.neighbours if neighbourFunction(grid).isAlive()])
    def moveGeneration(self, grid):
        count = self.liveNeighbourCount(grid)
        alive = (count == 2 and self.isAlive()) or count == 3 
        return Cell(self.neighbours, alive)

def cellLookup(x, y):
    return lambda g: g.grid[x][y]

class Grid(object):
    def __init__(self):
        self.grid = []

    def populate(self, size_x, size_y):
        for i in range(0, size_x):
            row = [Cell([], initialConditions(i,j)) for j in range(0, size_y)]
            self.grid.append(row)
        for i in range(0, size_x):
            for j in range(0, size_y):
                for (x_shift, y_shift) in directions:
                    x_coord = x_shift + i
                    y_coord = y_shift + j
                    if x_coord >= 0 and x_coord < size_x and y_coord >= 0 and y_coord < size_y:
                        self.grid[i][j].addNeighbour(cellLookup(x_coord, y_coord))

    def moveGeneration(self):
        g = Grid()
        for row in self.grid:
            newRow = [cell.moveGeneration(self) for cell in row]
            g.grid.append(newRow);
        return g

def run(stdscr):
    g = Grid()
    g.populate(35,90)
    turn = 0
    while True:
        stdscr.clear()
        stdscr.move(0,0)
        stdscr.addnstr('Turn %d' % turn, 100);
        r = 1
        for row in g.grid:
            stdscr.move(r, 0)
            stdscr.addnstr(''.join(['0' if cell.isAlive() else ' ' for cell in row]), 100)
            r = r + 1
        stdscr.refresh()
        time.sleep(0.2)
        turn += 1
        g = g.moveGeneration()

if __name__ == '__main__':
    curses.wrapper(run)

