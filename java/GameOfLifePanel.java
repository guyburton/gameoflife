import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

public class GameOfLifePanel extends JPanel{

    private static final int GRID_WIDTH = 50;
    private static final int GRID_HEIGHT = 60;
    private Grid grid;

    public GameOfLifePanel(Grid grid) {
        this.grid = grid;
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        final double width = getWidth();
        final double height = getHeight();

        final double unitsPerCellWidth = width / GRID_WIDTH;
        final double unitsPerCellHeight = height / GRID_HEIGHT;

        for (int y=0; y < grid.getHeight(); y++) {
            for (int x=0; x < grid.getWidth(); x++) {
                g.setColor(grid.isAlive(x, y) ? Color.BLACK : getBackground());
                g.fillRect(
                        (int)(unitsPerCellWidth * x),
                        (int)(unitsPerCellHeight * y),
                        (int) unitsPerCellWidth,
                        (int) unitsPerCellHeight);
            }
        }
    }

    private void nextGeneration() {
        grid = grid.nextGeneration();
    }

    public int getGeneration() {
        return grid.getGeneration();
    }

    interface InitialiseFunction {
        boolean isAlive(int x, int y);
    }

    private final static class Cell {
        private final boolean alive;
        private final int x;
        private final int y;

        private Cell(int x, int y, boolean alive) {
            this.alive = alive;
            this.x = x;
            this.y = y;
        }

        private boolean isAlive() {
            return alive;
        }

        public boolean nextGeneration(Grid g) {
            int neighbours = g.getLiveNeighboursCount(x, y);
            return (neighbours == 2 && isAlive()) || neighbours == 3;
        }
    }

    private final static class Grid {
        private final Cell[][] cells;
        private final int generation;

        private Grid(Cell[][] cells, int generation) {
            this.cells = cells;
            this.generation = generation;
        }

        /**
         * Returns a grid
         * @return
         */
        public Grid nextGeneration() {
            return initialise(getWidth(), getHeight(), generation + 1, new InitialiseFunction() {
                @Override
                public boolean isAlive(int x, int y) {
                    return cells[y][x].nextGeneration(Grid.this);
                }
            });
        }

        public static Grid initialise(int gridWidth, int gridHeight, int generation, InitialiseFunction initialiser) {
            Cell[][] cells = new Cell[gridHeight][gridWidth];
            for (int y=0; y<gridHeight; y++) {
                for (int x=0; x<gridWidth; x++) {
                    cells[y][x] = new Cell(x, y, initialiser.isAlive(x, y));
                }
            }
            return new Grid(cells, generation);
        }

        public int getLiveNeighboursCount(int x, int y) {
            int neighbours = 0;
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int neighbour_x = x + i;
                    int neighbour_y = y + j;
                    if ((i != 0 || j != 0) &&
                            neighbour_x >= 0 && neighbour_x < getWidth() &&
                            neighbour_y >= 0 && neighbour_y < getHeight()) {
                        Cell cell = cells[neighbour_y][neighbour_x];
                        if (cell.isAlive()) {
                            neighbours++;
                        }
                    }
                }
            }
            return neighbours;
        }

        private int getGeneration() {
            return generation;
        }

        public int getWidth() {
            return cells[0].length;
        }

        public int getHeight() {
            return cells.length;
        }

        public boolean isAlive(int x, int y) {
            return cells[y][x].isAlive();
        }
    }

    public static void main(String[] args) {
        final GameOfLifePanel panel = new GameOfLifePanel(Grid.initialise(GRID_WIDTH, GRID_HEIGHT, 0, new InitialiseFunction() {
            private final Random random = new Random();
            @Override
            public boolean isAlive(int x, int y) {
                return random.nextBoolean();
            }
        }));

        final JFrame frame = new JFrame();
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setSize(500,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        java.util.Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(
            new TimerTask() {
                @Override
                public void run() {
                    panel.nextGeneration();
                    frame.setTitle("Game of life, turn " + panel.getGeneration());
                    frame.repaint();
                }
            }, 1000, 200);
    }
}
