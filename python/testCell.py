import unittest
from conway import Cell

def createLiveCell():
    return Cell([], True)
def createDeadCell():
    return Cell([], False)
def connectCells(c1, c2):
    c1.addNeighbour(lambda x: c2)
    c2.addNeighbour(lambda x: c1)

class TestCell(unittest.TestCase):
    def testCellNeighbourCount(self):
        c1 = createLiveCell() 
        c2 = createLiveCell()
        connectCells(c1, c2)
        assert c1.liveNeighbourCount(None) == 1

    def testCellDiesWithNoNeighbour(self):
        c1 = createLiveCell()
        child = c1.moveGeneration(None)
        assert not child.isAlive()

    def testCellSurvivesGenerationBecauseTwoNeighbours(self):
        c1 = createLiveCell() 
        c2 = createLiveCell() 
        connectCells(c1, c2)
        connectCells(c1, c2)
        child = c1.moveGeneration(None)
        assert child.isAlive()

    def testCellDiesWithFourNeighbours(self):
        c1 = createLiveCell() 
        c2 = createLiveCell() 
        for i in range(0,4):
            connectCells(c1, c2) 
        child = c1.moveGeneration(None)
        assert not child.isAlive()

    def testCellSpawnedFromThreeNeighbours(self):
        c1 = createDeadCell()
        c2 = createLiveCell()
        for i in range(0,3):
            connectCells(c1, c2)
        child = c1.moveGeneration(None)
        assert child.isAlive()

if __name__ == '__main__':
    unittest.main()
